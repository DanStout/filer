package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.undo.UndoManager;

/**
 * Create the GUI, and handle events
 * 
 * @author Nick Brooks
 * @author George Faraj
 * @author Andy Kenney
 * @author George Sousa
 * @author Daniel Stout
 * 
 * @version 2014/Apr/1
 * 
 */
public class GUI implements ActionListener, DocumentListener
{
	// declarations
	JFrame frame, aboutFrame, assistFrame, fontSizeFrame;
	JMenuItem newItem, openItem, saveItem, saveAsItem, undoItem, redoItem, aboutItem, sendFileItem, getFileItem, exitItem, assistItem, fontSizeItem;
	JCheckBoxMenuItem autoSaveItem;
	JTextPane textPane;
	JFileChooser fileChooser;
	File currentFile;
	JPanel statusBar;
	String status = "Idle";
	JLabel statusLabel, wordCountLabel, charCountLabel, savedLabel;
	boolean isSaved = true, autoSaveEnabled = true;
	Timer timer;
	Client client;
	UndoManager undoManager;

	int port = 10500;
	// String host = "162.243.79.40";

	String host = "127.0.0.1";

	/**
	 * Initializes the primary JFrame
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
		wordCountLabel = new JLabel();
		charCountLabel = new JLabel();
		savedLabel = new JLabel();

		// populate the status bar
		statusBar.add(Box.createHorizontalStrut(10));
		statusBar.add(statusLabel);
		statusBar.add(Box.createHorizontalStrut(10));
		statusBar.add(new JLabel("|"));
		statusBar.add(Box.createHorizontalStrut(10));
		statusBar.add(wordCountLabel);
		statusBar.add(Box.createHorizontalStrut(10));
		statusBar.add(new JLabel("|"));
		statusBar.add(Box.createHorizontalStrut(10));
		statusBar.add(charCountLabel);
		statusBar.add(Box.createHorizontalStrut(10));
		statusBar.add(new JLabel("|"));
		statusBar.add(Box.createHorizontalStrut(10));
		statusBar.add(savedLabel);

		// adds the status bar to the frame on the south side (bottom)
		frame.add(statusBar, BorderLayout.SOUTH);

		// textPane
		textPane = new JTextPane();
		// add KeyListener to keep track of whether the user is typing and spacebar presses
		textPane.getDocument().addDocumentListener(this);
		// set font
		Font font = new Font("Arial", 10, 16);
		textPane.setFont(font);
		// create scroll pane and put the text pane into it
		JScrollPane scrollPane = new JScrollPane(textPane);
		frame.add(scrollPane);

		undoManager = new UndoManager();
		textPane.getDocument().addUndoableEditListener(undoManager);

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

		autoSaveItem = new JCheckBoxMenuItem("Enable Auto-Save on idle");
		autoSaveItem.setSelected(true);
		autoSaveItem.addActionListener(this);

		exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(this);
		exitItem.setAccelerator(KeyStroke.getKeyStroke('E', KeyEvent.CTRL_DOWN_MASK));

		// add populations to the menu
		fileMenu.add(newItem);
		fileMenu.add(openItem);
		fileMenu.addSeparator();
		fileMenu.add(saveItem);
		fileMenu.add(saveAsItem);
		fileMenu.add(autoSaveItem);
		fileMenu.addSeparator();
		fileMenu.add(exitItem);

		// editMenu
		JMenu editMenu = new JMenu("Edit");

		undoItem = new JMenuItem("Undo");
		undoItem.addActionListener(this);
		undoItem.setAccelerator(KeyStroke.getKeyStroke('Z', KeyEvent.CTRL_DOWN_MASK));

		redoItem = new JMenuItem("Redo");
		redoItem.addActionListener(this);
		redoItem.setAccelerator(KeyStroke.getKeyStroke('Y', KeyEvent.CTRL_DOWN_MASK));

		fontSizeItem = new JMenuItem("Font Size");
		fontSizeItem.addActionListener(this);

		editMenu.add(undoItem);
		editMenu.add(redoItem);
		editMenu.addSeparator();
		editMenu.add(fontSizeItem);

		// networkMenu
		JMenu networkMenu = new JMenu("Network");

		sendFileItem = new JMenuItem("Send File");
		sendFileItem.addActionListener(this);
		sendFileItem.setAccelerator(KeyStroke.getKeyStroke('T', KeyEvent.CTRL_DOWN_MASK));

		getFileItem = new JMenuItem("Get File");
		getFileItem.addActionListener(this);
		getFileItem.setAccelerator(KeyStroke.getKeyStroke('G', KeyEvent.CTRL_DOWN_MASK));

		networkMenu.add(sendFileItem);
		networkMenu.add(getFileItem);

		// helpMenu
		JMenu helpMenu = new JMenu("Help");

		aboutItem = new JMenuItem("About");
		aboutItem.addActionListener(this);
		helpMenu.add(aboutItem);

		assistItem = new JMenuItem("How to use Filer");
		assistItem.addActionListener(this);
		helpMenu.add(assistItem);

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
				if (saveCheck()) exit();
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

		// initialized as blank. If opening files before starting the program is added, this will be necessary.
		updateCountLabels();
		updateSavedLabel();

		ImageIcon logo = new ImageIcon("filer.png");

		frame.setIconImage(logo.getImage());
	}

	/**
	 * Initialize the aboutFrame JFrame
	 */
	public void fontSizeFrame()
	{
		if (fontSizeFrame == null)
		{
			String fontsize = JOptionPane.showInputDialog("Change font size by entering a value below");
			if (isInteger(fontsize))
			{
				int font_size = Integer.parseInt(fontsize);
				if (font_size > 100)
				{
					JOptionPane.showMessageDialog(frame, "Maximum font size is 100", "Size Error", JOptionPane.WARNING_MESSAGE);
				}

				else if (font_size < 8)
				{
					JOptionPane.showMessageDialog(frame, "Minimum font size is 8", "Size Error", JOptionPane.WARNING_MESSAGE);
				}

				else
				{
					Font font = new Font("Arial", 10, font_size);
					textPane.setFont(font);
				}
			}
			else
			{
				JOptionPane.showMessageDialog(frame, "The value for font size entered is invalid!", "Can't Change Size", JOptionPane.ERROR_MESSAGE);
			}

		}

		else
		{
			fontSizeFrame.toFront();
		}

	}

	/**
	 * Check whether the passed string is an integer
	 * 
	 * @param s - String to check
	 * @return true if integer, false otherwise
	 */
	public boolean isInteger(String s)
	{
		try
		{
			Integer.parseInt(s);
			return true;
		}
		catch (NumberFormatException e)
		{
			return false;
		}
	}

	/**
	 * Creates a JFrame containing information regarding the program
	 */
	public void aboutFrame()
	{
		if (aboutFrame == null)
		{

			String text = "<html><b>Filer is an application that was designed, using Java, to help users" + "\n<html><b>create documents, read them and save them locally or across a network." + "\n<html><b> Filer is currently compatible with HTML, JAVA, and TXT documents." + "\n";

			JOptionPane.showMessageDialog(frame, text, "What is Filer?", JOptionPane.PLAIN_MESSAGE);

			JLabel label1 = new JLabel(text);
			aboutFrame = new JFrame("About Filer");
			aboutFrame.setLocationRelativeTo(frame);
			aboutFrame.add(label1);
			aboutFrame.setSize(300, 200);
			aboutFrame.setResizable(false);
			aboutFrame.setVisible(true);

		}
		else aboutFrame.toFront();
	}

	/**
	 * Creates a JFrame containing information regarding the usage of the program
	 */
	public void assistFrame()
	{
		if (assistFrame == null)
		{

			// note about only being able to send saved files
			JLabel label1 = new JLabel("Which is which", JLabel.CENTER);
			assistFrame = new JFrame("About Filer");
			assistFrame.setLocationRelativeTo(frame);
			assistFrame.setSize(300, 200);
			assistFrame.setResizable(false);
			assistFrame.setVisible(true);
			assistFrame.add(label1);

		}
		else assistFrame.toFront();
	}

	/**
	 * Exit the program
	 */
	private void exit()
	{
		System.exit(0);
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

	private void updateSavedLabel()
	{
		savedLabel.setText("Saved: " + isSaved);
	}

	/**
	 * When the TimerTask runs, update the status to be "Idle"
	 */
	public class Task extends TimerTask
	{
		public void run()
		{
			// if the user has stopped typing, has enabled auto-saving and the file has a destination, save.
			if (autoSaveEnabled && currentFile != null && !isSaved) saveFile();
			else updateStatus("Idle");
			updateSavedLabel();
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
			// do nothing, exception just means the timer wasn't already running
		}
		timer = new Timer();
		timer.schedule(new Task(), time);
	}

	/**
	 * Check if the action should continue
	 * 
	 * @return true if the action (closing/creating new document) should continue, false if it should not.
	 */
	public boolean saveCheck()
	{
		boolean isDone = true;
		// isSaved boolean becomes false when the user types
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

	// call appropriate methods based on menu item events
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == sendFileItem)
		{
			sendFile();
		}
		else if (e.getSource() == getFileItem)
		{
			getFile();
		}

		else if (e.getSource() == openItem)
		{
			openFile();
		}
		else if (e.getSource() == saveAsItem)
		{
			saveFileAs();
		}
		else if (e.getSource() == saveItem)
		{
			saveFile();
		}
		else if (e.getSource() == newItem)
		{
			newFile();
		}
		else if (e.getSource() == exitItem)
		{
			if (saveCheck()) exit();
		}
		else if (e.getSource() == aboutItem)
		{
			aboutFrame();
		}
		else if (e.getSource() == assistItem)
		{
			assistFrame();
		}
		else if (e.getSource() == fontSizeItem)
		{
			fontSizeFrame();
		}
		else if (e.getSource() == undoItem)
		{
			if (undoManager.canUndo()) undoManager.undo();
		}
		else if (e.getSource() == redoItem)
		{
			if (undoManager.canRedo()) undoManager.redo();
		}
		else if (e.getSource() == autoSaveItem)
		{
			autoSaveEnabled = !autoSaveEnabled;
		}

	}

	/**
	 * Sends the current file to the server
	 */
	private void sendFile()
	{
		if (!textPane.getText().isEmpty())
		{
			if (initClient() && (saveCheck() && currentFile != null))
			{
				try
				{
					client.sendFile(currentFile);
					updateStatus(currentFile.getName() + " sent to server sucessfully");
				}
				catch (Exception e)
				{
					e.printStackTrace();
					showErrorMessage("Unable to send file");
				}
			}
		}
		else
		{
			showErrorMessage("Why would you want to back up an empty file?");
		}
	}

	/**
	 * Attempts to initiate the client
	 * 
	 * @return true if the client was successfully initiated, false otherwise
	 */
	private boolean initClient()
	{
		try
		{
			client = new Client(host, port);
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			showErrorMessage("Unable to connect to server");
			return false;
		}
	}

	/**
	 * Gets the list of available files from the server, asks the user which one they want and then fills the textPane with that content
	 */
	private void getFile()
	{
		System.out.println("Attempting to get file list");
		if (initClient())
		{

			try
			{
				String[] files = client.getFileList();
				if (files.length != 0)
				{
					String chosenFile = (String) JOptionPane.showInputDialog(frame, "Which file would you like to retrieve?", "Choose a file", JOptionPane.QUESTION_MESSAGE, null, files, files[0]);
					if (chosenFile != null)
					{
						setTextPaneContents(client.getFileContents(chosenFile));
					}
				}
				else
				{
					showErrorMessage("No files stored");
				}
			}
			catch (Exception ex)
			{
				showErrorMessage("Unable to fetch file");
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Sets the textPane contents and updates word count
	 * 
	 * @param s - text to set
	 */
	public void setTextPaneContents(String s)
	{
		textPane.setText(s);
		updateCountLabels();
	}

	/**
	 * Utility method for error JOPtionPanes
	 * 
	 * @param msg - message to display
	 */
	public void showErrorMessage(String msg)
	{
		JOptionPane.showMessageDialog(frame, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Create a new file
	 */
	private void newFile()
	{
		updateStatus("Attempting to create new file");
		startTimer(3000);
		if (saveCheck())
		{
			setTextPaneContents("");
			updateStatus("New File Created");
			frame.setTitle("Filer - Untitled.txt");
		}
	}

	/**
	 * Opens a file after presenting the user with a JFileChooser
	 */
	private void openFile()
	{
		// change status
		updateStatus("Opening file");
		startTimer(3000);
		// opens the file chooser
		int returnVal = fileChooser.showOpenDialog(fileChooser);

		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			// set the currently open file to be the selected file
			currentFile = fileChooser.getSelectedFile();

			// attempt to read file
			try
			{
				readFile(currentFile);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
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
		startTimer(3000);

		// if there is no opened file, call the SaveFileAs method
		if (currentFile == null) return saveFileAs();
		else
		{
			// if it is an existing file then the file is simply written to the drive
			writeFile(currentFile);
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
		startTimer(3000);

		int returnVal = fileChooser.showSaveDialog(fileChooser);
		// opens window
		if (returnVal == JFileChooser.APPROVE_OPTION)
		// user chooses to save item
		{
			currentFile = fileChooser.getSelectedFile();
			writeFile(currentFile);
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
			setTextPaneContents(contents);
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
	 * Update the wordCountLabel and charCountLabels
	 */
	private void updateCountLabels()
	{
		wordCountLabel.setText("Words: " + findWordCount());
		charCountLabel.setText("Characters: " + textPane.getText().length());
	}

	/**
	 * Find the word count of the textPane
	 * 
	 * @return the number of words in the textPane
	 */
	private int findWordCount()
	{
		String content = textPane.getText().trim(); // trim is necessary because leading/trailing whitespace can affect the wordcount

		// if the content is just 0+ whitespace, return 0. Otherwise, split it based on the regex for 1+ whitespace and return the number of items
		return content.matches("\\s*") ? 0 : content.split("\\s+").length;
	}

	public void changedUpdate(DocumentEvent e)
	{
	}

	public void insertUpdate(DocumentEvent e)
	{
		changeActions();
		updateStatus("Typing");
	}

	public void removeUpdate(DocumentEvent e)
	{
		changeActions();
		updateStatus("Deleting");
	}

	private void changeActions()
	{
		startTimer(800);
		isSaved = false;
		updateCountLabels();
		updateSavedLabel();
	}

}
