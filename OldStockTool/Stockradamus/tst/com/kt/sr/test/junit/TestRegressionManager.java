package com.kt.sr.test.junit;

import org.junit.Test;
import org.junit.Assert;

import com.KnappTech.sr.ctrl.reg.RegressionLauncher;
import com.KnappTech.sr.model.comp.Company;
import com.KnappTech.sr.persistence.CompanyManager;
import com.KnappTech.util.MethodResponse;

public class TestRegressionManager {
	@Test
	public void testShouldRegress() {
		Company c;
		try {
			c = CompanyManager.getIfStored("SPIL");
			RegressionLauncher rm = new RegressionLauncher(null);
			MethodResponse m = rm.shouldRegressCompany(c);
			Assert.assertTrue(m.toString(), m.isPass());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
