package app.appNet;

import java.awt.Adjustable;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.IOException;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import app.appGUI.GUI;
import tcp.ALTCPSocket;
import tcp.Redirector;

public class GroupMessageSender extends Thread {
	private GUI gui;
	private String mensagem;
	private volatile JTextPane caixa;
	private Redirector r;
	private JScrollPane pane;

	public GroupMessageSender (GUI gui, String mensagem, Redirector r,
		JTextPane caixa, JScrollPane pane){
		this.gui = gui;
		this.mensagem = mensagem;
		this.caixa = caixa;
		this.r = r;
		this.pane = pane;
	}

	public void run(){
		while(true){
			caixa.setText(caixa.getText() + "(⋯) " + GUI.nomeUsuarioGUI2 + ": " + mensagem + "\n");
			Thread[] threads = new Thread[gui.contactList.length];
			synchronized(gui.contactList) {
				for(int cont = 0; cont < gui.contactList.length; cont++){
					try {
						if (!mensagem.equals("") && !gui.contactList[cont][1].equals(GUI.nomeUsuarioGUI2)) {
							int porta =Integer.parseInt((gui.contactList[cont][0].substring(gui.contactList[cont][0].indexOf(' ') + 1)));
							String ip = gui.contactList[cont][0].substring(gui.contactList[cont][0].indexOf('/')+1, gui.contactList[cont][0].indexOf(' '));
							ALTCPSocket soquete = r.connect(ip , porta);
							String mensagemAEnviar = ("$§" + GUI.nomeUsuarioGUI2 + " " + mensagem);
	
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
							threads[cont] =
									new Thread(new Runnable(){
										public void run() {
											try {
												soquete.slowClose();
											} catch (InterruptedException e) {
												e.printStackTrace();
											}
										}
									});
							threads[cont].start();
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
			boolean one = true;
			for(int n = 0; n < gui.contactList.length; ++n){
				try {
					threads[n].join();
				} catch (InterruptedException e) {
				} catch (NullPointerException e) {
					if (one) {
						one = false;
					} else {
						e.printStackTrace();
					}
				}
			}
			caixa.setText(caixa.getText().replaceAll("(⋯)", "✓"));
			break;
		}
	}
}
