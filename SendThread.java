import java.net.InetAddress;
import java.util.HashMap;
import com.savarese.rocksaw.net.RawSocket;
import javafx.util.Pair;

/**
 * Created by root on 5/16/17.
 */
public class SendThread extends Thread {
    private RawSocket socket;
    private DataPacket data;
    private InetAddress dstAddress;
    private HashMap<Pair<InetAddress, Integer>, Boolean> ACKMap;
    public SendThread(RawSocket socket, InetAddress dstAddress,
                      DataPacket data , HashMap<Pair<InetAddress, Integer>, Boolean> ACKmap) {
        this.ACKMap = ACKmap;
        this.data = data;
        this.socket = socket;
        this.dstAddress = dstAddress;
        ACKMap.put(new Pair<>(dstAddress, data.getDstPort()), false);
    }

    @Override
    public void run() {
        while (!ACKMap.get(new Pair<>(dstAddress, data.getDstPort()))) {
            try {
                socket.write(dstAddress, data.getBytes());
                this.sleep(10000);
            }
            catch (Exception e) {
            }
        }
    }
}
