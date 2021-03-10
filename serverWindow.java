package JavaCW;

import java.awt.EventQueue;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Font;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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
import java.awt.event.ActionEvent;

public class serverWindow {

	private JFrame frmServer;
	private ServerSocket serverSocket; 																		//server socket
	private static int port = 4999;  // port number to be used
	private JTextArea txtrDisplayServer; // variable for server message board on UI
	private JList AllClientsServerList;  // variable on UI
	private JList OnlineClientsServerList; // variable on UI
	private static final long serialVersionUID = 1L;
	private static Map<String, Socket> allUsersList = new ConcurrentHashMap<>(); // keeps the mapping of all the																				// usernames used and their socket connections
	private static Set<String> activeUserSet = new HashSet<>(); // this set keeps track of all the active users 
	private DefaultListModel<String> activeCList = new DefaultListModel<String>(); // keeps list of active users for display on UI
	private DefaultListModel<String> allCList = new DefaultListModel<String>(); // keeps list of all users for display on UI
	private JScrollPane scrollPane_DisplayServer;
	private JScrollPane scrollPane_ClientServerList;
	private JScrollPane scrollPane_AllClientServerList;
	private JLabel lblLogo_Server;
	private JButton btnDisconnectServer;


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
		initialize();  // components of swing app will be initialized here.
		try {
			serverSocket = new ServerSocket(port);  // create a socket for server
			txtrDisplayServer.append("Server is running...\n");
			txtrDisplayServer.append("Server Port Number: " + port + "\n"); // print messages to server message board
			txtrDisplayServer.append("Waiting for Clients...\n");
			new ClientAccept().start(); // this will create a thread for client
		} catch (Exception e) {
			System.out.println("errori server nel serverview");
			e.printStackTrace();
		}
	}
	
	class ClientAccept extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					Socket clientSocket = serverSocket.accept();  // create a socket for client
					String idClient = new DataInputStream(clientSocket.getInputStream()).readUTF(); // this will receive the username sent from client register view
					DataOutputStream cOutStream = new DataOutputStream(clientSocket.getOutputStream()); // create an output stream for client
					if (activeUserSet != null && activeUserSet.contains(idClient)) { // if username is in use then we need to prompt user to enter new name
						cOutStream.writeUTF("ID already connected!");
					} else {
						allUsersList.put(idClient, clientSocket); // add new user to allUserList and activeUserSet
						activeUserSet.add(idClient);
						cOutStream.writeUTF(""); // clear the existing message
						activeCList.addElement(idClient); // add this user to the active user JList
						if (!allCList.contains(idClient)) // if username taken previously then don't add to allUser JList otherwise add it
							allCList.addElement(idClient);
						OnlineClientsServerList.setModel(activeCList); // show the active and allUser List to the swing app in JList
						AllClientsServerList.setModel(allCList);
						txtrDisplayServer.append("Client " + idClient + " connected...\n"); // print message on server that new client has been connected.
						new MsgRead(clientSocket, idClient).start(); // create a thread to read messages
						new PrepareCLientList().start(); //create a thread to update all the active clients
					}
				} catch (IOException ioex) {  // throw any exception occurs
					System.out.println("errori server nel client accept1");
					ioex.printStackTrace();
				} catch (Exception e) {
					System.out.println("errori server nel client accept2");
					e.printStackTrace();
				}
			}
		}
	}

	class MsgRead extends Thread { // this class reads the messages coming from client and take appropriate actions
		Socket socket;
		String Id;
		private MsgRead(Socket socket, String idClient) { // socket and username will be provided by client
			this.socket = socket;
			this.Id = idClient;
		}

		@Override
		public void run() {
			while (AllClientsServerList != null && !allUsersList.isEmpty()) {  // if allUserList is not empty then proceed further
				try {
					String message = new DataInputStream(socket.getInputStream()).readUTF(); // read message from client
					System.out.println("message read ==> " + message); // just print the message for testing
					String[] msgList = message.split(":"); // I have used my own identifier to identify what action to take on the received message from client
														// i have appended actionToBeTaken:clients_for_receiving_msg:message
					if (msgList[0].equalsIgnoreCase("multicast")) { // if action is multicast then send messages to selected active users
						String[] sendToList = msgList[1].split(","); //this variable contains list of clients which will receive message
						for (String usr : sendToList) { // for every user send message
							try {
								if (activeUserSet.contains(usr)) { // check again if user is active then send the message
									new DataOutputStream(((Socket) allUsersList.get(usr)).getOutputStream())
											.writeUTF(Id + ": " + msgList[2]); // put message in output stream
								}
							} catch (Exception e) { // throw exceptions
								e.printStackTrace();
							}
						}
					} else if (msgList[0].equalsIgnoreCase("broadcast")) { // if broadcast then send message to all active clients
						
						Iterator<String> itr1 = allUsersList.keySet().iterator(); // iterate over all users
						while (itr1.hasNext()) {
							String idName = (String) itr1.next(); // it is the username
							if (!idName.equalsIgnoreCase(Id)) { // we don't need to send message to ourself, so we check for our Id
								try {
									if (activeUserSet.contains(idName)) { // if client is active then send message through output stream
										new DataOutputStream(((Socket) allUsersList.get(idName)).getOutputStream())
												.writeUTF(Id + ": " + msgList[1]);
									
									}
								} catch (Exception e) {
									System.out.println("errori server nel run");
									e.printStackTrace(); // throw exceptions
								}
							}
						}
					} else if (msgList[0].equalsIgnoreCase("exit")) { // if a client's process is killed then notify other clients
						activeUserSet.remove(Id); // remove that client from active user set
						txtrDisplayServer.append("Client " + Id + " disconnected...\n"); // print message on server message board

						new PrepareCLientList().start(); // update the active and all user list on UI

						Iterator<String> itr = activeUserSet.iterator(); // iterate over other active users
						while (itr.hasNext()) {
							String idClient2 = (String) itr.next();
							if (!idClient2.equalsIgnoreCase(Id)) { // we don't need to send this message to ourself
								try {
									new DataOutputStream(((Socket) allUsersList.get(idClient2)).getOutputStream())
											.writeUTF(Id + " disconnected..."); // notify all other active user for disconnection of a user
								} catch (Exception e) { // throw errors
									System.out.println("errori server nel run2");
									e.printStackTrace();
								}
								new PrepareCLientList().start(); // update the active user list for every client after a user is disconnected
							}
						}
						activeCList.removeElement(Id); // remove client from Jlist for server
						OnlineClientsServerList.setModel(activeCList); //update the active user list
					}
				} catch (Exception e) {
					System.out.println("errori server nel run3");
					e.printStackTrace();
				}
			}
		}
	}

	class PrepareCLientList extends Thread { // it prepares the list of active user to be displayed on the UI
		@Override
		public void run() {
			try {
				String ids = "";
				Iterator itr = activeUserSet.iterator(); // iterate over all active users
				while (itr.hasNext()) { // prepare string of all the users
					String key = (String) itr.next();
					ids += key + ",";
				}
				if (ids.length() != 0) { // just trimming the list for the safe side.
					ids = ids.substring(0, ids.length() - 1);
				}
				itr = activeUserSet.iterator(); 
				while (itr.hasNext()) { // iterate over all active users
					String key = (String) itr.next();
					try {
						new DataOutputStream(((Socket) allUsersList.get(key)).getOutputStream())
								.writeUTF(":;.,/=" + ids); // set output stream and send the list of active users with identifier prefix :;.,/=
					} catch (Exception e) {
						System.out.println("errori server nel prepare1");
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				System.out.println("errori server nel prepare2");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmServer = new JFrame();
		frmServer.setTitle("Server");
		frmServer.getContentPane().setForeground(Color.WHITE);
		frmServer.getContentPane().setFont(new Font("Verdana Pro", Font.BOLD, 13));
		frmServer.getContentPane().setBackground(Color.DARK_GRAY);
		frmServer.setBounds(100, 100, 713, 467);
		frmServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmServer.getContentPane().setLayout(null);
		
		scrollPane_DisplayServer = new JScrollPane();
		scrollPane_DisplayServer.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane_DisplayServer.setBounds(20, 53, 404, 346);
		frmServer.getContentPane().add(scrollPane_DisplayServer);
		
		txtrDisplayServer = new JTextArea(); 
		txtrDisplayServer.setWrapStyleWord(true);
		txtrDisplayServer.setLineWrap(true);
		scrollPane_DisplayServer.setViewportView(txtrDisplayServer);
		txtrDisplayServer.setEditable(false);
		txtrDisplayServer.setFont(new Font("Verdana Pro", Font.PLAIN, 13));
		txtrDisplayServer.setBackground(new Color(255, 255, 255));
		//txtrDisplayServer.setText("Server is running...\r\n");
		
		scrollPane_ClientServerList = new JScrollPane();
		scrollPane_ClientServerList.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane_ClientServerList.setBounds(481, 65, 197, 135);
		frmServer.getContentPane().add(scrollPane_ClientServerList);
		
		OnlineClientsServerList = new JList();
		OnlineClientsServerList.setFont(new Font("Verdana Pro", Font.PLAIN, 13));
		scrollPane_ClientServerList.setViewportView(OnlineClientsServerList);
		
		scrollPane_AllClientServerList = new JScrollPane();
		scrollPane_AllClientServerList.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane_AllClientServerList.setBounds(481, 254, 197, 135);
		frmServer.getContentPane().add(scrollPane_AllClientServerList);
		
		AllClientsServerList = new JList();
		scrollPane_AllClientServerList.setViewportView(AllClientsServerList);
		AllClientsServerList.setFont(new Font("Verdana Pro", Font.PLAIN, 13));
		
		JLabel lblOnlineClientsServerLabel = new JLabel("Online Clients");
		lblOnlineClientsServerLabel.setForeground(Color.WHITE);
		lblOnlineClientsServerLabel.setFont(new Font("Verdana Pro", Font.BOLD, 13));
		lblOnlineClientsServerLabel.setBounds(481, 43, 130, 14);
		frmServer.getContentPane().add(lblOnlineClientsServerLabel);
		
		JLabel lblAllClientsServerLabel = new JLabel("All Clients");
		lblAllClientsServerLabel.setForeground(Color.WHITE);
		lblAllClientsServerLabel.setFont(new Font("Verdana Pro", Font.BOLD, 13));
		lblAllClientsServerLabel.setBounds(481, 232, 130, 14);
		frmServer.getContentPane().add(lblAllClientsServerLabel);
		
		btnDisconnectServer = new JButton("DISCONNECT");
		btnDisconnectServer.setBackground(Color.LIGHT_GRAY);
		btnDisconnectServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
				          
				    }
		});
		btnDisconnectServer.setFont(new Font("Verdana Pro", Font.BOLD, 11));
		btnDisconnectServer.setBounds(306, 26, 118, 28);
		frmServer.getContentPane().add(btnDisconnectServer);
		
		lblLogo_Server = new JLabel("Chat with Us!");
		lblLogo_Server.setHorizontalAlignment(SwingConstants.CENTER);
		lblLogo_Server.setForeground(Color.WHITE);
		lblLogo_Server.setFont(new Font("Verdana Pro", Font.BOLD, 15));
		lblLogo_Server.setBounds(13, 15, 130, 26);
		frmServer.getContentPane().add(lblLogo_Server);
		
		
	}
}
