package client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

public class Client
{
	Socket socket;
	InputStream is;
	BufferedOutputStream bos;
	DataOutputStream dos;
	FileInputStream fis;
	BufferedInputStream bis;
	ObjectInputStream ois;

	public Client(String hostname, int portnum)
	{
		try
		{
			socket = new Socket(hostname, portnum);
			bos = new BufferedOutputStream(socket.getOutputStream());
			dos = new DataOutputStream(bos);
		}
		catch (Exception e)
		{
			System.out.println("Unable to make connection");
			e.printStackTrace();
		}
	}

	public void sendFile(File file)
	{
		try
		{
			dos.writeByte(0);
			dos.flush();

			long fileSize = file.length();

			byte[] buffer = new byte[(int) fileSize];

			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);

			int count;

			dos.writeUTF(file.getName());
			dos.flush();

			while ((count = bis.read(buffer)) > 0)
			{
				dos.write(buffer, 0, count);
			}

			System.out.print("File sent: ");
			for (int i = 0; i < buffer.length; i++)
			{
				System.out.print((char) buffer[i]);
			}

			bos.close();
			bis.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public String[] getFileList()
	{
		File[] files = null;

		try
		{
			dos.writeByte(1);
			dos.flush();

			ois = new ObjectInputStream(socket.getInputStream());

			files = (File[]) ois.readObject();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		String[] filenames = new String[files.length];

		for (int i = 0; i < files.length; i++)
		{
			filenames[i] = files[i].getName();
		}

		System.out.println("File list received");
		return filenames;
	}

	public String getFileContents(String filename)
	{
		try
		{
			dos.writeUTF(filename);
			dos.flush();
			System.out.println("getting: " + filename);

			String contents = "";

			System.out.println(ois.read());

			return contents;

		}
		catch (Exception ex)
		{

		}
		return "";
	}
}
