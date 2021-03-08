package JavaCW;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.awt.Color;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.DropMode;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class firstWindow extends JFrame {
	
    /**
	 * 
	 */
	
	private JFrame frame;
	private JTextField txtInsertYourId;
	private JTextField txtInsertYourIp;
	private JTextField txtInsertYourPort;
	private JTextField txtInsertServerIp;
	private JTextField txtInsertServerPort;
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					firstWindow window = new firstWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public firstWindow() {
		initialize();
		
	}
	/**
	 * Initialise the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setFont(new Font("Verdana Pro", Font.BOLD, 13));
		frame.getContentPane().setForeground(Color.WHITE);
		frame.getContentPane().setBackground(Color.DARK_GRAY);
		frame.setBackground(Color.WHITE);
		frame.setBounds(100, 100, 713, 467);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		txtInsertYourId = new JTextField();
		
		txtInsertYourId.setForeground(Color.GRAY);
		txtInsertYourId.setFont(new Font("Verdana", Font.BOLD, 12));
		txtInsertYourId.setText("Insert your ID");
		txtInsertYourId.setDropMode(DropMode.INSERT);
		txtInsertYourId.setBounds(224, 166, 241, 33);
		frame.getContentPane().add(txtInsertYourId);
		txtInsertYourId.setColumns(10);
		
		txtInsertYourId.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
            	txtInsertYourId.setText("");
            }
        });
		
		txtInsertYourIp = new JTextField();
		txtInsertYourIp.setText("Insert your IP Address");
		txtInsertYourIp.setForeground(Color.GRAY);
		txtInsertYourIp.setFont(new Font("Verdana", Font.BOLD, 12));
		txtInsertYourIp.setDropMode(DropMode.INSERT);
		txtInsertYourIp.setColumns(10);
		txtInsertYourIp.setBounds(105, 210, 241, 33);
		frame.getContentPane().add(txtInsertYourIp);
		
		txtInsertYourIp.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
            	txtInsertYourIp.setText("");
            }
        });
		
		txtInsertYourPort = new JTextField();
		txtInsertYourPort.setText("Insert your Port Number");
		txtInsertYourPort.setForeground(Color.GRAY);
		txtInsertYourPort.setFont(new Font("Verdana", Font.BOLD, 12));
		txtInsertYourPort.setDropMode(DropMode.INSERT);
		txtInsertYourPort.setColumns(10);
		txtInsertYourPort.setBounds(356, 210, 241, 33);
		frame.getContentPane().add(txtInsertYourPort);
		
		txtInsertYourPort.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
            	txtInsertYourPort.setText("");
            }
        });
		
		txtInsertServerIp = new JTextField();
		txtInsertServerIp.setText("Insert Server IP Address");
		txtInsertServerIp.setForeground(Color.GRAY);
		txtInsertServerIp.setFont(new Font("Verdana", Font.BOLD, 12));
		txtInsertServerIp.setDropMode(DropMode.INSERT);
		txtInsertServerIp.setColumns(10);
		txtInsertServerIp.setBounds(105, 254, 241, 33);
		frame.getContentPane().add(txtInsertServerIp);
		
		txtInsertServerIp.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
            	txtInsertServerIp.setText("");
            }
        });
		
		txtInsertServerPort = new JTextField();
		txtInsertServerPort.setText("Insert Server Port Number");
		txtInsertServerPort.setForeground(Color.GRAY);
		txtInsertServerPort.setFont(new Font("Verdana", Font.BOLD, 12));
		txtInsertServerPort.setDropMode(DropMode.INSERT);
		txtInsertServerPort.setColumns(10);
		txtInsertServerPort.setBounds(356, 254, 241, 33);
		frame.getContentPane().add(txtInsertServerPort);
		
		txtInsertServerPort.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
            	txtInsertServerPort.setText("");
            }
        });
		
		JButton joinButton = new JButton("Join!");
		joinButton.setToolTipText("Join!");
		
		//does the action when the user click the button "Join!"
		joinButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int port = Integer.parseInt(txtInsertYourPort.getText()); 								// take the port input of the JTextField and declare it to integer
					String id = txtInsertYourId.getText(); 													// take the ID input of the JTextField and declare it to string
					String ip = txtInsertYourIp.getText();													// take the ID input of the JTextField and declare it to string
					Socket socket = new Socket(ip, port); 													// create a socket
					DataInputStream in = new DataInputStream(socket.getInputStream()); 						// create input stream
					DataOutputStream out = new DataOutputStream(socket.getOutputStream());					// create output stream
					out.writeUTF(id); 																		// send ID to the output stream
					
					String msgFromServer = new DataInputStream(socket.getInputStream()).readUTF(); 			// receive message on socket
					if(msgFromServer.equals("ID already connected!")) {										//if server sent this message then prompt user to enter other ID
						JOptionPane.showMessageDialog(frame,  "ID already connected!\n"); 					// show message in other dialog box
					}else {
						new secondWindow(id, socket); 														// or else create a new thread of Client view and close the firstWindow
						frame.dispose();
					}
				}catch(Exception ex) {
					JOptionPane.showMessageDialog(frame,  "Wrong port access, try again!\n");
					ex.printStackTrace();
				}
			
			}
		});
		joinButton.setForeground(Color.DARK_GRAY);
		joinButton.setFont(new Font("Verdana Pro", Font.BOLD, 13));
		joinButton.setBackground(Color.LIGHT_GRAY);
		joinButton.setBounds(286, 309, 127, 33);
		frame.getContentPane().add(joinButton);
		
		JLabel lblNewLabel = new JLabel("Chat with Us!");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setFont(new Font("Verdana Pro", Font.BOLD, 22));
		lblNewLabel.setBounds(264, 79, 168, 45);
		frame.getContentPane().add(lblNewLabel);
		
	}
}
