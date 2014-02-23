package stock.download;

import java.io.OutputStream;

public class OutputWithMeta {
	private final OutputStream outputStream;
	private String path;
	private String type;
	private String fromUri;
	private int lengthWritten;
	
	public OutputWithMeta(final OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFromUri() {
		return fromUri;
	}

	public void setFromUri(String fromUri) {
		this.fromUri = fromUri;
	}

	public int getLengthWritten() {
		return lengthWritten;
	}

	public void setLengthWritten(int lengthWritten) {
		this.lengthWritten = lengthWritten;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}
	
	
}
