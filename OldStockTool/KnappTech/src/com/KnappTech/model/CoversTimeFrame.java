package com.KnappTech.model;

import com.KnappTech.model.LiteDate;
import com.KnappTech.model.UpdateFrequency;

public interface CoversTimeFrame {
	public LiteDate getStartDate();
	public LiteDate getEndDate();
//	public void setStartDate(LiteDate startDate);
//	public void setEndDate(LiteDate endDate);
	public TimeFrame getTimeFrame();
	public UpdateFrequency getUpdateFrequency();
}
