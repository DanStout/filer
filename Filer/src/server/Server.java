package server;
import java.net.*;
import java.io.*;
public class Server {
	
	public static void main(String args[]) throws IOException{ 
		final int portnum = 5000;
		Protocol pro = new Protocol();
		try (
			//attempts to create server resources
			ServerSocket SSocket = new ServerSocket(portnum);
			//accepts client request and returns another socket object
			Socket client = SSocket.accept();
			//creates read/write tools
			PrintWriter output = new PrintWriter(client.getOutputStream(), true);
			BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
		
		){
			//pass string will be written back to client
			String pass;
			//loop (infinite)
			while(true){
				//when there is data to be read
				if(input.ready()){
					//protocol object is called to read client message
					pass = pro.process(input.readLine());
					//pass is written to the client
					output.write(pass);
					//break out of loop
					if(pass.equalsIgnoreCase("diconnect"))
						break;
				}
			}
		}
		catch (Exception e){
			System.out.println("error creating network socket, Port number:" + portnum);
			System.exit(1);
		}
		
		
	}
	
}
