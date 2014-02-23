package com.KnappTech.sr.model.constants;

import java.io.Serializable;

import com.KnappTech.model.Reportable;
import com.KnappTech.util.Filter;
import com.KnappTech.view.report.ReportRow;
import com.KnappTech.view.report.ReportSettings;

import java.util.HashSet;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public enum StatementType implements Serializable, Reportable  {
	INCOME ("Income Statement"),
	BALANCE ("Balance Sheet"),
	CASH ("Cash Flow"),
	GEN ("General");
	
	private String statementName = "";
	
	private StatementType(String name){
		this.statementName = name;
	}
	
	public String getName() {
		return statementName;
	}

	public String deflate() {
		int i = 0;
		if (this==INCOME){
			i=1;
		} else if (this==BALANCE) {
			i=2;
		} else if (this == CASH) {
			i = 3;
		} else if (this == GEN) {
			i = 4;
		}
		return "<st>"+i+"</st>";
	}

	public static StatementType inflate(String portion) {
		try {
			int st = Integer.valueOf(portion);
			if (st==1){
				return INCOME;
			} else if (st==2) {
				return BALANCE;
			} else if (st==3) {
				return CASH;
			} else if (st==4) {
				return GEN;
			}
		} catch (Exception e) {
			
		}
		return null;
	}
	
	public HashSet<String> getAcceptedEntries() {
		HashSet<String> acceptedEntries = new HashSet<String>();
		String resource = "/com/knappTech/stockradamus/constants/";
		if (this==INCOME){
			resource += "IncomeStatementEntries.txt";
		} else if (this==BALANCE) {
			resource += "BalanceStatementEntries.txt";
		} else if (this == CASH) {
			resource += "CashStatementEntries.txt";
		} else if (this == GEN) {
			resource += "GeneralStatementEntries.txt";
		}
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(resource)));
			String s;
			while ((s=br.readLine())!=null) {
				acceptedEntries.add(s);
			}
		} catch (Exception e) {
			
		}
		return acceptedEntries;
	}

	@Override
	public void updateReportRow(Filter<Object> instructions,ReportRow<?> row) {
		// TODO Auto-generated method stub
	}
}
