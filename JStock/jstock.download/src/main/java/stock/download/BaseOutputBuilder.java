package stock.download;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;


public abstract class BaseOutputBuilder {
	private final Logger logger = Logger.getLogger(getClass());

	private ExistingFileBehaviour behaviour = ExistingFileBehaviour.DELETEIFEMPTY;

	public ExistingFileBehaviour getBehaviour() {
		return behaviour;
	}

	public void setBehaviour(ExistingFileBehaviour behaviour) {
		this.behaviour = behaviour;
	}

	public enum ExistingFileBehaviour {
		DELETE, DELETEIFEMPTY, NEVERDELETE;
	}

	public void prepareToSave(final File saveFile) {
		try {
			if (!saveFile.exists()) {
				if (!saveFile.getParentFile().mkdirs()) {
					logger.warn("Could not create parents for: "+saveFile);
				}
				if (!saveFile.createNewFile()) {
					logger.warn("Could not create: "+saveFile);
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
	}

	protected void handleExisting(final File saveFile) {
		if (getBehaviour() == ExistingFileBehaviour.DELETE) {
			if (saveFile.exists() && !saveFile.delete()) {
				logger.warn("Failed to delete " + saveFile);
			} else {
				saveFile.delete();
			}
		} else if (getBehaviour() == ExistingFileBehaviour.DELETEIFEMPTY) {
			if (saveFile.exists()) {
				if (saveFile.length() < 1) {
					if (!saveFile.delete()) {
						logger.warn("Failed to delete " + saveFile);
					}
				} else {
					throw new IllegalStateException("The file already exists! "
							+ saveFile);
				}
			} else {
				// don't check if it worked.
				saveFile.delete();
			}
		} else if (saveFile.exists()) {
			throw new IllegalStateException("The file already exists! "
					+ saveFile);
		}
	}
}
