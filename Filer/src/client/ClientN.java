package client;

/**
 *
 * @author Andrew
 */
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Socket;

public class ClientN {
	public static void main(String[] argv) throws Exception {
		Socket sock = new Socket("localhost", 5001);
		byte[] mybytearray = new byte[1024];
		InputStream is = sock.getInputStream();
		FileOutputStream fos = new FileOutputStream("a.txt");
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		int bytesRead = is.read(mybytearray, 0, mybytearray.length);
		bos.write(mybytearray, 0, bytesRead);
		bos.close();
		sock.close();
	}
}
