package com.KnappTech.sr.model.user;

import com.KnappTech.model.LiteDate;
import com.KnappTech.model.Reportable;
import com.KnappTech.model.Validatable;
import com.KnappTech.util.Filter;
import com.KnappTech.view.report.ReportRow;

public class Transaction implements Reportable,Validatable {
	private static final long serialVersionUID = 201001181231L;
	private final LiteDate date;
	private final int quantity;
	private final double value;
	private final double fee;
	
	private Transaction(LiteDate date,int quantity,double value,double fee) {
		this.date = date;
		this.quantity = quantity;
		this.value = value;
		this.fee = fee;
	}
	
	public static final Transaction create(LiteDate date,
			int quantity,double value,double fee)
	{
		if (date!=null && quantity!=0) {
			return new Transaction(date,quantity,value,fee);
		}
		return null;
	}
	
	public LiteDate getDate() {
		return date;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public double getValue() {
		return value;
	}

	public double getFee() {
		return fee;
	}

	@Override
	public void updateReportRow(Filter<Object> instructions,ReportRow<?> row) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public boolean isValid() { return true; }
}
