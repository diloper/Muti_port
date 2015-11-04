package com.example.test1;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class TCPClient {

	TCPClient(int pot) {
		this.bindport = pot;
	}

	// 定義檢測SocketChannel的Selector物件
	private Selector selector = null;
	private int bindport;
	// 客户端SocketChannel
	private SocketChannel socketchannel = null;
	private String Server_ip = "192.168.7.71";
	private int Server_port = 5060;
	private String Address;
	private int Port, receiveread, saveread, readcount;
	public boolean runing = false;
	private ClientThread gg;

	public void Client() throws IOException {

		selector = Selector.open();

		InetSocketAddress inetsocketaddress = new InetSocketAddress(Server_ip, Server_port);

		// 調用open靜態方法創建連接到指定主機的SocketChannel
		// 打開Socket通道並將其連接到遠端位址。
		socketchannel = SocketChannel.open();
		socketchannel.socket().setReuseAddress(true);
		// 設置該socketchannel以非阻塞方式工作
		socketchannel.configureBlocking(false);

		// 將SocketChannel對象註冊到指定Selector
		socketchannel.register(selector, SelectionKey.OP_READ);
		SocketAddress port = new InetSocketAddress(bindport);
		socketchannel.socket().bind(port);
		socketchannel.connect(inetsocketaddress);
		
		runing = true;

		gg = new ClientThread();
		gg.start();
		socketchannel.finishConnect();
		// Log.e("finishConnect", "finishConnect");
		// 啟動讀取伺服器端資料的執行緒
		
		new Thread(sendMessageProcess).start();

	}

	public void stop() {
		runing = false;
		if (gg.isAlive())
			gg.interrupt();
		if (selector.isOpen())
			try {
				selector.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	@SuppressLint("NewApi")
	public boolean send(String str) {

		int o = 0;
		// try {
		// // if (socketchannel.finishConnect())
		// // o = socketchannel.write();
		//
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		if (o > 0)

			return true;
		else
			return false;
	}

	private Runnable sendMessageProcess = new Runnable() {
		int i = 0;
		// boolean r = true;

		@Override
		public void run() {

			if (socketchannel.isConnected()) {

				int por = socketchannel.socket().getLocalPort();
				// Log("sendMessageProcess", send_counter);
				String v = new String("TCP______________" + Integer.toString(por));
				// v = v + "_" + Integer.toString(count + 1);

				ByteBuffer buf = ByteBuffer.wrap(v.getBytes());
				ByteBuffer mSendBuffer = ByteBuffer.allocate(110);
				mSendBuffer.put(buf);
				mSendBuffer.flip();
				Log.e("send", "send");

				try {
					int size = socketchannel.write(mSendBuffer);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else
				Log.e("finishConnect fail", "finishConnect fail");

		}

	};

	// 定義讀取伺服器資料的執行緒
	private class ClientThread extends Thread {

		int SelecterCount = 0;

		@SuppressLint("NewApi")
		public void run() {
			try {

				// select()，執行處於阻塞網要的選擇操作。僅在至少選擇一個通道、調用此選擇器的 wakeup
				// 方法，或者當前的執行緒已中斷（以先到者為準）後此方法才返回。
				while (runing)
					while ((SelecterCount = selector.select()) > 0) {

						// 遍歷每個有可用IO操作Channel對應的SelectionKey
						for (SelectionKey selectionkey : selector.selectedKeys()) {

							// 刪除正在處理的SelectionKey
							selector.selectedKeys().remove(selectionkey);

							// 如果該SelectionKey對應的Channel中有可讀的資料
							if (selectionkey.isReadable()) {

								SocketChannel socketchannel = (SocketChannel) selectionkey.channel();
								ByteBuffer bytebuffer = ByteBuffer.allocate(1024);
								String Content = "";

								// read()讀取資料並存入可用的緩衝空間(用剩餘的空間來存取)，當空間滿時，則回傳0，對方關閉時則回傳-1。
								while ((receiveread = socketchannel.read(bytebuffer)) > 0) {
									++readcount;
									saveread = receiveread;
									socketchannel.read(bytebuffer);
									bytebuffer.flip();
									Content += StandardCharsets.UTF_8.decode(bytebuffer);
									System.out.println("Readcount : " + readcount);
								}
								if (receiveread != -1) {
									readcount = 0;
									Address = socketchannel.socket().getLocalAddress().toString();
									Port = socketchannel.socket().getLocalPort();
									System.out.println("SelecterCount : " + SelecterCount);
									System.out.println("訊息由   : " + Address + " : " + Port + " 接收");
									Address = socketchannel.socket().getInetAddress().toString();
									Port = socketchannel.socket().getPort();
									System.out.println("Server的位址 : " + Address + " : " + Port);
									// 列印輸出讀取的內容

									System.out.println("確認接收 : " + Content + " 收到 : " + saveread + " 個 byte");
								} else {
									selectionkey.cancel();
									if (selectionkey.channel() != null) {
										selectionkey.channel().close();
										System.out.println("Server Channel 已關閉");
									}
								}
							}
						}
					}
			} catch (IOException ioexception) {
				System.out.println("錯誤訊息 : " + ioexception.getMessage());
			}
		}
	}

	/*
	 * public static void main(String[] args) throws IOException { new
	 * TCPClient().Client(); }
	 */
}
