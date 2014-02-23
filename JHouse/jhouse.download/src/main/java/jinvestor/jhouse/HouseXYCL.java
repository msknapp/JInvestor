package jinvestor.jhouse;

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

import jinvestor.jhouse.download.ZillowDownloader;

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
