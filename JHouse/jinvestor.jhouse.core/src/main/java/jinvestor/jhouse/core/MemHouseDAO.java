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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jinvestor.jhouse.core.query.QueryNode;
import jinvestor.jhouse.core.query.QueryPart;
import jinvestor.jhouse.core.util.HouseQueryUtil;

public class MemHouseDAO implements HouseDAO {
	
	private Map<Long,House> index = new HashMap<Long,House>();

	@Override
	public void save(House house) {
		if (house != null) {
			index.put(house.getZpid(), house);
		}
	}

	@Override
	public void save(List<House> homes) {
		if (homes == null) {
			return;
		}
		for (House house : homes) {
			index.put(house.getZpid(), house);
		}
	}

	@Override
	public House load(long id) {
		return index.get(id);
	}

	@Override
	public List<House> query(String query) {
		List<House> homes = new ArrayList<House>();
		for (House house : index.values()) {
			if (HouseQueryUtil.matches(house, query)) {
				homes.add(house);
			}
		}
		return homes;
	}
	
	public static boolean matches(House house,String query) {
		QueryPart qp = QueryNode.parseNode(query).toQueryPart();
		return qp.passes(house);
	}

	@Override
	public void delete(House house) {
		if (house != null) {
			delete(house.getZpid());
		}
	}

	@Override
	public void delete(List<House> homes) {
		if (homes == null) {
			return;
		}
		for (House house : homes) {
			index.remove(house.getZpid());
		}
	}

	@Override
	public int count(String query) {
		if (query==null) {
			return index.size();
		}
		return query(query).size();
	}

	@Override
	public void delete(long id) {
		index.remove(id);
	}

	@Override
	public House queryFirst(String query) {
		List<House> homes = query(query);
		if (homes!=null && !homes.isEmpty()) {
			return homes.get(0);
		}
		return null;
	}
}