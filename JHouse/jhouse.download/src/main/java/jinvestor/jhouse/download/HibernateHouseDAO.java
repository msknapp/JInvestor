package jinvestor.jhouse.download;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
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
		
		// this throws a nosuch method error for openSession.
//		Session session = SessionFactoryUtils.getSession(sessionFactory, false);
		
		// this throws an error on the lack of a transaction.
		Session session = sessionFactory.getCurrentSession();
		HouseEntity entity = (HouseEntity) session.get(HouseEntity.class, id);
		return House.fromEntity(entity);
	}

	@Override
	public void save(House house) {
		sessionFactory.getCurrentSession().save(new HouseEntity(house));
	}

	@Override
	public void save(List<House> houses) {
		for (House house : houses) {
			save(house);
		}
	}

	@Override
	public List<House> query(String query) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery(query);
		List<HouseEntity> entities = q.list();
		List<House> houses = new ArrayList<House>();
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
		sessionFactory.getCurrentSession().delete(id);
	}

	@Override
	public void delete(House house) {
		delete(house.getZpid());
	}

	@Override
	public void delete(List<House> houses) {
		for (House house : houses) {
			delete(house);
		}
	}

	@Override
	public int count(String query) {
		return query(query).size();
	}
}