package app.appNet;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import app.appGUI.GUI;
import tcp.ALTCPSocket;
import tcp.Redirector;

public class Visualized extends Thread{
	private GUI gui;
	
	public Visualized (GUI gui){
		this.gui = gui;
	}
	
	public void run(){
		Redirector r = null;
		try {
			r = new Redirector();
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while(true){
			String mensagemAEnviar = ("$¨" + GUI.nomeUsuarioGUI2 + " ");
			try{
				Thread.sleep(600);
			if(gui.tabbedPane.getSelectedIndex()==0){
				try {
					InetAddress.getByName(gui.ipTextField_1.getText());
					if(!gui.ipTextField_1.getText().equals("")&&gui.visualizador[0]){
						ALTCPSocket soquete = r.connect(InetAddress.getByName(gui.ipTextField_1.getText()),Integer.parseInt(gui.portaTexto1.getText()));
						soquete.writeUTF(mensagemAEnviar);
						soquete.waitForAck();
						soquete.slowClose();
						gui.perdidos1.setText(String.valueOf(Integer.parseInt(gui.perdidos1.getText()) + soquete.packetsLost())); 
					}
				} catch (UnknownHostException e) {
					
				}
			}
			else if(gui.tabbedPane.getSelectedIndex()==1){
				try {
					InetAddress.getByName(gui.ipTextField_2.getText());
					if(!gui.ipTextField_2.getText().equals("")&&gui.visualizador[1]){
						ALTCPSocket soquete = r.connect(InetAddress.getByName(gui.ipTextField_2.getText()), Integer.parseInt(gui.portaTexto2.getText()));
						soquete.writeUTF(mensagemAEnviar);
						soquete.waitForAck();
						soquete.slowClose();
						gui.perdidos2.setText(String.valueOf(Integer.parseInt(gui.perdidos2.getText()) + soquete.packetsLost())); 
					}
				} catch (UnknownHostException e) {
					
				}
			}
			else if(gui.tabbedPane.getSelectedIndex()==2){
				try {
					InetAddress.getByName(gui.ipTextField_3.getText());
					if(!gui.ipTextField_3.getText().equals("")&&gui.visualizador[2]){
						ALTCPSocket soquete = r.connect(InetAddress.getByName(gui.ipTextField_3.getText()), Integer.parseInt(gui.portaTexto3.getText()));
						soquete.writeUTF(mensagemAEnviar);
						soquete.waitForAck();
						soquete.slowClose();
						gui.perdidos3.setText(String.valueOf(Integer.parseInt(gui.perdidos3.getText()) + soquete.packetsLost())); 
					}
				} catch (UnknownHostException e) {
					
				}
			}
			}catch (Exception e){
				
			}
		}
	}
}
