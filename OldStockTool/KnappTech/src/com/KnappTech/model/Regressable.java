package com.KnappTech.model;

import java.util.List;

import com.KnappTech.model.LiteDate;
import com.KnappTech.model.UpdateFrequency;

/**
 * All objects that can be regressed must implement this interface.
 * @author msknapp
 */
public interface Regressable extends CoversTimeFrame, 
KTObject {
	public void getRegressableList(LiteDate startDate, LiteDate endDate,
			UpdateFrequency updateFrequency, int offset);
	public List<Double> asFrequency(UpdateFrequency updateFrequency);
	public boolean isMultiple();
	public boolean canBeNegative();
}
