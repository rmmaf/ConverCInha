package app.appNet;

import app.appGUI.GUI;
import tcp.ALTCPSocket;
import tcp.Redirector;

public class SolicitarConversa extends Thread {
	private String  ip;
	private int port;
	public Redirector r ;

	public SolicitarConversa (String ip, int port){
		this.ip = ip;
		this.port = port;
	}

	public void run(){
		try{
			r = new Redirector();
			ALTCPSocket soquete = r.connect(ip, port);
			soquete.writeUTF("$*" + GUI.nomeUsuarioGUI2);
			soquete.slowClose();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
