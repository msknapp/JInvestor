package com.KnappTech.sr.ctrl.acq;

import com.KnappTech.sr.model.comp.Companies;
import com.KnappTech.sr.model.comp.Company;
import com.KnappTech.sr.persist.PersistenceRegister;

/**
 * Creates the stock indexes as company objects
 * @author Michael Knapp
 */
public class IndexCreater {
	
	public static void main(String[] args) {
		Companies indexes = null;
		try {
			indexes = (Companies) PersistenceRegister.company().getEverythingStored('^',false);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Company ind1 = Company.createIndicator("^DJA");
		indexes.add(ind1);

		ind1 = Company.createIndicator("^DJI");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^DJT");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^DJU");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^NYA");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^NIN");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^NTM");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^NUS");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^NWL");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^TV.N");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^IXBK");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^NBI");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^IXIC");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^IXK");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^NIF");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^IXID");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^IXIS");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^IXFN");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^IXUT");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^IXTR");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^NDX");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^TV.O");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^OEX");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^MID");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^GSPC");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^SPSUPX");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^SML");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^XAX");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^IIX");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^NWX");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^DWC");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^XMI");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^PSE");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^SOX");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^RUI");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^RUT");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^RUA");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^IRX");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^TNX");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^TYX");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^FVX");
		indexes.add(ind1);
		ind1 = Company.createIndicator("^XAU");
		indexes.add(ind1);
		ind1 = Company.createIndicator("GIM10.CME");
		indexes.add(ind1);
		ind1 = Company.createIndicator("RCIV10.CME");
		indexes.add(ind1);
		ind1 = Company.createIndicator("DJM10.CBT");
		indexes.add(ind1);
		ind1 = Company.createIndicator("ESM10.CME");
		indexes.add(ind1);
		ind1 = Company.createIndicator("YMM10.CBT");
		indexes.add(ind1);
		ind1 = Company.createIndicator("NQM10.CME");
		indexes.add(ind1);
		ind1 = Company.createIndicator("SPM10.CME");
		indexes.add(ind1);
		
		try {
			PersistenceRegister.company().saveAll(indexes,true);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
