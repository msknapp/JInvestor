package com.KnappTech.sr.ctrl.acq;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;

import com.KnappTech.model.LiteDate;
import com.KnappTech.model.UpdateFrequency;
import com.KnappTech.sr.ctrl.parse.ERParser;
import com.KnappTech.sr.model.Regressable.EconomicRecord;
import com.KnappTech.sr.model.Regressable.EconomicRecords;
import com.KnappTech.sr.model.beans.ERStatusBean;
import com.KnappTech.sr.model.constants.SourceAgency;
import com.KnappTech.sr.persist.PersistenceRegister;
import com.KnappTech.util.KTTimer;

public abstract class ERRetriever {
	private final SourceAgency sourceAgency;
	protected int startLetter = (int)('A');
	protected int endLetter = (int)('Z');
	
	public ERRetriever(SourceAgency sourceAgency) {
		this.sourceAgency = sourceAgency;
	}

	protected void retrieve() {
		Collection<EconomicRecord> newRecords = getERList();
		EconomicRecords myRecords = null;
		HashSet<String> idsWithLetter = null;
		for (int c = startLetter;c<=endLetter;c++) {
			PersistenceRegister.er().clear();
			idsWithLetter = new HashSet<String>();
			HashSet<EconomicRecord> recordsWithLetter = new HashSet<EconomicRecord>();
			for (EconomicRecord er : newRecords) {
				String id = er.getID();
				if ((int)(id.charAt(0))==c) {
					idsWithLetter.add(id);
					recordsWithLetter.add(er);
				}
			}
			try {
				System.out.println("waiting for all economic records to load. There are "
						+idsWithLetter.size()+" to load.");
				myRecords = (EconomicRecords) PersistenceRegister.er().getAllThatAreStored(idsWithLetter,true);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Finished loading all economic records.  In memory there are "
					+myRecords.size()+" loaded.");
			ERParser parser = getReader();
			myRecords.addAll(recordsWithLetter);
			update(parser,myRecords.getItems());
		}
	}
	
	private void update(ERParser parser, Collection<EconomicRecord> records) {
		LiteDate initialEndDate;
		LiteDate finalEndDate;
		for (EconomicRecord record : records) {
			try {
				System.out.println("Now working on record: "+record.getID());
				String msg = "ER Retreiver is taking too long with record "+record.getID();
				KTTimer escapeTimer = new KTTimer("er retrieve escaper",60,msg,true);
				if (record.getSourceAgency()==null || 
					record.getSourceAgency().equals(parser.getSourceAgency()))
				{
					initialEndDate = record.getEndDate();
					parser.update(record);
					if (Thread.interrupted()) {
						throw new InterruptedException();
					}
					finalEndDate = record.getEndDate();
					if (record.size()>2) {
						// don't want an excess amount of data stored
						// or used, stick with stuff that is updated often:
						if (record.determineUpdateFrequency().isMoreFrequent(UpdateFrequency.BIMONTHLY)) {
							if ((initialEndDate==null && finalEndDate!=null) || 
								(initialEndDate!=null && finalEndDate!=null && finalEndDate.after(initialEndDate)))
							{
								PersistenceRegister.er().save(record,true);
								System.out.println("  :)  :)  Saved progress for record: "+record.getID());
								try { // should save the status too.
									ERStatusBean bn = new ERStatusBean(record);
									PersistenceRegister.erStatus().save(bn, true);
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else {
								System.out.println("====> NOT SAVING RECORD because its "+
										"end date was unchanged. "+record.getID());
							}
						} else {
							System.out.println("====> NOT SAVING RECORD DESPITE SUCCESSFUL UPDATE because its "+
									"update frequency is too low. "+record.getID());
						}
					} else {
						System.out.println("After the update, there are no/few valid entries.");
					}
					if (Thread.interrupted()) {
						throw new InterruptedException();
					}
				}
				String id = record.getID();
				PersistenceRegister.er().remove(id);
				escapeTimer.stop();
			} catch (InterruptedException e) { // just continue.
//			} catch (RuntimeException e) {
//				e.printStackTrace();
//				System.err.println("Unable to update "+record.getID());
//			} catch (Exception e) {
//				String msg = "Exception caught while downloading Economic Record history, " +
//						"see EconomicRecords update function.";
//				e.printStackTrace();
//				System.err.println(msg);
			}
			Thread.interrupted(); // resets the interrupted value.
		}
	}
	
	private Collection<EconomicRecord> getERList() {
		File file = new File(getSeriesPath());
		Scanner fr = null;
		ArrayList<EconomicRecord> ers = new ArrayList<EconomicRecord>();
		try {
			fr = new Scanner(file);
			while (fr.hasNext()) {
				EconomicRecord er = EconomicRecord.createInstance(fr.next(), sourceAgency);
				if (!ers.add(er))
					System.err.println("Warning: failed to add an economic record to the set.");
			}
			if (ers.size()<1)
				System.err.println("Warning: failed to get the er list.");
		} catch (Exception e) {
			System.err.println("Exception caught while trying to get ER list.");
			e.printStackTrace();
		}
		return ers;
	}
	
	public abstract String getSeriesPath();
	public abstract ERParser getReader();
}