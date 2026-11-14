package game.gui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LogCapture {
	private final PrintStream originalOut;
	private TeeStream teeStream;
	private PrintStream tee;
	private final List<Consumer<String>> listeners = new ArrayList<>();

	public LogCapture() {
		this.originalOut = System.out;
	}

	public void start() {
		this.teeStream = new TeeStream(originalOut);
		this.tee = new PrintStream(teeStream, true);
		System.setOut(tee);
	}

	public void stop() {
		System.setOut(originalOut);
	}

	public void addListener(Consumer<String> listener) {
		listeners.add(listener);
	}

	public void clearListeners() {
		listeners.clear();
	}

	public void clear() {
		if (teeStream != null) teeStream.clear();
	}

	public List<String> drainLines() {
		if (teeStream == null) return new ArrayList<>();
		return teeStream.drain();
	}

	private class TeeStream extends OutputStream {
		private final PrintStream original;
		private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		public TeeStream(PrintStream original) {
			this.original = original;
		}

		@Override
		public synchronized void write(int b) throws IOException {
			original.write(b);
			buffer.write(b);
		}

		@Override
		public synchronized void write(byte[] b, int off, int len) throws IOException {
			original.write(b, off, len);
			buffer.write(b, off, len);
		}

		@Override
		public void flush() throws IOException {
			original.flush();
		}

		public synchronized List<String> drain() {
			String content = buffer.toString();
			buffer.reset();
			List<String> lines = new ArrayList<>();
			if (content.isEmpty()) return lines;
			for (String line : content.split("\\R")) {
				if (!line.isEmpty()) {
					lines.add(line);
					for (Consumer<String> l : listeners) l.accept(line);
				}
			}
			return lines;
		}

		public synchronized void clear() {
			buffer.reset();
		}
	}
}
