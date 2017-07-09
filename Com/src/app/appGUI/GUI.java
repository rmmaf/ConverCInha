package app.appGUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import app.appNet.ClienteServer;
import app.appNet.ControlRcv;
import app.appNet.FileSenderJava;
import app.appNet.GroupFileSender;
import app.appNet.GroupMessageSender;
import app.appNet.MessageSenderJava;
import app.appNet.RTTSenderNATmod;
import app.appNet.SolicitarConversa;
import app.appNet.Visualized;
import tcp.Redirector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import java.awt.SystemColor;
import javax.swing.AbstractAction;
import javax.swing.Action;
import java.awt.Font;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultCaret;
import javax.swing.event.ChangeEvent;
import javax.swing.ScrollPaneConstants;
import java.awt.Color;

public class GUI extends JFrame {

	public static final int IP_INDEX = 0;
	public static final int PORT_INDEX = 1;
	public static final int USER_INDEX = 2;

	public String[][] contactList;

	/**
	 * 
	 */
	public boolean [] visualizador= new boolean[3];
	public static boolean localchoosed1=false;
	public static boolean localchoosed2=false;
	public static boolean localchoosed3=false;
	public static boolean archivechoosed1=false;
	public static boolean archivechoosed2=false;
	public static boolean archivechoosed3=false;
	public static final long serialVersionUID = 1L;
	public static JPanel contentPane;
	public JPanel aba1;
	public JTextField textField_1;
	public JButton enviar_1;
	public JButton anexar_1;
	public static JTextPane caixaDeEntrada1;
	public JPanel aba2;
	public JTextField textField_2;
	public JButton enviar_2;
	public JButton anexar_2;
	public static JTextPane caixaDeEntrada2;
	public JPanel aba3;
	public JTextField textField_3;
	public JButton enviar_3;
	public JButton anexar_3;
	public static JTextPane caixaDeEntrada3;
	public JScrollPane scrollPane_1;
	public JScrollPane scrollPane_2;
	public JScrollPane scrollPane_3;
	public JTabbedPane tabbedPane;
	public JLabel label;
	public JTextField ipTextField_2;
	public JButton conectar_2;
	public JLabel label_1;
	public JTextField ipTextField_3;
	public JButton conectar_3;
	public JLabel label_3;
	public JTextField ipTextField_1;
	public JButton conectar_1;
	public JLabel lblUsuriosOnline;
	public JButton btnLocal1;
	public JButton btnLocalDeDownload;
	public JButton btnLocalDeDownload_1;
	public JTextPane local1;
	public JTextPane local2;
	public JTextPane local3;
	public File file1;
	public File file2;
	public File file3;
	public File fileGrupo;
	public JFileChooser chooser1;
	public JFileChooser chooser2;
	public JFileChooser chooser3;
	public JFileChooser chooserGrupo;
	public final Action envioArq1 = new SwingAction();
	public JButton btnEnviarArquivo2;
	public final Action envioArq2 = new SwingAction_1();
	public JButton btnEnviarArquivo3;
	public static ControlRcv receptor;
	public static boolean stopBool1;
	public static boolean stopBool2;
	public static boolean stopBool3;
	public JLabel rtt3;
	public JLabel rtt2;
	public JLabel rtt1;
	public RTTSenderNATmod rttShow1;
	public RTTSenderNATmod rttShow2;
	public RTTSenderNATmod rttShow3;
	public static JTextPane solicitacao;
	public JLabel lblQueremConversa;
	public Redirector r1;
	public Redirector r2;
	public Redirector r3;
	public Redirector redFile;
	public JLabel label_12;
	public JLabel perdidos1;
	public JLabel perdidos3;
	public JLabel perdidos2;
	public static String perda;
	public JTextPane online;
	public static String ipGUI2;
	public static String ipLocalGUI2;
	public static String nomeUsuarioGUI2;
	public static int portaGUI2;
	public JTextField portaTexto1;
	public JTextField portaTexto2;
	public JTextField portaTexto3;
	public JLabel label_13;
	public static String portA;
	public JPanel abaG;
	public JTextField mensagemGrupo;
	public JButton enviarMensagemGrupo;
	public JButton button_1;
	public JButton button_3;
	public JTextPane localGrupo;
	public JButton button_4;
	public JTextPane upandoGrupo;
	public JTextPane caixaDeEntradaGrupo;
	private JScrollPane paneGroup;
	private boolean archivechoosedGrupo;
	public Redirector rGroup;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI frame = new GUI(perda, ipGUI2, ipLocalGUI2, nomeUsuarioGUI2, portaGUI2,portA);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * Create the frame.
	 * @param integer 
	 * @param string3 
	 * @param string2 
	 * @param string 
	 */
	public GUI(String perda, String ipGUI2, String ipLocalGUI2, String nomeUsuarioGUI2, Integer portaGUI2, String port) {
		try {
			rGroup = new Redirector();
		} catch (SocketException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}

		contactList = new String[1][2];
		portA=port;
		GUI.perda = perda;
		if (perda == null) {
			GUI.perda = ""; 
		}
		GUI.ipGUI2 = ipGUI2;
		GUI.ipLocalGUI2 = ipLocalGUI2;
		GUI.nomeUsuarioGUI2 = nomeUsuarioGUI2;
		GUI.portaGUI2 = portaGUI2;

		try {
			redFile = new Redirector();
		} catch (SocketException e2) {
			e2.printStackTrace();
		}

		System.out.println(GUI.perda);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 777, 518);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setTitle("ConverCInha");

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {

			}
		});
		tabbedPane.setBounds(9, 1, 559, 467);
		contentPane.add(tabbedPane);

		aba1 = new JPanel();
		tabbedPane.addTab("Conversa 1", null, aba1, null);
		aba1.setLayout(null);
		aba1.setBorder(new EmptyBorder(5, 5, 5, 5));

		textField_1 = new JTextField();
		textField_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MessageSenderJava enviadorMensagens1;
				enviadorMensagens1 = new MessageSenderJava(perdidos1,GUI.nomeUsuarioGUI2, textField_1.getText(), ipTextField_1.getText(), Integer.parseInt(portaTexto1.getText()), r1,
						caixaDeEntrada1, scrollPane_1);// Thread envia e se destr�i
				enviadorMensagens1.start();
				textField_1.setText("");
			}
		});
		textField_1.setColumns(10);
		textField_1.setBounds(23, 394, 365, 20);
		aba1.add(textField_1);

		enviar_1 = new JButton("Enviar Mensagem");
		enviar_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MessageSenderJava enviadorMensagens1;
				enviadorMensagens1 = new MessageSenderJava(perdidos1,GUI.nomeUsuarioGUI2, textField_1.getText(), ipTextField_1.getText(), Integer.parseInt(portaTexto1.getText()), r1,
						caixaDeEntrada1, scrollPane_1);// Thread envia e se destr�i
				enviadorMensagens1.start();
				textField_1.setText("");

			}
		});
		enviar_1.setBounds(398, 394, 151, 20);
		aba1.add(enviar_1);

		anexar_1 = new JButton("Anexar");
		anexar_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooser1 = new JFileChooser();
				int value = chooser1.showOpenDialog(null);
				if (value == JFileChooser.APPROVE_OPTION) {
					file1 = chooser1.getSelectedFile();
				}
				archivechoosed1=true;
			}
		});
		anexar_1.setBounds(398, 374, 151, 20);
		aba1.add(anexar_1);

		label_3 = new JLabel("IP do Destinatário:");
		label_3.setBounds(398, 59, 108, 14);
		aba1.add(label_3);

		ipTextField_1 = new JTextField();
		ipTextField_1.setColumns(10);
		ipTextField_1.setBounds(398, 72, 146, 20);
		aba1.add(ipTextField_1);

		conectar_1 = new JButton("Conectar");
		conectar_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(localchoosed1){
					if (stopBool1) {
						try {
							r1 = new Redirector();
							SolicitarConversa sol1 = new SolicitarConversa(ipTextField_1.getText(), Integer.parseInt(portaTexto1.getText()));
							sol1.start();
							rttShow1 = new RTTSenderNATmod(rtt1, ipTextField_1.getText(), Integer.parseInt(portaTexto1.getText()));
							rttShow1.start();
							receptor.setIp1(ipTextField_1.getText());
							System.out.println(receptor.getIp1());
							stopBool1 = false;
							visualizador[0]=true;
							conectar_1.setEnabled(false);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}else{
					JOptionPane.showMessageDialog(null, "Local de Destino do Download Não Escolhido");
				}
			}
		});
		conectar_1.setBounds(398, 135, 151, 23);
		aba1.add(conectar_1);

		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(23, 47, 365, 336);
		aba1.add(scrollPane_1);

		caixaDeEntrada1 = new JTextPane();
		caixaDeEntrada1.setEditable(false);
		scrollPane_1.setViewportView(caixaDeEntrada1);

		btnLocal1 = new JButton("Local de Download");
		btnLocal1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooserDown = new JFileChooser();
				chooserDown.setCurrentDirectory(new java.io.File("."));
				chooserDown.setDialogTitle("choosertitle");
				chooserDown.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooserDown.setAcceptAllFileFilterUsed(false);
				if (chooserDown.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					local1.setText(chooserDown.getSelectedFile().getAbsolutePath());
				} else {
					local1.setText("Nada selecionado");
				}
				localchoosed1=true;
			}
		});
		btnLocal1.setBounds(23, 16, 151, 20);
		aba1.add(btnLocal1);

		local1 = new JTextPane();
		local1.setBackground(SystemColor.activeCaptionBorder);
		local1.setBounds(184, 16, 365, 20);
		aba1.add(local1);

		JButton btnEnviarArquivo = new JButton("Enviar Arquivo");
		btnEnviarArquivo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnEnviarArquivo.setAction(envioArq1);
		btnEnviarArquivo.setBounds(398, 355, 151, 20);
		aba1.add(btnEnviarArquivo);


		online = new JTextPane();
		contentPane.add(online);
		online.setEditable(false);
		online.setBounds(578, 46, 177, 215);

		rtt1 = new JLabel();
		rtt1.setBackground(SystemColor.activeCaptionBorder);
		rtt1.setBounds(418, 41, 126, 20);
		aba1.add(rtt1);

		JLabel lblRtt = new JLabel("RTT");
		lblRtt.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblRtt.setBounds(398, 47, 30, 14);
		aba1.add(lblRtt);

		label_12 = new JLabel("Pcts Perdidos");
		label_12.setFont(new Font("Tahoma", Font.PLAIN, 10));
		label_12.setBounds(398, 337, 67, 14);
		aba1.add(label_12);

		perdidos1 = new JLabel();
		perdidos1.setText("0");
		perdidos1.setFont(new Font("Tahoma", Font.PLAIN, 9));
		perdidos1.setBackground(SystemColor.activeCaptionBorder);
		perdidos1.setBounds(461, 337, 83, 14);
		aba1.add(perdidos1);

		aba2 = new JPanel();
		tabbedPane.addTab("Conversa 2", null, aba2, null);
		aba2.setLayout(null);
		aba2.setBorder(new EmptyBorder(5, 5, 5, 5));

		textField_2 = new JTextField();
		textField_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MessageSenderJava enviadorMensagens2;
				enviadorMensagens2 = new MessageSenderJava(perdidos2,GUI.nomeUsuarioGUI2, textField_2.getText(), ipTextField_2.getText(), Integer.parseInt(portaTexto2.getText()), r2,
						caixaDeEntrada2, scrollPane_2);// Thread envia e se destr�i
				enviadorMensagens2.start();
				textField_2.setText("");
			}
		});
		textField_2.setColumns(10);
		textField_2.setBounds(23, 393, 365, 20);
		aba2.add(textField_2);

		enviar_2 = new JButton("Enviar Mensagem");
		enviar_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MessageSenderJava enviadorMensagens2;
				enviadorMensagens2 = new MessageSenderJava(perdidos2,GUI.nomeUsuarioGUI2, textField_2.getText(), ipTextField_2.getText(), Integer.parseInt(portaTexto2.getText()), r2,
						caixaDeEntrada2, scrollPane_2);// Thread envia e se destr�i
				enviadorMensagens2.start();
				textField_2.setText("");
			}
		});
		enviar_2.setBounds(398, 393, 151, 20);
		aba2.add(enviar_2);

		anexar_2 = new JButton("Anexar");
		anexar_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooser2 = new JFileChooser();
				int value = chooser2.showOpenDialog(null);
				if (value == JFileChooser.APPROVE_OPTION) {
					file2 = chooser2.getSelectedFile();
				}
				archivechoosed2=true;
			}
		});
		anexar_2.setBounds(398, 373, 151, 20);
		aba2.add(anexar_2);

		ipTextField_2 = new JTextField();
		ipTextField_2.setColumns(10);
		ipTextField_2.setBounds(398, 75, 151, 20);
		aba2.add(ipTextField_2);

		conectar_2 = new JButton("Conectar");
		conectar_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(localchoosed2){
					if (stopBool2) {
						try {
							r2 = new Redirector();
							SolicitarConversa sol2 = new SolicitarConversa(ipTextField_2.getText(), Integer.parseInt(portaTexto2.getText()));
							sol2.start();
							rttShow2 = new RTTSenderNATmod(rtt2, ipTextField_2.getText(), Integer.parseInt(portaTexto2.getText()));
							rttShow2.start();
							receptor.setIp2(ipTextField_2.getText());
							stopBool2 = false;
							visualizador[1]=true;
							conectar_2.setEnabled(false);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}else{
					JOptionPane.showMessageDialog(null, "Local não selecionado");
				}
			}
		});
		conectar_2.setBounds(398, 128, 151, 23);
		aba2.add(conectar_2);

		scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(23, 47, 365, 336);
		aba2.add(scrollPane_2);

		caixaDeEntrada2 = new JTextPane();
		caixaDeEntrada2.setEditable(false);
		scrollPane_2.setViewportView(caixaDeEntrada2);
		DefaultCaret caret2 = (DefaultCaret)caixaDeEntrada2.getCaret();
		caret2.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		label = new JLabel("IP do Destinatário:");
		label.setBounds(398, 62, 108, 14);
		aba2.add(label);

		btnLocalDeDownload = new JButton("Local de Download");
		btnLocalDeDownload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooserDown = new JFileChooser();
				chooserDown.setCurrentDirectory(new java.io.File("."));
				chooserDown.setDialogTitle("choosertitle");
				chooserDown.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooserDown.setAcceptAllFileFilterUsed(false);
				if (chooserDown.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					local2.setText(chooserDown.getSelectedFile().getAbsolutePath());
				} else {
					local2.setText("Nada selecionado");
				}
				localchoosed2=true;
			}
		});
		btnLocalDeDownload.setBounds(23, 11, 151, 20);
		aba2.add(btnLocalDeDownload);

		local2 = new JTextPane();
		local2.setBackground(SystemColor.activeCaptionBorder);
		local2.setBounds(184, 11, 365, 20);
		aba2.add(local2);

		btnEnviarArquivo2 = new JButton("Enviar Arquivo");
		btnEnviarArquivo2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});
		btnEnviarArquivo2.setAction(envioArq2);
		btnEnviarArquivo2.setBounds(398, 353, 151, 20);
		aba2.add(btnEnviarArquivo2);

		JLabel label_6 = new JLabel("RTT");
		label_6.setFont(new Font("Tahoma", Font.PLAIN, 10));
		label_6.setBounds(398, 47, 30, 14);
		aba2.add(label_6);

		rtt2 = new JLabel();
		rtt2.setFont(new Font("Tahoma", Font.PLAIN, 9));
		rtt2.setBackground(SystemColor.activeCaptionBorder);
		rtt2.setBounds(419, 40, 130, 23);
		aba2.add(rtt2);

		JLabel label_11 = new JLabel("Pcts Perdidos");
		label_11.setFont(new Font("Tahoma", Font.PLAIN, 10));
		label_11.setBounds(403, 330, 67, 14);
		aba2.add(label_11);

		perdidos2 = new JLabel();
		perdidos2.setFont(new Font("Tahoma", Font.PLAIN, 9));
		perdidos2.setBackground(SystemColor.activeCaptionBorder);
		perdidos2.setBounds(466, 330, 83, 14);
		aba2.add(perdidos2);

		aba3 = new JPanel();
		tabbedPane.addTab("Conversa 3", null, aba3, null);
		aba3.setLayout(null);
		aba3.setBorder(new EmptyBorder(5, 5, 5, 5));

		textField_3 = new JTextField();
		textField_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MessageSenderJava enviadorMensagens3;
				enviadorMensagens3 = new MessageSenderJava(perdidos3,GUI.nomeUsuarioGUI2, textField_3.getText(), ipTextField_3.getText(), Integer.parseInt(portaTexto3.getText()), r3,
						caixaDeEntrada3, scrollPane_3);// Thread envia e se destr�i
				enviadorMensagens3.start();
				textField_3.setText("");
			}
		});
		textField_3.setColumns(10);
		textField_3.setBounds(23, 394, 365, 20);
		aba3.add(textField_3);

		enviar_3 = new JButton("Enviar Mensagem");
		enviar_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MessageSenderJava enviadorMensagens3;
				enviadorMensagens3 = new MessageSenderJava(perdidos3,GUI.nomeUsuarioGUI2, textField_3.getText(), ipTextField_3.getText(), Integer.parseInt(portaTexto3.getText()), r3,
						caixaDeEntrada3, scrollPane_3);// Thread envia e se destr�i
				enviadorMensagens3.start();
				textField_3.setText("");
			}
		});
		enviar_3.setBounds(398, 394, 151, 20);
		aba3.add(enviar_3);

		anexar_3 = new JButton("Anexar");
		anexar_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooser3 = new JFileChooser();
				int value = chooser3.showOpenDialog(null);
				if (value == JFileChooser.APPROVE_OPTION) {
					file3 = chooser3.getSelectedFile();
				}
				archivechoosed3=true;
			}
		});
		anexar_3.setBounds(398, 373, 151, 20);
		aba3.add(anexar_3);

		ipTextField_3 = new JTextField();
		ipTextField_3.setColumns(10);
		ipTextField_3.setBounds(398, 72, 151, 20);
		aba3.add(ipTextField_3);

		conectar_3 = new JButton("Conectar");
		conectar_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(localchoosed3){
					if (stopBool3) {
						try {
							r3 = new Redirector();
							SolicitarConversa sol3 = new SolicitarConversa(ipTextField_3.getText(), Integer.parseInt(portaTexto3.getText()));
							sol3.start();
							rttShow3 = new RTTSenderNATmod(rtt3, ipTextField_3.getText(), Integer.parseInt(portaTexto3.getText()));
							rttShow3.start();
							receptor.setIp3(ipTextField_3.getText());
							stopBool3 = false;
							visualizador[2]=true;
							conectar_3.setEnabled(false);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}else{
					JOptionPane.showMessageDialog(null, "Local de Recebimento de Download Não Escolhido");
				}
			}
		});
		conectar_3.setBounds(398, 132, 151, 23);
		aba3.add(conectar_3);

		scrollPane_3 = new JScrollPane();
		scrollPane_3.setBounds(23, 47, 365, 336);
		aba3.add(scrollPane_3);

		caixaDeEntrada3 = new JTextPane();
		caixaDeEntrada3.setEditable(false);
		scrollPane_3.setViewportView(caixaDeEntrada3);
		DefaultCaret caret3 = (DefaultCaret)caixaDeEntrada3.getCaret();
		caret3.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		label_1 = new JLabel("IP do Destinatário:");
		label_1.setBounds(398, 60, 108, 14);
		aba3.add(label_1);

		btnLocalDeDownload_1 = new JButton("Local de Download");
		btnLocalDeDownload_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooserDown = new JFileChooser();
				chooserDown.setCurrentDirectory(new java.io.File("."));
				chooserDown.setDialogTitle("choosertitle");
				chooserDown.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooserDown.setAcceptAllFileFilterUsed(false);
				if (chooserDown.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					local3.setText(chooserDown.getSelectedFile().getAbsolutePath());
				} else {
					local3.setText("Nada selecionado");
				}
				localchoosed3=true;
			}
		});
		btnLocalDeDownload_1.setBounds(23, 11, 151, 20);
		aba3.add(btnLocalDeDownload_1);

		local3 = new JTextPane();
		local3.setBackground(SystemColor.activeCaptionBorder);
		local3.setBounds(179, 11, 365, 20);
		aba3.add(local3);

		btnEnviarArquivo3 = new JButton("Enviar Arquivo");
		btnEnviarArquivo3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(archivechoosed3){
					FileSenderJava enviadorArquivos3;
					enviadorArquivos3 = new FileSenderJava(redFile, Integer.parseInt(portaTexto3.getText()), file3, ipTextField_3.getText());
					enviadorArquivos3.start();
				}else{
					JOptionPane.showMessageDialog(null, "Arquivo não anexado");
				}
			}
		});
		btnEnviarArquivo3.setBounds(398, 352, 151, 20);
		aba3.add(btnEnviarArquivo3);

		JLabel label_7 = new JLabel("RTT");
		label_7.setFont(new Font("Tahoma", Font.PLAIN, 10));
		label_7.setBounds(392, 42, 30, 14);
		aba3.add(label_7);

		rtt3 = new JLabel();
		rtt3.setBackground(SystemColor.activeCaptionBorder);
		rtt3.setBounds(414, 38, 130, 23);
		aba3.add(rtt3);

		solicitacao = new JTextPane();
		solicitacao.setEditable(false);
		solicitacao.setBounds(578, 299, 177, 169);
		contentPane.add(solicitacao);

		JLabel lblPctsPerdidos = new JLabel("Pcts Perdidos");
		lblPctsPerdidos.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblPctsPerdidos.setBounds(398, 333, 67, 14);
		aba3.add(lblPctsPerdidos);

		perdidos3 = new JLabel();
		perdidos3.setFont(new Font("Tahoma", Font.PLAIN, 9));
		perdidos3.setBackground(SystemColor.activeCaptionBorder);
		perdidos3.setBounds(461, 333, 83, 14);
		aba3.add(perdidos3);

		try {
			Visualized vi= new Visualized(this);
			vi.start();
		} catch (Exception e) {
			e.printStackTrace();
		}




		lblUsuriosOnline = new JLabel("Usuários Online:");
		lblUsuriosOnline.setBounds(577, 28, 108, 14);
		contentPane.add(lblUsuriosOnline);



		lblQueremConversa = new JLabel("Querem conversa:");
		lblQueremConversa.setBounds(577, 274, 108, 14);
		contentPane.add(lblQueremConversa);

		this.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {

				System.exit(0);
			}
		});










		Redirector redi;
		try {
			redi = null;
			try{
				if(portA.equals("")) {
					redi = new Redirector();
				} else {
					redi = new Redirector(Integer.parseInt(portA));
				}
			} catch (NullPointerException e) {
				redi = new Redirector();
			}
			ClienteServer mandarReceberInfoPServer = new ClienteServer(redi, GUI.ipGUI2, GUI.ipLocalGUI2, GUI.nomeUsuarioGUI2, GUI.portaGUI2, online, GUI.perda, this);
			mandarReceberInfoPServer.start();

			JLabel textPane = new JLabel();
			textPane.setBackground(SystemColor.activeCaptionBorder);
			textPane.setBounds(629, 1, 126, 20);
			contentPane.add(textPane);

			new RTTSenderNATmod(textPane, GUI.ipGUI2, GUI.portaGUI2).start();
			stopBool1 = true;
			stopBool2 = true;
			stopBool3 = true;
			caixaDeEntrada1.setText("Conversa 1:\n");
			caixaDeEntrada2.setText("Conversa 2:\n");
			caixaDeEntrada3.setText("Conversa 3:\n");

			receptor = new ControlRcv(this, redi, caixaDeEntrada1, caixaDeEntrada2, caixaDeEntrada3, solicitacao, GUI.perda, online);
		} catch (SocketException e1) {
			e1.printStackTrace();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}




		portaTexto3 = new JTextField();
		portaTexto3.setColumns(10);
		portaTexto3.setBounds(398, 112, 146, 20);
		aba3.add(portaTexto3);

		label_13 = new JLabel("Porta:");
		label_13.setBounds(398, 96, 40, 14);
		aba3.add(label_13);

		portaTexto2 = new JTextField();
		portaTexto2.setColumns(10);
		portaTexto2.setBounds(398, 108, 146, 20);
		aba2.add(portaTexto2);

		JLabel label_2 = new JLabel("Porta:");
		label_2.setBounds(398, 92, 40, 14);
		aba2.add(label_2);

		portaTexto1 = new JTextField();
		portaTexto1.setColumns(10);
		portaTexto1.setBounds(398, 106, 146, 20);
		aba1.add(portaTexto1);

		JLabel lblPorta = new JLabel("Porta:");
		lblPorta.setBounds(398, 90, 40, 14);
		aba1.add(lblPorta);

		abaG = new JPanel();
		abaG.setLayout(null);
		abaG.setBorder(new EmptyBorder(5, 5, 5, 5));
		tabbedPane.addTab("Conversa em Grupo", null, abaG, null);
		
		GUI gui = this;
		
		mensagemGrupo = new JTextField();
		mensagemGrupo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					GroupMessageSender mensageiroGrupo = new GroupMessageSender(gui , mensagemGrupo.getText(), rGroup, caixaDeEntradaGrupo, paneGroup);
					mensageiroGrupo.start();
					mensagemGrupo.setText("");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		mensagemGrupo.setColumns(10);
		mensagemGrupo.setBounds(23, 373, 365, 20);
		abaG.add(mensagemGrupo);
		

		enviarMensagemGrupo = new JButton("Enviar Mensagem");
		enviarMensagemGrupo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					GroupMessageSender mensageiroGrupo = new GroupMessageSender(gui , mensagemGrupo.getText(), rGroup, caixaDeEntradaGrupo, paneGroup);
					mensageiroGrupo.start();
					mensagemGrupo.setText("");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		enviarMensagemGrupo.setBounds(398, 373, 151, 20);
		abaG.add(enviarMensagemGrupo);

		button_1 = new JButton("Anexar");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooserGrupo = new JFileChooser();
				int value = chooserGrupo.showOpenDialog(null);
				if (value == JFileChooser.APPROVE_OPTION) {
					fileGrupo = chooserGrupo.getSelectedFile();
					upandoGrupo.setText(fileGrupo.getName());
				}
				archivechoosedGrupo=true;
			}
		});
		button_1.setBounds(292, 408, 96, 20);
		abaG.add(button_1);

		button_3 = new JButton("Local de Download");
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooserDown = new JFileChooser();
				chooserDown.setCurrentDirectory(new java.io.File("."));
				chooserDown.setDialogTitle("choosertitle");
				chooserDown.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooserDown.setAcceptAllFileFilterUsed(false);
				if (chooserDown.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					localGrupo.setText(chooserDown.getSelectedFile().getAbsolutePath());
				} else {
					localGrupo.setText("Nada selecionado");
				}
				localchoosed3=true;
			}
		});
		button_3.setBounds(23, 16, 151, 20);
		abaG.add(button_3);

		localGrupo = new JTextPane();
		localGrupo.setBackground(SystemColor.activeCaptionBorder);
		localGrupo.setBounds(184, 16, 365, 20);
		abaG.add(localGrupo);

		button_4 = new JButton("Enviar Arquivo");
		button_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(archivechoosedGrupo){
					for(int cont = 0; cont < gui.contactList.length; cont++){
						if(!gui.contactList[cont][1].equals(GUI.nomeUsuarioGUI2)){
							int porta =Integer.parseInt((gui.contactList[cont][0].substring(gui.contactList[cont][0].indexOf(' ') + 1)));
							String ip = gui.contactList[cont][0].substring(gui.contactList[cont][0].indexOf('/')+1, gui.contactList[cont][0].indexOf(' '));
							String user = gui.contactList[cont][1];
							new Thread(new GroupFileSender(redFile, porta, fileGrupo, ip, user + " ")).start();
							
						}
					}
				}else{
					JOptionPane.showMessageDialog(null, "Arquivo não anexado");
				}
			}
		});
		button_4.setBounds(398, 408, 151, 20);
		abaG.add(button_4);

		upandoGrupo = new JTextPane();
		upandoGrupo.setEditable(false);
		upandoGrupo.setBackground(Color.LIGHT_GRAY);
		upandoGrupo.setBounds(23, 408, 256, 20);
		abaG.add(upandoGrupo);

		paneGroup = new JScrollPane();
		paneGroup.setBounds(23, 44, 521, 313);
		abaG.add(paneGroup);

		caixaDeEntradaGrupo = new JTextPane();
		paneGroup.setViewportView(caixaDeEntradaGrupo);

		JLabel lblServerRtt = new JLabel("Server RTT");
		lblServerRtt.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblServerRtt.setBounds(575, 7, 70, 14);
		contentPane.add(lblServerRtt);
		receptor.start();
	}

	public class SwingAction extends AbstractAction {
		/**
		 * 
		 */
		public static final long serialVersionUID = 1L;

		public SwingAction() {
			putValue(NAME, "Enviar Arquivo");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}

		public void actionPerformed(ActionEvent e) {
			if(archivechoosed1){
				FileSenderJava enviadorArquivos1;
				enviadorArquivos1 = new FileSenderJava(redFile, Integer.parseInt(portaTexto1.getText()), file1, ipTextField_1.getText());
				enviadorArquivos1.start();
			}else{
				JOptionPane.showMessageDialog(null, "Arquivo não anexado");
			}
		}
	}

	public class SwingAction_1 extends AbstractAction {
		/**
		 * 
		 */
		public static final long serialVersionUID = 1L;

		public SwingAction_1() {
			putValue(NAME, "Enviar Arquivo");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}

		public void actionPerformed(ActionEvent e) {
			if(archivechoosed2){
				FileSenderJava enviadorArquivos2;
				enviadorArquivos2 = new FileSenderJava(redFile, Integer.parseInt(portaTexto2.getText()), file2, ipTextField_2.getText());
				enviadorArquivos2.start();
			}else{
				JOptionPane.showMessageDialog(null, "Arquivo não anexado");
			}
		}

	}
}