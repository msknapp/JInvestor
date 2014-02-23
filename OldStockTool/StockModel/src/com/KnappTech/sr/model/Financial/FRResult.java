package com.KnappTech.sr.model.Financial;

import com.KnappTech.model.KTObject;
import com.KnappTech.model.LiteDate;
import com.KnappTech.sr.model.AbstractKTSet;
import com.KnappTech.sr.model.Financial.FinancialIndicator;

public class FRResult extends AbstractKTSet<FinancialIndicator> implements
	KTObject 
{
	private LiteDate creationDate = LiteDate.getOrCreate();
	public double regressandVariance = -1;
	private double estimateValue = -1;
	private LiteDate estimateDate = LiteDate.getOrCreate(1988,(byte)0,(byte)1);
	private double coefficientOfDetermination = 0;
//	@Override
//	public boolean save(boolean multiThread) throws InterruptedException {
//		// TODO Auto-generated method stub
//		return false;
//	}
	
	@Override
	public int compareTo(String arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
}