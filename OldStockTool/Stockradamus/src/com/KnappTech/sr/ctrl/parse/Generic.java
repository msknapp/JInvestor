package com.KnappTech.sr.ctrl.parse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import com.KnappTech.sr.ctrl.PropertyManager;
import com.KnappTech.util.ConnectionCreater;
import com.KnappTech.util.DefaultConnectionCreater;
import com.KnappTech.util.KTTimer;

/**
 * Responsible for reading websites in general. 
 * @author msknapp
 */
public class Generic {
	
	public static String retrieveWebPage(String sourceURL, String sMethod)
	throws InterruptedException, IOException, FileNotFoundException, Exception {
		return retrieveWebPage(DefaultConnectionCreater.create(sourceURL,sMethod));
	}
	
	public static String retrieveWebPage(URL sourceURL, String sMethod)
	throws InterruptedException, IOException, FileNotFoundException, Exception {
		return retrieveWebPage(DefaultConnectionCreater.create(sourceURL,sMethod));
	}
	
	public static String retrieveWebPage(ConnectionCreater cc)
	throws InterruptedException, IOException, Exception {
		HttpURLConnection urlc = null;
		BufferedReader br = null;
		StringBuilder htmlData = new StringBuilder();
		int attempts = 0;
		
		String msg = "Downloading the page "+cc.getURLString()+" is taking a while...";
		String msg2 = "The page "+cc.getURLString()+" is taking too long so we are aborting its download.";
		
		KTTimer timer = new KTTimer("web page downloader",10,msg,false);
		KTTimer timer2 = new KTTimer("download halter",30,msg2,true);
		while (htmlData.length()<1 && attempts<4) {
			try {
			  	if (Thread.interrupted())
			  		throw new InterruptedException();
			  	urlc = cc.makeConnection();
			  	if (Thread.interrupted())
			  		throw new InterruptedException();
			  	if (Thread.interrupted())
			  		throw new InterruptedException();
			  	br = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
			  	int i;
			  	while ((i = br.read())!=-1) {
				  	if (Thread.interrupted())
				  		throw new InterruptedException();
			  		htmlData.append((char)i);
			  	}
			} catch (UnknownHostException e) {
				System.err.format("There was an unknown host exception, the internet may be unavailable,%n" +
						"  waiting a few seconds before the next request.  Attempts: %d, Wait seconds: %d, url: %s%n",
						attempts+1,5*(attempts+1),cc.getURLString());
				Thread.sleep(5*(attempts+1));
			} catch (InterruptedException e) {
				attempts=10; // forces abort.
				throw (InterruptedException)e;
			} catch (FileNotFoundException e){
				String msg3 = "FYI: Data for the following url does not exist:\n"+cc.getURLString();
				System.out.println(msg3);
				throw (FileNotFoundException)e;
			} catch (IOException e){
				if (e.getMessage().contains(" 502 ")) {
					String msg3 = "The web server provided status code 502: you are either not authorized " +
							"or the server is suffering some kind\nof programming error and so it is " +
							"not responding. (most likely the latter).";
					System.err.println(msg3);
				} else {
					System.err.println("Unknown IO exception in generic web page retrieve function.");
					e.printStackTrace();
				}
				throw e;
			} catch (Exception e){
				if (e instanceof InterruptedException)
					throw (InterruptedException)e;
				String msg3 = "There was an exception caught while trying to save the webpage.\n" +
				"URL: " +cc.getURLString() + '\n' + 
				" method: " + cc.getMethodString() + " message:\n" + e.getMessage() +
				"(Generic save web page function)";
				System.err.println(msg3);
				throw e;
			} finally {
				timer.stop();
				timer2.stop();
				if (br!=null)
					try { br.close(); } catch (Exception e) { e.printStackTrace(); }
				if (urlc!=null)
					try { urlc.disconnect(); } catch (Exception e) { e.printStackTrace(); }
			}
			attempts++;
		}
		if (htmlData.length()<1)
			System.err.println("Warning: failed to retrieve web html data after " +
					"a few attempts.  Internet may be down.");
		String htmlString = htmlData.toString();
		htmlString = htmlString.replace("\r", "\n");
		htmlString = htmlString.replace("\n\n", "\n");
		if (htmlString.startsWith("\n"))
			htmlString = htmlString.substring(1);
		return htmlString;
	}
}