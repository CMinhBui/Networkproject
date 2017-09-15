import javafx.util.Pair;

import javax.xml.crypto.Data;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.math.BigInteger;
import java.util.Arrays;
import java.lang.StringBuilder;
import java.lang.Byte;
import java.util.regex.Pattern;

public class util {
	public static byte[] concat(byte[] a, byte[] b){
		byte[] c = new byte[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}

	public static byte[] toByte(int a) {
		byte[] bytes = ByteBuffer.allocate(4).putInt(a).array();
		return bytes;
	}

	public static byte[] toByte(boolean a) {
		byte b = (byte)(a?1:0);
		byte[] bytes = {b};
		return bytes;
	}

	public static byte[] toByte(String hexString) {
		byte[] b = new BigInteger(hexString,16).toByteArray();
		if(b.length == 2) return b;
		return Arrays.copyOfRange(b, 1, b.length);
	}

	public static String toBinaryString( byte[] bytes )
	{
	    StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
	    for( int i = 0; i < Byte.SIZE * bytes.length; i++ )
	        sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
	    return sb.toString();
	}

	public static String toHexString (byte[] bytes) {
		char[] hexArray = "0123456789ABCDEF".toCharArray();
		char[] hexChars = new char[bytes.length * 2];
		for ( int j = 0; j < bytes.length; j++ ) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		int startPoint = hexChars.length - 4;
		StringBuffer hexString = new StringBuffer();
		for(int i = startPoint; i < hexChars.length; i++) hexString.append(hexChars[i]);

		return hexString.toString();
	}

	public static DataPacket extractDataPacket(byte[] bytes) {
		String versionAndIHL = toBinaryString(Arrays.copyOfRange(bytes, 0, 1));
		int headerLen = Integer.parseInt(versionAndIHL.substring(4,8),2)*4;
		int totalLen = bytes[3];
		byte[] dataInByte = Arrays.copyOfRange(bytes, headerLen, totalLen);
		DataPacket dataPack = new DataPacket(dataInByte);
		return dataPack;
	}

	public static String toString(byte[] bytes) {
		StringBuffer hexString = new StringBuffer();
		for (int i=0;i<bytes.length;i++) {
			String hex=Integer.toHexString(0xff & bytes[i]);
			if(hex.length()==1) hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}

	public static byte[] addClientInfo(byte[] data, InetAddress clientAddress, int clientPort) {
		byte[] newData = concat(clientAddress.getAddress(), toByte(clientPort));
		newData = concat(newData, data);
		return newData;
	}

	public static Pair<InetAddress, Integer> extractClientInfo(byte[] data) throws Exception{
		InetAddress clientAddess = InetAddress.getByAddress(Arrays.copyOfRange(data, 0, 4));
		int clientPort = ByteBuffer.wrap(Arrays.copyOfRange(data, 4, 8)).getInt();
		return new Pair<>(clientAddess, clientPort);
	}

	public static InetAddress toAddress(String s) throws Exception{
		String [] Ses = s.split(Pattern.quote("."));
		byte[] bytes = new byte[4];
		for(int i = 0; i < 4; i++) {
			int instance = Integer.parseInt(Ses[i]);
			if (instance > 127) instance = instance - 256;
			bytes[i] = Byte.parseByte("" + instance);
		}
		return InetAddress.getByAddress(bytes);
	}

	public static int calculateChecksum(byte[] buf) {
		int length = buf.length;
		int i = 0;

		int sum = 0;
		int data;

		while (length > 1) {
			data = (((buf[i] << 8) & 0xFF00) | ((buf[i + 1]) & 0xFF));
			sum += data;
			if ((sum & 0xFFFF0000) > 0) {
				sum = sum & 0xFFFF;
				sum += 1;
			}

			i += 2;
			length -= 2;
		}

		if (length > 0) {
			sum += (buf[i] << 8 & 0xFF00);
			if ((sum & 0xFFFF0000) > 0) {
				sum = sum & 0xFFFF;
				sum += 1;
			}
		}

		sum = ~sum;
		sum = sum & 0xFFFF;
		return sum;

	}

}