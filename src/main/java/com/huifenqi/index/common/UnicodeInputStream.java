
package com.huifenqi.index.common;

/**
BOMs:
  00 00 FE FF    = UTF-32, big-endian
  FF FE 00 00    = UTF-32, little-endian
  EF BB BF       = UTF-8,
  FE FF          = UTF-16, big-endian
  FF FE          = UTF-16, little-endian
***/

import java.io.*;
import java.nio.charset.Charset;
/**
 * @author majianchun
 *
 */
public class UnicodeInputStream extends InputStream {
	PushbackInputStream internalIn;
	boolean isInited = false;
	String defaultEnc;
	String encoding;

	private static final int BOM_SIZE = 4;

	public UnicodeInputStream(InputStream in, String defaultEnc) {
		internalIn = new PushbackInputStream(in, BOM_SIZE);
		this.defaultEnc = defaultEnc;
	}
	
	public UnicodeInputStream(InputStream in) {
		internalIn = new PushbackInputStream(in, BOM_SIZE);
		this.defaultEnc = Charset.defaultCharset().name();
	}
	
	public UnicodeInputStream(File file, String defaultEnc) throws FileNotFoundException {
		if(null == file || !file.exists())
			throw new FileNotFoundException();
		InputStream in = new FileInputStream(file);
		internalIn = new PushbackInputStream(in, BOM_SIZE);
		this.defaultEnc = defaultEnc;
	}
	
	public UnicodeInputStream(File file) throws FileNotFoundException {
		if(null == file || !file.exists())
			throw new FileNotFoundException();
		InputStream in = new FileInputStream(file);
		internalIn = new PushbackInputStream(in, BOM_SIZE);
		this.defaultEnc = Charset.defaultCharset().name();
	}
	
	public UnicodeInputStream(String fileName, String defaultEnc) throws FileNotFoundException
	{
		this(new File(fileName),defaultEnc);
	}
	
	public UnicodeInputStream(String fileName) throws FileNotFoundException
	{
		this(new File(fileName));
	}
	
	public String getDefaultEncoding() {
		return defaultEnc;
	}

	public String getEncoding() {
		if (!isInited) {
			try {
				init();
			} catch (IOException ex) {
				IllegalStateException ise = new IllegalStateException(
						"Init method failed.");
				ise.initCause(ise);
				throw ise;
			}
		}
		return encoding;
	}

	/**
	 * Read-ahead four bytes and check for BOM marks. Extra bytes are unread
	 * back to the stream, only BOM bytes are skipped.
	 */
	protected void init() throws IOException {
		if (isInited)
			return;

		byte bom[] = new byte[BOM_SIZE];
		int n, unread;
		n = internalIn.read(bom, 0, bom.length);

		if ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00)
				&& (bom[2] == (byte) 0xFE) && (bom[3] == (byte) 0xFF)) {
			encoding = "UTF-32BE";
			unread = n - 4;
		} else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)
				&& (bom[2] == (byte) 0x00) && (bom[3] == (byte) 0x00)) {
			encoding = "UTF-32LE";
			unread = n - 4;
		} else if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB)
				&& (bom[2] == (byte) 0xBF)) {
			encoding = "UTF-8";
			unread = n - 3;
		} else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF)) {
			encoding = "UTF-16BE";
			unread = n - 2;
		} else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)) {
			encoding = "UTF-16LE";
			unread = n - 2;
		} else {
			// Unicode BOM mark not found, unread all bytes
			encoding = defaultEnc;
			unread = n;
		}

		if (unread > 0)
			internalIn.unread(bom, (n - unread), unread);

		isInited = true;
	}

	public void close() throws IOException {
		isInited = true;
		internalIn.close();
	}

	public int read() throws IOException {
		isInited = true;
		return internalIn.read();
	}
}

