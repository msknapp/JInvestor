package com.KnappTech.sr.model.Regressable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.KnappTech.model.LiteDate;

public class ERScoreKeeper {
	private static final ERScoreKeeper instance = new ERScoreKeeper();
	private Hashtable<String,ERScore> scores = new Hashtable<String,ERScore>();
	private static List<String> bestOrder = new ArrayList<String>();
	private static LiteDate lastUpdate = null;
	private static IERScorePersister persister = null;
	
	private static boolean loading = false;
	
	private ERScoreKeeper() {}
	
	public static List<String> getBestOrder(int howMany) {
		lazyLoad();
		if (howMany>bestOrder.size() || howMany<1)
			howMany= bestOrder.size();
		return Collections.unmodifiableList(bestOrder.subList(0, howMany));
	}
	
	public static List<String> getAllUsed() {
		lazyLoad();
		List<String> allUsed = new ArrayList<String>();
		Iterator<String> iter = bestOrder.iterator();
		String id;
		while (iter.hasNext()) {
			id=iter.next();
			if (!getOrCreate(id).isUsed()) 
				break;
			allUsed.add(id);
		}
		return Collections.unmodifiableList(allUsed);
	}
	
	public static void determineBest() {
		// reset existing order.
		for (ERScore sc : instance.scores.values()) {
			sc.setOrder(999);
		}
		
		ArrayList<ERScore> tempScores = new ArrayList<ERScore>();
		LiteDate minEndDate = LiteDate.getOrCreate(Calendar.MONTH, -2);
		LiteDate maxStartDate = LiteDate.getOrCreate(Calendar.YEAR, -12);
		for (ERScore s : instance.scores.values()) {
			if (s.getNumberOfEntries()<240)
				continue;
			if (s.getEndDate().before(minEndDate))
				continue;
			if (s.getStartDate().after(maxStartDate))
				continue;
			tempScores.add(s);
		}
		Collections.sort(tempScores,new ERComp());
		// have to still remove what is redundant.
		// that is what's done here:
		int j = 0;
		ERScore sc1 = null;
		ERScore sc2 = null;
		while (j<tempScores.size()-1) {
			sc1 = tempScores.get(j);
			for (int i =tempScores.size()-1;i>j;i--) {
				sc2 = tempScores.get(i);
				if (sc1.isRedundant(sc2.getID())) {
					tempScores.remove(i);
				}
			}
			j++;
		}
		// finished removing redundant records.
		// save results.
		bestOrder.clear();
		for (int i = 0;i<tempScores.size();i++) {
			tempScores.get(i).setOrder(i);
			bestOrder.add(tempScores.get(i).getID());
		}
		if (persister!=null)
			persister.save();
	}
	
	private static class ERComp implements Comparator<ERScore>  {
		@Override
		public int compare(ERScore o1, ERScore o2) {
			if (o1==null && o2==null)
				return 0;
			if (o1==null)
				return -1;
			if (o2==null)
				return 1;
			int i = o2.getTimesUsed()-o1.getTimesUsed();
			if (i==0) {
				int j = o2.getNumberOfEntries()-o1.getNumberOfEntries();
				if (j==0)
					return 0;
				i = (j>0 ? 1 : -1);
			}
			return i;
		}
	}

	public static void lazyLoad() {
		if (instance.scores.size()>0)
			return;
		if (persister==null)
			throw new NullPointerException("Must set a persister before using ERScore data, use:\n" +
					"\tsetPersister(new DefaultPersister(\"C:\\\\yourpath\\\\scores.xml\");");
		try {
			loading = true;
			persister.lazyLoad();
		} finally {
			loading = false;
		}
	}
	
	public static void put(String id,ERScore score) {
		if (loading) 
			instance.scores.put(id, score);
	}
	
	public static void addToBestOrder(String id) {
		if (loading)
			bestOrder.add(id);
	}
	
	public static ERScoreKeeper getInstance() {
		return instance;
	}

	public static void setLastUpdate(LiteDate lastUpdate) {
		ERScoreKeeper.lastUpdate = lastUpdate;
	}

	public static LiteDate getLastUpdate() {
		return lastUpdate;
	}
	
	public static void clear() {
		instance.scores.clear();
		bestOrder.clear();
	}
	
	public static ERScore getOrCreate(String id) {
		ERScore score = instance.scores.get(id);
		if (score==null) {
			score = new ERScore(id);
			instance.scores.put(id, score);
		}
		return score;
	}
	
	public static void incrementTimesUsed(String id) {
		getOrCreate(id).incrementTimesUsed();
	}
	
	public static void markSimilar(String id,String id2) {
		getOrCreate(id).markSimilar(id2);
		getOrCreate(id2).markSimilar(id);
	}

	public Collection<ERScore> getItems() {
		return Collections.unmodifiableCollection(scores.values());
	}

	public static void setPersister(IERScorePersister persister) {
		ERScoreKeeper.persister = persister;
	}

	public static IERScorePersister getPersister() {
		return persister;
	}
}