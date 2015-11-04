package untility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

public class Sdcontrol {
	private File sdDir;
	private boolean sdCardExist;
	private File wallpaperDirectory;
	private FileChannel FileChannel;

	class Create_documentary extends Thread {
		String documentary;

		Create_documentary(String doc) {
			this.documentary = doc;
		}

		@Override
		public void run() {

			String path = sdDir.getPath() + "/" + "documentary";

			wallpaperDirectory = new File(path);
			// have the object build the directory structure, if needed.
			if (!wallpaperDirectory.exists())
				wallpaperDirectory.mkdirs();
			// create a File object for the output file

		}

	}

	class savefile extends Thread {
		byte[] meg = new byte[100];

		savefile(byte[] m) {
			this.meg = m;
		}

		@Override
		public void run() {
			ByteBuffer buf = ByteBuffer.allocate(meg.length);
			buf.clear();
			Log.e("meg", new String("" + meg.length));
			buf.put(meg);
			buf.flip();
			while (buf.hasRemaining()) {
				try {
					FileChannel.write(buf);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.e("savefile", "failure");
					e.printStackTrace();
				}
			}
			Log.e("savefile", "save done");
		}
	}

	class Create_file extends Thread {
		String file_name;
		String file_type;

		Create_file(String file_name) {
			this.file_name = file_name;
			this.file_type = ".txt";
		}

		Create_file(String file_name, String file_type) {
			this.file_name = file_name;
			this.file_type = file_type;
		}

		@Override
		public void run() {
			File outputFile = null;
			try {
				outputFile = new File(wallpaperDirectory, file_name + file_type);
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			// now attach the OutputStream to the file object, instead of a
			// String representation

			try {
				FileOutputStream fos = new FileOutputStream(outputFile);
				FileChannel = fos.getChannel();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				// toast(save_fail, e.getMessage());
				e.printStackTrace();
			}
		}

	}

	/**
	 * return getcurrentdate on the phone
	 */

	public String getcurrentdate() {
		Date date = new Date();
		// 設定日期格式
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 進行轉換
		String dateString = sdf.format(date);
		return dateString;
	}

	/**
	 * Open sd, retrun the path
	 * 
	 * @param x
	 *            1:inneral sd ;2:external
	 * @return File
	 */
	public File open(int x) {

		switch (x) {
		case 1:
			// internal
			sdDir = Environment.getDataDirectory();
			break;
		case 2:
			// exteranl
			if (sdCardExist)
				sdDir = Environment.getExternalStorageDirectory();
			else
				sdDir = Environment.getDataDirectory();
			break;

		}

		return Sdcontrol.this.sdDir = sdDir;

	}

	public boolean sd_staus() {
		sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		return sdCardExist;

	}

}
