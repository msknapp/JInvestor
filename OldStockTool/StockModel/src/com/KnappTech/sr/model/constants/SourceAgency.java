package com.KnappTech.sr.model.constants;

import java.io.Serializable;

import com.KnappTech.model.Reportable;
import com.KnappTech.util.Filter;
import com.KnappTech.view.report.ReportRow;
import com.KnappTech.view.report.ReportSettings;

public enum SourceAgency implements Serializable, Reportable {
	FRB ("Federal Reserve Board", ""),
	SLF ("Saint Louis Fed", ""),
	BLS ("Bureau of Labor & Statistics", ""),
	IMF ("International Monetary Fund", ""),
	CENSUS ("US Census Bureau", ""),
	YAHOO ("YAHOO", ""),
	CNNMONEY ("CNN", ""),
	GOOGLE ("GOOGLE", "");
	
	private String name = "";
	private String urlFormat = "";
	
	private SourceAgency(String name, String urlFormat){
		this.name = name;
		this.urlFormat = urlFormat;
	}
	
	public String getName() {
		return name;
	}
	
	public String getURLFormat() {
		return urlFormat;
	}

	public String deflate() {
		SourceAgency[] sa = values();
		for (int i = 0;i<sa.length;i++) {
			if (sa[i] == this) {
				return String.valueOf(i);
			}
		}
		return "-1";
	}

	public static SourceAgency inflate(String portion) {
		int i = Integer.parseInt(portion);
		SourceAgency[] uf = values();
		return uf[i];
		// TODO Check that this logic works.
	}

	@Override
	public void updateReportRow(Filter<Object> instructions,ReportRow<?> row) {
		// TODO Auto-generated method stub
	}

	public static SourceAgency parse(String sa) {
		SourceAgency[] sas = values();
		for (int i = 0;i<sas.length;i++) {
			if (sas[i].name().equalsIgnoreCase(sa) || sas[i].getName().equalsIgnoreCase(sa)) {
				return sas[i];
			}
		}
		return null;
	}

	public byte getIndex() {
		SourceAgency[] ufs = values();
		for (byte i = 0;i<ufs.length;i++) {
			if (ufs[i].name().equalsIgnoreCase(name())) {
				return i;
			}
		}
		return -1;
	}
	
	public static SourceAgency createFromIndex(byte i) {
		SourceAgency[] ufs = values();
		return ufs[i];
	}
}
