package jinvestor.jhouse.download;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jinvestor.jhouse.House;
import jinvestor.jhouse.util.HouseQueryUtil;

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