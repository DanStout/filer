package client;
import java.io.*;
import java.net.*;
public class ClientN {
	//this is the client network class, access this to get a connection to the server
	private Socket client;
	//this reader will be used to pass things from the GUI into the networking class
	//? how do we write into this class? I want to use (reader.ready();)
	//private BufferedReader reader = new BufferedReader();
	
	
	public ClientN(){	
	}
	
	//custom connect method so a user can pass in a port number and a host address
	public void connect (int portnum, String host, String n) throws InterruptedException, IOException {
		int tries = 0;
		//try block
		try{
			//create the client socket
			client = new Socket(host, portnum);
			//create stream writer to outputs data to the server
			PrintWriter out = new PrintWriter(client.getOutputStream());
			//create steam reader to take information in from the server
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			
			out.println(n);
			while (!in.readLine().equalsIgnoreCase("disconnect") && tries < 6){	
				out.flush();
				tries++;
			}
			if(tries == 6)
				System.out.println("data was unable to send after 5 attempts");
			
			
		}
		catch(IOException e){
			//error output
			System.out.print("there was an issue connecting to server at " + host + " on port number " + portnum + " please check the information and try again");
		}
		//finally to close client resources
		finally{
			//try block to close
			try{
				if(client != null)
					client.close();
			}
			//catch and error report, thread sleep used to delay exit
			catch (IOException e){
				System.out.println("there was an error closing recources, hard system exit in 30 seconds");
				Thread.sleep(30000);
				System.exit(1);
			}
			//finally for try block in the outer finally
			finally{
			}
		}
		
		
	}
	
	//this method will control the passing of data into the network class
	//the class will check to see that the string is valid before it passes in
	public String Ready(String s){
		return s;
	}
	

}
