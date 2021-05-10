package src.sersanleo.galaxies.util;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExtFileOutputStream extends FileOutputStream {

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

	public final void writeInt(int i) throws IOException {
		this.write(new byte[] { (byte) (i >>> 24), (byte) (i >>> 16), (byte) (i >>> 8), (byte) i });
	}

	public final void writeFloat(float f) throws IOException {
		this.writeInt(Float.floatToIntBits(f));
	}

	public final void writeBoolean(boolean b) throws IOException {
		this.write(b == true ? 1 : 0);
	}
}