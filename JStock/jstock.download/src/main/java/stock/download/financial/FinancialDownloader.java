package stock.download.financial;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Executor;

import org.apache.commons.collections4.Closure;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;

import stock.download.DOSPrevention;
import stock.download.DownloadHelper;
import stock.download.OutputWithMeta;
import stock.download.record.DownloadRecord;
import stock.download.record.DownloadRecordRepo;
import stock.download.util.HttpResponseUtils;

public class FinancialDownloader implements Closure<String> {
	private static final Logger logger = Logger
			.getLogger(FinancialDownloader.class);

	private static final FinancialCombo[] combos = new FinancialCombo[] {
			new FinancialCombo(FinancialType.BALANCE, FinancialPeriod.QUARTERLY),
			new FinancialCombo(FinancialType.CASHFLOW,
					FinancialPeriod.QUARTERLY),
			new FinancialCombo(FinancialType.INCOME, FinancialPeriod.QUARTERLY),
			new FinancialCombo(FinancialType.BALANCE, FinancialPeriod.ANNUALLY),
			new FinancialCombo(FinancialType.CASHFLOW, FinancialPeriod.ANNUALLY),
			new FinancialCombo(FinancialType.INCOME, FinancialPeriod.ANNUALLY), };

	private final DownloadHelper downloadHelper;
	private final DownloadRecordRepo repo;
	private final Executor threadPool;
	private final FinancialOutputBuilder outputBuilder;
	private final FinancialExtractor responseTransformer;

	public FinancialDownloader(DownloadHelper downloadHelper,
			DownloadRecordRepo repo, Executor threadPool,
			FinancialOutputBuilder outputBuilder,
			FinancialExtractor responseTransformer) {
		this.downloadHelper = downloadHelper;
		this.repo = repo;
		this.threadPool = threadPool;
		this.outputBuilder = outputBuilder;
		this.responseTransformer = responseTransformer;
	}

	@Override
	public void execute(String stockSymbol) {
		stockSymbol = stockSymbol.trim();
		for (final FinancialCombo combo : combos) {
			FinancialRunner runner = new FinancialRunner(combo, stockSymbol);
			if (threadPool != null) {
				// run in parallel
				threadPool.execute(runner);
			} else {
				// run in series
				runner.run();
			}
		}
	}

	private final class FinancialRunner implements Runnable {

		private final FinancialCombo combo;
		private final String stockSymbol;

		public FinancialRunner(final FinancialCombo combo,
				final String stockSymbol) {
			this.combo = combo;
			this.stockSymbol = stockSymbol;
		}

		@Override
		public void run() {
			OutputStream outputStream = null;
			DownloadRecord attemptRecord = null;

			try {
				String url = outputBuilder.determineUrl(stockSymbol, combo);
				if (DOSPrevention.shouldTry(repo, url)) {
					OutputWithMeta output = outputBuilder.buildOutputStream(
							stockSymbol, combo, url);
					outputStream = output.getOutputStream();
					attemptRecord = new DownloadRecord(url,
							stockSymbol);
					HttpResponse response = null;
					if (responseTransformer != null) {
						// cannot simply write the entire page to file.
						// this transformer takes the opportunity to 
						// extract just that portion of text we want to save
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						try {
							response = downloadHelper.download(url, baos);
							String webResponse = new String(baos.toByteArray());
							String usefulResponse = responseTransformer
									.transform(webResponse,stockSymbol,combo);
							IOUtils.write(usefulResponse, outputStream);
						} finally {
							IOUtils.closeQuietly(baos);
						}
					} else {
						response = downloadHelper.download(url, outputStream);
					}
					attemptRecord.updateFromResponse(response);
					attemptRecord.updateFromMeta(output);
					if (!HttpResponseUtils.passed(response)) {
						outputBuilder.handleFailure(stockSymbol, combo, url);
					}
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			} finally {
				IOUtils.closeQuietly(outputStream);
				if (attemptRecord != null) {
					repo.create(attemptRecord);
				}
			}
		}
	}

	public static final class FinancialCombo {
		private final FinancialType type;
		private final FinancialPeriod period;

		public FinancialCombo(FinancialType type, FinancialPeriod period) {
			this.type = type;
			this.period = period;
		}

		public FinancialType getType() {
			return type;
		}

		public FinancialPeriod getPeriod() {
			return period;
		}
	}

}
