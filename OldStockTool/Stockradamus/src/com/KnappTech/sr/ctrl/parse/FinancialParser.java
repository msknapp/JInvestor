package com.KnappTech.sr.ctrl.parse;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import com.KnappTech.model.LiteDate;
import com.KnappTech.sr.ctrl.PropertyManager;
import com.KnappTech.sr.model.Financial.FinancialEntry;
import com.KnappTech.sr.model.Financial.FinancialHistory;
import com.KnappTech.sr.model.Financial.FinancialStatementAtFrequency;
import com.KnappTech.sr.model.constants.StatementType;

public class FinancialParser {
	FinancialHistory financial = null;
	protected String data = "";
	private int currentIndex = 0;

	public void update(FinancialHistory financial) throws InterruptedException {
		this.financial = financial;
		LiteDate oneMonthAgo = LiteDate.getOrCreate(Calendar.MONTH,-1);
		boolean updatedRecently = financial.getLastUpdate().onOrAfter(oneMonthAgo);
		if (updatedRecently && financial.hasAllStatements())
			return;
		String ticker = financial.getID();
		try {
			StatementType[] typesOfStatements = StatementType.values();
			for (StatementType type : typesOfStatements) {
				if (type==StatementType.GEN)
					continue;
				for (int i = 0;i<2;i++) {
					boolean quarterly = (i==1);
					if (updatedRecently && financial.get(type).get(quarterly).hasEntries())
						continue; // it is well enough up to date.
					try {
						URL url = createURL(type,quarterly,ticker);
						data = Generic.retrieveWebPage(url, "GET");
						updateFR(type,quarterly);
					} catch (Exception e) {
						if (e instanceof InterruptedException)
							throw (InterruptedException)e;
						// just keep trying.
						e.printStackTrace();
						System.err.println("Exception while trying to update "+financial.getID()+
								" "+type.name()+(quarterly ? "quarterly " : "annual ")+" statement.");
					}
				}
			}
			financial.setLastUpdate(LiteDate.getOrCreate());
		} catch (Exception e) {
			if (e instanceof InterruptedException)
				throw (InterruptedException)e;
			e.printStackTrace();
		}
	}

	private void updateFR(StatementType type, boolean quarterly) {
		// let's start by trimming the data a little bit.
		if (type!=StatementType.GEN) {
			trimCNNData();
			updateNormalStatement(type,quarterly);
		}
	}

	private void updateNormalStatement(StatementType type, boolean quarterly) {
		String rowStart = "<td class=\"text\">";
		String cellStart = "<td class=\"periodData\">";
		int columnsPerRow = 4;
		currentIndex=0;
		FinancialStatementAtFrequency fsaf = financial.get(type,quarterly);
		while (currentIndex>=0) {
			String rowName = advancePast(rowStart);
			if (rowName!=null && !rowName.equals("")) {
				FinancialEntry entry = fsaf.mergeData(rowName);
				entry.clear(); // always start with a clean slate.
				int i = 0;
				while (i<columnsPerRow && currentIndex>=0) {
					String val = advancePast(cellStart);
					if (val!=null) {
						entry.parseAndAddValue(val);
						i++;
					}
				}
				fsaf.add(entry);
			}
		}
	}
	
	private String advancePast(String str) {
		int strIndex = data.indexOf(str,currentIndex);
		String value = null;
		if (strIndex>=0) {
			int strValueStartIndex = data.indexOf(">",strIndex)+1;
			int strValueEndIndex = data.indexOf("<",strValueStartIndex);
			value = data.substring(strValueStartIndex,strValueEndIndex);
			currentIndex = strValueEndIndex;
		} else {
			currentIndex=-1; // so the loop stops.
		}
		return value;
	}

	private void trimCNNData() {
		// TODO Auto-generated method stub
		String str = "<div id=\"cnnBody\">";
		int ind = data.indexOf(str);
		String end = "<div id=\"quigo528\"><!--";
		int ind2 = data.indexOf(end);
		data = data.substring(ind, ind2);
	}

	private URL createURL(StatementType type, boolean quarterly, String ticker) {
		URL url = null;
		try {
			url = new URL(createURLString(type, quarterly, ticker));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}

	private String createURLString(StatementType type, boolean quarterly, String ticker) {
		String ds = "";
		String tck = "symb="+ticker;
		if (type==StatementType.INCOME) {
			ds="IS";
		} else if (type==StatementType.BALANCE) {
			ds="BS";
		} else if (type==StatementType.CASH) {
			ds="CFS";
		} else {
			return PropertyManager.financialsProfileURL()+tck;
		}
		String prd = "A";
		if (quarterly) {
			prd="Q";
		}
		return PropertyManager.financialsTypicalURL()+
				tck+"&dataset="+ds+"&period="+prd;
	}
	
	
}
