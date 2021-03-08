package JavaCW;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import java.awt.Color;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

public class secondWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private JFrame frame;
	private JTextField TextArea;
	private JList OnlineClientsList;
	private JTextArea textDisplay;
	private JButton btnLogout;
	private JRadioButton rdbtnPrivate_msg;
	private JRadioButton rdbtnBroadcast_msg;

	DataInputStream in;
	DataOutputStream out;
	DefaultListModel<String> defMod;
	String id, clientIds = "";
	private JScrollPane scrollPane_OnlineClientsList;
	private JScrollPane scrollPane_TextArea;
	private JScrollPane scrollPane;
	private JTextArea textTypingArea;
	private JLabel lblLogo_2;

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					secondWindow window = new secondWindow();
//					window.frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the application.
	 */
	public secondWindow() {
		initialize();
	}
	
	public secondWindow(String id, Socket socket) { // constructor call, it will initialize required variables
		initialize(); // Initialise UI components
		this.id = id;
		try {
			frame.setTitle("Client View - " + id); // set title of UI
			defMod = new DefaultListModel<String>(); // default list used for showing active users on UI
			OnlineClientsList.setModel(defMod);// show that list on UI component JList named OnlineClientsList
			in = new DataInputStream(socket.getInputStream()); // initilize input and output stream
			out = new DataOutputStream(socket.getOutputStream());
			new Read().start(); // create a new thread for reading the messages
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	class Read extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					String mess = in.readUTF();  // read message from server, this will contain :;.,/=<comma seperated clientsIds>
					System.out.println("inside read thread : " + mess); // print message for testing purpose
					if (mess.contains(":;.,/=")) { // prefix(i know its random)
						mess = mess.substring(6); // comma separated all active user ids
						defMod.clear(); // clear the list before inserting fresh elements
						StringTokenizer strTok = new StringTokenizer(mess, ","); // split all the clientIds and add to defMod below
						while (strTok.hasMoreTokens()) {
							String actCln = strTok.nextToken();
							if (!id.equals(actCln)) // we do not need to show own user id in the active user list pane
								defMod.addElement(actCln); // add all the active user ids to the defaultList to display on active
													// user pane on client view
						}
					} else {
						textDisplay.append("" + mess + "\n"); //otherwise print on the clients message board
					}
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
			}
		}
	}


	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setForeground(Color.WHITE);
		frame.getContentPane().setFont(new Font("Verdana Pro", Font.BOLD, 13));
		frame.getContentPane().setBackground(Color.DARK_GRAY);
		frame.setBounds(100, 100, 713, 467);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JScrollPane scrollPane_textDisplay = new JScrollPane();
		scrollPane_textDisplay.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane_textDisplay.setBounds(20, 56, 404, 298);
		frame.getContentPane().add(scrollPane_textDisplay);
		
		textDisplay = new JTextArea();
		textDisplay.setWrapStyleWord(true);
		textDisplay.setLineWrap(true);
		scrollPane_textDisplay.setViewportView(textDisplay);
		textDisplay.setEditable(false);
		textDisplay.setFont(new Font("Verdana Pro", Font.PLAIN, 13));
		
		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(20, 365, 330,30);
		frame.getContentPane().add(scrollPane);
		textTypingArea = new JTextArea();
		textTypingArea.setWrapStyleWord(true);
		scrollPane.setViewportView(textTypingArea);
		textTypingArea.setLineWrap(true);
		textTypingArea.setFont(new Font("Verdana Pro", Font.PLAIN, 13));
		
		JButton btnSendButton = new JButton("SEND");
		btnSendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String textAreaMessage = textTypingArea.getText(); // get the message from textbox
				if (textAreaMessage != null && !textAreaMessage.isEmpty()) {  // only if message is not empty then send it further otherwise do nothing
					try {
						String messageToBeSentToServer = "";
						String cast = "broadcast"; // this will be an identifier to identify type of message
						int flag = 0; // flag used to check whether used has selected any client or not for multicast 
						if (rdbtnPrivate_msg.isSelected()) { // if 1-to-N is selected then do this
							cast = "multicast"; 
							List<String> clientList = OnlineClientsList.getSelectedValuesList(); // get all the users selected on UI
							if (clientList.size() == 0) // if no user is selected then set the flag for further use
								flag = 1;
							for (String selectedUsr : clientList) { // append all the usernames selected in a variable
								if (clientIds.isEmpty())
									clientIds += selectedUsr;
								else
									clientIds += "," + selectedUsr;
							}
							messageToBeSentToServer = cast + ":" + clientIds + ":" + textAreaMessage; // prepare message to be sent to server
						} else {
							messageToBeSentToServer = cast + ":" + textAreaMessage; // in case of broadcast we don't need to know userIds
						}
						if (cast.equalsIgnoreCase("multicast")) { 
							if (flag == 1) { // for multicast check if no user was selected then prompt a message dialog
								JOptionPane.showMessageDialog(frame, "No client selected");
							} else { // otherwise just send the message to the user
								out.writeUTF(messageToBeSentToServer);
								textTypingArea.setText("");
								textDisplay.append("Me - " + clientIds + ": " + textAreaMessage + "\n"); //show the sent message to the sender's message board
							}
						} else { // in case of broadcast
							out.writeUTF(messageToBeSentToServer);
							textTypingArea.setText("");
							textDisplay.append("Me - All: " + textAreaMessage + "\n");
						}
						clientIds = ""; // clear the all the client ids 
					} catch (Exception ex) {
						
					}
				}
			
			}
		});
		btnSendButton.setBackground(Color.LIGHT_GRAY);
		btnSendButton.setFont(new Font("Verdana Pro", Font.BOLD, 11));
		btnSendButton.setBounds(352, 365, 72, 30);
		frame.getContentPane().add(btnSendButton);
		
		JLabel lblOnlineClientsLabel = new JLabel("Online Clients");
		lblOnlineClientsLabel.setForeground(Color.WHITE);
		lblOnlineClientsLabel.setFont(new Font("Verdana Pro", Font.BOLD, 13));
		lblOnlineClientsLabel.setBounds(478, 80, 130, 14);
		frame.getContentPane().add(lblOnlineClientsLabel);
		
		scrollPane_OnlineClientsList = new JScrollPane();
		scrollPane_OnlineClientsList.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane_OnlineClientsList.setBounds(478, 106, 197, 248);
		frame.getContentPane().add(scrollPane_OnlineClientsList);
		
		OnlineClientsList = new JList();
		scrollPane_OnlineClientsList.setViewportView(OnlineClientsList);
		OnlineClientsList.setFont(new Font("Verdana", Font.PLAIN, 13));
		
		btnLogout = new JButton("LOGOUT");
		btnLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					out.writeUTF("exit"); // closes the thread and show the message on server and client's message
												// board
					textDisplay.append("You are disconnected.\n");
					frame.dispose(); // close the frame 
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnLogout.setFont(new Font("Verdana Pro", Font.BOLD, 11));
		btnLogout.setBackground(Color.LIGHT_GRAY);
		btnLogout.setBounds(584, 361, 92, 30);
		frame.getContentPane().add(btnLogout);
		
		rdbtnPrivate_msg = new JRadioButton("Private");
		rdbtnPrivate_msg.setSelected(true);
		rdbtnPrivate_msg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OnlineClientsList.setEnabled(true);
			}
		});
		rdbtnPrivate_msg.setForeground(Color.WHITE);
		rdbtnPrivate_msg.setBackground(Color.DARK_GRAY);
		rdbtnPrivate_msg.setFont(new Font("Verdana Pro", Font.BOLD, 11));
		rdbtnPrivate_msg.setBounds(479, 43, 92, 23);
		frame.getContentPane().add(rdbtnPrivate_msg);
		
		rdbtnBroadcast_msg = new JRadioButton("Broadcast");
		rdbtnBroadcast_msg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OnlineClientsList.setEnabled(false);
			}
		});
		rdbtnBroadcast_msg.setForeground(Color.WHITE);
		rdbtnBroadcast_msg.setFont(new Font("Verdana Pro", Font.BOLD, 11));
		rdbtnBroadcast_msg.setBackground(Color.DARK_GRAY);
		rdbtnBroadcast_msg.setBounds(573, 43, 111, 23);
		frame.getContentPane().add(rdbtnBroadcast_msg);
		
		ButtonGroup btngrp = new ButtonGroup();
		btngrp.add(rdbtnPrivate_msg);
		btngrp.add(rdbtnBroadcast_msg);

		frame.setVisible(true);
		
		JButton btnCleanChat = new JButton("CLEAN");
		btnCleanChat.setFont(new Font("Verdana Pro", Font.BOLD, 11));
		btnCleanChat.setBackground(Color.LIGHT_GRAY);
		btnCleanChat.setBounds(344, 26, 80, 30);
		frame.getContentPane().add(btnCleanChat);
		
		lblLogo_2 = new JLabel("Chat with Us!");
		lblLogo_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblLogo_2.setForeground(Color.WHITE);
		lblLogo_2.setFont(new Font("Verdana Pro", Font.BOLD, 15));
		lblLogo_2.setBounds(15, 15, 130, 26);
		frame.getContentPane().add(lblLogo_2);
		
		
	}
}

