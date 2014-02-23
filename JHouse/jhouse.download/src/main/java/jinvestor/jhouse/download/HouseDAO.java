package jinvestor.jhouse.download;

import java.util.List;

import jinvestor.jhouse.House;

public interface HouseDAO {
	void save(House house);
	void save(List<House> house);
	House load(long id);
	List<House> query(String query);
	House queryFirst(String query);
	void delete(long id);
	void delete(House house);
	void delete(List<House> house);
	int count(String query);
}
