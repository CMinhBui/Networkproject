import java.net.InetAddress;

/**
 * Created by root on 5/17/17.
 */
public class runClient {
    public static void main(String[] args) throws Exception{
        InetAddress serverAddress = util.toAddress(args[0]);
        int serverPort = Integer.parseInt(args[1]);
        String input = args[2];
        if(input.length() > 80) System.out.println("Input is too large..");
        else {
			Client client = new Client(80, serverAddress, serverPort, input);
        	client.start();
        }
    }
}
