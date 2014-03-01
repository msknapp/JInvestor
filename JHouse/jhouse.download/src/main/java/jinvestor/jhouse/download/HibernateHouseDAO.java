package jinvestor.jhouse.download;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jinvestor.jhouse.core.House;
import jinvestor.jhouse.core.HouseDAO;
import jinvestor.jhouse.core.HouseEntity;

@Repository("dao")
@Profile("mysql")
@Transactional
public class HibernateHouseDAO implements HouseDAO {

	private final SessionFactory sessionFactory;

	@Autowired
	public HibernateHouseDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public House load(long id) {
		Session session = sessionFactory.getCurrentSession();
		HouseEntity entity = (HouseEntity) session.get(HouseEntity.class, id);
		return entity==null?null:House.fromEntity(entity);
	}

	@Override
	public void save(House house) {
		sessionFactory.getCurrentSession().saveOrUpdate(new HouseEntity(house));
	}

	@Override
	public void save(List<House> houses) {
		if (houses!=null) {
			for (House house : houses) {
				save(house);
			}
		}
	}

	@Override
	public List<House> query(String query) {
		List<House> houses = new ArrayList<House>();
		if (query==null) {
			query="from house";
		}
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery(query);
		List<HouseEntity> entities = q.list();
		for (HouseEntity he : entities) {
			houses.add(House.fromEntity(he));
		}
		return houses;
	}

	@Override
	public House queryFirst(String query) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery(query);
		HouseEntity entity = (HouseEntity) q.uniqueResult();
		return (entity == null ? null : House.fromEntity(entity));
	}

	@Override
	public void delete(long id) {
		Session session = sessionFactory.getCurrentSession();
		HouseEntity h = (HouseEntity) session.get(HouseEntity.class, id);
		if (h!=null) {
			session.delete(h);
		}
	}

	@Override
	public void delete(House house) {
		if (house!=null) {
			sessionFactory.getCurrentSession().delete(new HouseEntity(house));
		}
	}

	@Override
	public void delete(List<House> houses) {
		for (House house : houses) {
			delete(house);
		}
	}

	@Override
	public int count(String query) {
		if (query == null) {
			Session session = sessionFactory.getCurrentSession();
			Query q = session.createQuery("select count(h) FROM house as h");
			Long n = (Long) q.uniqueResult();
			return n.intValue();
		}
		return query(query).size();
	}
}