package stock.download;

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
