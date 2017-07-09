package app.appNet;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class RTTReceiver extends Thread{
	private int port;
	public RTTReceiver(int port){
		this.port = port;
	}
	@SuppressWarnings("resource")
	public void run(){
		
		try{
			DatagramSocket serverSocket = new DatagramSocket(port);
			byte[] receiveData = new byte[1];
			byte[] sendData;
			InetAddress clientIP;
			int port;
			while(true){
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				clientIP = receivePacket.getAddress();
				port = receivePacket.getPort();
				sendData = ("1").getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientIP, port);
				serverSocket.send(sendPacket);
			}
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
