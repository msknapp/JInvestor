package jinvestor.jhouse.core.util;

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

import jinvestor.jhouse.core.util.HouseQueryUtil;
import jinvestor.jhouse.core.House;

import org.junit.Assert;
import org.junit.Test;

public class HouseQueryUtilTest {

	
	@Test
	public void matchesComplex() {
		House h = House.builder().beds((byte)3).baths(2f).acres(1).build();
		Assert.assertTrue(HouseQueryUtil.matches(h, "beds > 2 and (baths = 1 or baths=2) and acres > 0.5"));
		Assert.assertFalse(HouseQueryUtil.matches(h, "beds > 3 and (baths = 1 or baths=2) and acres > 0.5"));
		Assert.assertFalse(HouseQueryUtil.matches(h, "beds > 2 and (baths = 1 or baths=3) and acres > 0.5"));
	}
	
	@Test
	public void matchesSimple() {
		String query = "zpid = '12345'";
		House h = House.builder().zpid(12345L).build();
		Assert.assertTrue(HouseQueryUtil.matches(h, query));
		Assert.assertFalse(HouseQueryUtil.matches(h, "zpid='523'"));
	}
}
