package tcp;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class ALTCPSocket implements AutoCloseable {
	
	
	
	
	private static final int BASE_WINDOW_SIZE = 1048576;	//this number MUST, UNCONDITIONALLY, be a positive integer power of two
	static final int MAXIMUM_SEGMENT_SIZE = 1420;

	private static final byte SYN = 0;
	private static final byte SYNACK = 1;
	private static final byte STANDARD = 2;
	private static final byte LIFEQ = 3;
	private static final byte RESET = 6;
	static final byte CLOSE = 7;
	//2 commands left! sack, somethin else maybe

	private static final byte SYN_SENT = 0;
	private static final byte SYN_RECEIVED = 1;
	private static final byte ESTABILISHED = 2;
	private static final byte SLOW_CLOSE = 3;
	private static final byte CLOSE_SENT = 4;
	private static final byte CLOSE_RECEIVED = 5;
	private static final byte CLOSED = 6;
	
	private final Object receptionLock;

	private volatile byte STATE;
	private int PLE;

	private final Random renge;
	private int lossChance;

	private final InetAddress ip;
	private final int port;
	private final Redirector redirector;
	private final Object readNote;
	private volatile BitSet receptionCheck;
	private volatile byte[] receptionBuffer;
	private volatile int deliveredNum;
	private volatile int receivedNum;

	private volatile int ackedNum;

	private volatile int blockingNum;

	private volatile boolean threshBust;
	private volatile int congWin;
	private volatile int congThreshold;
	private int congAccu;
	private final Object congLock;

	private volatile int sentNum;
	private volatile int availableNum;
	private final Object writeNote;
	private final Object transmissionNote;
	private final Object transmissionLock;
	private volatile byte[] transmissionBuffer;
	private final LinkedBlockingQueue<RetransmitInfo> retransmissionList;

	private volatile long lifeSign;

	private boolean firstRun;
	private volatile boolean waiting;
	private byte duplicates;
	private volatile int ackTrack;
	private volatile long lastTime;
	private volatile long RTT;
	private volatile long devRTT;

	private final Object adminNote;
	private Thread transmissionThread;
	private Thread retransThread;
	private final Thread trueAdministrator;
	private final Thread receiver;
	
	final LinkedBlockingQueue<byte[]> recList;

	ALTCPSocket(Redirector R, InetAddress ip, int port) throws IOException {

		this.STATE = SYN_SENT;
		this.PLE = 0;
		
		this.receptionLock = new Object();

		this.renge = new Random(System.nanoTime());
		this.lossChance = 0;

		this.ip = ip;
		this.port = port;
		this.redirector = R;

		this.readNote = new Object();
		this.receptionCheck = new BitSet(BASE_WINDOW_SIZE);
		this.receptionBuffer = new byte[BASE_WINDOW_SIZE];
		this.deliveredNum = 0;
		this.receivedNum = 0;

		this.ackedNum = renge.nextInt();

		this.blockingNum = BASE_WINDOW_SIZE;

		this.congWin = 16*MAXIMUM_SEGMENT_SIZE;
		this.congThreshold = Integer.MAX_VALUE;
		this.congAccu = 0;
		this.congLock = new Object();

		this.writeNote = new Object();
		this.transmissionNote = new Object();
		this.sentNum = this.ackedNum;
		this.availableNum = this.ackedNum;
		this.transmissionBuffer = new byte[BASE_WINDOW_SIZE];
		this.transmissionLock = new Object();
		this.retransmissionList = new LinkedBlockingQueue<RetransmitInfo>();

		this.firstRun = true;
		this.waiting = true;
		this.duplicates = 0;
		this.ackTrack = 0;
		this.lastTime = System.nanoTime();
		this.RTT = 150;
		this.devRTT = 35;

		this.lifeSign = this.lastTime;

		this.adminNote = new Object();
		this.transmissionThread = null;
		this.retransThread = null;

		this.recList = redirector.internalConnect(this, ip, port);
		this.receiver = new Thread(new ReceptionProcessor(this, recList));
		this.trueAdministrator = new Thread(new TrueAdministrator(this));
		this.receiver.start();
		this.trueAdministrator.start();
	}

	//this is a REDIRECTOR-EXCLUSIVE constructor, necessary to effectively implement the LISTEN state
	ALTCPSocket(Redirector R, InetAddress ip, int port, byte[] firstPack) throws IOException {
		this.STATE = SYN_SENT;
		this.PLE = 0;
		
		this.receptionLock = new Object();

		this.renge = new Random(System.nanoTime());
		this.lossChance = 0;

		this.ip = ip;
		this.port = port;
		this.redirector = R;

		this.readNote = new Object();
		this.receptionCheck = new BitSet(BASE_WINDOW_SIZE);
		this.receptionBuffer = new byte[BASE_WINDOW_SIZE];
		this.deliveredNum = 0;
		this.receivedNum = 0;

		this.ackedNum = renge.nextInt();

		this.blockingNum = BASE_WINDOW_SIZE;

		this.congWin = 16*MAXIMUM_SEGMENT_SIZE;
		this.congThreshold = Integer.MAX_VALUE;
		this.congAccu = 0;
		this.congLock = new Object();

		this.writeNote = new Object();
		this.transmissionNote = new Object();
		this.sentNum = this.ackedNum;
		this.availableNum = this.ackedNum;
		this.transmissionBuffer = new byte[BASE_WINDOW_SIZE];
		this.transmissionLock = new Object();
		this.retransmissionList = new LinkedBlockingQueue<RetransmitInfo>();

		this.firstRun = true;
		this.waiting = true;
		this.duplicates = 0;
		this.ackTrack = 0;
		this.lastTime = System.nanoTime();
		this.RTT = 150;
		this.devRTT = 35;

		this.lifeSign = this.lastTime;

		this.adminNote = new Object();
		this.transmissionThread = null;
		this.retransThread = null;

		int ackNum = bytesToInt(firstPack, 4, 4);
		int seqNum = bytesToInt(firstPack, 0, 4);
		int temp = bytesToInt(firstPack, 8, 4);
		int blkNum = ackNum + (temp & 0x1FFFFFFF);
		int CTL = ((temp & 0xE0000000) >>> 29);

		//System.out.println(this + " SEQ: " + seqNum + "; ACK: " + ackNum + "; WIN: " + (blkNum - ackNum) + "; CTL: " + CTL);

		lifeSign = System.nanoTime();
		switch (CTL) {
		case SYN:
			receivedNum = seqNum;
			deliveredNum = seqNum;
			blockingNum = blkNum;
			STATE = SYN_RECEIVED;
			break;
		case SYNACK:
			updateRTT();
			receivedNum = seqNum;
			deliveredNum = seqNum;
			blockingNum = blkNum;
			STATE = ESTABILISHED;

			byte[] ack = new byte[12];
			standardEncapsulation(ack);
			redirector.send(ack, ip, port);

			transmissionThread = new Thread(new Transmitter(this));
			retransThread = new Thread(new Retransmitter(this));
			transmissionThread.start();retransThread.start();
			break;
		case CLOSE:
			try { close(); } catch (Exception e1) {}
			if (STATE == CLOSE_SENT) {
				STATE = CLOSE_RECEIVED;
			}
		default:
			break;
		}


		this.recList = redirector.internalConnect(this, ip, port);
		this.receiver = new Thread(new ReceptionProcessor(this, recList));
		this.trueAdministrator = new Thread(new TrueAdministrator(this));
		this.receiver.start();
		this.trueAdministrator.start();
	}

	private class TrueAdministrator implements Runnable {
		private ALTCPSocket sock;
		private int failures;

		public TrueAdministrator(ALTCPSocket sock){
			this.sock = sock;
			this.failures = 0;
		}

		public void run() {
			try {
				while(STATE != CLOSED){
					synchronized (adminNote) {
						switch (STATE){
						case SYN_SENT:
							byte[] syn = new byte[12];
							intIntoBytes(syn, 0, sentNum);
							sock.redirector.send(syn, sock.ip, sock.port);
							sock.lastTime = System.nanoTime();
							try { adminNote.wait(250 + 250*failures); failures++; } catch (InterruptedException e) {}
							continue;
						case SYN_RECEIVED:
							byte[] synack = new byte[12];
							standardEncapsulation(synack);
							synack[8] = (byte) ((SYNACK << 5) | (synack[8] & 0x1F));
							sock.redirector.send(synack, sock.ip, sock.port);
							sock.lastTime = System.nanoTime();
							try { adminNote.wait(500); } catch (InterruptedException e) {}
							continue;
						case ESTABILISHED:
						case SLOW_CLOSE:
							long nt = System.nanoTime();
							if (3000000000L <= nt - sock.lifeSign) {				//4 seconds (4 billion nanos) between life queries
								if(22000000000L <= (nt - sock.lifeSign)) {			//22 seconds (22 billion nanos) before considered dead
									byte[] close = new byte[12];
									close[8] = (byte) (CLOSE << 5);
									sock.redirector.send(close, sock.ip, sock.port);
									try{ finish(); } catch (Exception e){}
									continue;
								} else {
									byte[] ruthere = new byte[12];
									standardEncapsulation(ruthere);
									ruthere[8] = (byte) ((LIFEQ << 5) | (ruthere[8] & 0x1F));

									sock.ackTrack = sock.ackedNum;
									sock.lastTime = nt;
									sock.waiting = true;
									sock.redirector.send(ruthere, sock.ip, sock.port);
								}
								try { adminNote.wait(3000); } catch (InterruptedException e) {}
							} else {
								try { adminNote.wait((sock.lifeSign - nt + 3000000000L)/1000000, (int) ((sock.lifeSign - nt + 3000000000L) % 1000000)); } catch (InterruptedException e) {}
							}
							continue;
						case CLOSE_SENT:
						case CLOSE_RECEIVED:
							try{
								for (int resends = 0; STATE < CLOSED && resends < 7; ++resends) {
									byte[] close = new byte[12];
									close[8] = (byte) (CLOSE << 5);
									sock.redirector.send(close, sock.ip, sock.port);
									adminNote.wait(1000);
								}
							} catch (InterruptedException e) {}
							finish();
							return;
						}
					}
				}
			}catch (SocketException e) {

			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} 
	}

	private class Transmitter implements Runnable {
		private ALTCPSocket sock;

		public Transmitter (ALTCPSocket sock) {
			this.sock = sock;
		}

		public void run() {
			while(STATE == ESTABILISHED || STATE == SLOW_CLOSE){
				try {
					synchronized (transmissionNote) {
						int sent = sock.sentNum;
						int acked = sock.ackedNum;
						int cong = sock.congWin;

						if (0 < sock.availableNum - sent && sent - acked < cong && 0 < sock.blockingNum - sent) {
							int block = sock.blockingNum;
							int available = sock.availableNum;
							int packSize;

							if(available - sent < MAXIMUM_SEGMENT_SIZE) {
								packSize = available - sent;
							} else {
								packSize = MAXIMUM_SEGMENT_SIZE;
							}

							if (packSize > cong - (sent - acked)) {
								packSize = cong - (sent - acked);
							}

							if(packSize > block - sent) {
								packSize = block - sent;
							}

							byte[] sendMe = new byte[packSize + 12];
							int SN = sent;
							int AN = sent + packSize;

							if ((sent & (sock.transmissionBuffer.length - 1)) < (available & (sock.transmissionBuffer.length - 1)) || (sent & (sock.transmissionBuffer.length - 1)) + packSize <= sock.transmissionBuffer.length) {
								System.arraycopy(sock.transmissionBuffer, sent & (sock.transmissionBuffer.length - 1), sendMe, 12, packSize);
							} else {
								System.arraycopy(sock.transmissionBuffer, sent & (sock.transmissionBuffer.length - 1), sendMe, 12, sock.transmissionBuffer.length - (sent & (sock.transmissionBuffer.length - 1)));
								System.arraycopy(sock.transmissionBuffer, 0, sendMe, 12 + sock.transmissionBuffer.length - (sent & (sock.transmissionBuffer.length - 1)), packSize - (sock.transmissionBuffer.length - (sent & (sock.transmissionBuffer.length - 1))));
							}

							synchronized (transmissionLock) {
								if (0 < AN - sock.sentNum)
									sock.sentNum = AN;
							}

							sock.standardEncapsulation(sendMe, SN);
							try {
								sock.redirector.send(sendMe, sock.ip, sock.port);
							} catch (IOException e) {
								try {
									sock.close();
								} catch (Exception e1) {
									e1.printStackTrace();
								}
							}

							if (!sock.waiting || lastTime + 500000000 < System.nanoTime()) {
								sock.ackTrack = AN;
								sock.lastTime = System.nanoTime();
								sock.waiting = true;
							}

							sock.retransmissionList.put(new RetransmitInfo(SN, sendMe, sock.getTimeout()));
						} else {
							sock.transmissionNote.wait(1000);
						}
					}
				} catch (InterruptedException e) {}
			} 
		}
	}

	private class Retransmitter implements Runnable {
		private ALTCPSocket sock;

		public Retransmitter (ALTCPSocket sock) {
			this.sock = sock;
		}

		public void run() {
			while(STATE == ESTABILISHED || STATE == SLOW_CLOSE){
				try {
					RetransmitInfo ri = sock.retransmissionList.take();
					long nt = System.nanoTime();

					if (0 < ri.time - nt) {
						Thread.sleep((ri.time - nt)/1000000,(int) ((ri.time - nt)%1000000));
					}

					nt = System.nanoTime();
					int ack = sock.ackedNum;

					if (0 < ri.seqNum + ri.data.length - 12 - ack) {
						ri.time = nt + sock.getTimeout() + 10000000;
						sock.retransmissionList.put(ri);

						if(ri.seqNum + ri.data.length - ack <= sock.congWin) {
							PLE++;
							RTT += RTT>>8;
							devRTT += devRTT>>8;
							standardEncapsulation(ri.data, ri.seqNum);
							try {
								
								sock.redirector.send(ri.data, sock.ip, sock.port);
							} catch (IOException e) {
								try {
									sock.close();
								} catch (Exception e1) {
									e1.printStackTrace();
								}
							}
		
							if(sock.lastTime + sock.getTimeout() < nt) {
								sock.lastTime = nt;
								sock.ackTrack = ri.seqNum + ri.data.length;
								sock.waiting = true;
							}
		
							synchronized (congLock) {
								sock.congThreshold = congWin/2;
								sock.congThreshold -= sock.congThreshold%MAXIMUM_SEGMENT_SIZE;
								if (congThreshold < MAXIMUM_SEGMENT_SIZE*16)
									congThreshold = MAXIMUM_SEGMENT_SIZE*16;
								sock.congWin = 16*MAXIMUM_SEGMENT_SIZE;
								sock.threshBust = false;
							}
						}
					}
				} catch (InterruptedException e) {}
			}
		}
	}

	private class RetransmitInfo {
		public long time;
		public int seqNum;
		public byte[] data;

		public RetransmitInfo(int seq, byte[] data, long timeout) {
			this.seqNum = seq;
			this.data = data;

			this.time = System.nanoTime() + timeout;
			//FIX THIS LATER
		}
	}

	private long bytesToLong(byte[] b, int a, int c) {
		long result = 0;
		for (int i = a; i < a + c; i++) {
			result <<= 8;
			result |= (b[i] & 0xFF);
		}
		return result;
	}

	private int bytesToInt(byte[] b, int a, int c) {
		int result = 0;
		for (int i = a; i < a + c; i++) {
			result <<= 8;
			result |= (b[i] & 0xFF);
		}
		return result;
	}

	private void longIntoBytes (byte[] b, int a, long l) {
		for (int i = 0; i < 8; i++) {
			b[a + i] = (byte) (l >> (56 - (i << 3)));
		}
	}

	private void intIntoBytes(byte[] b, int a, int k) {
		for (int i = 0; i < 4; i++) {
			b[a + i] = (byte) (k >> (24 - (i << 3)));
		}
	}

	private void updateRTT() {
		if(firstRun) {
			RTT = System.nanoTime() - lastTime;
			devRTT = RTT;
			firstRun = false;
			waiting = false;
		} else {
			if (waiting && 0 <= ackedNum - ackTrack) {
				long diff = (System.nanoTime() - lastTime);
				//if (diff < 50000) diff = 10000000;
				long devDiff = diff - RTT;
				if (devDiff < 0) {
					devDiff = -devDiff;
				}

				devRTT -= devRTT / 4;
				devRTT += devDiff / 4;
				RTT -= RTT / 8;
				RTT += diff / 8;
				waiting = false;
			}
		}
	}

	public long getRTT() {
		return RTT;
	}

	private long getTimeout() {
		return RTT + 4 * devRTT;
	}

	private void standardEncapsulation (byte[] b) {
		intIntoBytes(b, 0, sentNum);

		int rn = receivedNum;
		int dn = deliveredNum;
		intIntoBytes(b, 4, rn);
		intIntoBytes(b, 8, dn + receptionBuffer.length - rn);

		b[8] |= (ALTCPSocket.STANDARD << 5);
	}

	private void standardEncapsulation (byte[] b, int seqNum) {
		intIntoBytes(b, 0, seqNum);

		int rn = receivedNum;
		int dn = deliveredNum;
		intIntoBytes(b, 4, rn);
		intIntoBytes(b, 8, dn + receptionBuffer.length - rn);

		b[8] |= (ALTCPSocket.STANDARD << 5);
	}

	private void inOrder(byte[] segment) {
		synchronized (receptionLock) {
			byte[] ack = new byte[12];
	
			if (receptionBuffer.length < (receivedNum & (receptionBuffer.length - 1)) + segment.length - 12) {
				System.arraycopy(segment, 12, receptionBuffer, receivedNum & (receptionBuffer.length - 1), receptionBuffer.length - (receivedNum & (receptionBuffer.length - 1)));
				receptionCheck.set(receivedNum & (receptionBuffer.length - 1), receptionBuffer.length);
				System.arraycopy(segment, 12 + receptionBuffer.length - (receivedNum & (receptionBuffer.length - 1)), receptionBuffer, 0, segment.length - 12 - (receptionBuffer.length - (receivedNum & (receptionBuffer.length - 1))));
				receptionCheck.set(0, segment.length - 12 - (receptionBuffer.length - (receivedNum & (receptionBuffer.length - 1))));
			} else {
				System.arraycopy(segment, 12, receptionBuffer, receivedNum & (receptionBuffer.length - 1), segment.length - 12);
				receptionCheck.set(receivedNum & (receptionBuffer.length - 1), segment.length - 12 + (receivedNum & (receptionBuffer.length - 1)));
			}
	
			int rn = receivedNum;
			int dn = deliveredNum;
			if (receptionCheck.nextClearBit(rn & (receptionBuffer.length - 1)) == receptionBuffer.length) {
				rn += receptionBuffer.length - (rn & (receptionBuffer.length - 1));
				rn += receptionCheck.nextClearBit(0);
			} else {
				rn += receptionCheck.nextClearBit(rn & (receptionBuffer.length - 1)) - (rn & (receptionBuffer.length - 1));
			}
	
			if (0 > dn + receptionBuffer.length - rn) {
				rn = dn + receptionBuffer.length;
			}
	
			receivedNum = rn;
	
			standardEncapsulation(ack);
			try {
				redirector.send(ack, ip, port);
			} catch (IOException e) {
				try {
					close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
		
		synchronized (readNote) {
			readNote.notifyAll();
		}
	}

	private void outOfOrder(byte[] segment, int seqNum) {
		synchronized (receptionLock) {
		if(0 > receivedNum - seqNum){
			if (receptionBuffer.length < (seqNum & (receptionBuffer.length - 1)) + segment.length - 12) {
				System.arraycopy(segment, 12, receptionBuffer, seqNum & (receptionBuffer.length - 1), receptionBuffer.length - (seqNum & (receptionBuffer.length - 1)));
				receptionCheck.set(seqNum & (receptionBuffer.length - 1), receptionBuffer.length);
				System.arraycopy(segment, 12 + receptionBuffer.length - (seqNum & (receptionBuffer.length - 1)), receptionBuffer, 0, segment.length - 12 - (receptionBuffer.length - (seqNum & (receptionBuffer.length - 1))));
				receptionCheck.set(0, segment.length - 12 - (receptionBuffer.length - (seqNum & (receptionBuffer.length - 1))));

			} else {
				System.arraycopy(segment, 12, receptionBuffer, seqNum & (receptionBuffer.length - 1), segment.length - 12);
				receptionCheck.set(seqNum & (receptionBuffer.length - 1), segment.length - 12 + (seqNum & (receptionBuffer.length - 1)));
			}
		}

		//sack here, if you do implement it

		byte[] ack = new byte[12];
		standardEncapsulation(ack);
		try {
			redirector.send(ack, ip, port);
		} catch (IOException e) {
			try {
				close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		}
	}

	private class ReceptionProcessor implements Runnable {
		private ALTCPSocket sock;
		private LinkedBlockingQueue<byte[]> list;

		public ReceptionProcessor(ALTCPSocket sogget, LinkedBlockingQueue<byte[]> bytes) {
			this.sock = sogget;
			this.list = bytes;
		}

		@Override
		public void run() {
			while(STATE != CLOSED) {
				byte[] segment = null;
				try {
					segment = list.take();
				} catch (InterruptedException e) {continue;}

				if (renge.nextInt(100) < lossChance) {
					continue;
				}

				int seqNum = bytesToInt(segment, 0, 4);
				int ackNum = bytesToInt(segment, 4, 4);
				int temp = bytesToInt(segment, 8, 4);
				int blkNum = ackNum + (temp & 0x1FFFFFFF);
				int CTL = ((temp & 0xE0000000) >>> 29);

				//System.out.println(this + " STATE: " + STATE + " SEQ: " + seqNum + "; ACK: " + ackNum + "; WIN: " + (blkNum - ackNum) + "; CTL: " + CTL);

				lifeSign = System.nanoTime();

				switch (STATE) {	//ALL continue omissions in this block are INTENTIONAL
				case SYN_SENT:
					switch (CTL) {
					case SYN:
						receivedNum = seqNum;
						deliveredNum = seqNum;
						blockingNum = blkNum;
						STATE = SYN_RECEIVED;
						synchronized (adminNote) { adminNote.notifyAll(); }
						continue;
					case SYNACK:
						updateRTT();
						receivedNum = seqNum;
						deliveredNum = seqNum;
						blockingNum = blkNum;
						STATE = ESTABILISHED;
						synchronized (adminNote) { adminNote.notifyAll(); }

						byte[] ack = new byte[12];
						standardEncapsulation(ack);
						try {
							redirector.send(ack, ip, port);
						} catch (IOException e) {
							try {
								close();
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}

						transmissionThread = new Thread(new Transmitter(sock));
						retransThread = new Thread(new Retransmitter(sock));
						transmissionThread.start();retransThread.start();
						continue;
					case CLOSE:
						try { close(); } catch (Exception e1) {}
						if (STATE == CLOSE_SENT) {
							STATE = CLOSE_RECEIVED;
							synchronized (adminNote) { adminNote.notifyAll(); }
						}
					default:
						continue;
					}
				case SYN_RECEIVED:
					switch (CTL) {
					case SYN:
						if(receivedNum!=seqNum)
							reset();
						continue;
					case SYNACK:
						if(receivedNum!=seqNum || sentNum!=ackNum){
							byte[] rst = new byte[12];
							rst[8] = (byte) (RESET << 5);
							try {
								redirector.send(rst, ip, port);
							} catch (IOException e) {
								try {
									close();
								} catch (Exception e1) {
									e1.printStackTrace();
								}
							}
							reset();
						} else {
							STATE = ESTABILISHED;
							synchronized (adminNote) { adminNote.notifyAll(); }

							if(availableNum == sentNum) {
								byte[] ack = new byte[12];
								standardEncapsulation(ack);
								try {
									redirector.send(ack, ip, port);
								} catch (IOException e) {
									try {
										close();
									} catch (Exception e1) {
										e1.printStackTrace();
									}
								}
							}

							transmissionThread = new Thread(new Transmitter(sock));
							retransThread = new Thread(new Retransmitter(sock));
							transmissionThread.start();retransThread.start();
						}
						continue;
					case LIFEQ:
						byte[] ack = new byte[12];
						standardEncapsulation(ack);
						try {
							redirector.send(ack, ip, port);
						} catch (IOException e) {
							try {
								close();
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
					case STANDARD:
						updateRTT();
						STATE = ESTABILISHED;
						synchronized (adminNote) { adminNote.notifyAll(); }

						transmissionThread = new Thread(new Transmitter(sock));
						retransThread = new Thread(new Retransmitter(sock));
						transmissionThread.start();retransThread.start();
						standard(segment, seqNum, ackNum, blkNum);
						continue;

					case RESET:
						reset();
						continue;

					case CLOSE:
						try{ close(); } catch (Exception e){}
						if (STATE == CLOSE_SENT) {
							STATE = CLOSE_RECEIVED;
							synchronized (adminNote) { adminNote.notifyAll(); }
						}
						continue;
					}
				case ESTABILISHED:
				case SLOW_CLOSE:
					switch(CTL){
						case LIFEQ:
						{byte[] ack = new byte[12];
						standardEncapsulation(ack);
						try {
							redirector.send(ack, ip, port);
						} catch (IOException e) {
							try {
								close();
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}}
					case STANDARD:
						standard(segment, seqNum, ackNum, blkNum);
						continue;
					case SYNACK:
						if(receivedNum!=seqNum || sentNum!=ackNum){
							byte[] rst = new byte[12];
							rst[8] = (byte) (RESET << 5);
							try {
								redirector.send(rst, ip, port);
							} catch (IOException e) {
								try {
									close();
								} catch (Exception e1) {
									e1.printStackTrace();
								}
							};
							reset();
						} else {
							byte[] ack = new byte[12];
							standardEncapsulation(ack);
							try {
								redirector.send(ack, ip, port);
							} catch (IOException e) {
								try {
									close();
								} catch (Exception e1) {
									e1.printStackTrace();
								}
							}
						}
						continue;

					case SYN:
					case RESET:
					case CLOSE:
						try{ close(); } catch (Exception e){}
						if (STATE == CLOSE_SENT){
							STATE = CLOSE_RECEIVED;
							synchronized (adminNote) { adminNote.notifyAll(); }
						}
						continue;
					}
				case CLOSE_SENT:
					switch(CTL){
					case CLOSE:
						{byte[] close = new byte[12];
						close[8] = (byte) (CLOSE<<5);
						try {
							redirector.send(close, ip, port);
						} catch (IOException e) {
							try {
								close();
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}}
						synchronized (adminNote) { adminNote.notifyAll(); }
						finish();
						continue;
					default:
						byte[] close = new byte[12];
						close[8] = (byte) (CLOSE<<5);
						try {
							redirector.send(close, ip, port);
						} catch (IOException e) {
							try {
								close();
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
						continue;
					}
				case CLOSE_RECEIVED:
					if(CTL==CLOSE){
						synchronized (adminNote) { adminNote.notifyAll(); }
						finish();
					} else {
						byte[] close = new byte[12];
						close[8] = (byte) (CLOSE<<5);
						try {
							redirector.send(close, ip, port);
						} catch (IOException e) {
							try {
								close();
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
					}
					continue;
				}
			}
		}

	}

	private void standard (byte[] segment, int seqNum, int ackNum, int blkNum) {		
		if(firstRun) {
			RTT = System.nanoTime() - lastTime;
			devRTT = RTT;
			firstRun = false;
			waiting = false;
		} else {
			if (waiting && 0 <= ackedNum - ackTrack) {
				long diff = (System.nanoTime() - lastTime);
				long devDiff = diff - RTT;
				if (devDiff < 0) {
					devDiff = -devDiff;
				}

				devRTT -= devRTT / 4;
				devRTT += devDiff / 4;
				RTT -= RTT / 8;
				RTT += diff / 8;
				waiting = false;
			}
		}

		boolean notifyTransmitter = false;

		if (0 < ackNum - ackedNum) {
			if(!threshBust) {
				synchronized (congLock) {
					congWin += ackNum - ackedNum;
					if (congThreshold <= congWin){
						threshBust = true;
					}
				}
			} else {
				synchronized (congLock) {
					congAccu += ackNum - ackedNum;
					while (congAccu >= congWin) {
						congAccu -= congWin;
						congWin += MAXIMUM_SEGMENT_SIZE;
					}
				}
			}

			duplicates = 0;
			ackedNum = ackNum;

			notifyTransmitter = true;
			synchronized (writeNote) {
				writeNote.notifyAll();
			}
		} else if (ackNum == ackedNum) {
			if (0 < sentNum - ackedNum) {
				duplicates++;
				if (duplicates==3) {
					resend(ackNum);

					synchronized (congLock) {
						congThreshold = (congWin + congWin%2) / 2;
						congThreshold -= congThreshold%MAXIMUM_SEGMENT_SIZE;
						if (congThreshold < MAXIMUM_SEGMENT_SIZE*4)
							congThreshold = MAXIMUM_SEGMENT_SIZE*4;
						congWin = congThreshold;
						threshBust = true;
					}
				}
			}
		}

		if (0 < blkNum - blockingNum) {
			blockingNum = blkNum;
			notifyTransmitter = true;
		}

		if (notifyTransmitter) {
			synchronized (transmissionNote) {
				transmissionNote.notifyAll();
			}
		}

		if (segment.length > 12) {
			if (seqNum == receivedNum) {
				inOrder(segment);
			} else {
				outOfOrder(segment, seqNum);
			}
		}
	}

	private void resend(int seq) {
		PLE++;

		int block = blockingNum;
		int available = availableNum;
		int packSize = -12;
		if(available - seq < MAXIMUM_SEGMENT_SIZE) {
			packSize = available - seq;
		} else {
			packSize = MAXIMUM_SEGMENT_SIZE;
		}

		if(packSize > block - seq) {
			packSize = block - seq;
		}
		byte[] sendMe = new byte[packSize + 12];

		if ((seq & (transmissionBuffer.length - 1)) < (available & (transmissionBuffer.length - 1)) || (seq & (transmissionBuffer.length - 1)) + packSize <= transmissionBuffer.length) {
			System.arraycopy(transmissionBuffer, seq & (transmissionBuffer.length - 1), sendMe, 12, packSize);
		} else {
			System.arraycopy(transmissionBuffer, seq & (transmissionBuffer.length - 1), sendMe, 12, transmissionBuffer.length - (seq & (transmissionBuffer.length - 1)));
			System.arraycopy(transmissionBuffer, 0, sendMe, 12 + transmissionBuffer.length - (seq & (transmissionBuffer.length - 1)), packSize - (transmissionBuffer.length - (seq & (transmissionBuffer.length - 1))));
		}

		synchronized (transmissionLock) {
			if (0 < seq + packSize - sentNum) {
				sentNum = seq + packSize;
			}
		}

		standardEncapsulation(sendMe, seq);
		try {
			redirector.send(sendMe, ip, port);
		} catch (IOException e) {
			try {
				close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		ackTrack = seq + packSize;
		lastTime = System.nanoTime();
		waiting = true;
	}

	/*MULTITHREAD-UNSAFE*/
	//need synchronization for multiple readers
	public int read(byte[] receptacle, int off, int len) throws IOException {
		if (STATE > ESTABILISHED && receivedNum == deliveredNum)
			throw new IOException("EOS");
		int bytesLeft = len;
		
		//System.out.println("read");
		
		if(receivedNum == deliveredNum + receptionBuffer.length) {
			new Thread(new Runnable() {
				public void run() {
					try {
					Thread.sleep(50);
					byte[] b = new byte[12];
					standardEncapsulation(b);
						redirector.send(b, ip, port);
					} catch (IOException e) {
					} catch (InterruptedException e) {
					}
				}
			}).start();
		}

		synchronized (receptionLock) {
			int rn = receivedNum;
			int dn = deliveredNum;
			int limit;
			if (len <= rn - dn) {
				limit = len;
			} else {
				limit = rn - dn;
			}
			boolean wrap = (dn & (receptionBuffer.length - 1)) + limit >= receptionBuffer.length;
			if (wrap) {
				System.arraycopy(receptionBuffer, dn & (receptionBuffer.length - 1), receptacle, off, receptionBuffer.length - (dn & (receptionBuffer.length - 1)));
				receptionCheck.clear(dn & (receptionBuffer.length - 1), receptionBuffer.length);
				limit -= receptionBuffer.length - (dn & (receptionBuffer.length - 1));
				bytesLeft -= receptionBuffer.length - (dn & (receptionBuffer.length - 1));
				dn += (receptionBuffer.length - dn) & (receptionBuffer.length - 1);
	
				System.arraycopy(receptionBuffer, 0, receptacle, off + len - limit, limit);
				receptionCheck.clear(0, limit);
				dn += limit;
				bytesLeft -= limit;
			} else {
				System.arraycopy(receptionBuffer, dn & (receptionBuffer.length - 1), receptacle, off, limit);
				receptionCheck.clear(dn & (receptionBuffer.length - 1), limit + (dn & (receptionBuffer.length - 1)));
				dn += limit;
				bytesLeft -= limit;
			}
			deliveredNum = dn;
		}
		return len - bytesLeft;
	}

	public int read(byte[] receptacle) throws IOException {
		return read(receptacle, 0, receptacle.length);
	}

	public int read() throws InterruptedException, IOException {
		byte[] b = new byte[1];
		while(true){
			synchronized (readNote) {
				int k = read(b,0,1);
				if(k == 1){
					return (int) b[0] & 0xFF;
				}
				readNote.wait(100);
			}
		}
	}

	public String readUTF() throws InterruptedException, IOException {
		byte[] len = new byte[2];

		int bytesRead = 0;
		synchronized (readNote) {
			bytesRead += read(len, bytesRead, 2-bytesRead);
			while (bytesRead < 2) {
				readNote.wait(500);
				bytesRead += read(len, bytesRead, 2-bytesRead);
			}
		}

		int strinlen = (((int)len[0] & 0xFF) << 8) | ((int)len[1] & 0xFF);
		byte[] strin = new byte[strinlen];

		bytesRead = 0;
		synchronized (readNote) {
			bytesRead += read(strin, bytesRead, strinlen-bytesRead);
			while (bytesRead < strinlen) {
				readNote.wait(500);
				bytesRead += read(strin, bytesRead, strinlen-bytesRead);
			}
		}

		return new String(strin, StandardCharsets.UTF_8.name());
	}

	public long readLong() throws InterruptedException, IOException {
		byte[] b = new byte[8];
		byte bytesRead = 0;

		synchronized (readNote) {
			bytesRead += read(b, bytesRead, 8-bytesRead);
			while (bytesRead < 8) {
				readNote.wait(500);
				bytesRead += read(b, bytesRead, 8-bytesRead);
			}
		}
		return bytesToLong(b, 0, 8);
	}

	public int readInt() throws InterruptedException, IOException {
		byte[] b = new byte[4];
		byte bytesRead = 0;

		synchronized (readNote) {
			bytesRead += read(b, bytesRead, 4-bytesRead);
			while (bytesRead < 4) {
				bytesRead += read(b, bytesRead, 4-bytesRead);
				readNote.wait(500);
			}
		}
		return bytesToInt(b, 0, 4);
	}

	/* MULTITHREAD-UNSAFE VERSION */
	//need synchronization for multiple writers
	public void write(byte[] data, int off, int len) throws IOException, InterruptedException {
		while (true) {
			if(STATE > ESTABILISHED) throw new IOException("Socket closed");
			int acked = ackedNum;
			boolean madeIt = false;
			synchronized (writeNote) {
				if(len < acked + transmissionBuffer.length - availableNum) {
					if((acked & (transmissionBuffer.length - 1)) <= (availableNum & (transmissionBuffer.length - 1))) {
						if(len <= transmissionBuffer.length - (availableNum & (transmissionBuffer.length - 1))) {
							System.arraycopy(data, off, transmissionBuffer, availableNum & (transmissionBuffer.length - 1), len);

						} else {
							System.arraycopy(data, off, transmissionBuffer, availableNum & (transmissionBuffer.length - 1), transmissionBuffer.length - (availableNum & (transmissionBuffer.length - 1)));
							System.arraycopy(data, off + transmissionBuffer.length - (availableNum & (transmissionBuffer.length - 1)), transmissionBuffer, 0, len - (transmissionBuffer.length - (availableNum & (transmissionBuffer.length - 1))));

						}
					} else {
						System.arraycopy(data, off, transmissionBuffer, availableNum & (transmissionBuffer.length - 1), len);
					}
					availableNum += len;
					madeIt = true;
				}
				if (madeIt) {
					synchronized (transmissionNote) {
						transmissionNote.notifyAll();
					}
					return;
				}
				writeNote.wait(1000);
			}
		}
	}

	public void write(byte[] data) throws IOException, InterruptedException {
		write(data, 0, data.length);
	}

	public void writeUTF (String s) throws IOException, InterruptedException {
		byte[] strin = s.getBytes(StandardCharsets.UTF_8.name());
		if (strin.length > 65535) throw new IOException("maximum length exceeded");

		byte[] len = new byte[2];
		len[0] = (byte) (strin.length >>> 8);
		len[1] = (byte) strin.length;

		write(len,0,2);
		write(strin,0,strin.length);
	}

	public void writeLong (long l) throws IOException, InterruptedException {
		byte[] b = new byte[8];
		longIntoBytes(b, 0, l);
		write(b);
	}

	public void writeInt (int i) throws IOException, InterruptedException {
		byte[] b = new byte[4];
		intIntoBytes(b, 0, i);
		write(b);
	}

	private void finish() {
		if(STATE == CLOSED) return;
		redirector.disconnect(ip, port);
		STATE = CLOSED;
		synchronized (adminNote) { adminNote.notifyAll(); }
		receiver.interrupt();

		transmissionThread = null;
		retransThread = null;
	}

	private void reset() {
		STATE = SYN_SENT;
		ackedNum = renge.nextInt();
		sentNum = ackedNum;
		deliveredNum = ackedNum;
		synchronized (adminNote) { adminNote.notifyAll(); }

		if(transmissionThread!=null)
			transmissionThread.interrupt();
		if(retransThread!=null)
			retransThread.interrupt();

		try {
			if(transmissionThread!=null)
				transmissionThread.join();
			if(retransThread!=null)
				retransThread.join();
		} catch (Exception e) {}
	}

	public void waitForAck() throws InterruptedException {
		while(STATE < ESTABILISHED) {
			synchronized (transmissionNote) {
				transmissionNote.wait(500);
			}
		}
		if (STATE > SLOW_CLOSE) return;
		synchronized (transmissionNote) {
			while ((0 < (availableNum - ackedNum)) && (STATE == SLOW_CLOSE || STATE == ESTABILISHED))
				transmissionNote.wait(500);
		}
	}

	public void slowClose() throws InterruptedException {
		try {
			while(STATE < ESTABILISHED) {
				synchronized (transmissionNote) {
					transmissionNote.wait(500);
				}
			}
			if (STATE > SLOW_CLOSE) return;

			STATE = SLOW_CLOSE;
			synchronized (adminNote) { adminNote.notifyAll(); }
			synchronized (transmissionNote) {
				while ((0 < (availableNum - ackedNum)) && (STATE == SLOW_CLOSE))
					transmissionNote.wait(500);
			}

			if (STATE == SLOW_CLOSE)
				close();

		} catch (InterruptedException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws Exception {
		if(STATE >= CLOSE_SENT) return;

		byte[] close = new byte[12];
		close[8] = (byte) (CLOSE << 5);
		redirector.send(close, ip, port);

		STATE = CLOSE_SENT;

		if(transmissionThread!=null)
			transmissionThread.interrupt();
		if(retransThread!=null)
			retransThread.interrupt();

		try {
			if(transmissionThread!=null)
				transmissionThread.join();
			if(retransThread!=null)
				retransThread.join();
		} catch (Exception e) {}
	}

	public void setForcedLossChance(int loss) {
		lossChance = loss;

		if (lossChance < 0) {
			lossChance = 0;
		} else if (lossChance > 100) {
			lossChance = 100;
		}
	}

	public int getForcedLossChance() {
		return lossChance;
	}

	public InetAddress getIP(){
		return ip;
	}

	public int getPort(){
		return port;
	}

	public int packetsLost(){
		return PLE;
	}
}