package com.example.test1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.StrictMode;
import android.provider.Telephony.Threads;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import untility.encrypy_xor;

public class MainActivity extends Activity implements OnClickListener, TCP.CALLBACK {
	private boolean UCP_final = false;
	private TextView tvDisplay;
	private EditText strPort, etIP, fiport;
	private Button btStartOrClose;
	private boolean isRunning = false;
	private DatagramChannel mClientChannel;
	private Selector mSelector;
	private int MAX_PACKET_SIZE = 1024;
	private InetSocketAddress mRemoteAddress;
	//private int port = 5060;
	private String ip = "0.0.0.0";
	private String send_message = "";
	private SelectionKey ope[];
	private int finaleport;
	private int startport;
	private int currntstartport;
	private int currntfinaleport;
	private RadioGroup radGrp;
	private int Minport = 1023;
	private int Maxport = 65536;
	private Boolean[] arrart = null;
	private int selector_limter = 12;
	private TCP tcp;
	private Timer checkUDP = null;
	private Timer checkTCP = null;
	private encrypy_xor enc;
	protected boolean show = true;
	private Thread UT = null;
	private Thread TT = null;
	private Object LOCKA = new Object();
	private int register_port1;
	private int Selecter_key;
	private boolean creatfiel_flag = true;
	private float total_times;
	private ProgressBar progressbar1;
	private TextView per;
	private boolean autoflag = false;
	protected long send_delay = 120;
	private WakeLock mWakeLock = null;
	private WifiLock mWifiLock = null;

	private int totalport;
	private int cal_time;

	private static Context mContext = null;
	private TCPClient o;
	private int io = 4000;
	private Button ipset;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		// initial UI component
		strPort = (EditText) findViewById(R.id.xml_etPort);
		etIP = (EditText) findViewById(R.id.xml_etIP);
		fiport = (EditText) findViewById(R.id.xml_etPort2);
		btStartOrClose = (Button) findViewById(R.id.xml_btStartOrClose);

		btStartOrClose.setOnClickListener(this);
		tcp = new TCP(this);
		creatfiel = null;
		progressbar1 = (ProgressBar) findViewById(R.id.progressBar1);
		progressbar1.setMax(100); // 設定最大值是100
		per = (TextView) findViewById(R.id.per);
		per.setText("");
		// progressbar1.setProgress(50); // 設定目前進度0
		radGrp = (RadioGroup) findViewById(R.id.radioGroup1);
		ipset = (Button) findViewById(R.id.ipset);
		ipset.setOnClickListener(this);
		InitialWifiLock();
		AcquireWifiLock();
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		ipset.setVisibility(View.INVISIBLE);
	}

	private void ReleaseWifiLock() {
		Log.e("ReleaseWifiLock", "123");

		// Release wifi lock
		if (mWakeLock.isHeld())
			mWakeLock.release();
		// Release wake lock
		if (mWifiLock.isHeld())
			mWifiLock.release();

	}

	private void AcquireWifiLock() {
		Log.e("AcquireWifiLock", "123");
		// Acquire wake lock
		if (!mWakeLock.isHeld())
			mWakeLock.acquire();
		// Acquire wifi lock
		if (!mWifiLock.isHeld())
			mWifiLock.acquire();
	}

	public void InitialWifiLock() {
		Log.e("InitialWifiLock", "123");
		if (mContext == null) {
			mContext = this;
			// Create a wake lock
			Log.e("mContext == null", "123");
			PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
			mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "NTILWakeLock");
			mWakeLock.setReferenceCounted(true);
			// Create a wifi lock
			WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
			mWifiLock = wifiManager.createWifiLock("NTILWifiLock");
			mWifiLock.setReferenceCounted(true);

		}
	}

	private void close_file() {
		int ps, POs = 0, lsd;
		long last = 0;
		String temp = "", real;
		// Log.e("onDestroy", "onDestroy");
		ByteBuffer buffer = ByteBuffer.allocate(40);
		MessageDigest alga = null;

		List<String> data = new ArrayList<String>();
		try {

			inChannel.position(0);
			long dd = fos.length();
			enc = new encrypy_xor();

			byte[] digesta = null;

			try {
				alga = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while (inChannel.read(buffer) != -1) {
				alga.update(buffer);
				buffer.flip();
				String ss = Charset.defaultCharset().decode(buffer).toString();
				ps = ss.indexOf("\r\n");
				if (ps < 0) {
					temp = temp + ss;
				} else {

					real = temp + ss.substring(0, ps + 2);

					temp = ss.substring(ps + 2);
					data.add(real);

				}
				buffer.clear();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String dd;
		byte[] digesta = alga.digest();
		enc.setSEED(enc.byte2hex(digesta));
		try {
			inChannel.position(0);
			dd = enc.getSEED() + "acgem";
			buffer = ByteBuffer.allocate(dd.getBytes().length);
			buffer.put(dd.getBytes());
			buffer.flip();
			inChannel.write(buffer);

			buffer.clear();
			Iterator<String> it = data.iterator();
			while (it.hasNext()) {

				dd = enc.encrypt(it.next());
				// int i = (int) (Math.random() * 26);
				// i = 97 + i;
				// char ch = (char) i;
				dd = dd + "acgem";

				buffer = ByteBuffer.allocate(dd.getBytes().length);
				buffer.put(dd.getBytes());
				buffer.flip();

				inChannel.write(buffer);

				buffer.clear();
				// buffer1.flip();
			}

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			inChannel.close();
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		// ClientThread.interrupted();
		Log.e("onDestroy", "onDestroy");
		if (isRunning)
			isRunning = false;
		super.onDestroy();
		mContext = null;
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ipset:
			if (etIP.getText().toString().contains("192.168.7.71"))
				etIP.setText("54.199.71.207");
			else
				etIP.setText("192.168.7.71");
			break;
		case R.id.xml_btStartOrClose:
			if (etIP.getText().length() != 0 && strPort.getText().length() != 0) {
				ip = etIP.getText().toString();
				startport = Integer.valueOf(strPort.getText().toString());
				finaleport = Integer.valueOf(fiport.getText().toString());
				if (Maxport >= finaleport && Minport < startport && finaleport >= startport)

					if (!isRunning) {
						inntiall();
						btStartOrClose.setClickable(false);
						btStartOrClose.setText("Close");
						System.gc();
						if (mSelector != null) {
							if (mSelector.isOpen())
								try {
									Log("mSelector", "mSelector close");
									mSelector.close();
									mSelector = null;
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

						}
						// TCP_thread_start(ip, port,finaleport);
						totalport = finaleport - startport + 1;
						// recycle times
						// cal_re>> remainder
						cal_time = totalport / selector_limter;
						// int cal_re = totalport % selector_limter;
						if (cal_time == 1)
							cal_time--;
						// cal_time--;
						// else
						// cal_time++;
						// total_times = cal_time;
						intiprocessbar();
						// Start_udp_port_test(startport, startport +
						// selector_limter);
						switch (radGrp.getCheckedRadioButtonId()) {
						case R.id.TCP:

							tcp.setip(ip);
							per.setText("TCP Working...");
							if (cal_time == 0)
								TCP_thread_start(startport, finaleport);
							else

								TCP_thread_start(startport, startport + selector_limter);

							break;
						case R.id.UDP:

							per.setText("UDP Working...");
							if (cal_time == 0)
								Start_udp_port_test(startport, finaleport);
							else
								Start_udp_port_test(startport, startport + selector_limter);
							// UT = new Thread(startUDPClientProcess);
							// UT.setDaemon(true);
							// UT.setName("UT");
							// UT.start();
							break;
						case R.id.Auto:
							per.setText("Auto Working...");
							if (cal_time == 0)
								Start_udp_port_test(startport, finaleport);
							else
								// Start_udp_port_test(startport, startport +
								// selector_limter);
								Start_udp_port_test(startport, startport + selector_limter);
							// UT = new Thread(startUDPClientProcess);
							// UT.start();
							autoflag = true;
							total_times = total_times * 2;
							break;

						}
						radGrp.setEnabled(false);
						// if (UT == null) {
						// UT = new Thread(startUDPClientProcess);
						// UT.start();
						// }
					} else {
						btStartOrClose.setText("Start");
						// stop udp client process
						isRunning = false;
						//
					}
			} else {
				tvDisplay.setText("");
				if (Maxport < finaleport)
					tvDisplay.setText("over 65536");
				else if (Minport > startport)
					tvDisplay.setText("lower than 1024");
				else if (finaleport > startport)
					tvDisplay.setText(" finaleport lower than startport");
			}
			break;

		default:
			break;
		}
	}

	private void intiprocessbar() {
		// TODO Auto-generated method stub
		progressbar1.setProgress(0);
		per.setText("");
	}

	private void auto() {
		// TODO Auto-generated method stub

		UdptoTcp = true;
		// currntfinaleport=0;
		// int ramg = finaleport - startport + 1;
		// cal_time = ramg / selector_limter;
		// cal_re = ramg % selector_limter;
		// if (cal_re == 0)
		// cal_time--;
		// else
		// cal_time++;
		// total_times = cal_time;

		Message msg = Message.obtain();
		msg.what = 2;
		// msg.setData(bundle);
		mHandler.sendMessage(msg);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (cal_time == 0)
			TCP_thread_start(startport, finaleport);
		else

			// TCP_thread_start(startport, startport + selector_limter);

			TCP_thread_start(startport, startport + selector_limter);
	}

	private void TCP_thread_start(int po, int fianl) {
		currntstartport = po;
		currntfinaleport = fianl;
		tcp.arrart4 = null;
		tcp.setip(ip);
		tcp.setipadport(po, fianl);
		// try {
		// Thread.sleep(1000);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
		// Thread[] threads = new Thread[threadGroup.activeCount()];
		// threadGroup.enumerate(threads);
		// for (int nIndex = 0; nIndex < threads.length; nIndex++) {
		// if (threads[nIndex] != null &&
		// threads[nIndex].getName().equals("TT")) {
		// threads[nIndex].interrupt();
		// Log.e("stopthreads", "stopthreads");
		// }
		// }

		if (TT == null) {
			TT = new Thread(tcp.startTCPClientProcess);
			TT.setDaemon(true);
			TT.setName("TT");
			tcp.isRunning = true;
			TT.start();
		}
	}

	private void Start_udp_port_test(int strp, int finprt) {
		Selecter_key = 0;
		register_port1 = 0;

		currntstartport = strp;
		currntfinaleport = finprt;
		isRunning = true;
		UT = null;
		if (UT == null) {

			UT = new Thread(startUDPClientProcess);
			UT.setDaemon(true);
			UT.setName("UT");
			UT.start();

		}
	}

	private void inntiall() {
		Selecter_key = 0;
	}

	// start udp client process
	private Runnable startUDPClientProcess = new Runnable() {

		private Set<SelectionKey> mIterators;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {

				Log.e("start", "SelectionKey");

				if (arrart == null) {
					int g2 = currntfinaleport - currntstartport + 1;
					Log.e("currntfinaleport", new String("" + g2));
					arrart = new Boolean[currntfinaleport - currntstartport + 1];

					// mRemoteAddress = new InetSocketAddress(ip, port);
					ope = new SelectionKey[currntfinaleport - currntstartport + 1];

					try {

						mSelector = Selector.open();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						Log.e("show", "Selector opening  fail.");

						e1.printStackTrace();
					}
				}
				Arrays.fill(arrart, false);

				// Log.e("port", new String("" + port1));
				for (register_port1 = currntstartport; register_port1 <= currntfinaleport; register_port1++) {
					try {
						// Log.e("port1", new String("" + port1));
						mClientChannel = DatagramChannel.open();
						mClientChannel.configureBlocking(false);
						mClientChannel.socket().setReuseAddress(true);
						mRemoteAddress = new InetSocketAddress(ip, register_port1);
					//	SocketAddress address = new InetSocketAddress(register_port1);
						// Log.e("sdfsd", new String("" + port));
					//	mClientChannel.socket().bind(address);
						mClientChannel.connect(mRemoteAddress);
						showProcessbar(register_port1);
					} catch (IOException e) {
						e.printStackTrace();
//						if (show) {
							int op = register_port1 - currntstartport;
							Log.e("show", " channel opening fail." + new String("" + op));
							// show = false;
//						}
					}
					// Log.e("register", new String("" + o));
					ope[Selecter_key] = mClientChannel.register(mSelector, SelectionKey.OP_READ);
					Selecter_key++;

				}
				if (mRemoteAddress == null)
					return;
				send_counter = 0;
				new Thread(sendMessageProcess).start();

				ByteBuffer mReceiveBuffer = ByteBuffer.allocate(MAX_PACKET_SIZE);
				while (isRunning) {

					try {
						DatagramChannel mChannel;
						mSelector.select();
						// synchronized (LOCKA) {

						// Iterator<SelectionKey> mIterator =
						// mSelector.selectedKeys().iterator();
						// Log.e("SelectionKey isRunning", "SelectionKey");
						mIterators = mSelector.selectedKeys();
						if (mIterators.size() > 0) {
							// Log("LOCK selectedKeys", "selectedKeys");
							for (SelectionKey mKey : mIterators) {

								// mIterator.remove();

								if (mKey.isReadable()) {
									mReceiveBuffer.clear();
									mChannel = (DatagramChannel) mKey.channel();
									int len = mChannel.read(mReceiveBuffer);
									// Log.e("isReadable", "0");
									if (len > 0) {
										// Log.e("isReadable",
										// "isReadable");
										// mReceiveBuffer.flip();
										// int mSize =
										// mReceiveBuffer.limit();
										// byte[] mMessageBuffer = new
										// byte[mSize];
										// mReceiveBuffer.get(mMessageBuffer);
										final int number = mChannel.socket().getPort();
										// display the message which is
										// received
										// int op = number -
										// currntstartport;

										if ((number - currntstartport) <= arrart.length) {
											arrart[number - currntstartport] = true;
//											Log.e(" not overflow", new String("" + number + "-----" + currntstartport));
										} else
											Log.e("overflow", new String("" + number + "-" + currntstartport));
										if (mChannel.isRegistered()) {
											mChannel.keyFor(mSelector).cancel();
										}
										if (!mChannel.socket().isClosed()) {
											mChannel.socket().disconnect();
											mChannel.socket().close();
										}

									}
								}

							}
						} else
							continue;
						// }
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.e("NIOUDP_Client", "The selector selects fail.");
					} catch (CancelledKeyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (UnsupportedOperationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				// close channel and selector
				// try {
				// mClientChannel.keyFor(mSelector).cancel();
				// mClientChannel.disconnect();
				// mClientChannel.socket().close();
				// mClientChannel.close();
				// mSelector.close();
				// mSelector = null;
				// mClientChannel = null;
				// } catch (IOException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// Log.e("NIOUDP_Client", "Close channel fail.");
				// }

				//
			} catch (ClosedChannelException c) {
				c.printStackTrace();
				Log.e("NIOUDP_Client", "Channel registering fail.");
				isRunning = false;
				return;
			}
			//
		}
	};
	// private int occ = 0;
	private int send_counter;

	//
	private void creat_file() {
		if (creatfiel == null) {
			Log.e("creat_file", "creat_file");
			open();
			String path = sdDir.getPath() + "/Network_test_log";

			// 目前時間
			date = new Date();
			// 設定日期格式
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// 進行轉換
			String dateString = sdf.format(date);

			wallpaperDirectory = new File(path);
			// have the object build the directory structure, if needed.
			if (!wallpaperDirectory.exists())
				wallpaperDirectory.mkdirs();

			// MediaScannerConnection.scanFile(this, new String[]
			// {path.toString()}, null, null);
			// MediaScannerConnection.scanFile(getApplicationContext(), new
			// String[] {wallpaperDirectory.toString()}, null, null);
			// create a File object for the output file
			File outputFile = new File(wallpaperDirectory,
					dateString + "start port" + startport + "--final port" + finaleport + ".txt");
			// now attach the OutputStream to the file object, instead of a
			// String representation

			try {
				fos = new RandomAccessFile(outputFile, "rw");

				inChannel = fos.getChannel();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				// toast(save_fail, e.getMessage());
				e.printStackTrace();
			}

			// creatfiel = new openfilepath_create_file();
			// creatfiel.start();
		}

	}

	// send the message
	private Runnable sendMessageProcess = new Runnable() {

		// boolean r = true;

		@Override
		public void run() {
			Log.e("sendMessageProcess", "sendMessageProcess");
			// Log.e("sendMessageProcess", new String("g=" + g + "occ=" + occ));
			while (send_counter < Selecter_key) {
				try {

					mClientChannel = (DatagramChannel) ope[send_counter].channel();
					if (mClientChannel.isConnected()) {
						// Log.e("sendMessageProcess", "isRegistered");
						int por = mClientChannel.socket().getLocalPort();
						// mClientChannel.socket().getRemoteSocketAddress();
						String v = new String("UDP________________________________________________________________________________" + Integer.toString(por));
						// v = v + "_" + Integer.toString(count + 1);

						ByteBuffer buf = ByteBuffer.wrap(v.getBytes());
						ByteBuffer mSendBuffer = ByteBuffer.allocate(MAX_PACKET_SIZE);
						mSendBuffer.put(buf);
						mSendBuffer.flip();

						int size = mClientChannel.send(mSendBuffer, mClientChannel.socket().getRemoteSocketAddress());

						// if (size == 0) {
						// mClientChannel.keyFor(mSelector)
						// .interestOps(mClientChannel.keyFor(mSelector).interestOps()
						// ^ SelectionKey.OP_READ);
						// // Log.e("NIOUDP_Client", "ByteBuffer is full.");
						// }
						Thread.sleep(send_delay);
						send_counter++;

					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("NIOUDP_Client", "Channel is already closed.");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			checkUDP = new Timer("checkUDP");
			checkUDP.schedule(new check_timer(checkUDP), 3000);

		}
	};
	private File sdDir;
	private RandomAccessFile fos;
	private FileChannel inChannel;
	private Date date;
	private File wallpaperDirectory;
	private openfilepath_create_file creatfiel;
	private SimpleDateFormat sdf;
	private int[] arrart1;
	private boolean UdptoTcp;
	private float base;
	public boolean TCP_final = false;

	public class check_timer extends TimerTask {

		private boolean useflag = false;

		check_timer(int[] ar, Timer T) {
			this.useflag = true;
			arrart1 = new int[ar.length];
			arrart1 = ar;
			Thread R = getThread("TT");
			if (R != null) {
				// tcp.mSelector.wakeup();
				R.interrupt();
				// R.stop();
				Log.e("TT", "TT");
				tcp.isRunning = false;

			}
			// t = T
			// t = T;
		}

		check_timer(Timer T) {
			this.useflag = false;
			Thread R = getThread("UT");
			if (R != null) {
				// tcp.mSelector.wakeup();
				R.interrupt();
				// R.stop();
				Log.e("UT", "UT");
				isRunning = false;

			}
			// t = T;
		}

		@Override
		public void run() {
			SocketChannel mChannel;
			DatagramChannel mChannel1;
			// TODO 自動產生的方法 Stub
			// Stop recev information
			if (isRunning) {
				UT.interrupt();
				isRunning = false;
				UT = null;

			}

			if (creatfiel_flag) {
				creat_file();
				creatfiel_flag = false;
			}
			if (this.useflag) {

				// tcp.isRunning = false;
				// Thread R = getThread("TT");
				// if (R != null) {
				// // tcp.mSelector.wakeup();
				// R.interrupt();
				// // R.stop();
				// Log.e("R", "R");
				// tcp.isRunning = false;
				// TT = null;
				// }
				if (TT != null) {
					TT.interrupt();
					tcp.isRunning = false;

					TT = null;
					// tcp.close_object();
				}
				// Log.e("checking", new String("" + arrart1.length));
				for (int e1 = 0; arrart1.length > e1; e1++) {
					int failport = e1 + tcp.currntstartport;
					if (arrart1[e1] == 0) {
						savefile1((new String("TCP " + failport)).getBytes());

					}
					mChannel = (SocketChannel) tcp.ope[e1].channel();

					if (mChannel.isConnected()) {
						try {
							mChannel.socket().close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					if (mChannel.isRegistered())
						mChannel.keyFor(tcp.mSelector).cancel();
					// Log.e("checking", "checking");
				}
				if (currntfinaleport <= finaleport) {

					if (currntfinaleport == finaleport)
						close_object_tcp();
					else {
						if ((currntfinaleport + 1 + selector_limter) >= finaleport) {

							if (!TCP_final) {
								// tcp.mSelector.wakeup();
								Log.e("final", "final");
								Log.e("TCP_thread_start", new String(""));
								TCP_thread_start(currntfinaleport + 1, finaleport);
								TCP_final = true;
							}

						} else {

							// Log.e("TCP_thread_start", new String("" +
							// tcp.currntfinaleport));

							TCP_thread_start(currntfinaleport + 1, currntfinaleport + 1 + selector_limter);
						}
					}

					// showProcessbar();
					checkTCP.cancel();
					checkTCP = null;

					// Log.e("cal_time", new String("" + cal_time));

				}

			} else

			{

				Log.e("check", new String("check"));
				for (int e = 0; arrart.length > e; e++) {
					int failport = e + currntstartport;
					if (arrart[e] == false) {
						savefile1((new String("UDP " + failport)).getBytes());

					}
					mChannel1 = (DatagramChannel) ope[e].channel();

					if (mChannel1.isConnected()) {
						mChannel1.socket().disconnect();
					}
					if (mChannel1.isRegistered())
						mChannel1.keyFor(mSelector).cancel();
				}
				if (currntfinaleport <= finaleport) {

					if (currntfinaleport == finaleport) {
						close_object_ucp();
					} else {
						// Log.e("cancle", "cancle");
						arrart = null;
						// mRemoteAddress = null;
						ope = null;
						if (UT != null) {
							// UT.interrupt();
							isRunning = false;
							UT = null;
						}
						Log.e("next", new String("" + currntfinaleport));
						if ((currntfinaleport + 1 + selector_limter) >= finaleport) {
							if (!UCP_final) {
								Start_udp_port_test(currntfinaleport + 1, finaleport);
								UCP_final = true;
							}
						} else {
							// Log.e("currntfinaleport", new String("" +
							// currntfinaleport));
							Start_udp_port_test(currntfinaleport + 1, currntfinaleport + 1 + selector_limter);
						}

						// mChannel = null;
					}
					// Log.e("Running",
					// new String("" + currntfinaleport + 1 + " !!!" +
					// currntfinaleport + 1 + selector_limter));
					//
					// Log.e("Running", new String("" + cal_time));
					// cal_time--;
					// showProcessbar();
					checkUDP.cancel();
					checkUDP = null;

				}

			}

			System.gc();
		}

	}

	private void savefile1(byte[] meg) {
		date = new Date();
		// 設定日期格式
		sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
		// 進行轉換
		String dateString = sdf.format(date) + "  ";

		String doc2 = null;
		try {

			doc2 = new String(meg, "UTF-8");

		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String file = dateString + doc2 + "\r\n";

		// String kk = enc.encrypt(file);

		int p = file.getBytes().length;
		// /int p = kk.getBytes().length + dd.getBytes().length;
		ByteBuffer buf = ByteBuffer.allocate(p);
		buf.clear();
		// 目前時間
		// date = new Date();
		// // 設定日期格式
		// sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// // 進行轉換
		// String dateString = sdf.format(date);
		// dateString += dateString + meg;

		// buf.put(dateString.getBytes());

		buf.put(file.getBytes());
		// buf.put("\r\n".getBytes());
		// Log.e("meg", new String(" " + meg.length +
		// dateString.getBytes().length));
		buf.flip();

		while (buf.hasRemaining()) {
			try {
				inChannel.write(buf);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// mHandler.obtainMessage(save_fail, e).sendToTarget();
				// Log.e("savefile", "failure");
				e.printStackTrace();

			}
		}
		// Log.e("savefile", "save done");

	}

	public void showProcessbar(int i) {

		// TODO Auto-generated method stub

		float l, dd, j, currentp;
		l = i;

		j = startport;

		if (autoflag) {
			if (UdptoTcp) {
				currentp = (float) (l - j + 1) / (totalport * 2);
				currentp += 0.5;
				Log.e("persm", new String("currentp" + currentp));
			} else
				currentp = (l - j + 1) / (totalport * 2);
		} else
			currentp = (l - j + 1) / totalport;

		Message msg = Message.obtain();
		msg.what = 1;
		// Log.e("persm", new String("l" + l));

		Bundle bundle = new Bundle();
		bundle.putFloat("s", currentp * 100);
		msg.setData(bundle);
		mHandler.sendMessage(msg);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:

				float paa = msg.getData().getFloat("s");
				// Log.e("poss", new String("" + paa));

				progressbar1.setProgress((int) paa);
				String s = String.valueOf(paa);
				if (paa < 100)
					per.setText(s.substring(0, s.indexOf(".") + 2) + "%...");
				else {
					if (!per.getText().toString().contains("分析中"))

						per.append("分析中....");

				}
				break;
			case 2:
				per.append(" TCP");
				// per.setText("Working");
				break;
			case 3:
				per.setText("finish");
				show_AlertDialog();
				break;
			}
		}
	};

	public void close_object_tcp() {
		// TODO Auto-generated method stub

		if (TT != null) {
			if (tcp.isRunning)
				tcp.isRunning = false;
			TT.interrupt();
			TT = null;
		}
		tcp.close_object();
		UdptoTcp = false;
		autoflag = false;
		TCP_final = false;
		// tcp.isRunning = false;
		// TT.interrupt();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				btStartOrClose.setText("Start");
				btStartOrClose.setClickable(true);
			}
		});
		Message msg = Message.obtain();
		msg.what = 3;
		mHandler.sendMessage(msg);

	}

	private void show_AlertDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("詢問");
		dialog.setMessage("使否存檔");
		dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub

			}

		});
		dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub

				close_file();
				creatfiel_flag = true;
				creatfiel = null;
			}

		});

		dialog.show();
	}

	private void close_object_ucp() {
		// TODO Auto-generated method stub
		Log.e("test1", "test1");
		mSelector.wakeup();
		UCP_final = false;
		// isRunning = false;
		if (UT != null) {
			// UT.interrupt();
			isRunning = false;
			UT = null;
		}
		//
		// UT.interrupt();
		// display the message which is received
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				btStartOrClose.setText("Start");
				btStartOrClose.setClickable(true);
			}
		});
		// Log.e("UDP", "close ");
		ope = null;
		arrart = null;
		if (mSelector.isOpen()) {
			try {
				mSelector.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mSelector = null;

		}
		Log.e("UDP finish", "UDP finish");
		System.gc();
		if (autoflag)
			auto();
		else {
			Message msg = Message.obtain();
			msg.what = 3;
			mHandler.sendMessage(msg);
		}

	}

	private void open() {
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

		if (sdCardExist)
			sdDir = Environment.getExternalStorageDirectory();
		else
			sdDir = Environment.getDataDirectory();

	}

	class openfilepath_create_file extends Thread {
		@Override
		public void run() {
			open();
			String path = sdDir.getPath() + "/Network_test_log";

			// 目前時間
			date = new Date();
			// 設定日期格式
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// 進行轉換
			String dateString = sdf.format(date);

			wallpaperDirectory = new File(path);
			// have the object build the directory structure, if needed.
			if (!wallpaperDirectory.exists())
				wallpaperDirectory.mkdirs();

			// MediaScannerConnection.scanFile(this, new String[]
			// {path.toString()}, null, null);
			// MediaScannerConnection.scanFile(getApplicationContext(), new
			// String[] {wallpaperDirectory.toString()}, null, null);
			// create a File object for the output file
			File outputFile = new File(wallpaperDirectory, dateString + ".txt");
			// now attach the OutputStream to the file object, instead of a
			// String representation

			try {
				fos = new RandomAccessFile(outputFile, "rw");

				inChannel = fos.getChannel();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				// toast(save_fail, e.getMessage());
				e.printStackTrace();
			}

		}

	}

	//
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

	@Override
	public void NioUDPClient_RecvByte(int[] packet) {

		// TODO Auto-generated method stub
		if (checkTCP == null) {
			checkTCP = new Timer("checkTCP");
			checkTCP.schedule(new check_timer(packet, checkTCP), 0);

		}
	}

	@Override
	public void process(int p) {
		// TODO Auto-generated method stub
		showProcessbar(p);
	}
}