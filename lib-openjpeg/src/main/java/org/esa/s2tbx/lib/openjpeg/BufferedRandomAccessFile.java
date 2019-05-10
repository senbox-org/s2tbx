package org.esa.s2tbx.lib.openjpeg;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.Set;

public class BufferedRandomAccessFile implements IRandomAccessFile {

	private final Path file;
	private final boolean canSetFilePosition;
	private final byte[] byteBuffer;

	private long byteBufferStreamOffset;
	private int byteBufferPosition;
	private int byteRead;
	private boolean isEOFInBuffer;

	public BufferedRandomAccessFile(Path file, int bufferSize, boolean canSetFilePosition) throws IOException {
		this.file = file;
		this.canSetFilePosition = canSetFilePosition;
		this.byteBuffer = new byte[bufferSize];

		readBuffer(0);
	}
	
	@Override
	public final short readShort() throws IOException {
		return (short) ((read() << 8) | (read()));
	}

	@Override
	public final int readUnsignedShort() throws IOException {
		return ((read() << 8) | read());
	}

	@Override
	public final int readInt() throws IOException {
		return ((read() << 24) | (read() << 16) | (read() << 8) | read());
	}

	@Override
	public final long readUnsignedInt() throws IOException {
		return (long) ((read() << 24) | (read() << 16) | (read() << 8) | read());
	}

	@Override
	public final long readLong() throws IOException {
		return (((long) read() << 56) | ((long) read() << 48) | ((long) read() << 40) | ((long) read() << 32)
				| ((long) read() << 24) | ((long) read() << 16) | ((long) read() << 8) | ((long) read()));
	}

	@Override
	public final float readFloat() throws IOException {
		return Float.intBitsToFloat((read() << 24) | (read() << 16) | (read() << 8) | (read()));
	}

	@Override
	public final double readDouble() throws IOException {
		return Double.longBitsToDouble(
				((long) read() << 56) | ((long) read() << 48) | ((long) read() << 40) | ((long) read() << 32)
						| ((long) read() << 24) | ((long) read() << 16) | ((long) read() << 8) | ((long) read()));
	}

	@Override
	public long getPosition() {
		return (byteBufferStreamOffset + byteBufferPosition);
	}

	@Override
	public long getLength() throws IOException {
		return Files.size(this.file);
	}

	@Override
	public void seek(long offset) throws IOException {
		if ((offset >= byteBufferStreamOffset) && (offset < (byteBufferStreamOffset + byteBuffer.length))) {
			if (isEOFInBuffer && offset > byteBufferStreamOffset + byteRead) {
				// We are seeking beyond EOF in read-only mode!
				throw new EOFException();
			}
			long result = offset - this.byteBufferStreamOffset;
			if (result > Integer.MAX_VALUE) {
				throw new IllegalStateException("The byte buffer position " + result + " is greater than the maximmum integer number " + Integer.MAX_VALUE + ".");
			}
			this.byteBufferPosition = (int)result;
		} else {
			readBuffer(offset);
		}
	}

	@Override
	public final int read() throws IOException {
		if (byteBufferPosition < byteRead) { // The byte can be read from the buffer
			// In Java, the bytes are always signed.
			return (byteBuffer[byteBufferPosition++] & 0xFF);
		} else if (isEOFInBuffer) { // EOF is reached
			byteBufferPosition = byteRead + 1; // Set position to EOF
			throw new EOFException();
		} else { // End of the buffer is reached
			readBuffer(byteBufferStreamOffset + byteBufferPosition);
			return read();
		}
	}

	@Override
	public final void readFully(byte b[], int off, int len) throws IOException {
		int clen; // current length to read
		while (len > 0) {
			// There still is some data to read
			if (byteBufferPosition < byteRead) { // We can read some data from buffer
				clen = byteRead - byteBufferPosition;
				if (clen > len)
					clen = len;
				System.arraycopy(byteBuffer, byteBufferPosition, b, off, clen);
				byteBufferPosition += clen;
				off += clen;
				len -= clen;
			} else if (isEOFInBuffer) {
				byteBufferPosition = byteRead + 1; // Set position to EOF
				throw new EOFException();
			} else { // Buffer empty => get more data
				readBuffer(byteBufferStreamOffset + byteBufferPosition);
			}
		}
	}

	private void readBuffer(long offset) throws IOException {
		// don't allow to seek beyond end of file if reading only
		if (offset >= getLength()) {
			throw new EOFException();
		}
		// set new offset
		this.byteBufferStreamOffset = offset;

		Set<? extends OpenOption> options = Collections.emptySet();
		long bufferSize = Math.min(10 * 1024, this.byteBufferStreamOffset);
		if (bufferSize > Integer.MAX_VALUE) {
			throw new IllegalStateException("The buffer size " + bufferSize + " is greater than the maximmum integer number " + Integer.MAX_VALUE + ".");
		}
		int capacity = (int)bufferSize;
		ByteBuffer byteBufferObject = ByteBuffer.allocate(capacity);

		if (this.canSetFilePosition) {
			try (FileChannel fileChannel = FileChannel.open(this.file, StandardOpenOption.READ);
				 InputStream inputStream = Channels.newInputStream(fileChannel.position(this.byteBufferStreamOffset))) {

				readFromInputStream(inputStream);
			}
		} else {
			FileSystemProvider fileSystemProvider = this.file.getFileSystem().provider();
			try (SeekableByteChannel seekableByteChannel = fileSystemProvider.newByteChannel(this.file, options)) {
				while (seekableByteChannel.position() < this.byteBufferStreamOffset) {
					seekableByteChannel.read(byteBufferObject);
					byteBufferObject.clear();
					int difference = (int)(this.byteBufferStreamOffset - seekableByteChannel.position());
					if (difference < byteBufferObject.capacity()) {
						byteBufferObject.limit(difference);
					}
				}
				try (InputStream inputStream = Channels.newInputStream(seekableByteChannel)) {
					readFromInputStream(inputStream);
				}
			}
		}

		byteBufferPosition = 0;
		if (byteRead < byteBuffer.length) { // Not enough data in input file.
			isEOFInBuffer = true;
			if (byteRead == -1) {
				byteRead++;
			}
		} else {
			isEOFInBuffer = false;
		}
	}

	private void readFromInputStream(InputStream inputStream) throws IOException {
		this.byteRead = inputStream.read(this.byteBuffer, 0, this.byteBuffer.length);
	}
}
