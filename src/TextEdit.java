import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileSystemView;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public final class TextEdit extends JFrame implements ActionListener {
	private static JTextArea area;
	private static JFrame frame;
	private static int returnValue = 0;
	private static Stack charStack = new Stack();
	private static Stack redoStack = new Stack();

	public TextEdit() {
		run();
	}

	public void run() {
		frame = new JFrame("Text Edit");
		
		// Set the look-and-feel (LNF) of the application
		// Try to default to whatever the host system prefers
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException ex) {
			Logger.getLogger(TextEdit.class.getName()).log(Level.SEVERE, null, ex);
		}

		// Set attributes of the app window
		area = new JTextArea();
		area.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if((int)e.getKeyChar()==8) {//Backsapce
					if(!charStack.isEmpty()) {
						charStack.pop();
						redoStack.pop();
						
					}
				}
				else {
					charStack.push(e.getKeyChar());
					redoStack.push(e.getKeyChar());
				}
				System.out.println("charStack: "+charStack.toString()+"\nredoStack: "+redoStack.toString());
				//charStack.push(e.getKeyChar());
				//redoStack.push(e.getKeyChar());
			}
		});
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(area);
		frame.setSize(640, 480);
		

		// Build the menu
		JMenuBar menu_main = new JMenuBar();

		JMenu menu_file = new JMenu("File");

		JMenuItem menuitem_new = new JMenuItem("New");
		JMenuItem menuitem_open = new JMenuItem("Open");
		JMenuItem menuitem_save = new JMenuItem("Save");
		JMenuItem menuitem_quit = new JMenuItem("Quit");

		menuitem_new.addActionListener(this);
		menuitem_open.addActionListener(this);
		menuitem_save.addActionListener(this);
		menuitem_quit.addActionListener(this);

		menu_main.add(menu_file);

		menu_file.add(menuitem_new);
		menu_file.add(menuitem_open);
		menu_file.add(menuitem_save);
		menu_file.add(menuitem_quit);

		frame.setJMenuBar(menu_main);
		
		JMenu editMenu = new JMenu("Edit");
		menu_main.add(editMenu);

		JMenuItem undoMenuItem = new JMenuItem("Undo");
		undoMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(charStack.size()==0) return;
				redoStack.push(charStack.pop());
				String text = "";
				for(int i=0; i<charStack.size(); i++) {
					text += charStack.get(i);
				}
				area.setText(text);
				System.out.println("In undo:= charStack: "+charStack.toString()+"\nIn undo:= redoStack: "+redoStack.toString());
			}
		});
		editMenu.add(undoMenuItem);
		
		JMenuItem redoMenuItem = new JMenuItem("Redo");
		redoMenuItem.setMnemonic('y');
		redoMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(redoStack.size()==area.getText().length()) return;
				charStack.push(redoStack.pop());
				String text = "";
				for(int i=0; i<redoStack.size(); i++) {
					text += redoStack.get(i);
				}
				area.setText(text);
				System.out.println("In redo:= charStack: "+charStack.toString()+"\nIn redo:= redoStack: "+redoStack.toString());
			}
		});
		editMenu.add(redoMenuItem);
		
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String ingest = null;
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		jfc.setDialogTitle("Choose destination.");
		jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		String ae = e.getActionCommand();
		if (ae.equals("Open")) {
			returnValue = jfc.showOpenDialog(null);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File f = new File(jfc.getSelectedFile().getAbsolutePath());
				try {
					FileReader read = new FileReader(f);
					Scanner scan = new Scanner(read);
					while (scan.hasNextLine()) {
						String line = scan.nextLine() + "\n";
						ingest = ingest + line;
					}
					area.setText(ingest);
				} catch (FileNotFoundException ex) {
					ex.printStackTrace();
				}
			}
			// SAVE
		} else if (ae.equals("Save")) {
			returnValue = jfc.showSaveDialog(null);
			try {
				File f = new File(jfc.getSelectedFile().getAbsolutePath());
				FileWriter out = new FileWriter(f);
				out.write(area.getText());
				out.close();
			} catch (FileNotFoundException ex) {
				Component f = null;
				JOptionPane.showMessageDialog(f, "File not found.");
			} catch (IOException ex) {
				Component f = null;
				JOptionPane.showMessageDialog(f, "Error.");
			}
		} else if (ae.equals("New")) {
			area.setText("");
		} else if (ae.equals("Quit")) {
			System.exit(0);
		}
	}
	
	public static void main(String[] args) {
		  TextEdit runner = new TextEdit();
	}
}