package stock.download;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import stock.core.FileLineSource;
import stock.core.util.CloseableSourceUtil;
import stock.download.financial.FinancialDownloader;
import stock.download.ph.PHDownloader;

public class DownloadCommandLine {
	
	public static void main(String[] args) {
		Options options = new Options();
		options.addOption("y", false, "download yahoo price histories.");
		options.addOption("g", false, "download google price histories.");
		options.addOption("f", false, "download financial records from yahoo.");
		options.addOption("s", false, "download slf data");
		PosixParser parser = new PosixParser();
		try {
			final CommandLine commandLine = parser.parse(options, args);
			final ApplicationContext context = loadAppContext();
			if (commandLine.hasOption('y')) {
				downloadYahoo(context);
			}
			if (commandLine.hasOption('g')) {
				downloadGoogle(context);
			}
			if (commandLine.hasOption('s')) {
				downloadSlf(context);
			}
			if (commandLine.hasOption('f')) {
				downloadYahooFinancials(context);
			}
		} catch (ParseException e) {
			HelpFormatter formatter = new HelpFormatter();
			String cmdLineSyntax = "downloads files";
			formatter.printHelp(cmdLineSyntax, options);
		}
	}

	private static void downloadYahooFinancials(ApplicationContext context) {
		FinancialDownloader dl = (FinancialDownloader) context.getBean("yahooFinancialDownloader");
		FileLineSource symbolSource = (FileLineSource) context.getBean("tickerSymbolSource");
		CloseableSourceUtil.process(symbolSource, dl);
	}

	private static void downloadSlf(ApplicationContext context) {
		IndicatorDownloader dl = (IndicatorDownloader) context.getBean("slfDownloader");
		FileLineSource symbolSource = (FileLineSource) context.getBean("slfSeries");
		CloseableSourceUtil.process(symbolSource, dl);
	}

	private static void downloadGoogle(ApplicationContext context) {
		PHDownloader dl = (PHDownloader) context.getBean("googlePH");
		FileLineSource symbolSource = (FileLineSource) context.getBean("tickerSymbolSource");
		CloseableSourceUtil.process(symbolSource, dl);
	}

	public static ApplicationContext loadAppContext() {
		final ApplicationContext context = new ClassPathXmlApplicationContext(
				"classpath:app-context.xml");
		return context;
	}

	public static void downloadYahoo(final ApplicationContext context) {
		PHDownloader dl = (PHDownloader) context.getBean("yahooPH");
		FileLineSource symbolSource = (FileLineSource) context.getBean("tickerSymbolSource");
		CloseableSourceUtil.process(symbolSource, dl);
	}
}