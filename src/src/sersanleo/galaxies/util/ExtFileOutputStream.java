package src.sersanleo.galaxies.util;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class ExtFileOutputStream extends FileOutputStream {
	public static final Charset CHARSET = Charset.forName("UTF-8");

	public ExtFileOutputStream(String name) throws FileNotFoundException {
		super(name);
	}

	public ExtFileOutputStream(File file) throws FileNotFoundException {
		super(file);
	}

	public ExtFileOutputStream(FileDescriptor fdObj) {
		super(fdObj);
	}

	public ExtFileOutputStream(String name, boolean append) throws FileNotFoundException {
		super(name, append);
	}

	public ExtFileOutputStream(File file, boolean append) throws FileNotFoundException {
		super(file, append);
	}

	public final void writeLong(long i) throws IOException {
		this.write(new byte[] { (byte) (i >>> 56), (byte) (i >>> 48), (byte) (i >>> 40), (byte) (i >>> 32),
				(byte) (i >>> 24), (byte) (i >>> 16), (byte) (i >>> 8), (byte) i });
	}

	public final void writeInt(int i) throws IOException {
		this.write(new byte[] { (byte) (i >>> 24), (byte) (i >>> 16), (byte) (i >>> 8), (byte) i });
	}

	public final void writeVector2i(Vector2i v) throws IOException {
		this.writeInt(v.x);
		this.writeInt(v.y);
	}

	public final void writeFloat(float f) throws IOException {
		this.writeInt(Float.floatToIntBits(f));
	}

	public final void writeBoolean(boolean b) throws IOException {
		this.write(b == true ? 1 : 0);
	}

	public final void writeString(String s) throws IOException {
		this.writeInt(s.length());
		this.write(s.getBytes(CHARSET));
	}
}