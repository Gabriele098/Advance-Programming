package prova2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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
	private String ip;

	/**
	 * Launch the application
	 * for testing purposes 
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

//	public secondWindow() {
//		initialize();
//	}
	
	public secondWindow(String id, Socket socket, String ip, int port) { // constructor call, it will initialize required variables
		/**
		 * Main class for the second window that consists on the chat section
		 * By calling the constructor, it will initialize the required variables
		 * @param id 
		 * @param socket 
		 * @param ip
		 * @param port
		 */
		initialize(); 																				// initialise UI components
		this.id = id;																				// identify the parameter variable id
		this.ip = ip;																				// identify the parameter variable ip
		// try and catch to set up input and output to read and send the messages
		try {
			frame.setTitle("Client View - " + id); 													// setting the title of User Interface
			defMod = new DefaultListModel<String>(); 												// on the User interface, the default list is used to display active users.
			OnlineClientsList.setModel(defMod);														// show that list on UI component JList named OnlineClientsList
			in = new DataInputStream(socket.getInputStream()); 										// initilize the input stream
			out = new DataOutputStream(socket.getOutputStream());									// initilize the output stream
			new Read().start(); 																	// to read the messages, start a new thread
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	class Read extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					String mess = in.readUTF();  													// read message from server
					System.out.println("inside read thread: " + mess); 								// print message from the server for testing purpose
					// if condition that checks the message from the server and does something
					// for each specific prefix
					if (mess.contains(":;.,/=")) { 													// if the mess contain the prefix
						mess = mess.substring(6); 													// comma that separate all active user id
						defMod.clear(); 															// clear the list before inserting new elements
						StringTokenizer strTok = new StringTokenizer(mess, ",");					// split all the clientIds and add to defMod below
						textDisplay.append("\n" + "Active Client: ");								// display text
						// while loop that separate each client id and add element to the defaultList to be displayed
						while (strTok.hasMoreTokens()) {											// while loop that iterates over all tokens
							String actCln = strTok.nextToken();										// define actCln as container for each token
							textDisplay.append(actCln + ", ");										// display the online client id
							if (!id.equals(actCln)) 												// in the active user list window, we don't need to show our own user id
								defMod.addElement(actCln); 											// add all the active user id to the defaultList to display on active user pane on client view
													
						}
							textDisplay.append("" + "\n");
					
					// same procedure about taking the IP of the clients 
					} else if (mess.contains("----->")) {
							mess = mess.substring(6);												
							StringTokenizer strTok2 = new StringTokenizer(mess, ",");
							textDisplay.append("\n" + "Online IP addresses: ");
							while (strTok2.hasMoreTokens()) {
								String actClnip = strTok2.nextToken();
								textDisplay.append(actClnip + ", ");
						}
							textDisplay.append("" + "\n");
						
					} else {
						textDisplay.append("" + mess + "\n"); 										// Else, print a message on the client's message board
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
		frame.getContentPane().setLayout(null);
		
		// action when the client close the window
		frame.addWindowListener(new WindowAdapter() {
			  public void windowClosing(WindowEvent we) {
					try {
						out.writeUTF("exit"); 														// closes the thread and show the message on server and client's message field
						textDisplay.append("You are disconnected.\n");
						frame.dispose(); 															// close the frame 
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				  }
			});
		
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
		// action for the button SEND. It will send and receive messages from the server
		btnSendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String textAreaMessage = textTypingArea.getText(); 									// get the message from text field
				DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") 	// take the actual time
			            .withZone(ZoneId.systemDefault());
				String time = DATE_TIME.format(new Date().toInstant());								// give the actual time to the variable "time"
				if (textAreaMessage != null && !textAreaMessage.isEmpty()) {  						// if message text field is not empty then send it further
					// try and catch that check if the client want to send a private message to someone or a message to everyone
					try {
						String messageToBeSentToServer = "";										// create a empty variable to contain the message to send to the server
						String cast = "broadcast"; 													// this will serve as an identifier for the message type
						int flag = 0; 																// this flag is used to determine whether or not a client has been chosen for multicast 
						if (rdbtnPrivate_msg.isSelected()) { 										// if private is selected then does this
							cast = "multicast"; 													// set identifier as multicast
							List<String> clientList = OnlineClientsList.getSelectedValuesList(); 	// gets all the users selected on the User Interface
							if (clientList.size() == 0) 											// Set the flag for later use if no user is selected.
								flag = 1;
							// for loop to assign the clients id to a variable 
							for (String selectedUsr : clientList) {
								if (clientIds.isEmpty())
									clientIds += selectedUsr;
								else
									clientIds += "," + selectedUsr;
							}
							messageToBeSentToServer = cast + ":" + clientIds + ":" + textAreaMessage; 						// prepares a message to be sent to the server
						} else {
							messageToBeSentToServer = cast + ":" + textAreaMessage; 										// in case of broadcast we don't need to know the clients id
						}
						// if condition that sends the messages to the server (multicast or broadcast)
						if (cast.equalsIgnoreCase("multicast")) { 
							if (flag == 1) { 																				// If no user has been chosen for multicast, a message window will be shown
								JOptionPane.showMessageDialog(frame, "No client selected!");
							} else { 																						// otherwise, send the message to the specific user
								out.writeUTF(messageToBeSentToServer);														// send the message through the output writer
								textTypingArea.setText("");
								textDisplay.append(time + " -> " +"Me - " + clientIds + ": " + textAreaMessage + "\n"); 	// display the sent message on the message board of the sender
							}
						} else { 																							// otherwise, in case of broadcast
							out.writeUTF(messageToBeSentToServer);															// message to send
							textTypingArea.setText("");
							textDisplay.append(time + " -> " + "Me - All: " + textAreaMessage + "\n");						// display the sent message on the message board of the sender
						}
						clientIds = ""; 																					// clear the all the client id 
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			
			}
		});
		btnSendButton.setBackground(Color.LIGHT_GRAY);
		btnSendButton.setFont(new Font("Verdana Pro", Font.BOLD, 11));
		btnSendButton.setBounds(352, 365, 72, 30);
		frame.getContentPane().add(btnSendButton);
		
		// label
		JLabel lblOnlineClientsLabel = new JLabel("Online Clients");
		lblOnlineClientsLabel.setForeground(Color.WHITE);
		lblOnlineClientsLabel.setFont(new Font("Verdana Pro", Font.BOLD, 13));
		lblOnlineClientsLabel.setBounds(478, 80, 130, 14);
		frame.getContentPane().add(lblOnlineClientsLabel);
		
		// scroll panel
		scrollPane_OnlineClientsList = new JScrollPane();
		scrollPane_OnlineClientsList.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane_OnlineClientsList.setBounds(478, 106, 197, 248);
		frame.getContentPane().add(scrollPane_OnlineClientsList);
		
		// list of the online client
		OnlineClientsList = new JList();
		scrollPane_OnlineClientsList.setViewportView(OnlineClientsList);
		OnlineClientsList.setFont(new Font("Verdana", Font.PLAIN, 13));
		
		// logout button
		btnLogout = new JButton("LOGOUT");
		// action when the client close the window from the button "LOGOUT"
		btnLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					out.writeUTF("exit"); 																// closes the thread and show the message on server and client's message field
					textDisplay.append("You are disconnected.\n");
					frame.dispose(); 																	// close the frame 
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnLogout.setFont(new Font("Verdana Pro", Font.BOLD, 11));
		btnLogout.setBackground(Color.LIGHT_GRAY);
		btnLogout.setBounds(584, 361, 92, 30);
		frame.getContentPane().add(btnLogout);
		
		// radio button for Private message
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
		
		// radio button for broadcast message
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
		
		// group the radio button so it can be selected just one of them per time
		ButtonGroup btngrp = new ButtonGroup();
		btngrp.add(rdbtnPrivate_msg);
		btngrp.add(rdbtnBroadcast_msg);

		frame.setVisible(true);
		
		// button to clean the chat display
		JButton btnCleanChat = new JButton("CLEAN");
		btnCleanChat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnCleanChat.setText("");
			}
		});
		btnCleanChat.setFont(new Font("Verdana Pro", Font.BOLD, 11));
		btnCleanChat.setBackground(Color.LIGHT_GRAY);
		btnCleanChat.setBounds(344, 26, 80, 30);
		frame.getContentPane().add(btnCleanChat);
		
		// label of the logo
		lblLogo_2 = new JLabel("Chat with Us!");
		lblLogo_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblLogo_2.setForeground(Color.WHITE);
		lblLogo_2.setFont(new Font("Verdana Pro", Font.BOLD, 15));
		lblLogo_2.setBounds(15, 15, 130, 26);
		frame.getContentPane().add(lblLogo_2);
		
		
		
		
	}
}

