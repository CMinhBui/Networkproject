import javafx.util.Pair;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by root on 6/3/17.
 */
public class Client extends Thread {
    Protocol clientProtocol;
    InetAddress serverAddress;
    int serverPort;
    String message;
    public Client(int port, InetAddress serverAddress, int serverPort, String message) throws Exception{
        this.clientProtocol = new Protocol(port);
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.message = message;
        this.clientProtocol.start();
    }

    @Override
    public void run() {
        DataPacket dataPack = new DataPacket(clientProtocol.getPort() , serverPort, message.getBytes());
        clientProtocol.send(serverAddress, dataPack);

        while (clientProtocol.getDataMap().size() < 2) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.perform();
    }

    private void perform() {
        System.out.println("Hash value of \"" + message + "\"");
        byte[] MD5value = new byte[12];
        byte[] SHA256value = new byte[12];
        HashMap<Pair<InetAddress, Integer>, Pair<DataPacket, Boolean>> rcvDatas = clientProtocol.getDataMap();
        Set<Pair<InetAddress, Integer>> keySet = rcvDatas.keySet();
        for(Pair<InetAddress, Integer> i : keySet) {
            byte[] rcvData = rcvDatas.get(i).getKey().getData();
            if(rcvData[0] == 1) {
                MD5value = Arrays.copyOfRange(rcvData, 1, rcvData.length);
            } else {
                SHA256value = Arrays.copyOfRange(rcvData, 1, rcvData.length);
            }
        }

        System.out.println("SHA256: " + util.toString(SHA256value));
        System.out.println("MD5: " + util.toString(MD5value));
        System.out.println("Done. Waiting for client to shutdown........");

        this.clientProtocol.shutdown();
    }
}
