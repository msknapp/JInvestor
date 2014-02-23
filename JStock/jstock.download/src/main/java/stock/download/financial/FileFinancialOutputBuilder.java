package stock.download.financial;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.util.DigestUtils;

import stock.download.BaseOutputBuilder;
import stock.download.OutputWithMeta;
import stock.download.financial.FinancialDownloader.FinancialCombo;

public abstract class FileFinancialOutputBuilder extends BaseOutputBuilder implements FinancialOutputBuilder {
	private final Logger logger = Logger.getLogger(getClass());
	private final String pathFormat;
	
	public FileFinancialOutputBuilder(final String pathFormat) {
		this.pathFormat = pathFormat;
	}

	@Override
	public abstract String determineUrl(String stockSymbol, FinancialCombo combo);

	@Override
	public OutputWithMeta buildOutputStream(String stockSymbol,
			FinancialCombo combo, String url) throws IOException {
		String md5 = DigestUtils.md5DigestAsHex(url.getBytes());
		String path = getPath(stockSymbol,combo,md5);
		File saveFile = new File(path);
		super.prepareToSave(saveFile);
		OutputWithMeta owm= new OutputWithMeta(new FileOutputStream(saveFile));
		owm.setFromUri(url);
		owm.setPath(path);
		owm.setType("file");
		return owm;
	}
	
	private String getPath(String stockSymbol,
			FinancialCombo combo, String md5) {
		return String.format(pathFormat,stockSymbol,combo.getType().name(),
				combo.getPeriod().name(),md5);
	}

	@Override
	public void handleFailure(String stockSymbol, FinancialCombo combo,
			String url) {
		String md5 = DigestUtils.md5DigestAsHex(url.getBytes());
		final File saveFile = new File(getPath(stockSymbol, combo,md5));
		if (saveFile.exists() && !saveFile.delete()) {
			logger.warn("Could not delete "+saveFile);
		} // target/tmp/yahoo/FOO/BALANCE_ANNUALLY_a4e1208a7222996931cc3ff380853a01.txt
		// target/tmp/yahoo/GE/BALANCE_ANNUALLY_a4e1208a7222996931cc3ff380853a01.txt
	}
}