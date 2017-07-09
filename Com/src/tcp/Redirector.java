package tcp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Random;

public class Redirector {
	private volatile boolean closed;

	private final DatagramSocket sock;
	private final ConcurrentHashMap<Long, ALTCPSocket> mappings;
	private final LinkedBlockingQueue<ALTCPSocket> acceptList;

	private final Random renge;
	private int lossChance;

	public Redirector() throws SocketException {
		this.closed = false;

		this.sock = new DatagramSocket();
		this.mappings = new ConcurrentHashMap<Long, ALTCPSocket>();
		this.acceptList = new LinkedBlockingQueue<ALTCPSocket>();
		
		this.renge = new Random(System.nanoTime());
		this.lossChance = 0;

		new Thread(new Receiver(this)).start();
	}

	public Redirector(int port) throws SocketException {
		this.closed = false;

		this.sock = new DatagramSocket(port);
		this.mappings = new ConcurrentHashMap<Long, ALTCPSocket>();
		this.acceptList = new LinkedBlockingQueue<ALTCPSocket>();

		this.renge = new Random(System.nanoTime());
		this.lossChance = 0;

		new Thread(new Receiver(this)).start();
	}

	private class Receiver implements Runnable {
		private Redirector red;

		public Receiver(Redirector red) {
			this.red = red;
		}

		@Override
		public void run() {
			while (!closed) {
				try {
					byte[] data = new byte[2048];
					DatagramPacket pack = new DatagramPacket(data, 2048);
					red.sock.receive(pack);
					
					if (pack.getLength() == 1) {
						byte[] resDat = new byte[1];
						DatagramPacket res = new DatagramPacket(resDat, 1, pack.getAddress(), pack.getPort());
						red.sock.send(res);
						continue;
					}

					if (red.renge.nextInt(100) < red.lossChance || pack.getLength() < 12)
						continue;

					long ip = red.addressToLong(pack.getAddress(), pack.getPort());
					ALTCPSocket alt = red.mappings.get(ip);

					if (alt == null) {
						if (data[8] != -32) {
							acceptList.put(new ALTCPSocket(red, pack.getAddress(), pack.getPort(), Arrays.copyOf(pack.getData(), pack.getLength())));
						}
					} else {
						alt.recList.put(Arrays.copyOf(pack.getData(), pack.getLength()));
					}
				} catch (IOException e) {
					if (!e.getMessage().equals("socket closed"))
						e.printStackTrace();
				} catch (InterruptedException e) {}
			}
		}
	}

	long addressToLong(InetAddress ip, int port) {
		byte[] ipbytes = ip.getAddress();
		long result = 0;

		for (int i = 0; i < 4; i++) {
			result <<= 8;
			result |= (ipbytes[i] & 0xFF);
		}
		result <<= 16;
		result |= (port & 0xFFFF);
		return result;
	}

	LinkedBlockingQueue<byte[]> internalConnect(ALTCPSocket alt, InetAddress address, int port) throws IOException {
		long ip = addressToLong(address, port);
		LinkedBlockingQueue<byte[]> list = new LinkedBlockingQueue<byte[]>();
		if (mappings.containsKey(ip)) {
			throw new IOException("Already connected to " + address.toString());
		} else {
			mappings.put(ip, alt);
		}
		return list;
	}

	void disconnect(InetAddress address, int port) {
		long ip = addressToLong(address, port);
		mappings.remove(ip);
	}

	void send(byte[] data, InetAddress ip, int port) throws IOException {
		sock.send(new DatagramPacket(data, data.length, ip, port));
	}

	public ALTCPSocket accept() throws InterruptedException, IOException {
		if (closed)
			throw new IOException("redirector closed");
		
		ALTCPSocket alt = acceptList.take();
		
		return alt;
	}

	public void setLossChance(int loss) throws IOException {
		if (closed)
			throw new IOException("redirector closed");
		if (loss < 0)
			lossChance = 0;
		else if (loss > 100)
			lossChance = 100;
		else
			lossChance = loss;
	}

	public void close() throws Exception {
		Enumeration<Long> num = mappings.keys();
		LinkedList<Thread> que = new LinkedList<Thread>();
		while(num.hasMoreElements()) {
			Thread t = new Thread(new Runnable(){
				public void run() {
					try {
						mappings.get(num.nextElement()).slowClose();
					} catch (Exception e) {
					}
				}
			});
			t.start();
			que.add(t);
		}
		while (!que.isEmpty()) {
			que.remove().join();
		}
		
	}
	
	public ALTCPSocket connect(InetAddress ip, int port) throws IOException {
		return new ALTCPSocket(this, ip, port);
	}
	
	public ALTCPSocket connect(String ip, int port) throws IOException {
		return new ALTCPSocket(this, InetAddress.getByName(ip), port);
	}
	
	public void fortify() {
		new Thread(new Receiver(this)).start();
	}
}
