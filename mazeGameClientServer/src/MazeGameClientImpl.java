import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.TimeUnit;

/**
 * Created by Benze on 8/24/15.
 */
public class MazeGameClientImpl implements MazeGameClient {

    private final int GAME_INIT = 0;

    private final int GAME_START = 1;

    private final int GAME_END = 2;

    private volatile int gameStatus = GAME_INIT;

    private int playerId;

    private GameState gameState;

    /** Main io thread for user input */
    private Thread ioThread;

    public MazeGameClientImpl(Thread thread) {
        this.ioThread = thread;
    }

    @Override
    public synchronized void notifyStart(int playerId, GameState state) throws RemoteException {
        gameStatus = GAME_START;
        this.playerId = playerId;
        this.gameState = state;
        this.notifyAll();
    }

    @Override
    public synchronized void notifyEnd(GameState state) throws RemoteException {
        gameStatus = GAME_END;
        gameState = state;
        // interrupt the io thread
        ioThread.interrupt();
    }

    public synchronized boolean isGameStarted() {
        return (GAME_START == gameStatus);
    }

    public synchronized boolean isGameEnd() {
        return (GAME_END == gameStatus);
    }

    public void gaming(MazeGameServer server) {
        // when game is not end, print out game states and wait for user input
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while(!isGameEnd()) {
            System.out.println(gameState.toString());
            try {
                while(!br.ready()) {
                    TimeUnit.MILLISECONDS.sleep(200);
                }
                String input = br.readLine();
                if(input != null)
                    gameState = server.move(playerId, input);  // a blocking operation
            } catch (IOException e) {
                System.err.println("io error.");
            } catch (InterruptedException e) {
                System.err.println("received interruption, game end.");
            }
        }
        System.out.println("********Game End********");
        System.out.println(gameState.toString());
    }

    public static void main(String[] args) {
        String host = (args.length < 1) ? null : args[0];
        int port = Integer.parseInt(args[1]);
        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            MazeGameServer server = (MazeGameServer) registry.lookup("MazeGameServer");
            MazeGameClientImpl player = new MazeGameClientImpl(Thread.currentThread());
            UnicastRemoteObject.exportObject(player, 0);
            boolean success = server.joinGame(player);
            if(success) {
                System.out.println("join game successfully, waiting for game start...");
                while(!player.isGameStarted()) {
                    synchronized (player) {
                        player.wait();
                    }
                }
                System.out.println("game start!");
                player.gaming(server);
                // TODO: clean exit
            }
        } catch (RemoteException e) {
            // join game failed
            e.printStackTrace();
        } catch (NotBoundException e) {
            // look up MazeServer failed
            e.printStackTrace();
        } catch (InterruptedException e) {
            // interrupted when waiting for game start
            e.printStackTrace();
        }

    }
}
