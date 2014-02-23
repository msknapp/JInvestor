package com.kt.sr.test.junit;

import org.junit.Test;
import org.junit.Assert;

import com.KnappTech.sr.model.comp.Company;
import com.KnappTech.sr.persistence.CompanyManager;

public class TestCompanyManager {
	
	
	@Test
	public void testGetCompany() {
		try {
			Company company = CompanyManager.getIfStored("SPIL");
			Assert.assertNotNull(company);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
