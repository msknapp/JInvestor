package com.KnappTech.sr.model.comp;

import java.util.Collection;

import com.KnappTech.sr.model.Regressable.RecordSet;

public class CurrencyExchanges extends RecordSet<CurrencyExchange> {
	private static final long serialVersionUID = 201001181309L;
	
	public CurrencyExchanges() {
		
	}
	
	public CurrencyExchanges(Collection<CurrencyExchange> currencies) {
		super(currencies);
	}

	@Override
	public int compareTo(String o) {
		return 0;
	}

	@Override
	public CurrencyExchanges createInstance() {
		return new CurrencyExchanges();
	}

//	@Override
//	public Report<?> getReport(Filter<Object> instructions) {
//		Report<CurrencyExchange> report = new Report<CurrencyExchange>("Currency_Exchanges");
//		ReportRow<CurrencyExchange> row;
//		for (CurrencyExchange curr : items) {
//			row = report.put(curr);
//			if (instructions.include(curr))
//				curr.updateReportRow(instructions,row);
//		}
//		return report;
//	}

//	@Override
//	public void updateReportRow(Filter<Object> instructions,ReportRow<?> row) {
//		row.put("size",ReportColumnFormat.INTFORMAT,items.size());
//	}
	
	public CurrencyExchanges clone() {
		CurrencyExchanges c = new CurrencyExchanges();
		for (CurrencyExchange cc : items) {
			c.add((CurrencyExchange) cc.clone());
		}
		return c;
	}
	
	public String toString() {
		String s = "";
		for (CurrencyExchange c : items) {
			s+=c.toString()+"\n";
		}
		if (s.length()<1) {
			s+="There is no money in this.";
		}
		return s;
	}

//	@Override
//	public boolean save(boolean multiThread) {
//		return false;
//	}
}
