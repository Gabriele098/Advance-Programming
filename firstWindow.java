package Grafica;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.Color;
import javax.swing.BoxLayout;
import javax.swing.JMenuBar;
import java.awt.GridBagLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.GridBagConstraints;
import javax.swing.UIManager;
import javax.swing.DropMode;
import javax.swing.JFormattedTextField;
import javax.swing.JProgressBar;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

public class firstWindow {

	private JFrame frame;
	private JTextField txtChatWithUs;
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
	 * Initialize the contents of the frame.
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
		
		txtChatWithUs = new JTextField();
		txtChatWithUs.setBounds(256, 75, 168, 45);
		txtChatWithUs.setFont(new Font("Verdana", Font.BOLD, 21));
		txtChatWithUs.setForeground(Color.WHITE);
		txtChatWithUs.setBackground(Color.DARK_GRAY);
		txtChatWithUs.setText("Chat with Us!");
		frame.getContentPane().add(txtChatWithUs);
		txtChatWithUs.setColumns(10);
		
		txtInsertYourId = new JTextField();
		txtInsertYourId.setForeground(Color.GRAY);
		txtInsertYourId.setFont(new Font("Verdana", Font.BOLD, 12));
		txtInsertYourId.setText("Insert your ID");
		txtInsertYourId.setDropMode(DropMode.INSERT);
		txtInsertYourId.setBounds(224, 166, 241, 33);
		frame.getContentPane().add(txtInsertYourId);
		txtInsertYourId.setColumns(10);
		
		txtInsertYourIp = new JTextField();
		txtInsertYourIp.setText("Insert your IP Address");
		txtInsertYourIp.setForeground(Color.GRAY);
		txtInsertYourIp.setFont(new Font("Verdana", Font.BOLD, 12));
		txtInsertYourIp.setDropMode(DropMode.INSERT);
		txtInsertYourIp.setColumns(10);
		txtInsertYourIp.setBounds(105, 210, 241, 33);
		frame.getContentPane().add(txtInsertYourIp);
		
		txtInsertYourPort = new JTextField();
		txtInsertYourPort.setText("Insert your Port Number");
		txtInsertYourPort.setForeground(Color.GRAY);
		txtInsertYourPort.setFont(new Font("Verdana", Font.BOLD, 12));
		txtInsertYourPort.setDropMode(DropMode.INSERT);
		txtInsertYourPort.setColumns(10);
		txtInsertYourPort.setBounds(356, 210, 241, 33);
		frame.getContentPane().add(txtInsertYourPort);
		
		txtInsertServerIp = new JTextField();
		txtInsertServerIp.setText("Insert Server IP Address");
		txtInsertServerIp.setForeground(Color.GRAY);
		txtInsertServerIp.setFont(new Font("Verdana", Font.BOLD, 12));
		txtInsertServerIp.setDropMode(DropMode.INSERT);
		txtInsertServerIp.setColumns(10);
		txtInsertServerIp.setBounds(105, 254, 241, 33);
		frame.getContentPane().add(txtInsertServerIp);
		
		txtInsertServerPort = new JTextField();
		txtInsertServerPort.setText("Insert Server Port Number");
		txtInsertServerPort.setForeground(Color.GRAY);
		txtInsertServerPort.setFont(new Font("Verdana", Font.BOLD, 12));
		txtInsertServerPort.setDropMode(DropMode.INSERT);
		txtInsertServerPort.setColumns(10);
		txtInsertServerPort.setBounds(356, 254, 241, 33);
		frame.getContentPane().add(txtInsertServerPort);
		
		JButton joinButton = new JButton("Join!");
		joinButton.setToolTipText("Join!");
		joinButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(txtInsertYourId.getText());
				System.out.println(txtInsertYourIp.getText());
				System.out.println(txtInsertYourPort.getText());
				System.out.println(txtInsertServerIp.getText());
				System.out.println(txtInsertServerPort.getText());
			}
		});
		joinButton.setForeground(Color.DARK_GRAY);
		joinButton.setFont(new Font("Verdana Pro", Font.BOLD, 13));
		joinButton.setBackground(Color.LIGHT_GRAY);
		joinButton.setBounds(286, 309, 127, 33);
		frame.getContentPane().add(joinButton);
	}
	
}
