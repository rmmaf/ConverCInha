package app.appNet;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import javax.swing.JTextPane;

public class RTTSender extends Thread {
	private String endereco;
	private JTextPane rtt;
	private int port;
	public RTTSender(String endereco,int port, JTextPane rtt){
		this.endereco = endereco;
		this.rtt = rtt;
		this.port = port;
	}

	public void run(){
		long estRtt = 0;
		try {
			@SuppressWarnings("resource")
			DatagramSocket clientSocket = new DatagramSocket();
			clientSocket.setSoTimeout(2500);
			InetAddress IPServer = InetAddress.getByName(endereco);
			byte[] sendData;
			sendData = ("1").getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPServer, port);
			boolean primeiro = true;
			long valor;
			while(true){
				try {
					long tempoAtual = System.nanoTime();
					clientSocket.send(sendPacket); 
					byte[] receiveData = new byte[1];
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					clientSocket.receive(receivePacket);
					valor =  ((System.nanoTime() - tempoAtual));
					if(primeiro){//primeiro valor do estRtt
						estRtt = valor;
						primeiro = false;//so iguala uma vez
					}
					estRtt = 7*(estRtt>>3) + (valor>>3);
					rtt.setText(estRtt/1000 + " μs");
					Thread.sleep(1000);
				} catch (SocketTimeoutException e) {}
			}
		}catch (IOException | InterruptedException e) {
			e.printStackTrace();
			
		}
	}



}

