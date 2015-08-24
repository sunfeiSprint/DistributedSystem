package mazegame.client;

import mazegame.maze.MazePlayer;
import mazegame.maze.MazeServer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by Benze on 8/23/15.
 */
public class MazePlayerImpl implements MazePlayer {

    @Override
    public String helloPlayer(String msg) throws RemoteException{
        System.out.println("hello player, " + msg);
        return "ok.";
    }

    public static void main(String[] args) {

        String host = (args.length < 1) ? null : args[0];
        int port = Integer.parseInt(args[1]);
        String name = args[2];
        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            MazeServer server = (MazeServer) registry.lookup("MazeServer");
            MazePlayer player = new MazePlayerImpl();
            UnicastRemoteObject.exportObject(player, 0);
            String response = server.helloServer(player, name);
            System.out.println("response: " + response);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
