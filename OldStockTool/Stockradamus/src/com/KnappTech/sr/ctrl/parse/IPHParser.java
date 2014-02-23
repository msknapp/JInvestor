package com.KnappTech.sr.ctrl.parse;

import com.KnappTech.sr.model.Regressable.PriceHistory;
import com.KnappTech.util.MethodResponse;

public interface IPHParser {
	public void setPriceHistory(PriceHistory history);
	public MethodResponse update() throws InterruptedException;
}
