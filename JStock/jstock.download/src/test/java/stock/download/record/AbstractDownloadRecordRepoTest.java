package stock.download.record;

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

import org.junit.Assert;
import org.junit.Test;

import stock.core.TimeSpan;

public abstract class AbstractDownloadRecordRepoTest {
	
	@Test
	public void create() throws ParseException {
		String uri = "http://localhost/foo";
		String tickerSymbol="GE";
		DownloadRecord dr = new DownloadRecord(uri, tickerSymbol);
		dr.setAppVersion("1");
		dr.setDataTimeSpan(new TimeSpan("2001-01-01"));
		dr.setMd5("foo");;
		dr.setResponseCode(202);
		dr.setResponseMessage("haha");
		dr.setSaveFilePath("/var/local/blah");
		dr.setServiceDenied(false);
		dr.setSystem("mine");
		
		DownloadRecordRepo repo = getRepo();
		Assert.assertEquals(0,repo.count());
		repo.create(dr);
		Assert.assertEquals(1,repo.count());
		DownloadRecord recovered1 = repo.getLatest(uri);
		Assert.assertEquals(dr,recovered1);
		DownloadRecord recovered2 = repo.getLatestSuccess(uri);
		Assert.assertEquals(dr,recovered2);
		DownloadRecordQuery query = new DownloadRecordQuery();
		query.setUri(uri);
		DownloadRecord rec = repo.get(query).get(0);
		Assert.assertEquals(dr,rec);
		Assert.assertEquals(dr.getMd5(),rec.getMd5());
		query.setUri("lkjew");
		Assert.assertTrue(repo.get(query).isEmpty());
		
		DownloadRecord evil = new DownloadRecord("lkjae", "EOIJ");
		repo.create(evil);
		query.setUri(null);
		query.setTickerSymbol("EOIJ");
		DownloadRecord wjer = repo.get(query).get(0);
		Assert.assertNotNull(wjer);
		
		DownloadRecord cln = new DownloadRecord(dr);
		cln.setMd5("jwrliwlijwe");
		repo.update(cln);
		Assert.assertEquals(2,repo.count());
		rec = repo.getLatest(uri);
		Assert.assertEquals("jwrliwlijwe",rec.getMd5());
		
		repo.delete(rec);
		Assert.assertEquals(1,repo.count());
		
	}

	@Test
	public void getLatest() {
	}

	@Test
	public void getLatestSuccess() {
	}

	@Test
	public void get() {
	}

	@Test
	public void update() {
	}

	@Test
	public void delete() {
	}

	@Test
	public void count() {
	}
	
	public abstract DownloadRecordRepo getRepo();
}
