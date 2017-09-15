import java.net.InetAddress;
/**
 * Created by root on 5/17/17.
 */
public class runServer {
    public static void main(String[] args) throws Exception{
        int serverIport = Integer.parseInt(args[0]);
        int serverMD5Port = Integer.parseInt(args[1]);
        int server256Port = Integer.parseInt(args[2]);
        System.out.println(InetAddress.getLocalHost());    
        ServerFrontEnd serverFrontEnd = new ServerFrontEnd(serverIport, serverMD5Port, server256Port);
        EncodingServer MD5Server = new EncodingServer(serverMD5Port, "MD5");
        EncodingServer SHA256Server = new EncodingServer(server256Port, "SHA-256");

        serverFrontEnd.start();
        MD5Server.start();
        SHA256Server.start();
    }
}
