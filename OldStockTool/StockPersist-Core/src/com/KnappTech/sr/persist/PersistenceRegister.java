package com.KnappTech.sr.persist;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;

import com.KnappTech.sr.model.Financial.FinancialEntryType;
import com.KnappTech.sr.model.Financial.FinancialHistory;
import com.KnappTech.sr.model.Regressable.EconomicRecord;
import com.KnappTech.sr.model.Regressable.PriceHistory;
import com.KnappTech.sr.model.beans.ERStatusBean;
import com.KnappTech.sr.model.beans.PriceHistoryStatusBean;
import com.KnappTech.sr.model.comp.Company;
import com.KnappTech.sr.model.comp.Industry;
import com.KnappTech.sr.model.comp.Sector;
import com.KnappTech.sr.model.user.Investor;

/**
 * Persistence Managers get Registered here
 * @author michael
 */
public class PersistenceRegister {
	
	private static final Hashtable<Class<?>,IPersistenceManager<?>> MANAGERS = new 
		Hashtable<Class<?>,IPersistenceManager<?>>();
	
	static {
		lazyInitialize();
	}
	
	public static final IPersistenceManager<?> get(Class<?> clazz) {
		return MANAGERS.get(clazz);
	}
	
	public static final void set(IPersistenceManager<?> manager) {
		if (manager==null)
			return;
		if (MANAGERS.get(manager.getManagedClass())==null)
			MANAGERS.put(manager.getManagedClass(), manager);
	}
	
	public static final IPersistenceManager<EconomicRecord> er() {
		return (IPersistenceManager<EconomicRecord>) MANAGERS.get(EconomicRecord.class);
	}
	
	public static final IPersistenceManager<PriceHistory> ph() {
		return (IPersistenceManager<PriceHistory>) MANAGERS.get(PriceHistory.class);
	}
	
	public static final IPersistenceManager<Company> company() {
		return (IPersistenceManager<Company>) MANAGERS.get(Company.class);
	}
	
	public static final IPersistenceManager<FinancialHistory> financial() {
		return (IPersistenceManager<FinancialHistory>) MANAGERS.get(FinancialHistory.class);
	}
	
	public static final IPersistenceManager<Sector> sector() {
		return (IPersistenceManager<Sector>) MANAGERS.get(Sector.class);
	}
	
	public static final IPersistenceManager<Industry> industry() {
		return (IPersistenceManager<Industry>) MANAGERS.get(Industry.class);
	}
	
	public static final IPersistenceManager<Investor> investor() {
		return (IPersistenceManager<Investor>) MANAGERS.get(Investor.class);
	}

	public static IPersistenceManager<FinancialEntryType> financialType() {
		return (IPersistenceManager<FinancialEntryType>) MANAGERS.get(FinancialEntryType.class);
	}

	public static IPersistenceManager<ERStatusBean> erStatus() {
		return (IPersistenceManager<ERStatusBean>) MANAGERS.get(ERStatusBean.class);
	}

	public static IPersistenceManager<PriceHistoryStatusBean> phStatus() {
		return (IPersistenceManager<PriceHistoryStatusBean>) MANAGERS.get(PriceHistoryStatusBean.class);
	}
	
	private static final void lazyInitialize() {
		lazyInitialize("EconomicRecord");
		lazyInitialize("PriceHistory");
		lazyInitialize("Investor");
		lazyInitialize("Industry");
		lazyInitialize("Sector");
		lazyInitialize("Company");
		lazyInitialize("FinancialHistory");
		lazyInitialize("FinancialType");
		lazyInitialize("ERStatusBean");
		lazyInitialize("PriceHistoryStatusBean");
	}
	
	private static final void lazyInitialize(String name) {
		String fullName = "com.KnappTech.sr.persistence."+name+"Manager";
		boolean worked = false;
		try {
			Class<IPersistenceManager> c = (Class<IPersistenceManager>) Class.forName(fullName);
			Method m = c.getMethod("getInstance", null);
			m.invoke(c, null);
			worked = true;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if (!worked) 
			System.err.println("Failed to initialize "+name);
	}
}