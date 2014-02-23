package com.KnappTech.util;

import java.io.IOException;
import java.net.HttpURLConnection;

public interface ConnectionCreater {
	public HttpURLConnection makeConnection() throws IOException;
	public String getURLString();
	public String getMethodString();
}
