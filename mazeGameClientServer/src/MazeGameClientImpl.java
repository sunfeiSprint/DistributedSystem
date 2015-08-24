import java.io.Console;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

/**
 * Created by Benze on 8/24/15.
 */
public class MazeGameClientImpl implements MazeGameClient {

    private final int GAME_INIT = 0;

    private final int GAME_START = 1;

    private final int GAME_END = 2;

    private volatile int gameStatus = GAME_INIT;

    private String playerId;

    private GameStates gameStates;

    @Override
    public synchronized void notifyStart(String playerId, GameStates state) throws RemoteException {
        gameStatus = GAME_START;
        this.playerId = playerId;
        this.gameStates = state;
        this.notifyAll();
    }

    @Override
    public synchronized void notifyEnd(GameStates state) throws RemoteException {
        gameStatus = GAME_END;
        gameStates = state;
       // System.console().
    }

    public synchronized boolean isGameStarted() {
        return (GAME_START == gameStatus);
    }

    public synchronized boolean isGameEnd() {
        return (GAME_END == gameStatus);
    }

    public void gaming(MazeGameServer server) {
        // when game is not end, print out game states and wait for user input
        Console console = System.console();
        while(!isGameEnd()) {
            console.printf(gameStates.toString());
            String move = console.readLine("move:");
            if(move != null) {
                try {
                    gameStates = server.move(playerId, move);
                } catch (RemoteException e) {
                    System.err.println("remote exception: " + e);
                }
            }
        }
        console.printf("********Game End********");
        console.printf(gameStates.toString());
    }

    public static void main(String[] args) {
        String host = (args.length < 1) ? null : args[0];
        int port = Integer.parseInt(args[1]);
        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            MazeGameServer server = (MazeGameServer) registry.lookup("MazeServer");
            MazeGameClientImpl player = new MazeGameClientImpl();
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
            // get registry failed
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
