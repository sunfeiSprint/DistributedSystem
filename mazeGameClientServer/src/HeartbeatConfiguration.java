import java.io.Serializable;
import java.net.InetAddress;

/**
 * Created by Benze on 9/21/15.
 */
public class HeartbeatConfiguration implements Serializable {

    public HeartbeatConfiguration(InetAddress address, int port) {
        this.inetAddress = address;
        this.port = port;
    }

    public InetAddress inetAddress;

    public int port;
}
