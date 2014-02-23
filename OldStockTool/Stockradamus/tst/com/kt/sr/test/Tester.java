package com.kt.sr.test;

import com.KnappTech.sr.model.Financial.FinancialHistory;
import com.KnappTech.sr.persist.PersistenceRegister;


public class Tester {
	
	public Tester() {}
	
	public static void main(String[] args) {
		FinancialHistory fh=null;
		try {
			PersistenceRegister.financialType().getEverythingStored(false);
			fh = PersistenceRegister.financial().getIfStored("A", false);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(fh);
		
	}
	
}