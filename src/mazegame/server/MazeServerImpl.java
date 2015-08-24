package mazegame.server;

import mazegame.maze.MazeServer;
import mazegame.maze.MazePlayer;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Benze on 8/23/15.
 */
public class MazeServerImpl implements MazeServer {

    private static final int REGISTRY_PORT = 8080;

    private static Registry rmiRegistry;

    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private static class DelayNotify implements Runnable {

        MazePlayer player;

        public DelayNotify(MazePlayer player) {
            this.player = player;
        }

        @Override
        public void run() {
            try {
                player.helloPlayer("this is server, game start!");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String helloServer(MazePlayer player, String msg) throws RemoteException {
        System.out.println("hello server from player: " + msg);
        // send a message to player asynchronously in 3 seconds
        executor.schedule(new DelayNotify(player), 5, TimeUnit.SECONDS);
        return "hello, " + msg;

    }

    public static void main(String[] args) {
//        if (System.getSecurityManager() == null) {
//            System.setSecurityManager(new SecurityManager());
//        }

        try {
            MazeServer server = new MazeServerImpl();
            MazeServer stub = (MazeServer) UnicastRemoteObject.exportObject(server, 0);
            // Bind the remote object's stub in the registry
            rmiRegistry = LocateRegistry.createRegistry(REGISTRY_PORT);
            rmiRegistry.bind("MazeServer", stub);
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
