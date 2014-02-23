package stock.download.ph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.springframework.util.DigestUtils;

import stock.core.StockSymbol;
import stock.core.TimeSpan;
import stock.download.BaseOutputBuilder;
import stock.download.OutputWithMeta;

public class FilePHOutputBuilder extends BaseOutputBuilder implements PHOutputBuilder<Object> {
	private static final Logger logger = Logger
			.getLogger(FilePHOutputBuilder.class);
	private final String format;
	private final String saveDir;
	private final String subDirName;

	public FilePHOutputBuilder(final String format, final String saveDir,
			final String subDirName) {
		this.format = format;
		this.saveDir = saveDir;
		this.subDirName = subDirName;
	}

	@Override
	public OutputWithMeta buildOutputStream(Object stockSymbol,
			TimeSpan ts, final String url)
			throws FileNotFoundException {
		final File saveFile = determineSaveFile(stockSymbol, url);
		prepareToSave(saveFile);
		OutputWithMeta owm = new OutputWithMeta(new FileOutputStream(saveFile));
		owm.setPath(saveFile.getAbsolutePath());
		owm.setType("file");
		owm.setFromUri(url);
		return owm;
	}

	private File determineSaveFile(Object stockSymbol, final String url) {
		final String md5 = DigestUtils.md5DigestAsHex(url.getBytes());
		final String savePath = determineSavePath(saveDir, md5, stockSymbol);
		final File saveFile = new File(savePath);
		handleExisting(saveFile);
		return saveFile;
	}

	public String determineSavePath(String saveDir, String md5,
			Object stockSymbol) {
		return saveDir
				+ File.separator
				+ getSubDirName()
				+ (getSubDirName() == null ? "" : File.separator
						+ getId(stockSymbol)) + "_" + md5;
	}

	public String getSubDirName() {
		return subDirName;
	}

	@Override
	public String determineUrl(Object stockSymbol, TimeSpan ts) {
		final String url = String.format(format, getId(stockSymbol),
				TimeSpan.getStart(ts), TimeSpan.getEnd(ts),
				(TimeSpan.getStart(ts)!=null?TimeSpan.getStart(ts).get(Calendar.DAY_OF_MONTH)-1:1),
				(TimeSpan.getEnd(ts)!=null?TimeSpan.getEnd(ts).get(Calendar.DAY_OF_MONTH)-1:0));
		return url;
	}

	@Override
	public void handleFailure(Object stockSymbol, TimeSpan ts, String url) {
		final File saveFile = determineSaveFile(stockSymbol, url);
		saveFile.delete();
	}
	
	private String getId(Object o) {
		if (o instanceof StockSymbol) {
			return ((StockSymbol)o).getSymbol();
		}
		return o.toString();
	}
}