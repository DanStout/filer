package server;


/**
*
* @author Andrew
*/
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
   public static void main(String[] args) throws IOException {
       ServerSocket servsock = new ServerSocket(5001);
       
           File myFile = new File("a.txt");
           while (true) {
               
               Socket sock = servsock.accept();
               System.out.println("User connected");
               byte[] mybytearray = new byte[(int) myFile.length()];
               BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
               bis.read(mybytearray, 0, mybytearray.length);
               OutputStream os = sock.getOutputStream();
               os.write(mybytearray, 0, mybytearray.length);
               os.flush();
               bis.close();
               sock.close();
               servsock.close();
          
       }
   }
}

