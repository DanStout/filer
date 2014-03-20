package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

//import javax.swing.undo;
/**
 * Create the GUI, and handle events
 * 
 * @author Nick Brooks
 * @author George Faraj
 * @author Andy Kenney
 * @author George Sousa
 * @author Daniel Stout
 * 
 * @version Mar 17, 2014
 * 
 */
public class GUI implements ActionListener, KeyListener
{
	// declarations
	JFrame frame, aboutFrame;
	JMenuItem newItem, openItem, saveItem, saveAsItem, undoItem, redoItem, aboutItem, syncItem, exitItem;
	JTextPane textPane;
	JFileChooser fileChooser;
	File openedFile;
	JPanel statusBar;
	String status = "Idle";
	JLabel statusLabel, wordCountLabel;
	boolean isSaved = true;
	Timer timer;

	/**
	 * Initialize the aboutFrame JFrame
	 */
	public void aboutFrame()
	{
		if (aboutFrame == null)
		{
			aboutFrame = new JFrame("About Filer");
			aboutFrame.setLocationRelativeTo(frame);
			aboutFrame.setSize(300, 200);
			aboutFrame.setResizable(false);
			aboutFrame.setVisible(true);
		}
		else aboutFrame.toFront();
	}

	/**
	 * Initialize the frame JFrame
	 */
	public GUI()
	{
		// creates a new JFrame
		frame = new JFrame();

		try
		{
			// makes the window look like the native systems windows
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			// outputs errors if any occur
			e.printStackTrace();
		}

		// statusBar
		statusBar = new JPanel();
		// line axis is a horizontal layout
		statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.LINE_AXIS));
		// as wide as the frame, and 20 pixels tall
		statusBar.setPreferredSize(new Dimension(frame.getWidth(), 20));
		statusLabel = new JLabel("Status: Idle");
		wordCountLabel = new JLabel("Words: 0");

		// populate the status bar
		statusBar.add(Box.createHorizontalStrut(10));
		statusBar.add(statusLabel);
		statusBar.add(Box.createHorizontalStrut(10));
		statusBar.add(new JLabel("|"));
		statusBar.add(Box.createHorizontalStrut(10));
		statusBar.add(wordCountLabel);

		// adds the status bar to the frame on the south side (bottom)
		frame.add(statusBar, BorderLayout.SOUTH);

		// textPane
		textPane = new JTextPane();
		// add KeyListener to keep track of whether the user is typing and spacebar presses
		textPane.addKeyListener(this);
		// set font
		Font font = new Font("Arial", 10, 16);
		textPane.setFont(font);
		// create scroll pane and put the text pane into it
		JScrollPane scrollPane = new JScrollPane(textPane);
		frame.add(scrollPane);

		// create menu bar
		JMenuBar menuBar = new JMenuBar();

		// fileMenu
		JMenu fileMenu = new JMenu("File");

		newItem = new JMenuItem("New");
		newItem.addActionListener(this);
		newItem.setAccelerator(KeyStroke.getKeyStroke('N', KeyEvent.CTRL_DOWN_MASK));

		openItem = new JMenuItem("Open");
		openItem.addActionListener(this);
		openItem.setAccelerator(KeyStroke.getKeyStroke('O', KeyEvent.CTRL_DOWN_MASK));

		saveItem = new JMenuItem("Save");
		saveItem.addActionListener(this);
		saveItem.setAccelerator(KeyStroke.getKeyStroke('S', KeyEvent.CTRL_DOWN_MASK));

		saveAsItem = new JMenuItem("Save As");
		saveAsItem.addActionListener(this);
		saveAsItem.setAccelerator(KeyStroke.getKeyStroke('S', KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK));

		exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(this);
		exitItem.setAccelerator(KeyStroke.getKeyStroke('E', KeyEvent.CTRL_DOWN_MASK));

		// add populations to the menu
		fileMenu.add(newItem);
		fileMenu.add(openItem);
		fileMenu.addSeparator();
		fileMenu.add(saveItem);
		fileMenu.add(saveAsItem);
		fileMenu.addSeparator();
		fileMenu.add(exitItem);

		// editMenu
		JMenu editMenu = new JMenu("Edit");

		// populate menu button "edit"
		undoItem = new JMenuItem("Undo");
		undoItem.setAccelerator(KeyStroke.getKeyStroke('Z', KeyEvent.CTRL_DOWN_MASK));

		redoItem = new JMenuItem("Redo");
		redoItem.setAccelerator(KeyStroke.getKeyStroke('Y', KeyEvent.CTRL_DOWN_MASK));

		editMenu.add(undoItem);
		editMenu.add(redoItem);

		// networkMenu
		JMenu networkMenu = new JMenu("Network");
		syncItem = new JMenuItem("Sync");
		networkMenu.add(syncItem);

		// helpMenu
		JMenu helpMenu = new JMenu("Help");
		aboutItem = new JMenuItem("About");
		aboutItem.addActionListener(this);
		helpMenu.add(aboutItem);

		// add menus to menuBar
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(networkMenu);
		menuBar.add(helpMenu);

		// sets up the system respond to the exit button being pressed
		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent we)
			{
				// if the saveCheck returns true, exit the program
				if (saveCheck()) System.exit(0);
			}
		});

		// initialize JFileChooser and add file filters for .txt and .java files to it
		fileChooser = new JFileChooser();
		FileNameExtensionFilter txtFilter = new FileNameExtensionFilter("TXT files", "txt");
		FileNameExtensionFilter javaFilter = new FileNameExtensionFilter("JAVA files", "java");
		fileChooser.addChoosableFileFilter(txtFilter);
		fileChooser.addChoosableFileFilter(javaFilter);

		// sets the frame title, size, location, menubar, and declares that the "x" button will do nothing initially (as there needs to be a check on close as to whether the document has been saved)
		frame.setTitle("Filer - Untitled.txt");
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		frame.setJMenuBar(menuBar);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setVisible(true);

		// timer for use in determining when the program is idle
		timer = new Timer();

	}

	/**
	 * Update the statusLabel in the statusBar
	 * 
	 * @param newStatus - the status to set
	 */
	private void updateStatus(String newStatus)
	{
		statusLabel.setText("Status: " + newStatus);
	}

	/**
	 * When the TimerTask runs, update the status to be "Idle"
	 */
	public class Task extends TimerTask
	{
		public void run()
		{
			updateStatus("Idle");
		}
	}

	/**
	 * Cancel the timer, then create a new timer and schedule it to run after 1 second
	 */
	private void startTimer(int time)
	{
		try
		{
			timer.cancel();
		}
		catch (Exception ex)
		{

		}
		timer = new Timer();
		timer.schedule(new Task(), time);
	}

	/**
	 * Check if the action should continue
	 * 
	 * @return true if the action (closing/creating new document) should continue, false if not
	 */
	public boolean saveCheck()
	{
		boolean isDone = true;
		// isSaved boolean is updated through out the program
		if (!isSaved)
		{
			// dialog box appears to prompt user
			int choiceVal = JOptionPane.showConfirmDialog(frame, "Would you like to save?");
			if (choiceVal == JOptionPane.YES_OPTION)
			{
				// user chooses to save
				if (!saveFile()) isDone = false;
			}
			// user chooses cancel
			else if (choiceVal == JOptionPane.CANCEL_OPTION) isDone = false;
		}
		// if the user chooses no the method returns true
		return isDone;
	}

	// responds to button presses by the user
	public void actionPerformed(ActionEvent e)
	{
		//sync button
		if(e.getSource() == syncItem){
			//attempt to send data
			try{
				//create client
				ClientN client = new ClientN();
				//use connect method
				client.connect(5000, "LocalHost", textPane.getText());
			}
			catch(IOException e1){
				System.out.println("error connecting and sending file to server");
			}
			catch (InterruptedException e2) {
				System.out.println("error connecting and sending file to server");
			}
		}
		// open file button
		if (e.getSource() == openItem)
		{
			// change status
			updateStatus("Opening file");
			// opens the file chooser
			int returnVal = fileChooser.showOpenDialog(fileChooser);

			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				// set the currently open file to be the selected file
				openedFile = fileChooser.getSelectedFile();

				// attempt to read file
				try
				{
					readFile(openedFile);
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
			startTimer(3000);
		}
		// remaining buttons call connected methods
		else if (e.getSource() == saveAsItem)
		{
			saveFileAs();
			startTimer(3000);
		}
		else if (e.getSource() == saveItem)
		{
			saveFile();
			startTimer(3000);
		}
		else if (e.getSource() == newItem)
		{
			newFile();
			startTimer(3000);
		}
		else if (e.getSource() == exitItem)
		{
			if (saveCheck()) System.exit(0);
		}
		else if (e.getSource() == aboutItem)
		{
			aboutFrame();
		}

	}

	/**
	 * Create a new file
	 */
	private void newFile()
	{
		updateStatus("Attempting to create new file");
		if (saveCheck())
		{
			textPane.setText("");
			updateStatus("New File Created");
			frame.setTitle("Filer - Untitled.txt");
		}
	}

	/**
	 * Attempts to save file, will call the saveFileAs() method if there is no openedFile
	 * 
	 * @return whether the file has been successfully saved
	 */
	private boolean saveFile()
	{
		updateStatus("Saving File");

		// if there is no opened file, call the SaveFileAs method
		if (openedFile == null) return saveFileAs();
		else
		{
			// if it is an existing file then the file is simply written to the drive
			writeFile(openedFile);
			return true;
		}
	}

	/**
	 * Opens a JFileChooser to pick a destination for the file
	 * 
	 * @return true if the file has been saved or if the user doesn't want to save. Returns false if the user cancels at any point
	 */
	private boolean saveFileAs()
	{
		updateStatus("Saving File");

		int returnVal = fileChooser.showSaveDialog(fileChooser);
		// opens window
		if (returnVal == JFileChooser.APPROVE_OPTION)
		// user chooses to save item
		{
			writeFile(fileChooser.getSelectedFile());
			return true;
		}
		else if (returnVal == JFileChooser.CANCEL_OPTION) return false; // cancel option
		return true;

	}

	// read text from file at print in the textPane
	/**
	 * Update the textPane with data read from a file on the disk
	 * 
	 * @param file - the file to read from
	 * @throws Exception due to issues with IO
	 */
	private void readFile(File file) throws Exception
	{
		// creates String to store textPane data
		String contents = "";
		// creates buffered reader
		BufferedReader inputStream = null;

		// attempts to read line into the string
		try
		{
			inputStream = new BufferedReader(new FileReader(file));

			String l;
			while ((l = inputStream.readLine()) != null)
			{
				contents += (l + "\r\n"); // \n required for newlines, \r (carriage return) required for notepad
			}
			textPane.setText(contents);
			updateStatus(file.getName() + " opened sucessfully");
			frame.setTitle("Filer - " + file.getName());
		}
		// prints error
		catch (Exception e)
		{
			e.printStackTrace();
		}
		// close stream
		finally
		{
			if (inputStream != null) inputStream.close();
		}
	}

	/**
	 * Write the contents of the textPane to the disk
	 * 
	 * @param file - the file to write to
	 */
	private void writeFile(File file)
	{
		try
		{
			// get the extension of the chosen file filter
			String descrip = fileChooser.getFileFilter().getDescription().substring(0, 3);

			// if the user has selected a type for the file, ignore any extensions they may have typed
			if (!descrip.equals("All"))
			{
				String extension = "";
				if (descrip.equals("TXT")) extension = ".txt";
				else if (descrip.equals("JAV")) extension = ".java";

				// get the path that precedes the extension
				Pattern p = Pattern.compile("[^.]*");
				Matcher m = p.matcher(file.getPath());
				m.find();

				// new file name with selected extension applied
				file = new File(m.group(0) + extension);
			}

			// outputs textPane contents to the file
			PrintWriter outputStream = new PrintWriter(new FileWriter(file));
			outputStream.print(textPane.getText());
			outputStream.close();
			updateStatus(file.getName() + " Saved sucessfully");
			frame.setTitle("Filer - " + file.getName());
			isSaved = true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * Update the wordCountLabel
	 */
	private void updateWordLabel()
	{
		wordCountLabel.setText("Words: " + findWordCount());
	}

	/**
	 * Find the word count of the textPane
	 * 
	 * @return the number of words in the textPane
	 */
	private int findWordCount()
	{
		return textPane.getText().split("\\s+").length;
	}

	// detects if the user is typing
	public void keyPressed(KeyEvent e)
	{
		updateStatus("Typing");
		startTimer(800);
		// update the wordCountLabel when space is pressed (new word)
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			updateWordLabel();
		}
	}

	// when key released, start idle timer
	public void keyReleased(KeyEvent e)
	{
		isSaved = false;
	}

	// method required by KeyListener interface
	public void keyTyped(KeyEvent e)
	{

	}
}
