package com.KnappTech.sr.model.beans;

import java.io.Serializable;
import java.util.Calendar;

import com.KnappTech.model.LiteDate;
import com.KnappTech.xml.INode;
import com.KnappTech.xml.NodeExpressable;
import com.KnappTech.xml.SimpleNode;

@Deprecated
public class LiteDateBean implements Serializable, NodeExpressable {
	public static final long serialVersionUID = 201102131359L;

	private byte tYear = (byte) 0; // 0 = 1980, 20= 2000, etc.
	private byte tMonth = Calendar.JANUARY; // 0 = January, 11 = December.
	private byte tDay = 1; // 1 to 31.
	
	public LiteDateBean() {
		
	}
	
	public LiteDateBean(LiteDate d) {
		this.settYear((byte) (d.getYear()-LiteDate.BASEYEAR));
		this.settMonth(d.getMonth());
		this.settDay(d.getDay());
	}
	
	public LiteDateBean(INode n) {
		String format = "yyyyMMdd";
		String v = n.getValue();
		LiteDate d = LiteDate.getOrCreate(v, format);
		this.settYear((byte) (d.getYear()-LiteDate.BASEYEAR));
		this.settMonth(d.getMonth());
		this.settDay(d.getDay());
	}

	@Override
	public INode toNode() {
		String format = "yyyyMMdd";
		SimpleNode sn = new SimpleNode("d",getDate().getFormatted(format));
		return sn;
	}
	
	public LiteDate getDate() {
		return LiteDate.getOrCreate(tYear+LiteDate.BASEYEAR, tMonth, tDay);
	}

	public void settYear(byte tYear) {
		this.tYear = tYear;
	}

	public byte gettYear() {
		return tYear;
	}

	public void settMonth(byte tMonth) {
		this.tMonth = tMonth;
	}

	public byte gettMonth() {
		return tMonth;
	}

	public void settDay(byte tDay) {
		this.tDay = tDay;
	}

	public byte gettDay() {
		return tDay;
	}
}
