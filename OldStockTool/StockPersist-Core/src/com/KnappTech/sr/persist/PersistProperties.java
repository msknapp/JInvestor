package com.KnappTech.sr.persist;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

public class PersistProperties {
	private static final String PROPLOC = "conf/persist.properties";
	private static Properties properties;
	
	private static boolean ec2Instance = false;

	static {
		InputStream is = null;
		try {
			File f = new File(PROPLOC);
			is = new FileInputStream(f);
			properties = new Properties();
			properties.load(is);
		} catch (Exception e) {
			System.err.println("properties could not be initialized correctly.");
		} finally {
			if (is!=null) {
				try { is.close(); } catch (IOException e) { e.printStackTrace(); }
			}
		}
	}
	
	public static Collection<String> loadPortfolioTickers() {
		String path = getPortfolioTickersPath();
		BufferedReader br = null;
		Collection<String> tickers = new ArrayList<String>();
		try {
			File file = new File(path);
			
			if (file.exists()) {
				br = new BufferedReader(new FileReader(file));
				String line;
				while ((line = br.readLine())!=null) {
					tickers.add(line);
				}
			} else {
				System.err.println("Could not find file to load tickers from.");
			}
		} catch (IOException e) {
			System.err.println("IOException caught while loading portfolio tickers.");
		}
		return tickers;
	}
	
	public static String getPortfolioTickersPath() {
		String otherDir = getOtherDirectoryPath();
		String acPath = properties.getProperty("portfolioTickers");
		return otherDir+File.separator+acPath;
	}
	
	public static String getOtherDirectoryPath() {
		String baseDir = getBaseDirectory();
		String otherDir = properties.getProperty("otherDirectory");
		return baseDir+File.separator+otherDir;
	}
	
	public static String getBaseDirectory() {
		if (isWindows()) {
			if (!ec2Instance) {
				return properties.getProperty("windowsBaseDirectory");
			} else {
				return properties.getProperty("ec2WindowsBaseDirectory");
			}
		} else {
			if (!ec2Instance) {
				return properties.getProperty("linuxBaseDirectory");
			} else {
				return properties.getProperty("ec2LinuxBaseDirectory");
			}
		}
	}
	
	public static boolean isWindows() {
		String os = System.getProperty("os.name").toLowerCase();
		return (os.contains("windows"));
	}

	public static String getDataDirectoryPath() {
		String baseDir = getBaseDirectory();
		String otherDir = properties.getProperty("dataDirectory");
		return baseDir+File.separator+otherDir;
	}
}
