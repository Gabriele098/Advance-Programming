package prova2;

import java.awt.EventQueue;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Font;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JTextArea;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;

public class serverWindow {

	private JFrame frmServer;
	private ServerSocket serverSocket; 																		//server socket
	private static int port = 4999;  																		// port number to be used
	private static String ip_server = "169.255.1.1";														// server IP address
	private static String coordinator = "";																	// variable for the coordinator
	private static String coordinatorip = "";
	private JTextArea txtrDisplayServer; 																	// variable for server message board on UI
	private JList AllClientsServerList;  																	// list to show on User Interface
	private JList OnlineClientsServerList; 																	// list to show on User Interface
	private static final long serialVersionUID = 1L;
	private static Map<String, Socket> allUsersList = new ConcurrentHashMap<>(); 							// keeps the mapping of all the clients used and their socket connections
	private static Map<String, Socket> allUsersListIP = new ConcurrentHashMap<>();							// this for the IP
	private static Set<String> activeUserSet = new HashSet<>(); 											// set that keeps track of all the active clients 
	private static Set<String> activeUserSetIP = new HashSet<>();											// this for the IP
	private DefaultListModel<String> activeCList = new DefaultListModel<String>(); 							// keeps list of active client to display on User Interface
	private DefaultListModel<String> allCList = new DefaultListModel<String>(); 							// keeps list of all client to display on User Interface
	private DefaultListModel<String> activeCListIP = new DefaultListModel<String>();						// this for the IP
	private JScrollPane scrollPane_DisplayServer;
	private JScrollPane scrollPane_ClientServerList;
	private JScrollPane scrollPane_AllClientServerList;
	private JLabel lblLogo_Server;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					serverWindow window = new serverWindow();
					window.frmServer.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public serverWindow() {
		initialize();  																						// components of the program will be initialized
		// try and catch that create the socket and call the class "ClientAccept" 
		try {
			serverSocket = new ServerSocket(port);  														// create a socket for the server
			txtrDisplayServer.append("Server is running...\n");												// display informations regarding the server
			txtrDisplayServer.append("Server IP Address: " + ip_server + "\n");
			txtrDisplayServer.append("Server Port Number: " + port + "\n");
			txtrDisplayServer.append("Waiting for Clients...\n");
			new ClientAccept().start(); 																	// call the class that create a thread for client
		} catch (Exception e) {																				// throw any exception occurs
			e.printStackTrace();
		}
	}
	
	class ClientAccept extends Thread {
		/**
		 * Class that checks if the client has all the requirements
		 * to establish a connection with the server.
		 */
		@Override
		public void run() {
			while (true) {
				try {
					Socket clientSocket = serverSocket.accept();  											// create a socket for client
					String idClient = new DataInputStream(clientSocket.getInputStream()).readUTF(); 		// receive the ID sent from client register view
					String ipClient = new DataInputStream(clientSocket.getInputStream()).readUTF(); 		// this for the IP
					
					DataOutputStream cOutStream = new DataOutputStream(clientSocket.getOutputStream()); 	// create an output stream for client
					if (activeUserSet != null && activeUserSet.contains(idClient)) { 						// if the ID is already in use, the user will be prompted to enter a new ID
						cOutStream.writeUTF("ID already connected!");										
					} else {
						// condition to set the first client as coordinator
						if (activeUserSet != null && activeUserSet.isEmpty()) { 
							coordinator = idClient;
							coordinatorip = ipClient;
							cOutStream.writeUTF("coordinator");												// prompt to the firstWindow that this client is the coordinator
						}
						// add client to allUserList and activeUserSet and IP to allUserListIP and activeUserSetIP
						allUsersList.put(idClient, clientSocket);
						allUsersListIP.put(ipClient, clientSocket);
						activeUserSet.add(idClient);
						activeUserSetIP.add(ipClient);
						cOutStream.writeUTF(""); 															// clear the existing message
						activeCList.addElement(idClient); 													// add this client to the active client JList
						activeCListIP.addElement(ipClient);
						// If ID client has already been taken, don't apply it to the allUser JList
						if (!allCList.contains(idClient))
							allCList.addElement(idClient);
							
						OnlineClientsServerList.setModel(activeCList); 										// In JList, it displays the active client list to the application.
						AllClientsServerList.setModel(allCList); 											// displays the all clients lists to the application.
						txtrDisplayServer.append("Client " + idClient + " connected...\n"); 				// print message on server that new client has been connected.
						
						// create thread to read messages and the thread to update all the active clients
						new MsgRead(clientSocket, idClient, ipClient).start(); 								
						new PrepareCLientList().start(); 													
					}
				} catch (IOException ioex) {
					ioex.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	class MsgRead extends Thread {
		/**
		 * Class that reads the messages from the client
		 * and make the necessary actions
		 */
		Socket socket;
		String Id;
		String Ip;
		private MsgRead(Socket socket, String idClient, String ipClient) {															// socket, id and ip will be provided by client
			this.socket = socket;
			this.Id = idClient;
			this.Ip = ipClient;
		}

		@Override
		public void run() {
			// while allUserList is not empty then proceed further
			while (AllClientsServerList != null && !allUsersList.isEmpty()) {
				try {
					String message = new DataInputStream(socket.getInputStream()).readUTF(); 										// read message from client
					System.out.println("read message -> " + message); 																// print the message for testing purposes
					DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") 								// set up the actual time in a specific pattern
				            .withZone(ZoneId.systemDefault());
					String time = DATE_TIME.format(new Date().toInstant());															// assign the time to a variable
  
					String[] msgList = message.split(":"); 																			// We used our own identifier to determine what step to take in response to the client's message.
																																	// We have appended "actionToBeTaken:clients_for_receiving_msg:message".
					// messages would be sent to selected client if the action is multicast.
					if (msgList[0].equalsIgnoreCase("multicast")) {
						String[] sendToList = msgList[1].split(","); 																//variable that stores the list of clients which will receive message
						// for every user send message
						for (String usr : sendToList) { 
							try {
								if (activeUserSet.contains(usr)) { 																	// checks again if the client is active then sends the message
									new DataOutputStream(((Socket) allUsersList.get(usr)).getOutputStream())
											.writeUTF(time + " -> " + Id + ": " + msgList[2]); 										// write message in output stream
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					// if broadcast send message to all active clients
					} else if (msgList[0].equalsIgnoreCase("broadcast")) { 
						Iterator<String> itr1 = allUsersList.keySet().iterator(); 													// iterate over all clients
						while (itr1.hasNext()) {
							String idName = (String) itr1.next(); 																	// store the client ID
							// We don't need to give ourselves a message, so we look for our Id
							if (!idName.equalsIgnoreCase(Id)) {
								try {
									// if the client is active then send message through output stream
									if (activeUserSet.contains(idName)) { 															
										new DataOutputStream(((Socket) allUsersList.get(idName)).getOutputStream())
												.writeUTF(time + " -> " + Id + ": " + msgList[1]);									// write message in output stream
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					// notify other users if a client's process is killed or disconnected
					} else if (msgList[0].equalsIgnoreCase("exit")) {
						activeUserSet.remove(Id); 																					// remove the client ID from active client ID set
						activeUserSetIP.remove(Ip);																					// remove the client IP from active client IP set
						txtrDisplayServer.append("Client " + Id + " disconnected...\n"); 											// print message on server message board
						
						// refresh the active and all client list on User Interface
						new PrepareCLientList().start(); 																			

						Iterator<String> itr = activeUserSet.iterator(); 															
						Iterator<String> itr1 = activeUserSetIP.iterator();															
						// while loop that iterate over other active clients IP and client ID
						while (itr.hasNext() && itr1.hasNext()) {
							String idClient2 = (String) itr.next();
							String ipClient2 = (String) itr1.next();
							if (!idClient2.equalsIgnoreCase(Id) && !ipClient2.equalsIgnoreCase(Ip)) {								// this message does not need to be sent to ourselves
								try {
									if (activeCList.get(0).equals(Id)) { 															//if the first client (coordinator) left, the next one will be the coordinator
										coordinator = activeCList.get(1);															
										new DataOutputStream(((Socket) allUsersList.get(idClient2)).getOutputStream())
										.writeUTF(Id + " disconnected..." + "\n" + "The new Coordinator is: " + coordinator); 		// notify all other active client for disconnection of a user and write the new coordinator id 			
									}else{
										new DataOutputStream(((Socket) allUsersList.get(idClient2)).getOutputStream())
											.writeUTF(Id + " disconnected...");														// write message in output steam
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
								// when a user is disconnected, refresh the active user list for each active user
								new PrepareCLientList().start(); 
							}
						}
						activeCList.removeElement(Id); 																				// remove client ID from Jlist for server
						activeCList.removeElement(Ip); 																				// remove client IP from Jlist for server
						OnlineClientsServerList.setModel(activeCList); 																//update the active clients list
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	class PrepareCLientList extends Thread {
		/**
		 * Class that prepares the list of active client
		 * to be displayed on the UI
		 */
		@Override
		public void run() {
			try {
				String ids = "";
				String ips = "";
				Iterator itr = activeUserSet.iterator();
				Iterator itr1 = activeUserSetIP.iterator();
				// while loop that will iterate over all active clients
				while (itr.hasNext() && itr1.hasNext()) {
					String key = (String) itr.next();
					String key1 = (String) itr1.next();																				// this part of the code will prepare string of all the clients
					ids += key + ",";
					ips += key1 + ",";
				}
				// to be on the safe side, the list has been trimmed.
				if (ids.length() != 0) { // just trimming the list for the safe side.
					ids = ids.substring(0, ids.length() - 1);
					ips = ips.substring(0, ips.length() - 1);
				}
				itr = activeUserSet.iterator(); 
				itr1 = activeUserSetIP.iterator();
				// iterate over all active client
				while (itr.hasNext() && itr1.hasNext()) { 
					String key = (String) itr.next();
					String key1 = (String) itr1.next();
					try {
						new DataOutputStream(((Socket) allUsersListIP.get(key1)).getOutputStream())								// set output stream and send the list of active client IP with identifier prefix ---->	
								.writeUTF("----->" + ips);	
						new DataOutputStream(((Socket) allUsersList.get(key)).getOutputStream())
								.writeUTF(":;.,/=" + ids); 																		// set output stream and send the list of active client ID with identifier prefix :;.,/=
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		// set up the window of the GUI
		frmServer = new JFrame();
		frmServer.setTitle("Server");
		frmServer.getContentPane().setForeground(Color.WHITE);
		frmServer.getContentPane().setFont(new Font("Verdana Pro", Font.BOLD, 13));
		frmServer.getContentPane().setBackground(Color.DARK_GRAY);
		frmServer.setBounds(100, 100, 713, 467);
		frmServer.getContentPane().setLayout(null);
		// action to close the window
		frmServer.addWindowListener(new WindowAdapter() {
			  public void windowClosing(WindowEvent we) {
			    System.exit(0);
			  }
			});
		// scroll panel of the display
		scrollPane_DisplayServer = new JScrollPane();
		scrollPane_DisplayServer.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane_DisplayServer.setBounds(20, 53, 404, 346);
		frmServer.getContentPane().add(scrollPane_DisplayServer);
		// display of the server
		txtrDisplayServer = new JTextArea(); 
		txtrDisplayServer.setWrapStyleWord(true);
		txtrDisplayServer.setLineWrap(true);
		scrollPane_DisplayServer.setViewportView(txtrDisplayServer);
		txtrDisplayServer.setEditable(false);
		txtrDisplayServer.setFont(new Font("Verdana Pro", Font.PLAIN, 13));
		txtrDisplayServer.setBackground(new Color(255, 255, 255));
		// scroll panel for the client list
		scrollPane_ClientServerList = new JScrollPane();
		scrollPane_ClientServerList.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane_ClientServerList.setBounds(481, 65, 197, 135);
		frmServer.getContentPane().add(scrollPane_ClientServerList);
		// list of the online clients
		OnlineClientsServerList = new JList();
		OnlineClientsServerList.setFont(new Font("Verdana Pro", Font.PLAIN, 13));
		scrollPane_ClientServerList.setViewportView(OnlineClientsServerList);
		// scroll panel for the active client list
		scrollPane_AllClientServerList = new JScrollPane();
		scrollPane_AllClientServerList.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane_AllClientServerList.setBounds(481, 254, 197, 135);
		frmServer.getContentPane().add(scrollPane_AllClientServerList);
		// list of all the clients added
		AllClientsServerList = new JList();
		scrollPane_AllClientServerList.setViewportView(AllClientsServerList);
		AllClientsServerList.setFont(new Font("Verdana Pro", Font.PLAIN, 13));
		// label
		JLabel lblOnlineClientsServerLabel = new JLabel("Online Clients");
		lblOnlineClientsServerLabel.setForeground(Color.WHITE);
		lblOnlineClientsServerLabel.setFont(new Font("Verdana Pro", Font.BOLD, 13));
		lblOnlineClientsServerLabel.setBounds(481, 43, 130, 14);
		frmServer.getContentPane().add(lblOnlineClientsServerLabel);
		// label
		JLabel lblAllClientsServerLabel = new JLabel("All Clients");
		lblAllClientsServerLabel.setForeground(Color.WHITE);
		lblAllClientsServerLabel.setFont(new Font("Verdana Pro", Font.BOLD, 13));
		lblAllClientsServerLabel.setBounds(481, 232, 130, 14);
		frmServer.getContentPane().add(lblAllClientsServerLabel);
		// button disconnect that shutdown the server
		JButton btnDisconnectServer = new JButton("DISCONNECT");
		btnDisconnectServer.setBackground(Color.LIGHT_GRAY);
		btnDisconnectServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnDisconnectServer.setFont(new Font("Verdana Pro", Font.BOLD, 11));
		btnDisconnectServer.setBounds(306, 24, 118, 28);
		frmServer.getContentPane().add(btnDisconnectServer);
		// logo
		lblLogo_Server = new JLabel("Chat with Us!");
		lblLogo_Server.setHorizontalAlignment(SwingConstants.CENTER);
		lblLogo_Server.setForeground(Color.WHITE);
		lblLogo_Server.setFont(new Font("Verdana Pro", Font.BOLD, 15));
		lblLogo_Server.setBounds(15, 15, 130, 26);
		frmServer.getContentPane().add(lblLogo_Server);
		
		
	}
}
