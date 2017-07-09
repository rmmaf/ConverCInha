package app.appNet;

import java.io.IOException;
import java.net.InetAddress;

import javax.swing.JTextPane;

import app.appGUI.GUI;
import tcp.ALTCPSocket;
import tcp.Redirector;

public class ControlRcv extends Thread {
	// $% == text $& == file
	private volatile String ip1;
	private volatile String ip2;
	private volatile String ip3;
	private volatile JTextPane texto1;
	private volatile JTextPane texto2;
	private volatile JTextPane texto3;
	private volatile boolean stopBoolean;
	private volatile JTextPane solicitacao;
	private String perda;
	private Redirector redi;
	private GUI gui;

	public ControlRcv(GUI gui, Redirector redi, JTextPane texto1, JTextPane texto2, JTextPane texto3,
			JTextPane solicitacao, String perda, JTextPane tabela) {
		this.gui = gui;
		this.texto1 = texto1;
		this.texto2 = texto2;
		this.texto3 = texto3;
		ip1 = "0.0.0.0";
		ip2 = "0.0.0.0";
		ip3 = "0.0.0.0";
		stopBoolean = true;
		this.perda = perda;
		this.solicitacao = solicitacao;
		this.redi = redi;
	}

	synchronized public void run() {
			redi.fortify();
			redi.fortify();
			redi.fortify();
			if (!perda.equals(""))
				try {
					redi.setLossChance(Integer.parseInt(perda));
				} catch (NumberFormatException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			while (stopBoolean) {
				try {
					ALTCPSocket soqueteReceber = redi.accept();
					String mensagem = soqueteReceber.readUTF();
					if (mensagem.substring(0, 2).equals("$*")) {
						solicitacao.setText(
						solicitacao.getText() + mensagem.substring(2) + "\n");
					} else {
						String user = mensagem.substring(2, mensagem.indexOf(' '));
						int port;
						try {
							synchronized (gui.contactList) {
								for (int k = 0;; ++k) {
									if (user.equals(gui.contactList[k][1])) {
										port = Integer.parseInt(gui.contactList[k][0].substring(gui.contactList[k][0].indexOf(' ') + 1));
										break;
									}
								}
							}
						} catch (ArrayIndexOutOfBoundsException e) {
							continue;
						}
						if (mensagem.substring(0, 2).equals("$%")) {// mensagem
							if (soqueteReceber.getIP().equals(InetAddress.getByName(ip1)) && port == Integer.parseInt(gui.portaTexto1.getText())) {
								texto1.setText(texto1.getText() + user + ":" + mensagem.substring(mensagem.indexOf(' ')) + "\n");
							} else if (soqueteReceber.getIP().equals(InetAddress.getByName(ip2)) && port == Integer.parseInt(gui.portaTexto2.getText())) {
								texto2.setText(texto2.getText() + user + ":" + mensagem.substring(mensagem.indexOf(' ')) + "\n");
							} else if (soqueteReceber.getIP().equals(InetAddress.getByName(ip3)) && port == Integer.parseInt(gui.portaTexto3.getText())) {
								texto3.setText(texto3.getText() + user + ":" + mensagem.substring(mensagem.indexOf(' ')) + "\n");
							}
						} else if (mensagem.substring(0, 2).equals("$&") ) {// arquivo
							if (soqueteReceber.getIP().equals(InetAddress.getByName(ip1)) && port == Integer.parseInt(gui.portaTexto1.getText())) {
								new Thread(new FileReceiverJava(soqueteReceber, gui, (byte) 1)).start();
								continue;
							} else if (soqueteReceber.getIP().equals(InetAddress.getByName(ip2)) && port == Integer.parseInt(gui.portaTexto2.getText())) {
								new Thread(new FileReceiverJava(soqueteReceber, gui, (byte) 2)).start();
								continue;
							} else if (soqueteReceber.getIP().equals(InetAddress.getByName(ip3)) && port == Integer.parseInt(gui.portaTexto3.getText())) {
								new Thread(new FileReceiverJava(soqueteReceber, gui, (byte) 3)).start();
								continue;
							}
						} else if (mensagem.substring(0, 2).equals("$¨")) {
							if (soqueteReceber.getIP().equals(InetAddress.getByName(ip1)) && port == Integer.parseInt(gui.portaTexto1.getText())) {
								texto1.setText(texto1.getText().replaceAll("(✓)", "✔✔"));
							} else if (soqueteReceber.getIP().equals(InetAddress.getByName(ip2)) && port == Integer.parseInt(gui.portaTexto2.getText())) {
								texto2.setText(texto2.getText().replaceAll("(✓)", "✔✔"));
							} else if (soqueteReceber.getIP().equals(InetAddress.getByName(ip3)) && port == Integer.parseInt(gui.portaTexto3.getText())) {
								texto3.setText(texto3.getText().replaceAll("(✓)", "✔✔"));
							}
						} else if(mensagem.substring(0, 2).equals("$§")){
							gui.caixaDeEntradaGrupo.setText(gui.caixaDeEntradaGrupo.getText() +  user + ":" + mensagem.substring(mensagem.indexOf(' ')) + "\n");
						} else if(mensagem.substring(0, 2).equals("$+")){
							new Thread(new GroupFileReceiverJava(soqueteReceber, gui, user)).start();
							continue;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		try {
			redi.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.print("fechado");
	}

	public void setBool(boolean b) {
		stopBoolean = b;
	}

	public String getIp1() {
		return ip1;
	}

	public void setIp1(String ip1) {
		this.ip1 = ip1;
	}

	public String getIp2() {
		return ip2;
	}

	public void setIp2(String ip2) {
		this.ip2 = ip2;
	}

	public String getIp3() {
		return ip3;
	}

	public void setIp3(String ip3) {
		this.ip3 = ip3;
	}

	public JTextPane getTexto1() {
		return texto1;
	}

	public void setTexto1(JTextPane texto1) {
		this.texto1 = texto1;
	}

	public JTextPane getTexto2() {
		return texto2;
	}

	public void setTexto2(JTextPane texto2) {
		this.texto2 = texto2;
	}

	public JTextPane getTexto3() {
		return texto3;
	}

	public void setTexto3(JTextPane texto3) {
		this.texto3 = texto3;
	}
}