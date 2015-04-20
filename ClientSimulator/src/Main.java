import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class Main {

	public static void main(String[] args) throws UnknownHostException, IOException {
		//telnet: redir add tcp:20200:20201
		InetAddress serverAddr = InetAddress.getByName("localhost");
		Socket socket = new Socket(serverAddr, 20200);
		PrintWriter out =
		        new PrintWriter(socket.getOutputStream(), true);
		
	}

}
