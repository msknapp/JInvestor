package com.KnappTech.sr.ctrl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Properties;

public class PropertyManager {
	public static final boolean BUGFINDMODE = false;
	private static final String PROPLOC = "conf/props.properties";
	private static Properties properties;
	
	public static boolean ec2Instance = false;
	
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
	
	public static boolean isWindows() {
		String os = System.getProperty("os.name").toLowerCase();
		return (os.contains("windows"));
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
	
	public static String getIndustriesPath() {
		String baseDir = getBaseDirectory();
		String industryDirectory = properties.getProperty("industryDirectory");
		String industryFile = properties.getProperty("industryFile");
		String fullPath = baseDir+File.separator+
				industryDirectory+File.separator+industryFile;
		return fullPath;
	}
	
	public static String getSectorsPath() {
		String baseDir = getBaseDirectory();
		String sectorDirectory = properties.getProperty("sectorDirectory");
		String sectorFile = properties.getProperty("sectorFile");
		String fullPath = baseDir+File.separator+
				sectorDirectory+File.separator+sectorFile;
		return fullPath;
	}
	
	public static String getBLSSeriesPath() {
		String otherDir = getOtherDirectoryPath();
		String file = properties.getProperty("blsSeriesFile");
		String str = otherDir+File.separator+file;
		return str;
	}
	
	public static String getFRBSeriesPath() {
		String otherDir = getOtherDirectoryPath();
		String file = properties.getProperty("frbSeriesFile");
		String str = otherDir+File.separator+file;
		return str;
	}
	
	public static String getSLFSeriesPath() {
		String otherDir = getOtherDirectoryPath();
		String file = properties.getProperty("slfSeriesFile");
		String str = otherDir+File.separator+file;
		return str;
	}
	
	public static String getIMFSeriesPath() {
		String otherDir = getOtherDirectoryPath();
		String file = properties.getProperty("imfSeriesFile");
		String str = otherDir+File.separator+file;
		return str;
	}

	public static String getReportTemplatePath() {
		String otherDir = getOtherDirectoryPath();
		String file = properties.getProperty("reportTemplate");
		return otherDir+File.separator+file;
	}

	public static String getReportDirectoryPath() {
		String baseDir = getBaseDirectory();
		String file = properties.getProperty("reportDirectory");
		return baseDir+File.separator+file;
	}
	
	public static String getPHObjectRootOrig() {
		String baseDir = getBaseDirectory();
		String objDir = properties.getProperty("objectStorageBasePath");
		String priceHistoryDirectory = properties.getProperty("priceHistoryDirectoryOrig");
		return baseDir+File.separator+objDir+
				File.separator+priceHistoryDirectory;
	}
	
	public static String getPHObjectRoot() {
		String baseDir = getBaseDirectory();
		String objDir = properties.getProperty("objectStorageBasePath");
		String priceHistoryDirectory = properties.getProperty("priceHistoryDirectory");
		return baseDir+File.separator+objDir+
				File.separator+priceHistoryDirectory;
	}

	public static String getFHObjectRoot() {
		String baseDir = getBaseDirectory();
		String objDir = properties.getProperty("objectStorageBasePath");
		String priceHistoryDirectory = properties.getProperty("financialHistoryDirectory");
		return baseDir+File.separator+objDir+
				File.separator+priceHistoryDirectory;
	}
	
	public static String getERObjectRootOrig() {
		String baseDir = getBaseDirectory();
		String objDir = properties.getProperty("objectStorageBasePath");
		String economicRecordDirectory = properties.getProperty("economicRecordDirectoryOrig");
		return baseDir+File.separator+objDir+
				File.separator+economicRecordDirectory;
	}
	
	public static String getERObjectRoot() {
		String baseDir = getBaseDirectory();
		String objDir = properties.getProperty("objectStorageBasePath");
		String economicRecordDirectory = properties.getProperty("economicRecordDirectory");
		return baseDir+File.separator+objDir+
				File.separator+economicRecordDirectory;
	}
	
	public static String getCompanyObjectRoot() {
		String baseDir = getBaseDirectory();
		String objDir = properties.getProperty("objectStorageBasePath");
		String companyDirectory = properties.getProperty("companyDirectory");
		return baseDir+File.separator+objDir+
				File.separator+companyDirectory;
	}
	
	public static String getObjectStorageBasePath() {
		String baseDir = getBaseDirectory();
		String objDir = properties.getProperty("objectStorageBasePath");
		return baseDir+File.separator+objDir;
	}
	
	public static String getIndustryObjectPath() {
		String industryDirectory = properties.getProperty("industryDirectory");
		return getObjectStorageBasePath()+File.separator+industryDirectory;
	}

	public static String getSectorObjectPath() {
		String sectorDirectory = properties.getProperty("sectorDirectory");
		return getObjectStorageBasePath()+File.separator+sectorDirectory;
	}
	
	public static String getAcceptableCompaniesPath() {
		String otherDir = getOtherDirectoryPath();
		String acPath = properties.getProperty("acceptableCompanies");
		return otherDir+File.separator+acPath;
	}
	
	public static String getDateFormatForFiles() {
		return "ddMMyy_hhmmss";
	}
	
	public static String getDateStringForFiles() {
		SimpleDateFormat sdf = new SimpleDateFormat(getDateFormatForFiles());
		String d = sdf.format(Calendar.getInstance().getTime());
		return d;
	}
	
	public static String getReportOutputDirectory() {
		String baseDir = getBaseDirectory();
		String acPath = properties.getProperty("reportOutputDirectory");
		return baseDir+File.separator+acPath;
	}
	
	public static String financialsRootSite() {
		return "http://money.cnn.com/quote/";
	}
	
	public static String financialsTypicalURL() {
		return financialsRootSite()+"financials/financials.html?";
	}
	
	public static String financialsProfileURL() {
		return financialsRootSite()+"snapshot/snapshot.html?";
	}

	public static String getFinancialEntryTypesObjectPath() {
		String financialEntryTypesDirectory = 
			properties.getProperty("financialEntryTypesDirectory");
		return getObjectStorageBasePath()+File.separator+financialEntryTypesDirectory;
	}
	
	public static String getFirefoxCommand() {
		if (isWindows()) {
			return "C:\\Program Files\\Mozilla Firefox\\firefox";
		} else {
			return "firefox";
		}
	}
	
	public static String getOtherDirectoryPath() {
		String baseDir = getBaseDirectory();
		String otherDir = properties.getProperty("otherDirectory");
		return baseDir+File.separator+otherDir;
	}
	
	public static String getPortfolioTickersPath() {
		String otherDir = getOtherDirectoryPath();
		String acPath = properties.getProperty("portfolioTickers");
		return otherDir+File.separator+acPath;
	}
	
	public static Collection<String> loadPortfolioTickers() {
		String path = PropertyManager.getPortfolioTickersPath();
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

	public static String getDataDirectoryPath() {
		String baseDir = getBaseDirectory();
		String otherDir = properties.getProperty("dataDirectory");
		return baseDir+File.separator+otherDir;
	}
	
	public static boolean automaticallyStartUpdates() {
		Boolean b = Boolean.parseBoolean(properties.getProperty("automaticallyStartUpdates"));
		return b.booleanValue();
	}
	
	public static Properties getProperties() {
		return properties;
	}
	
	public static void printProperties() {
		System.out.println("Properties are:");
		for (Object p : properties.keySet()) {
			System.out.println("\t"+p+"="+properties.getProperty(p.toString()));
		}
	}
}