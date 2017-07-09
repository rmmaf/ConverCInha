package app.appNet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.swing.JLabel;
import javax.swing.JTextPane;

public class RTTSenderNATmod extends Thread {

	private InetAddress target;
	private int port;
	private JLabel RTTField;

	public RTTSenderNATmod(JLabel RTTField, String target, int port) throws UnknownHostException {
		this.RTTField = RTTField;
		this.target = InetAddress.getByName(target);
		this.port = port;
	}

	@Override
	public void run() {
		DatagramSocket ds = null;
		while (ds == null) {
			try {
				ds = new DatagramSocket();
				ds.setSoTimeout(2000);
			} catch (SocketException e) {
			}
		}

		byte[] data = new byte[1];
		DatagramPacket pack = new DatagramPacket(data, 1, target, port);
		byte[] recData = new byte[1];
		DatagramPacket recPack = new DatagramPacket(data, 1);
		long RTT = 0;
		boolean first = true;
		byte timeoutCounter = 0;

		while (true) {
			try {
				long time = System.nanoTime();
				ds.send(pack);

				ds.receive(recPack);
				time = System.nanoTime() - time;
				if (first) {
					RTT = time;
					first = false;
				} else {
					RTT = 7 * (RTT/8) + (time/8);
					RTTField.setText(String.valueOf(RTT / 1000) + "μs");
				}
				Thread.sleep(2000);
			} catch (SocketTimeoutException e) {
				timeoutCounter++;
				if (timeoutCounter > 4) {
					RTTField.setText("unknown");
					first = true;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				ds.close();
				e.printStackTrace();
				break;
			} catch (InterruptedException e) {
			}
		}
	}

}
