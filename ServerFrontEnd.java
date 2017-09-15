import javafx.util.Pair;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by root on 6/3/17.
 */
public class ServerFrontEnd extends Thread{
    Protocol protocol;
    int MD5Port;
    int SHAPort;
    boolean running;

    public ServerFrontEnd(int port, int MD5Port, int SHAPort) throws Exception{
        this.MD5Port = MD5Port;
        this.SHAPort = SHAPort;
        this.protocol = new Protocol(port);
        this.protocol.start();
        running = true;
    }

    @Override
    public void run() {
        while (running) {
            perform();
            try {
                Thread.sleep(100);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void shutdown() {
        protocol.shutdown();
        running = false;
    }

    private void perform() {
        if(!protocol.getDataMap().isEmpty()) {
            HashMap<Pair<InetAddress, Integer>, Pair<DataPacket, Boolean>> rcvDatas = protocol.getDataMap();
            Set<Pair<InetAddress, Integer>> keySet = rcvDatas.keySet();
            for( Pair<InetAddress, Integer> clientInfo : keySet) {

                if(rcvDatas.get(clientInfo).getValue()) continue;

                DataPacket clientDataPack = rcvDatas.get(clientInfo).getKey();
                byte[] data = clientDataPack.getData();
                data = util.addClientInfo(data, clientInfo.getKey(), clientInfo.getValue());
                DataPacket dataPacketToMD5 = new DataPacket(protocol.getPort(), MD5Port, data);
                DataPacket dataPacketToSHA256 = new DataPacket(protocol.getPort(), SHAPort, data);

                try {
                    protocol.send(InetAddress.getLocalHost(), dataPacketToMD5);
                    protocol.send(InetAddress.getLocalHost(), dataPacketToSHA256);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                rcvDatas.put(clientInfo, new Pair<>(clientDataPack, true)
                );
            }
        }
    }
}
