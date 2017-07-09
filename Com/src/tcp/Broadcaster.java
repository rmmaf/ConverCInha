package tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Broadcaster {

	private final Redirector red;
	private final int port;

	public Broadcaster(Redirector red, int port) {
		this.red = red;
		this.port = port;
		new Thread(new Ohayou()).start();
	}

	private class Ohayou implements Runnable {
		public Ohayou() {
		}

		@Override
		public void run() {
			byte[] pasg = { 'p', 'a', 's', 'g' };

			InetAddress broadcast;
			try {
				broadcast = InetAddress.getByName("255.255.255.255");
				while (true) {
					try {
						red.send(pasg, broadcast, port);
					} catch (IOException e) {
					}
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
					}
				}
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}
		}
	}
}
