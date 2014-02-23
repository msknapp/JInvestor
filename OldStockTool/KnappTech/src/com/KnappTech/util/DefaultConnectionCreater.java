package com.KnappTech.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DefaultConnectionCreater implements ConnectionCreater {
	private final URL url;
	private final String method;
	
	private DefaultConnectionCreater(URL url,String method) {
		this.url = url;
		this.method = method;
	}
	
	public static final DefaultConnectionCreater create(String url) {
		return create(url,"GET");
	}
	
	public static final DefaultConnectionCreater create(String url,String method) {
		try {
			URL u = new URL(url);
			return new DefaultConnectionCreater(u,method);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("malformed url.");
		}
	}

	public static final DefaultConnectionCreater create(URL url, String method) {
		return new DefaultConnectionCreater(url,method);
	}
	
	public HttpURLConnection makeConnection() throws IOException {
		HttpURLConnection urlc = null;
		urlc = (HttpURLConnection)url.openConnection();
	  	urlc.setRequestMethod(method);
	  	urlc.setDoOutput(true);
	  	urlc.setDoInput(true);
	  	urlc.setUseCaches(false);
	  	urlc.setAllowUserInteraction(false);
	  	urlc.setRequestProperty("Content-type", "text/xml; charset=" + "UTF-8");
	  	return urlc;
	}

	@Override
	public String getURLString() {
		return url.toString();
	}

	@Override
	public String getMethodString() {
		return method;
	}
}
