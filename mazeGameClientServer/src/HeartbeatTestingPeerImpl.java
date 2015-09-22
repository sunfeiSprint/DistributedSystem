import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.Serializable;
import java.net.*;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.*;

/**
 * Created by Benze on 9/21/15.
 */
public class HeartbeatTestingPeerImpl implements HeartbeatTestingPeer{

    private Executor executor = Executors.newSingleThreadExecutor();

    private class HeartbeatClient implements Runnable {

        private HeartbeatConfiguration heartbeatConfiguration;

        private DatagramSocket socket;

        public HeartbeatClient (DatagramSocket socket, HeartbeatConfiguration heartbeatConfiguration) {
            this.socket = socket;
            this.heartbeatConfiguration = heartbeatConfiguration;
        }

        @Override
        public void run() {
            // periodically sends heartbeat message to peer
            System.out.println("heartbeat client to: " + heartbeatConfiguration.inetAddress +
                    " ,port: " + heartbeatConfiguration.port);
            try {
                socket.setSoTimeout(1000);
            } catch (SocketException e) {
                e.printStackTrace();
            }

            while(!Thread.interrupted()) {
                // ping the heartbeat server
                try {
                    System.out.println("ping");
                    socket.send(new DatagramPacket(new byte[1], 1, heartbeatConfiguration.inetAddress,
                            heartbeatConfiguration.port));
                    DatagramPacket pong = new DatagramPacket(new byte[1], 1);
                    socket.receive(pong);
                    System.out.println("pong");
                    TimeUnit.SECONDS.sleep(1);
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    onHeartbeatServerDie();
                    Thread.currentThread().interrupt();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class HeartbeatServer implements Runnable {

        private DatagramSocket socket;

        private ScheduledExecutorService clockExecutor = Executors.newSingleThreadScheduledExecutor();

        private volatile boolean heartbeatReceived = false;

        private volatile boolean clientHasDied = false;

        public HeartbeatServer (DatagramSocket socket) {
            this.socket = socket;
        }

        public synchronized boolean hasReceivedHeartbeat() {
            return heartbeatReceived;
        }

        public synchronized void clearHeartbeat() {
            heartbeatReceived = false;
        }

        public synchronized void setReceivedHeartbeat() {
            heartbeatReceived = true;
        }

        @Override
        public void run() {
            // periodically receive heartbeat message and respond
            ScheduledFuture future = clockExecutor.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    if (hasReceivedHeartbeat()) {
                        clearHeartbeat();
                    } else {
                        onHeartbeatClientDie();
                        clientHasDied = true;
                        socket.close();
                        clockExecutor.shutdownNow();
                        // TODO: stop this thread
                    }
                }
            }, 2, 2, TimeUnit.SECONDS);

            // TODO: how to determine that peer is dead?
            while(!clientHasDied) {
                try {
                    DatagramPacket ping = new DatagramPacket(new byte[1], 1);
                    // receive ping
                    socket.receive(ping);
                    System.out.println("receive ping");
                    setReceivedHeartbeat();
                    // respond
                    socket.send(new DatagramPacket(new byte[1], 1, ping.getAddress(),
                            ping.getPort()));
                    System.out.println("send pong");
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public HeartbeatConfiguration heartbeatConfig() {
        try {
            System.out.println("receive heartbeat request, setting up server");
            DatagramSocket socket = new DatagramSocket(0);
            InetAddress localHost = InetAddress.getByName("localhost");
            HeartbeatConfiguration heartbeatConfig = new HeartbeatConfiguration(localHost,
                    socket.getLocalPort());
            executor.execute(new HeartbeatServer(socket));
            return heartbeatConfig;
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setUpHeartbeatThread(HeartbeatTestingPeer peer) {
        // set up heartbeat socket
        try {
            DatagramSocket socket = new DatagramSocket(0);
            HeartbeatConfiguration heartbeatConfiguration = peer.heartbeatConfig();
            executor.execute(new HeartbeatClient(socket, heartbeatConfiguration));
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void onHeartbeatServerDie() {
        System.out.println("server dies");
    }

    public void onHeartbeatClientDie() {
        System.out.println("client dies");
    }

    public static void startAsHost() {
        try {
            HeartbeatTestingPeerImpl server = new HeartbeatTestingPeerImpl();
            HeartbeatTestingPeer stub = (HeartbeatTestingPeer) UnicastRemoteObject.exportObject(server, 0);
            // Bind the remote object's stub in the registry
            Registry rmiRegistry = LocateRegistry.createRegistry(8888);
            rmiRegistry.bind("Heartbeat", stub);
            System.out.println("Server ready");
        } catch (AccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }
    }

    public static void startAsClient() {
        String host = "localhost";
        int port = 8888;
        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            HeartbeatTestingPeer server = (HeartbeatTestingPeer) registry.lookup("Heartbeat");
            HeartbeatTestingPeerImpl player = new HeartbeatTestingPeerImpl();
            UnicastRemoteObject.exportObject(player, 0);
            player.setUpHeartbeatThread(server);
        } catch (RemoteException e) {
            // join game failed
            e.printStackTrace();
        } catch (NotBoundException e) {
            // look up MazeServer failed
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if(args.length < 1) {
            System.exit(0);
        }
        if(args[0].equals("host")) {
            startAsHost();
        } else {
            startAsClient();
        }
    }


}
