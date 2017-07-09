package server;

import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.SystemColor;
import javax.swing.border.CompoundBorder;
import java.awt.Window.Type;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextField;
import javax.swing.DropMode;
import java.awt.Dialog.ModalExclusionType;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;

public class GUIServer {
	
	String ip=null;
	private JFrame frmServer;
	public static Object[][] contacts= new Object[10][5];
	private static String[] columns = {"IP", "Username","Porta", "Horário de Entrada", "Disponível"};
	public static JTable table= new JTable();
	private JTextField portField;
	private JLabel ipHead_1;
	private JTextField perda;
	
	public static void setTable(Object[][] contacts){
		table= new JTable(contacts,columns);
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUIServer window = new GUIServer();
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
	public GUIServer() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmServer = new JFrame();
		frmServer.setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
		frmServer.setTitle("SERVER");
		frmServer.setBounds(100, 100, 577, 306);
		frmServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmServer.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		frmServer.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		table= new JTable(contacts,columns);
		table.setEnabled(false);
		table.setRowSelectionAllowed(false);
		table.setSurrendersFocusOnKeystroke(true);
		table.setBorder(new CompoundBorder());
		table.setBackground(SystemColor.controlHighlight);
		scrollPane.setViewportView(table);
		
		JPanel header = new JPanel();
		frmServer.getContentPane().add(header, BorderLayout.NORTH);
		header.setLayout(new BorderLayout(0, 0));
		
		JPanel portConfig = new JPanel();
		header.add(portConfig, BorderLayout.CENTER);
		portConfig.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblNewLabel = new JLabel("Porta:");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		portConfig.add(lblNewLabel);
		
		portField = new JTextField();
		portField.setBackground(SystemColor.control);
		portField.setHorizontalAlignment(SwingConstants.CENTER);
		portConfig.add(portField);
		portField.setColumns(10);
		
		JLabel ipHead = null;
		
		try {
			ip=""+InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			ipHead = new JLabel("IP: Não disponível");
		}
		
		JPanel panel = new JPanel();
		header.add(panel, BorderLayout.WEST);
		
		JLabel lblPorcentagemDePerda = new JLabel("Porcentagem de perda:");
		panel.add(lblPorcentagemDePerda);
		lblPorcentagemDePerda.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		perda = new JTextField();
		perda.setHorizontalAlignment(SwingConstants.CENTER);
		perda.setColumns(10);
		perda.setBackground(SystemColor.menu);
		panel.add(perda);
		
		JPanel panel_1 = new JPanel();
		header.add(panel_1, BorderLayout.NORTH);
		panel_1.setLayout(new BorderLayout(0, 0));
		try {
			ipHead_1 = new JLabel("IP:"+InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(null, "IP não disponível");
		}
		panel_1.add(ipHead_1);
		ipHead_1.setHorizontalAlignment(SwingConstants.CENTER);
		ipHead_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JButton start = new JButton("Iniciar");
		header.add(start, BorderLayout.EAST);
		
		JPanel panel_2 = new JPanel();
		frmServer.getContentPane().add(panel_2, BorderLayout.SOUTH);
		panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.X_AXIS));
		
		JLabel label = new JLabel("Pacotes perdidos:");
		label.setHorizontalAlignment(SwingConstants.LEFT);
		label.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panel_2.add(label);
		
		JLabel pacotesperdidos = new JLabel("");
		pacotesperdidos.setHorizontalAlignment(SwingConstants.LEFT);
		pacotesperdidos.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panel_2.add(pacotesperdidos);
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if((Integer.parseInt(perda.getText()))<0||(Integer.parseInt(perda.getText()))>100){
						(new JOptionPane(JOptionPane.ERROR_MESSAGE)).showMessageDialog(null,"Porcentagem inválida");
					}
					if (portField.getText() == "") portField.setText("0");
					else{(new Server(frmServer,Integer.parseInt(portField.getText()),ip,portField,start,table,columns,perda,Integer.parseInt(perda.getText()),pacotesperdidos)).start();
					portField.setEnabled(false);
					portField.setEditable(false);
					perda.setEnabled(false);
					perda.setEditable(false);
					start.setEnabled(false);
					}
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(null,"Porta e Porcentagem só aceitam números","",JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}
}