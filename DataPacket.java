import com.savarese.rocksaw.net.RawSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class DataPacket{
	private int srcPort, dstPort, len;
	private boolean ACK;
	private String checksum;
	private byte[] data;

	public DataPacket(int src,int dst,byte[] data) {
		this.srcPort = src;
		this.dstPort = dst;
		this.data = data;
		this.len = data.length + 16;
		this.ACK = false;
		this.checksum = this.caculateChecksum();

	}

	public DataPacket(int src, int dst, boolean ACK) {
		this.srcPort = src;
		this.dstPort = dst;
		this.data = new byte[0];
		this.len = 16;
		this.ACK = ACK;
		this.checksum = this.caculateChecksum();
	}

	public DataPacket(byte[] bytes) {
		this.srcPort = ByteBuffer.wrap(Arrays.copyOfRange(bytes, 0, 4)).getInt();
		this.dstPort = ByteBuffer.wrap(Arrays.copyOfRange(bytes, 4, 8)).getInt();
		this.checksum = util.toHexString(Arrays.copyOfRange(bytes, 8, 10));
		this.len = ByteBuffer.wrap(Arrays.copyOfRange(bytes, 10, 14)).getInt();
		this.ACK = bytes[14] == 1;
		this.data = (len == 16) ? new byte[0] :Arrays.copyOfRange(bytes, 16, len);
	}

	public byte[] getBytes() {
		byte[] bytes = util.concat(util.toByte(srcPort), util.toByte(dstPort));
		bytes = util.concat(bytes, util.toByte(checksum));
		bytes = util.concat(bytes, util.toByte(len));
		bytes = util.concat(bytes, util.toByte(ACK));
		byte[] padding = {0};
		bytes = util.concat(bytes, padding);
		bytes = util.concat(bytes, data);
		return bytes;
	}

	public int getSrcPort() {
		return this.srcPort;
	}

	public int getLen() { return this.len; }

	public int getDstPort() {
		return this.dstPort;
	}

    public boolean isAck() { return this.ACK; }

    public byte[] getData() { return data; }

    public String getChecksum() { return checksum; }

    private String caculateChecksum() {
		byte[] header = util.toByte(srcPort);
		header = util.concat(header, util.toByte(dstPort));
		header = util.concat(header, util.toByte(len));
		header = util.concat(header, util.toByte(ACK));
		byte[] padding = {0};
		header = util.concat(header,padding);
		int checksumValue = util.calculateChecksum(util.concat(header, data));
		return util.toHexString(util.toByte(checksumValue));
	}

	public boolean isEqual(DataPacket otherPack) {
		if(otherPack == null) return false;
		boolean isEqual = true;
		byte[] otherPackBytes = otherPack.getBytes();
		byte[] thisPackBytes = this.getBytes();
		if(otherPackBytes.length != thisPackBytes.length) return false;
		for(int i = 0; i < thisPackBytes.length; i++) {
			if(otherPackBytes[i] != thisPackBytes[i]) return false;
		}
		return true;
	}

}