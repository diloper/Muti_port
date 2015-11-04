package com.example.test1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.example.test1.MainActivity.check_timer;

import android.provider.Telephony.Threads;
import android.util.Log;

public class TCP {

	protected boolean isRunning = false;
	private SocketChannel mClientChannel;
	protected Selector mSelector;
	private int MAX_PACKET_SIZE = 1024;
	private InetSocketAddress mRemoteAddress;
	private int port = 5060;
	private String ip;
	private String send_message = "";
	protected SelectionKey ope[] = new SelectionKey[1000];
	// private int finaleport = 1120;
	// private int startport = 1028;
	int o = 0;
	private SimpleDateFormat sdf = new SimpleDateFormat("ss");
	protected int[] arrart4;
	private Object LOCK = new Object();
	private CALLBACK Callback = null;
	protected int currntfinaleport;
	protected int currntstartport;
	protected int Selecter_key;
	protected int register_port1;
	private long send_delay = 50;
	private long finish_wait = 30;

	public TCP(CALLBACK mcontext) {
		// TODO Auto-generated constructor stub
		Callback = mcontext;

	}

	public void setip(String i) {
		ip = i;

	}

	public void setipadport(int p, int f) {
		currntstartport = p;
		currntfinaleport = f;

		// startport = p;
		// finaleport = f;
	}

	Thread getThread(String strCashireID) {
		ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
		Thread[] threads = new Thread[threadGroup.activeCount()];
		threadGroup.enumerate(threads);

		for (int nIndex = 0; nIndex < threads.length; nIndex++)

		{
			if (threads[nIndex] != null && threads[nIndex].getName().equals(strCashireID))
				return threads[nIndex];
		}

		return null;

	}

	public interface CALLBACK {
		void NioUDPClient_RecvByte(int[] arrart4);

		void process(int p);

	}

	private void Log(String tag, int contenet) {
		Log.e(new String("" + tag), new String("" + contenet));
	}

	private void Log(String tag, String contenet) {
		Log.e(new String("" + tag), new String("" + contenet));
	}

	private void Log(int tag, int contenet) {

		Log.e(new String("" + tag), new String("" + contenet));
	}

	private Date date2;
	private int three_hand_count = 0;
	private int check_time = 0;

	public class check_int extends TimerTask {
		@Override
		public void run() {
			check_time = arrart4.length;
			Log(" arrart4.length", arrart4.length);
			for (int jd = 0; arrart4.length > jd; jd++) {
				Log(" check_time", check_time);

				if (arrart4[jd] == 0) {
					Log("arrart", jd);
					continue;
				} else
					check_time--;
			}
			Log(" check_time", check_time);
			if (check_time <= 0) {
				check_int.cancel();
				check_int = null;
				Log.e("check_int", " check_int finish");
				send_counter = 0;
				new Thread(sendMessageProcess).start();

			}
		}
	}

	public class cancle extends TimerTask {
		@Override
		public void run() {
			Log("Callback", "Callback");

			Thread R = getThread("TT");
			if (R != null) {
				// tcp.mSelector.wakeup();
				R.interrupt();
				// R.stop();
				Log.e("R", "R");
				isRunning = false;
			}
			Callback.NioUDPClient_RecvByte(arrart4);
			cnacle.cancel();
			cnacle = null;
			// check_int.cancel();

		}
	}

	public class three_hand extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			date2 = new Date();
			// 設定日期格式

			int itself = Integer.valueOf(sdf.format(date2));
			three_hand_count++;
			// Log("three_hand-finishConnection_q", itself -
			// finishConnection_q);
			// Log("time", finishConnection_q);
			if (Math.abs(itself - finishConnection_q) > 1) {
				Log("Start", "sendMessageProcess");
				send_counter = 0;
				three_hand.cancel();
				three_hand = null;
				// new Thread(sendMessageProcess).start();

			} else if (three_hand_count > 7) {
				Log("Start", "sendMessageProcess");
				send_counter = 0;
				three_hand.cancel();
				three_hand = null;
				// new Thread(sendMessageProcess).start();
			}

		}

	};

	private Timer check_int = null;
	private Timer three_hand = null;
	// start udp client process
	public Runnable startTCPClientProcess = new Runnable() {

		private int conut = 0;
		private Set<SelectionKey> mIterators;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {

				if (arrart4 == null) {
					int g2 = currntfinaleport - currntstartport + 1;
					// Log.e("currntfinaleport", new String("" + g2));
					arrart4 = new int[currntfinaleport - currntstartport + 1];

					// mRemoteAddress = new InetSocketAddress(ip, port);
					ope = new SelectionKey[currntfinaleport - currntstartport + 1];

					try {

						mSelector = Selector.open();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						Log.e(TCP, "Selector opening  fail.");
						e1.printStackTrace();
					}
				}
				for (int jd = 0; arrart4.length > jd; jd++)
					arrart4[jd] = 0;
				// Arrays.fill(arrart4, false);

				// mSelector.wakeup();
				Log("currntfinaleport", currntstartport + "---" + currntfinaleport);
				for (register_port1 = currntstartport; register_port1 <= currntfinaleport; register_port1++) {
					try {
						// Log.e("register", new String("" + register_port1));

						mClientChannel = SocketChannel.open();
						mClientChannel.configureBlocking(false);
						mClientChannel.socket().setReuseAddress(true);
						// SocketAddress address = new
						// InetSocketAddress(register_port1);

						// mClientChannel.socket().bind(address);
						mRemoteAddress = new InetSocketAddress(ip, register_port1);
						Callback.process(register_port1);

						mClientChannel.connect(mRemoteAddress);
					} catch (IOException e) {
						e.printStackTrace();
						Log.e(TCP, " channel opening fail.");
					}
					// this.pendingChanges.add(new ChangeRequest(socketChannel,
					// ChangeRequest.REGISTER, SelectionKey.OP_CONNECT));

					ope[Selecter_key] = mClientChannel.register(mSelector, SelectionKey.OP_READ);
					Selecter_key++;
					// Log.e("Selecter_key", new String("" + Selecter_key));
					// mClientChannel.keyFor(selector)
				}
				if (check_int == null) {
					check_int = new Timer("check_int");
					check_int.schedule(new check_int(), 500, 500);
				}
				finishconet_counter = 0;
				three_hand_count = 0;
				// new Thread(finishProcess).start();
				// Log.e(TCP, new String("" + arrart.length));
				finishConnection_q = 0;
				// if (three_hand == null) {
				// three_hand = new Timer("three_hand");
				// three_hand.schedule(new three_hand(), 500, 1000 * 2);
				// }
				// int p=arrart.length;
				// the selector select the channel event and receive message
				ByteBuffer mReceiveBuffer = ByteBuffer.allocate(MAX_PACKET_SIZE);
				finishConnection();
				// check_tcpthreehand()
				while (isRunning) {
					try {
						// Thread.sleep(10);
						// Log("isRunning in", "");

						mSelector.select();
						SocketChannel mChannel;

						// synchronized (LOCK) {
						// android.util.Log.e("ReceiveLOCK", "ReceiveLOCK");
						// if (mSelector.selectNow() == 0)
						// continue;
						// for (Element element : new
						// ArrayList<Element>(mElements))
						// ArrayList mIterator = new ArrayList();

						// arlist[]
						// Iterator<SelectionKey> mIterator =
						// mSelector.selectedKeys().iterator();

						// mIterators = new
						// Object[mSelector.selectedKeys().size()];
						// mSelector.selectedKeys().toArray(mIterators);

						mIterators = mSelector.selectedKeys();
						if (mIterators.size() > 0) {
							Log("LOCK selectedKeys", "selectedKeys");
							for (SelectionKey mKey : mIterators) {
								// System.out.println(temp);
								Log("LOCK", "");
								if (!mKey.isValid()) {
									Log.e("UDP", "mKey.isValid");
									mKey.cancel();
									mKey.channel().close();
									continue;

								}
								// SelectionKey mKey = (SelectionKey)
								// mIterators[o];
								// conut = mSelector.selectedKeys().size();
								// while (mIterator.hasNext()) {
								// SelectionKey mKey =
								// mIterator.next();mIterator.remove();
								mChannel = (SocketChannel) mKey.channel();

								if (mKey.isReadable() && mChannel.isConnected()) {
									mReceiveBuffer.clear();

									int len = mChannel.read(mReceiveBuffer);
									if (len > 0) {
										int localport = mChannel.socket().getPort();

										if ((localport - currntstartport) >= 0) {
											arrart4[localport - currntstartport] = 1;
											Log.e(" not overflow",
													new String("" + localport + "-----" + currntstartport));
										} else
											Log.e("overflow", new String("" + localport + "-----" + currntstartport));

										if (mChannel.isRegistered()) {
											mChannel.keyFor(mSelector).cancel();
										}
										if (!mChannel.socket().isClosed()) {

											mChannel.socket().close();

										}

									}
								}
							}

						} else
							continue;
						// }
						// }

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.e(TCP, "The selector selects fail.");
					} catch (CancelledKeyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			} catch (ClosedChannelException c) {
				c.printStackTrace();
				Log.e(TCP, "Channel registering fail.");
				isRunning = false;
				return;
			}

		}
	};
	//

	protected int send_counter;
	private Timer cnacle = null;
	// send the message
	private Runnable sendMessageProcess = new Runnable() {
		int i = 0;
		// boolean r = true;

		@Override
		public void run() {
			Log(send_counter, Selecter_key);
			while (send_counter < Selecter_key) {
				try {
					Log("sendMessageProcess", send_counter);
					mClientChannel = (SocketChannel) ope[send_counter].channel();
					send_counter++;
					if (mClientChannel.isConnected()) {

						int por = mClientChannel.socket().getLocalPort();
						// Log("sendMessageProcess", send_counter);
						String v = new String("TCP______________" + Integer.toString(por));
						// v = v  + "_" + Integer.toString(count + 1);

						ByteBuffer buf = ByteBuffer.wrap(v.getBytes());
						ByteBuffer mSendBuffer = ByteBuffer.allocate(MAX_PACKET_SIZE);
						mSendBuffer.put(buf);
						mSendBuffer.flip();

						android.util.Log.e("send", "send");
						int size = mClientChannel.write(mSendBuffer);

						Thread.sleep(send_delay);
						// if (size == 0) {
						// mClientChannel.keyFor(mSelector).interestOps(
						// mClientChannel.keyFor(mSelector).interestOps() ^
						// SelectionKey.OP_WRITE);
						// // Log.e(TCP, "ByteBuffer is full.");
						// }

					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e(TCP, "Channel is already closed.");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// cal_time--;
			// new Thread(checkfailport).start();
			Selecter_key = 0;
			if (cnacle == null) {
				cnacle = new Timer("check_int");
				cnacle.schedule(new cancle(), 1000 * 3);
			}
		}

	};

	private String TCP = "TCP connect";
	private int finishconet_counter = 0;

	private int finishConnection_q;

	private void finishConnection() throws ClosedByInterruptException {
		// TODO Auto-generated method stub
		int send_counter = 0;
		int localport = 0;
		SocketChannel mClientChanne;
		// Log.e("finishConnection", "finishConnection");
		while (send_counter < Selecter_key) {
			// sdf.format(date);

			mClientChanne = (SocketChannel) ope[send_counter].channel();
			//send_counter++;

			// Date date = new Date();
			// 設定日期格式
			// Log("generated", "finishConnection");
			// finishConnection_q = Integer.valueOf(sdf.format(date));
			// Log("time", finishConnection_q);
			// SocketChannel socketChannel = (SocketChannel) mKey.channel();

			// Finish the connection. If the connection operation failed
			// this will raise an IOException.

			// SocketAddress s =
			// mClientChanne.socket().getRemoteSocketAddress();
			try {
				if (mClientChanne.finishConnect()) {
					localport = mClientChanne.socket().getPort();
					// int localport = Integer.valueOf(s);
					Log.e("finishConnect", new String("" + localport));
					arrart4[localport - currntstartport] = 1;
					// String v = new String("TCP_" +
					// Integer.toString(localport));
					// // v = v + "_" + Integer.toString(count + 1);
					//
					// ByteBuffer buf = ByteBuffer.wrap(v.getBytes());
					// ByteBuffer mSendBuffer =
					// ByteBuffer.allocate(MAX_PACKET_SIZE);
					// mSendBuffer.put(buf);
					// mSendBuffer.flip();

					// int size = mClientChanne.write(mSendBuffer);

				} else {

					Log.e("finishConnect fail", new String("" + localport));
					arrart4[send_counter] = -2;
				}

				// int por = socketChannel.socket().getLocalPort();
				// Log("sendMessageProcess", send_counter);

				/// android.util.Log.e("sendLOCK", "sendLOCK");
				send_counter++;
				// }
				// Log.e(TCP, "TCP finishConncet ");
			} catch (IOException e) {
				arrart4[send_counter] = -2;
				// Cancel the channel's registration with our selector
				// System.out.println(e);
				// Log.e(TCP, "TCP finishConncet Fail");
				// mKey.cancel();
				// selector.close();
				return;
			}
			// Log.e("finishconet", new String("" + finishconet_counter));
			// finishconet_counter++;
			// Log.e(TCP, "finishconet_counter");
			// Register an interest in writing on this channel
			// mKey.interestOps(SelectionKey.OP_READ |
			// SelectionKey.OP_WRITE);
			try {
				Thread.sleep(finish_wait);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// Selecter_key = 0;
		// if (finishconet_counter == arrart.length) {
		// Log.e("sendMessageProcess", new String("" + sendMessageProcess));
		// send_counter = 0;
		// new Thread(sendMessageProcess).start();
		//
		// }
	}

	public void close_object() {
		// TODO Auto-generated method stub
		// mSelector.wakeup();
		// isRunning = false;
		ope = null;
		arrart4 = null;
		// if (mSelector.isOpen()) {
		// try {
		// mSelector.close();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// mSelector = null;
		// }
		Log.e("finish", "finish");
	}

}