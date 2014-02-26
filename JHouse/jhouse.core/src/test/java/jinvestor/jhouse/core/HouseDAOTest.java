package jinvestor.jhouse.core;

/*
 * #%L
 * jinvestor.parent
 * %%
 * Copyright (C) 2014 Michael Scott Knapp
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.text.ParseException;

import jinvestor.jhouse.core.House;
import jinvestor.jhouse.core.HouseDAO;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;

public abstract class HouseDAOTest {

	
	public abstract HouseDAO getDao();
	
	@Test
	public void crud() throws ParseException {
		House testHouse = makeHouse();
		HouseDAO dao = getDao();
		House existing = dao.load(testHouse.getZpid());
		if (existing !=null) {
			dao.delete(existing);
			existing = dao.load(testHouse.getZpid());
			Assert.assertNull(existing);
		}
		int initialSize = dao.count(null);
		dao.save(testHouse);
		int nsize=dao.count(null);
		Assert.assertEquals(initialSize+1,nsize);
		House recovered = dao.load(testHouse.getZpid());
		Assert.assertTrue(testHouse.strictlyEquals(recovered));
		House testHouse2 = testHouse.cloneToBuilder().baths(2).build();
		Assert.assertFalse(testHouse.strictlyEquals(testHouse2));
		dao.save(testHouse2);
		House rec2 = dao.load(testHouse.getZpid());
		Assert.assertFalse(testHouse.strictlyEquals(rec2));
		Assert.assertTrue(testHouse2.strictlyEquals(rec2));
		nsize=dao.count(null);
		Assert.assertEquals(initialSize+1,nsize);
		dao.delete(testHouse);
		House rec3 = dao.load(testHouse.getZpid());
		Assert.assertNull(rec3);
		nsize=dao.count(null);
		Assert.assertEquals(initialSize,nsize);
	}

	private House makeHouse() throws ParseException {
		return House.builder().acres(0.8f).address("123 Rose Lane, CherryVille MN, 12345")
			.baths(3.5f).beds((byte)4).latitude(825938).longitude(-285293)
			.lastSoldDate(DateUtils.parseDate("8/15/13", "dd/mm/yy"))
			.soldPrice(529600).zpid(-98523L).build();
	}
}
