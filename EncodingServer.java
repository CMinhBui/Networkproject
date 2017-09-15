import javafx.util.Pair;

import java.net.InetAddress;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by root on 6/3/17.
 */
public class EncodingServer extends Thread {
    private Protocol protocol;
    String algorithm;
    boolean running;
    public EncodingServer(int port, String algorithm) throws Exception{
        this.protocol = new Protocol(port);
        this.algorithm = algorithm;
        this.running = true;
        protocol.start();
    }

    @Override
    public void run() {
        while (running) {
            perform();
            try {
                Thread.sleep(100);
            }
            catch (Exception e) {
            }
        }
    }

    private void perform() {
        HashMap<Pair<InetAddress, Integer>, Pair<DataPacket,Boolean>> rcvDatas = protocol.getDataMap();
        if(!rcvDatas.isEmpty()) {
            Set<Pair<InetAddress, Integer>> keySet = rcvDatas.keySet();

            for( Pair<InetAddress, Integer> i : keySet) {
                if(rcvDatas.get(i).getValue()) continue;
                DataPacket rcvDataPack = rcvDatas.get(i).getKey();
                byte[] rcvData = rcvDataPack.getData();

                try {
                    Pair<InetAddress, Integer> clientInfo = util.extractClientInfo(rcvData);
                    rcvData = Arrays.copyOfRange(rcvData, 8, rcvData.length);
                    MessageDigest encoder = MessageDigest.getInstance(algorithm);
                    byte[] encodedData = encoder.digest(rcvData);
                    encodedData = util.concat(util.toByte(algorithm.equals("MD5")), encodedData);
                    DataPacket newDataPack = new DataPacket(protocol.getPort(), clientInfo.getValue(), encodedData);
                    protocol.send(clientInfo.getKey(), newDataPack);
                }catch (Exception e) {
                    e.printStackTrace();
                }

                rcvDatas.put(i, new Pair<>(rcvDataPack, true)
                );
            }
        }
    }

    public void shutdow() {
        running = false;
    }
}
