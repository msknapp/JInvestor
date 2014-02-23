package com.KnappTech.sr.ctrl.parse;

import java.io.IOException;
import java.util.Calendar;

import com.KnappTech.model.LiteDate;
import com.KnappTech.sr.model.Regressable.EconomicRecord;
import com.KnappTech.sr.model.constants.SourceAgency;
import com.KnappTech.util.ConnectionCreater;

public abstract class ERParser {
	protected EconomicRecord record = null;
	protected String data = "";

	public void update(EconomicRecord economicRecord) throws InterruptedException {
		record = economicRecord;
		if (economicRecord==null) 
			throw new NullPointerException("Given a null economic record to update.");
		LiteDate fiveDaysAgo = LiteDate.getOrCreate(Calendar.DAY_OF_MONTH,-5);
		LiteDate tenDaysAgo = LiteDate.getOrCreate(Calendar.DAY_OF_MONTH,-10);
		LiteDate lsu = record.getLastSuccessfulUpdate();
		LiteDate initialEndDate = record.getEndDate();
		if (lsu!=null && lsu.onOrAfter(fiveDaysAgo) && initialEndDate.onOrAfter(tenDaysAgo))
			System.out.println("Skipping update for "+record.getID()+
					", it was last successfully updated on "+
					record.getLastSuccessfulUpdate().toString());
		System.out.println(record.getID()+" needs update, trying now.");
		record.setLastAttemptedUpdate(LiteDate.getOrCreate());
		try {
			if (!isConstantURL() || data.equals(""))
				data = Generic.retrieveWebPage(getConnectionCreater());
			if (data==null || data.equals("") || data.equals("null"))
				throw new IllegalStateException("Failed to download data, cannot update "+record.getID());
			if (record.isLocked())
				throw new IllegalStateException("The record is locked, cannot update "+record.getID());
			updateER();
			if (record.getEndDate()==null) {
				System.out.println("==> update failed for: "+record.getID()+", end date is null.");
				return;
			}
			if (!record.getEndDate().after(initialEndDate)) {
				System.out.println("==> update failed for: "+record.getID()+
						", end date is unchanged from: "+record.getEndDate()+", initial: "+initialEndDate);
				return;
			}
			System.out.println("... update worked for: "+record.getID());
			record.setLastSuccessfulUpdate(LiteDate.getOrCreate());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			if (e instanceof InterruptedException)
				throw (InterruptedException)e;
			e.printStackTrace();
		}
 	}
	
	public abstract SourceAgency getSourceAgency();
	protected abstract ConnectionCreater getConnectionCreater();
	protected abstract boolean isConstantURL();
	protected abstract void updateER() throws InterruptedException;
}
