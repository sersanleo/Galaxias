package src.sersanleo.galaxies.util;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ExtFileInputStream extends FileInputStream {
	public ExtFileInputStream(String arg0) throws FileNotFoundException {
		super(arg0);
	}

	public ExtFileInputStream(File arg0) throws FileNotFoundException {
		super(arg0);
	}

	public ExtFileInputStream(FileDescriptor arg0) {
		super(arg0);
	}

	public final long readLong() throws IOException {
		byte[] bytes = new byte[8];
		this.read(bytes);
		return ByteBuffer.wrap(bytes).getLong();
	}

	public final int readInt() throws IOException {
		byte[] bytes = new byte[4];
		this.read(bytes);
		return ByteBuffer.wrap(bytes).getInt();
	}

	public final Vector2i readVector2i() throws IOException {
		return new Vector2i(readInt(), readInt());
	}

	public final float readFloat() throws IOException {
		return Float.intBitsToFloat(readInt());
	}

	public final boolean readBoolean() throws IOException {
		return read() != 0;
	}

	public final String readString() throws IOException {
		byte[] bytes = new byte[readInt()];
		read(bytes);
		return new String(bytes, ExtFileOutputStream.CHARSET);
	}
}