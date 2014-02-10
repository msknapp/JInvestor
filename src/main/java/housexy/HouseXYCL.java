package housexy;

import housexy.download.ZillowDownloader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class HouseXYCL {

	public static void main(String[] arguments) {
		Options options = new Options();
		options.addOption("d",false,"download data");
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		try {
			PosixParser parser = new PosixParser();
			CommandLine commandLine = parser.parse(options, arguments);
			if (commandLine.hasOption('d')) {
				ZillowDownloader dl = context.getBean(ZillowDownloader.class);
				dl.download();
			}
		} catch (Exception e) {
			HelpFormatter hf = new HelpFormatter();
			String cmdLineSyntax = "A tool for analyzing the housing market using Zillow.";
			hf.printHelp(cmdLineSyntax, options);
		}
	}
}
