package app.appNet;

import java.awt.Adjustable;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import tcp.ALTCPSocket;
import tcp.Redirector;

public class MessageSenderJava extends Thread {
	private String username;
	private String mensagem;
	private String endereco;
	private int porta;
	JTextPane caixa;
	private Redirector r;
	private JScrollPane pane;
	private JLabel perdidos;

	public MessageSenderJava(JLabel perdidos, String username, String mensagem, String endereco, int porta, Redirector r,
			JTextPane caixa, JScrollPane pane) {
		this.username = username;
		this.porta = porta;
		this.mensagem = mensagem;
		this.endereco = endereco;
		this.caixa = caixa;
		this.r = r;
		this.pane = pane;
		this.perdidos=perdidos;
	}

	public void run() {
		int p=0;
		synchronized (caixa) {
			while (true) {
				try {
					if (!mensagem.equals("")) {
						ALTCPSocket soquete = r.connect(endereco, porta);
						String mensagemAEnviar = ("$%" + username + " " + mensagem);
						caixa.setText(caixa.getText() + "(⋯) " + username + ": " + mensagem + "\n");
	
						JScrollBar verticalBar = pane.getVerticalScrollBar();
						int pos = verticalBar.getMaximum();
						AdjustmentListener scroller = new AdjustmentListener() {
							@Override
							public void adjustmentValueChanged(AdjustmentEvent event) {
								Adjustable adjustable = event.getAdjustable();
								adjustable.setValue(pos);
	
								verticalBar.removeAdjustmentListener(this);
							}
						};
						verticalBar.addAdjustmentListener(scroller);
	
						soquete.writeUTF(mensagemAEnviar);
						soquete.waitForAck();// confirma que o outro lado recebeu a
												// mensagem
						soquete.slowClose();
						caixa.setText(caixa.getText().replaceAll("(⋯)", "✓"));
						perdidos.setText(String.valueOf((Integer.parseInt(perdidos.getText()) + soquete.packetsLost())));
						p=soquete.packetsLost();
						break;
					}
				} catch (IOException e) {
					if (e.getMessage().substring(0, 10).equals("Already co")) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {
						}
						continue;
					}
					e.printStackTrace();
					break;
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
				
			}
		}
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public int getPorta() {
		return porta;
	}

	public void setPorta(int porta) {
		this.porta = porta;
	}
}
