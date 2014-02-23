package com.KnappTech.sr.ctrl.acq;

import com.KnappTech.sr.ctrl.PropertyManager;
import com.KnappTech.sr.ctrl.parse.BLSParser;
import com.KnappTech.sr.ctrl.parse.ERParser;
import com.KnappTech.sr.model.constants.SourceAgency;

public class BLSRetriever extends ERRetriever {
	// skipped workplace injuries, all state/county statistics, 
	// international statistics (except imports/exports), and 
	// regional statistics.
	
	public BLSRetriever() {
		super(SourceAgency.BLS);
	}
	
	public static void main(String[] args) {
		BLSRetriever blsr = new BLSRetriever();
		if (args!=null && args.length==2) {
			String s1 = args[0];
			char c = s1.charAt(0);
			blsr.startLetter=(int)c;
			String s2 = args[1];
			char c2 = s2.charAt(0);
			blsr.endLetter=(int)c2;
		}
		blsr.retrieve();
	}

	@Override
	public ERParser getReader() {
		return new BLSParser();
	}

	@Override
	public String getSeriesPath() {
		return PropertyManager.getBLSSeriesPath();
	}
}