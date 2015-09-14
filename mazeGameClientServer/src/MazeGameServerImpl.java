import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Benze on 8/31/15.
 */
public class MazeGameServerImpl implements MazeGameServer{

    private static Registry rmiRegistry;

    private final int GAME_INIT = 0;

    private final int GAME_PENDING_START = 1;

    private final int GAME_START = 2;

    private final int GAME_END = 3;

    private volatile int gameStatus = GAME_INIT;

//    private Map<Integer, Player> players = new HashMap<Integer, Player>();
    private Map<Integer, MazeGameClient> clients = new HashMap<>();

    private int playerNum = 0;

    private int numOfTreasure;

    private int dimension;

    /** registry port for testisng */
    private static final int REGISTRY_PORT = 8888;

    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    /** controller of the maze game */
    private Game game;

    private class GameInitializeTask implements Runnable {
        @Override
        public void run() {
            System.out.println("game start");
            gameStatus = GAME_START;
            // notifyGameStart is non-blocking.
            for(Integer id : clients.keySet()) {
                MazeGameClient client = clients.get(id);
                try {
                    client.notifyStart(id, game.createMsgForPlayer(id));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class GameEndTask implements Runnable {
        @Override
        public void run() {
            System.out.println("game end");
            // notifyGameEnd
            for(Integer id : clients.keySet()) {
                MazeGameClient client = clients.get(id);
                try {
                    client.notifyEnd(game.createMsgForPlayer(id));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            try {
                rmiRegistry.unbind(MazeGameServer.NAME);
                UnicastRemoteObject.unexportObject(MazeGameServerImpl.this, false);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (NotBoundException e) {
                e.printStackTrace();
            }
            executor.shutdown();
        }
    }

    public MazeGameServerImpl(int dimension, int numOfTreasure) {
        this.numOfTreasure = numOfTreasure;
        this.dimension = dimension;
        game = new Game(numOfTreasure, dimension);
    }

    @Override
    public synchronized boolean joinGame(MazeGameClient client) throws RemoteException {
        if(gameStatus == GAME_INIT) {
            // The first player join in, notify game start in 20 seconds
            clients.put(playerNum, client);
            game.addPlayer(playerNum, new Player());
            playerNum++;
            //TODO: change back to 20
            executor.schedule(new GameInitializeTask(), 5, TimeUnit.SECONDS);
            gameStatus = GAME_PENDING_START;
            System.out.println("first client");
            return true;
        } else if (gameStatus == GAME_PENDING_START) {
            clients.put(playerNum, client);
            game.addPlayer(playerNum, new Player());
            playerNum++;
            System.out.println("new client");
            return true;
        } else {
            // game started, can't join anymore
            System.out.println("join refuse");
            return false;
        }
    }

    @Override
    public ServerMsg move(int playerID, char dir) throws RemoteException {
        if(gameStatus == GAME_START) {
            if(game.playerMove(playerID, dir)) {
                if (game.isGameOver()) {
                    // game is over, send back game_over response and remove player
                    gameStatus = GAME_END;
                    ServerMsg endMsg = game.createGameOverMsgForPlayer(playerID);
                    // notify other players
                    executor.execute(new GameEndTask());
                    return endMsg;
                }
            }
            return game.createMsgForPlayer(playerID);
        } else {
            // receive move request when game is over, send back game over message
            return game.createGameOverMsgForPlayer(playerID);
        }
    }

    public static void main(String[] args) {
        if(args.length < 2) {
            System.out.println("usage: <arg0> dimension, <arg1> number of treasure");
        }
        int dimension = Integer.valueOf(args[0]);
        int numOfTreasure = Integer.valueOf(args[1]);
        try {
            MazeGameServerImpl server = new MazeGameServerImpl(dimension, numOfTreasure);
            MazeGameServer stub = (MazeGameServer) UnicastRemoteObject.exportObject(server, 0);
            // Bind the remote object's stub in the registry
            rmiRegistry = LocateRegistry.createRegistry(REGISTRY_PORT);
            rmiRegistry.bind("MazeGameServer", stub);
            System.out.println("Server ready");
        } catch (AccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }
    }
}
