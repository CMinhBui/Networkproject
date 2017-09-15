import com.savarese.rocksaw.net.RawSocket;
import javafx.util.Pair;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;

public class Protocol extends Thread{
	private HashMap<Pair<InetAddress, Integer>, Boolean> ACKMap;
	private RawSocket socket;
	private HashMap<Pair<InetAddress, Integer>, Pair<DataPacket, Boolean>> dataMap;
	private int port;
	private byte[] readBuffer;
	private boolean running;

	public Protocol(int port) throws Exception{
		this.socket = new RawSocket();
		this.socket.open(RawSocket.PF_INET, 169);
		this.socket.bind(InetAddress.getLocalHost());
		this.socket.setReceiveTimeout(1);
		this.ACKMap = new HashMap<>();
		this.dataMap = new HashMap<>();
		this.port = port;
		this.running = true;
		this.readBuffer = new byte[200];
	}

	//listening
	@Override
	public void run() {
		while(running) {
			try {
				byte[] srcAd = new byte[4];
				socket.read(readBuffer, srcAd);
				DataPacket dataPack = util.extractDataPacket(readBuffer);
				if(dataPack.getDstPort() == port && util.calculateChecksum(dataPack.getBytes()) == 0) {
					if (dataPack.isAck()) {
						Pair<InetAddress, Integer> otherSideInfo =
								new Pair<>(InetAddress.getByAddress(srcAd), dataPack.getSrcPort());
						ACKMap.put(otherSideInfo, true);
					} else {
						InetAddress otherSide = InetAddress.getByAddress(srcAd);
						dataMap.put(
								new Pair<>(otherSide, dataPack.getSrcPort()),
								new Pair<>(dataPack, false)
						);
						this.sendACK(otherSide, dataPack.getSrcPort());
					}
				}
			}
			catch (Exception e) {
			}
		}
	}

	public void send(InetAddress address, DataPacket dataPack) {
		SendThread sendThread = new SendThread(socket, address, dataPack, ACKMap);
		sendThread.start();
	}

	private void sendACK(InetAddress address, int dstPort) throws Exception{
		DataPacket ACKPack = new DataPacket(port, dstPort, true);
		socket.write(address, ACKPack.getBytes());
	}

	public HashMap<Pair<InetAddress, Integer>, Pair<DataPacket, Boolean>> getDataMap() {return dataMap;}

	public void shutdown() {
		this.running = false;
	}

	public int getPort() { return port; }
}