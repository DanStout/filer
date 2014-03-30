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
	public void connect (int portnum, String host, File n) throws InterruptedException, IOException {
		String name = n.getName();
		//try block
		try{
			//create the client socket
			client = new Socket(host, portnum);
			//create stream writer to outputs data to the server
			PrintWriter out = new PrintWriter(client.getOutputStream());
			//create steam reader to take information in from the server
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			
			//connection check
			out.write("connection check");
			in.ready();
			if (!in.readLine().equalsIgnoreCase("confirmed"))
				return;
			
			
			out.write("write to server");
			in.ready();
			if(in.readLine().equalsIgnoreCase("ready for write to server")){
				
				//write the name of the file to the server
				out.write(name);
				
				try{
					//will execute the code to send file to client
					byte[] mybytearray = new byte[(int) n.length()]; //NEED TO MAKE ARRAY LIST
					//create byte array of proper size
					BufferedInputStream bis = new BufferedInputStream(new FileInputStream(n));
					//populates the array using this input stream
					bis.read(mybytearray, 0, mybytearray.length);
					//	populate the array of bytes
					OutputStream os = client.getOutputStream();
					//send the array of bytes over the socket
					os.write(mybytearray, 0, mybytearray.length);
					//close the buffered input stream
					bis.close();
			
					out.write("disconnect");
				}
				catch(Exception e){
					System.out.println("there was an error sending the file");
				}
			}
			
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
   
	
	
	
	
      //this version of the connect method does not require a file to be input in its call, it will be used for pulling the file
   	public void connect (int portnum, String host) throws InterruptedException, IOException {

		//try block
		try{
			//create the client socket
			client = new Socket(host, portnum);
			//create stream writer to outputs data to the server
			PrintWriter out = new PrintWriter(client.getOutputStream());
			//create an input stream
			InputStream in = client.getInputStream(); 
			//create steam reader to take information in from the server
			BufferedReader intext = new BufferedReader(new InputStreamReader(client.getInputStream()));
			
					
			//connection check
			out.write("connection check");
			intext.ready();
			if (!intext.readLine().equalsIgnoreCase("confirmed"))
				return;
			
					//sends request to pull file
					out.println("pull file");
					//waits for reply
					intext.ready();
					//response
					if(intext.readLine().equalsIgnoreCase("ready for write to client")){
						int i = 0;
						//waits for the server to send the list of files
						intext.ready();
						//prompts user for file choice
						try{
							//set i equal to file choice
							//i will return return -1 if error occurred
							i = GUI.FileNames(intext.readLine());
							//error check
							if(i == -1)
								return;
						}
						catch (Exception e){
							System.out.println("there was an error getting the file list");
						}
						//sends the file choice to the server
					out.write(i);
					

					byte[] mybytearray = new byte[4096];
					//creates a byte array 4 kb is size
   		         	FileOutputStream fos = new FileOutputStream("Newly Pulled File.txt");
   		         	//creates file output stream to create a file
   		         	BufferedOutputStream bos = new BufferedOutputStream(fos);
   		         	//outputs stream to the file
   		         	int bytesRead = in.read(mybytearray, 0, mybytearray.length);
   		         	//reads in the array of bytes to make the file
   		         	bos.write(mybytearray, 0, bytesRead);
   		         	//writes the array to the file
   		         	bos.close();
   		         	//close stream	
   		         	out.write("disconnect");
				}
				else {
   		      		System.out.println("there was an error in the server");
				}
			
         
			
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
	
	public  String ListServer(int portnum, String host) throws InterruptedException{
		String ret = null;
		
		
		//try block
		try{
			//create the client socket
			client = new Socket(host, portnum);
			//create stream writer to outputs data to the server
			PrintWriter out = new PrintWriter(client.getOutputStream());
			//create steam reader to take information in from the server
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
					
			//connection check
			out.write("connection check");
			in.ready();
			if (!in.readLine().equalsIgnoreCase("confirmed"))
				return "connection error";
			
			try{
				out.write("request list");
				in.ready();
				ret = in.readLine();
			}
			catch(Exception e){
				System.out.println("there was an error getting the file list from the server");
			}
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
		return ret;
	}

}
