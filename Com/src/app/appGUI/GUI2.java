package app.appGUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.UIManager;
import java.awt.Font;

public class GUI2 extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	public JTextField username;
	private JLabel lblEnd;
	public JTextField end;
	public JTextField servport;
	private JLabel lblPortaDo;
	private JLabel lblPercentualDePerda;
	private JTextField perda;
	private JPanel panel_4;
	private JLabel lblPorta;
	private JTextField porta;
	private JPanel panel_5;
	private JLabel portaBroadlabel;
	private JTextField portaBroad;
	private JButton escutar;
	private JPanel panel_6;
	private JButton serverNat;
	private DatagramSocket ds;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI2 frame = new GUI2();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GUI2() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 384, 319);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JButton btnConectar = new JButton("Conectar");
		btnConectar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						InetAddress ip;
						try {
							ip = InetAddress.getLocalHost();
							int i;
							if (!porta.getText().equals("")) {
								i = Integer.valueOf(porta.getText());
								DatagramSocket sock = new DatagramSocket(i);
								sock.close();
							}
							i = Integer.valueOf(perda.getText());
							GUI frame = new GUI(perda.getText(), end.getText().substring(end.getText().indexOf('/')+1), ip.toString(),username.getText().replace('#', '_').replace(' ', '_'), Integer.valueOf(servport.getText()),porta.getText());
							frame.setVisible(true);
							try { ds.close(); } catch (NullPointerException e) {}
							dispose();
						} catch (UnknownHostException e) {
							JOptionPane.showMessageDialog(null, "IP invalido");
							// e.printStackTrace();
						} catch (NumberFormatException e) {
							JOptionPane.showMessageDialog(null, "Porta e Porcentagem só aceitam números");
							// e.printStackTrace();
						} catch (SocketException e) {
							JOptionPane.showMessageDialog(null, "Porta Ocupada");
						}
					}
				});
			}
		});
		contentPane.setLayout(new GridLayout(0, 2, 0, 0));
		
		panel_6 = new JPanel();
		contentPane.add(panel_6);
		panel_6.setLayout(new BorderLayout(0, 0));
		
		panel_5 = new JPanel();
		panel_6.add(panel_5, BorderLayout.CENTER);
		panel_5.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		portaBroadlabel = new JLabel("Porta para broadcast:");
		panel_5.add(portaBroadlabel);
		
		portaBroad = new JTextField();
		portaBroad.setColumns(10);
		panel_5.add(portaBroad);
		
		serverNat = new JButton("...");
		serverNat.setEnabled(false);
		serverNat.setFont(new Font("Tahoma", Font.PLAIN, 9));
		serverNat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = serverNat.getText();
				end.setText(s.substring(0, s.indexOf(':')));
				servport.setText(s.substring(s.indexOf(':') + 1));
			}
		});
		serverNat.setHorizontalAlignment(SwingConstants.CENTER);
		serverNat.setBackground(UIManager.getColor("Button.background"));
		panel_6.add(serverNat, BorderLayout.SOUTH);
		
		escutar = new JButton("Escutar");
		escutar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					int portBroadcast = Integer.parseInt(portaBroad.getText());
					ds = new DatagramSocket(portBroadcast);
					
					new Thread(new Runnable() {
						public void run(){
							byte[] pasg = {'p','a','s','g'};
							byte[] buf = new byte[4];
							DatagramPacket pack = new DatagramPacket(pasg,4);
							while (true) {
								try {
									ds.receive(pack);
								} catch (IOException e) {
									break;
								}
								if(pack.getData().equals(pasg)) {
									serverNat.setText(pack.getAddress() + ":" + pack.getPort());
									serverNat.setEnabled(true);
								}
							}
						}
					}).start();

					portaBroad.setEnabled(false);
					escutar.setEnabled(false);
				} catch (NumberFormatException e){
					JOptionPane.showMessageDialog(null, "Porta só recebe números");
				} catch (SocketException e) {
					JOptionPane.showMessageDialog(null, "Porta ocupada");
				}
			}
		});
		contentPane.add(escutar);

		JPanel panel = new JPanel();
		contentPane.add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblNomeDoUsurio = new JLabel("Nome do Usuário:");
		panel.add(lblNomeDoUsurio);

		username = new JTextField();
		panel.add(username);
		username.setColumns(10);

		JPanel panel_3 = new JPanel();
		contentPane.add(panel_3);

		lblPercentualDePerda = new JLabel("Percentual de perda:");
		panel_3.add(lblPercentualDePerda);

		perda = new JTextField();
		panel_3.add(perda);
		perda.setColumns(10);

		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1);

		lblEnd = new JLabel("Endereço do Servidor:");
		panel_1.add(lblEnd);

		end = new JTextField();
		panel_1.add(end);
		end.setColumns(10);

		JPanel panel_2 = new JPanel();
		contentPane.add(panel_2);

		lblPortaDo = new JLabel("Porta do Servidor:");
		panel_2.add(lblPortaDo);

		servport = new JTextField();
		panel_2.add(servport);
		servport.setColumns(10);

		panel_4 = new JPanel();
		contentPane.add(panel_4);

		lblPorta = new JLabel("Porta a ser utilizada (opcional):");
		panel_4.add(lblPorta);

		porta = new JTextField();
		porta.setColumns(10);
		panel_4.add(porta);
		contentPane.add(btnConectar);
	}
}