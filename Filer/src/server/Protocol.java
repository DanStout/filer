package server;

import java.util.ArrayList;

// the protocol class will be in charge of managing information retrieved from the client
public class Protocol
{
	private ArrayList<String> projects = new ArrayList<String>();

	// constructor
	public Protocol()
	{
	}

	// getters and setters
	public ArrayList<String> getProjects()
	{
		return projects;
	}

	public void setProjects(ArrayList<String> projects)
	{
		this.projects = projects;
	}

	// method to process info from the client
	public String process(String input)
	{
		// initial connection handshake
		if (input.equalsIgnoreCase("connection check")) return "confirmed";

		// disconnect code from client
		else if (input.equalsIgnoreCase("disconnect")) return "disconnect";

		// not actual code, ignore this
		return "make the ide happy";
	}

}
