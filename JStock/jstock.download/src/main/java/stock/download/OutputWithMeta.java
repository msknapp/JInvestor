package stock.download;

/*
 * #%L
 * jinvestor.parent
 * %%
 * Copyright (C) 2014 Michael Scott Knapp
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
