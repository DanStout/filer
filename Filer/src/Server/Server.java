package Server;
import java.net.*;
import java.io.*;
public class Server {
	int portnum = 5000;
	public Server(){ 
		try {
			ServerSocket SSocket = new ServerSocket(portnum);
			Socket client = SSocket.accept();
			PrintWriter output = new PrintWriter(client.getOutputStream(), true);
			BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
		
		}
		catch (Exception e){
			System.out.println("error creating network socket");
		}
	}
}
