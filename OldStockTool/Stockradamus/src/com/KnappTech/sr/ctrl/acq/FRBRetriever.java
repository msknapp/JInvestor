package com.KnappTech.sr.ctrl.acq;

import com.KnappTech.sr.ctrl.PropertyManager;
import com.KnappTech.sr.ctrl.parse.ERParser;
import com.KnappTech.sr.ctrl.parse.FRBParser;
import com.KnappTech.sr.model.constants.SourceAgency;

public class FRBRetriever extends ERRetriever {
	
	public FRBRetriever() {
		super(SourceAgency.FRB);
	}

	public static void main(String[] args) {
		FRBRetriever frbr = new FRBRetriever();
		frbr.retrieve();
	}

	@Override
	public ERParser getReader() {
		return new FRBParser();
	}

	@Override
	public String getSeriesPath() {
		return PropertyManager.getFRBSeriesPath();
	}
}
