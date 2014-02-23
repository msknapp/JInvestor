package com.KnappTech.sr.ctrl.acq;

import com.KnappTech.sr.ctrl.PropertyManager;
import com.KnappTech.sr.ctrl.parse.ERParser;
import com.KnappTech.sr.ctrl.parse.SLFParser;
import com.KnappTech.sr.model.constants.SourceAgency;

public class SLFRetriever extends ERRetriever {

	public static final String SAINTLOUISFEDAPIKEY = "0fdc21ba8efcfea5c5416f3fabad4f9e";
	
	public SLFRetriever() {
		super(SourceAgency.SLF);
	}
	
	public static void main(String[] args) {
		SLFRetriever slfr = new SLFRetriever();
		slfr.retrieve();
	}

	@Override
	public ERParser getReader() {
		return new SLFParser();
	}

	@Override
	public String getSeriesPath() {
		return PropertyManager.getSLFSeriesPath();
	}
}
