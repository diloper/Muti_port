package untility;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class encrypy_xor {
	private static final int RADIX = 16;
	private static final int UTF_8 = 0;
	private String SEED = "0933910847463829232312312";
	private byte[] l;

	public void setSEED(String s) {
		this.SEED = s;
	}

	public String encrypt(String password) {
		if (password == null)
			return "";
		if (password.length() == 0)
			return "";

		BigInteger bi_passwd = new BigInteger(password.getBytes());

		BigInteger bi_r0 = new BigInteger(SEED);
		BigInteger bi_r1 = bi_r0.xor(bi_passwd);

		return bi_r1.toString(RADIX);
	}

	public String encrypt1(byte[] password) {
		if (password == null)
			return "";
		if (password.length == 0)
			return "";

		BigInteger bi_passwd = new BigInteger(password);

		BigInteger bi_r0 = new BigInteger(SEED);
		BigInteger bi_r1 = bi_r0.xor(bi_passwd);

		return bi_r1.toString(RADIX);
	}

	public String decrypt(String encrypted) {
		if (encrypted == null)
			return "";
		if (encrypted.length() == 0)
			return "";

		BigInteger bi_confuse = new BigInteger(SEED);

		try {
			BigInteger bi_r1 = new BigInteger(encrypted, RADIX);
			BigInteger bi_r0 = bi_r1.xor(bi_confuse);

			return new String(bi_r0.toByteArray());
		} catch (Exception e) {
			return "";
		}
	}

	public String getSEED() {
		return this.SEED;
	}

	public String byte2hex(byte[] b) // 二行制转字符串
	{
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			int i = (int) b[n];

			if (i < 0)
				i = i * -1;

			hs = hs + new String("" + i);

		}

		return hs;
	}

}