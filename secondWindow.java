package Grafica;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.Color;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.JScrollBar;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class secondWindow {

	private JFrame frame;
	
	static String username;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					secondWindow window = new secondWindow();
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
	public secondWindow() {
		initialize();
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
		
		JTextArea textDisplay = new JTextArea();
		textDisplay.setBounds(10, 57, 404, 298);
		frame.getContentPane().add(textDisplay);
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(10, 365, 330, 30);
		frame.getContentPane().add(textArea);
		
		JButton btnSendButton = new JButton("SEND");
		btnSendButton.setBackground(Color.LIGHT_GRAY);
		btnSendButton.setFont(new Font("Verdana Pro", Font.BOLD, 11));
		btnSendButton.setBounds(342, 365, 72, 30);
		frame.getContentPane().add(btnSendButton);
		
		JButton btnEndChat = new JButton("X");
		btnEndChat.setBackground(Color.LIGHT_GRAY);
		btnEndChat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnEndChat.setFont(new Font("Verdana Pro", Font.BOLD, 12));
		btnEndChat.setBounds(367, 26, 47, 30);
		frame.getContentPane().add(btnEndChat);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setFont(new Font("Verdana Pro", Font.BOLD, 12));
		comboBox.setBackground(Color.DARK_GRAY);
		comboBox.setBounds(480, 84, 209, 30);
		frame.getContentPane().add(comboBox);
		
		JLabel lblOnlineLabel = new JLabel("Online Clients");
		lblOnlineLabel.setForeground(Color.WHITE);
		lblOnlineLabel.setFont(new Font("Verdana Pro", Font.BOLD, 13));
		lblOnlineLabel.setBounds(480, 62, 130, 14);
		frame.getContentPane().add(lblOnlineLabel);
		
		JLabel lblClientName = new JLabel("#name of the client youre talking with");
		lblClientName.setForeground(Color.WHITE);
		lblClientName.setFont(new Font("Verdana Pro", Font.BOLD, 13));
		lblClientName.setBounds(10, 32, 314, 24);
		frame.getContentPane().add(lblClientName);
		
		JButton btnRefreshButton = new JButton("REFRESH");
		btnRefreshButton.setBackground(Color.LIGHT_GRAY);
		btnRefreshButton.setFont(new Font("Verdana Pro", Font.BOLD, 12));
		btnRefreshButton.setBounds(576, 125, 113, 23);
		frame.getContentPane().add(btnRefreshButton);
		
		JButton btnStartChatButton = new JButton("START CHAT");
		btnStartChatButton.setBackground(Color.LIGHT_GRAY);
		btnStartChatButton.setFont(new Font("Verdana Pro", Font.BOLD, 12));
		btnStartChatButton.setBounds(559, 332, 130, 23);
		frame.getContentPane().add(btnStartChatButton);
	}

	private javax.swing.JLabel lblClientName;
	private static javax.swing.JTextArea textDisplay;
	private javax.swing.JButton btnSendButton;
	private static javax.swing.JTextArea textArea;
	


}

