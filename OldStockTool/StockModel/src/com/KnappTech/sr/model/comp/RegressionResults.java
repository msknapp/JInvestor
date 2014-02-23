package com.KnappTech.sr.model.comp;

//import java.io.IOException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.KnappTech.model.KTSet;
import com.KnappTech.model.LiteDate;
import com.KnappTech.sr.model.AbstractKTSet;
import com.KnappTech.sr.model.beans.RegressionResultBean;
import com.KnappTech.sr.model.beans.RegressionResultsBean;
//import com.KnappTech.util.Filter;
//import com.KnappTech.view.report.ReportRow;

public class RegressionResults extends AbstractKTSet<RegressionResult>
		implements KTSet<RegressionResult>, Serializable {
	private static final long serialVersionUID = 201003271025L;
	
	public RegressionResults() {
		super();
	}
	
	public RegressionResults(RegressionResults original) {
		super(original);
	}
	
	private RegressionResults(RegressionResultsBean rrsb) {
		List<RegressionResultBean> rrl  = rrsb.getRegressionResults();
		List<RegressionResult> tempList = new ArrayList<RegressionResult>(rrl.size());
		for (RegressionResultBean rrb : rrl) {
			RegressionResult rr = RegressionResult.create(rrb);
			if (rr!=null) 
				tempList.add(rr);
		}
		addAll(tempList);
	}

	public static RegressionResults create(RegressionResultsBean rb) {
		return new RegressionResults(rb);
	}

	public String[][] getEntries() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getEntryColumnNames() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public RegressionResult getMostRecent() {
		RegressionResult mostRecent = null;
		LiteDate mostRecentDate = LiteDate.EPOCH;
		LiteDate creationDate = null;
		for (RegressionResult res : items) {
			if (res==null) 
				continue;
			creationDate = res.getCreationDate();
			if (creationDate==null){
				System.out.println("Found a regression result with no creation date.");
				continue;
			}
			if (creationDate.before(LiteDate.getOrCreate(2010, (byte)Calendar.JANUARY, (byte)1))) {
				System.out.println("Found a regression result whose creation date is impossible: "+creationDate);
				continue;
			}
			if (creationDate.after(mostRecentDate)) {
				mostRecent=res;
				mostRecentDate = res.getCreationDate();
			} else if (creationDate.equals(mostRecentDate)) {
				if (res.isR2GreaterThan(mostRecent.getCoefficientOfDetermination())) {
					mostRecent=res;
					mostRecentDate = res.getCreationDate();
				}
			}
		}
		return mostRecent;
	}

	public RegressionResult getMostAccurate() {
		RegressionResult mostAccurate = null;
		double highestR2 = -1;
		for (RegressionResult res : items) {
			if (res.getCoefficientOfDetermination()>highestR2) {
				mostAccurate=res;
				highestR2 = res.getCoefficientOfDetermination();
			}
		}
		if (highestR2==0) {
			double lowestVariance = Double.MAX_VALUE;
			for (RegressionResult res : items) {
				if (res.getRegressandVariance()<lowestVariance) {
					mostAccurate=res;
					lowestVariance = res.getRegressandVariance();
				}
			}
		}
		return mostAccurate;
	}

//	@Override
//	public void updateReportRow(Filter<Object> instructions,ReportRow<?> row) {
//		super.updateReportRow(instructions,row);
//	}

	@Override
	public int compareTo(String o) {
		return 0;
	}

//	@Override
//	public boolean save(boolean multiThread) throws InterruptedException {
//		return false;
//	}
	
	public synchronized final RegressionResults clone() {
		return new RegressionResults(this);
	}
}