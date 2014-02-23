package com.KnappTech.sr.persist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.KnappTech.model.Identifiable;
import com.KnappTech.model.IdentifiableList;

public abstract class AbstractPM<E extends Identifiable<String>> implements IPersistenceManager<E> {

	protected static final String DOCSTART = "<?xml version=\"1.0\"?>";
	
	protected final Hashtable<String,E> objects = new Hashtable<String,E>(200);
	protected final ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(15);
	protected final List<Loader> loaders = new ArrayList<Loader>();
	protected final List<Saver> savers = new ArrayList<Saver>();

	@Override
	public void clear() {
		synchronized (objects) {
			objects.clear();
		}
	}
	
	@Override
	public boolean hasLoaded(String id) {
		if (id==null)
			throw new NullPointerException("Must specify an id.");
		if (id.length()<1 || id.equals("null"))
			throw new IllegalArgumentException("Must specify an id.");
		synchronized (objects) {
			return objects.get(id)!=null;
		}
	}

	@Override
	public E getIfLoaded(String id) {
		if (id==null)
			throw new NullPointerException("Must specify an id.");
		if (id.length()<1 || id.equals("null"))
			throw new IllegalArgumentException("Must specify an id.");
		synchronized (objects) {
			return objects.get(id);
		}
	}

	@Override
	public void remove(String id) {
		if (id==null)
			throw new NullPointerException("Must specify an id.");
		if (id.length()<1 || id.equals("null"))
			throw new IllegalArgumentException("Must specify an id.");
		synchronized (objects) {
			objects.remove(id);
		}
	}

	@Override
	public void remove(Collection<String> ids) {
		if (ids==null)
			throw new NullPointerException("Must specify id.");
		if (ids.size()<1)
			throw new IllegalArgumentException("Must specify ids.");
		synchronized (objects) {
			for (String id : ids)
				if (id!=null && id.length()>0 && !id.equalsIgnoreCase("null"))
					objects.remove(id);
		}
	}

	@Override
	public IdentifiableList<E,String> getAllThatAreLoaded(Collection<String> ids) {
		IdentifiableList<E, String> ls = createSetType();
		if (ls==null) // assume this is not supported.
			return null;
		E obj = null;
		ArrayList<E> tempList = new ArrayList<E>(ids.size());
		synchronized (objects) {
			for (String id : ids) {
				if (id==null || id.length()<1 || id.equalsIgnoreCase("null"))
					continue;
				obj = objects.get(id);
				if (obj!=null) 
					tempList.add(obj);
			}
		}
		ls.addAll(tempList);
		return ls;
	}

	@Override
	public IdentifiableList<E,String> getAllThatAreLoaded() {
		IdentifiableList<E, String> ls = createSetType();
		if (ls==null) // assume this is not supported.
			return null;
		// objects.size() is immediate, no need to synchronize on it.
		ArrayList<E> tempList = new ArrayList<E>(objects.size());
		synchronized (objects) {
			for (E obj : objects.values()) {
				if (obj!=null) 
					tempList.add(obj);
			}
		}
		ls.addAll(tempList);
		return ls;
	}

	@Override
	public boolean save(E obj, boolean separateThread) {
		if (obj==null)
			throw new NullPointerException("Must provide an object to save.");
		if (obj.getID()==null || obj.getID().length()<1 || obj.getID().equalsIgnoreCase("null"))
			throw new IllegalArgumentException("Tried saving an object with an illegal id.");
		Saver svr = new Saver(obj);
		if (separateThread) {
			threadPool.execute(svr);
			return true;
		} else {
			svr.run();
			return hasStored(obj.getID());
		}
	}
	
	private class Saver implements Runnable {
		private E obj = null;
		private boolean running = false;
		private boolean finished = false;
		
		public Saver(E obj) {
			if (obj==null)
				throw new NullPointerException("Must provide an object to save.");
			if (obj.getID()==null || obj.getID().length()<1 || obj.getID().equalsIgnoreCase("null"))
				throw new IllegalArgumentException("Tried saving an object with an illegal id.");
			this.obj = obj;
			synchronized (savers) {
				savers.add(this);
			}
		}
		
		public void run() {
			if (finished || running)
				return;
			try {
				running = true;
				doSave(obj);
			} catch(Exception e) {
				System.out.println("Error saving id: "+obj.getID());
				e.printStackTrace(); 
			} finally {
				synchronized (savers) {
					savers.remove(this);
				}
				running = false;
				finished = true;
			}
		}
		
		public boolean isRunning() {
			return running;
		}
		
		public boolean isFinished() {
			return finished;
		}
		
		public E getObj() {
			return obj;
		}
		
		public String getID() {
			return obj.getID();
		}
	}
	
	protected abstract void doSave(E obj);
	protected abstract E doLoad(String id);
	
	private class Loader implements Runnable {
		private String id = null;
		private boolean running = false;
		private boolean finished = false;
		
		public Loader(String id) {
			if (id==null || id.length()<1 || id.equalsIgnoreCase("null"))
				throw new IllegalArgumentException("Tried loading an object with an illegal id.");
			this.id = id;
			synchronized (loaders) {
				loaders.add(this);
			}
		}
		
		public void run() {
			if (finished || running)
				return;
			try {
				running = true;
				E obj = doLoad(id);
				if (obj!=null) {
					synchronized (objects) {
						objects.put(obj.getID(), obj);
					}
				} else if (hasStored(id)) {
					System.out.println("Loader failed to load id that is stored: "+id);
				}
			} catch(Exception e) {
				System.out.println("Error loading id: "+id);
				e.printStackTrace(); 
			} finally {
				synchronized (loaders) {
					loaders.remove(this);
				}
				running = false;
				finished = true;
			}
		}
		
		public boolean isRunning() {
			return running;
		}
		
		public boolean isFinished() {
			return finished;
		}
		
		public String getID() {
			return id;
		}
	}

	@Override
	public boolean saveAll(IdentifiableList<E,String> objects, boolean multiThread)
			throws InterruptedException {
		boolean worked = true;
		synchronized (objects) {
			for (E obj : objects.getItems()) {
				if (obj==null)
					continue;
				if (!save(obj, multiThread))
					worked = false;
			}
		}
		return worked;
	}

	@Override
	public void Save(boolean separateThread) throws InterruptedException {
		synchronized (objects) {
			for (E obj : objects.values())
				if (obj!=null)
					this.save(obj, separateThread);
		}
	}

	@Override
	public IdentifiableList<E,String> getEverythingStored(char letter,boolean separateThread)
			throws InterruptedException {
		Set<String> ids = getAllStoredIDs(letter);
		return getAllThatAreStored(ids, separateThread);
	}

	@Override
	public IdentifiableList<E,String> getEverythingStored(boolean separateThread)
			throws InterruptedException {
		Set<String> ids = getAllStoredIDs();
		return getAllThatAreStored(ids, separateThread);
	}

	@Override
	public IdentifiableList<E,String> getAllThatAreStored(Collection<String> ids, boolean separateThread)
			throws InterruptedException {
		IdentifiableList<E,String> objs = createSetType();
		E obj = null;
		ArrayList<E> tempList = new ArrayList<E>(ids.size());
		for (String id : ids) {
			if (id==null || id.length()<1 || id.equalsIgnoreCase("null"))
				continue;
			obj = getIfStored(id, separateThread);
			if (obj!=null)
				tempList.add(obj);
			else if (hasStored(id))
				System.out.println("Failed to load "+id);
				
		}
		objs.addAll(tempList);
		return objs;
	}

	@Override
	public E getIfStored(String id, boolean separateThread)
			throws InterruptedException {
		E obj = loadIfStored(id, separateThread);
		if (obj!=null)
			return obj;
		return waitFor(id);
	}

	public E loadIfStored(String id, boolean separateThread)
			throws InterruptedException {
		E obj = getIfLoaded(id);
		if (obj!=null)
			return obj;
		if (!hasStored(id))
			return null;
		Loader ldr = getLoader(id);
		if (ldr==null)
			ldr = new Loader(id);
		if (separateThread)
			threadPool.execute(ldr);
		else 
			ldr.run();
		obj = getIfLoaded(id);
		return obj;
	}
	
	public E waitFor(String id) throws InterruptedException {
		E obj = getIfLoaded(id);
		if (obj!=null)
			return obj;
		Loader ldr = getLoader(id);
		if (ldr==null) {// may have become loaded.
			obj = getIfLoaded(id);
			if (obj==null && hasStored(id))
				System.out.println("  :(  :*(  Failed to load object "+id);
			return obj;
		}
		while (!ldr.isFinished()) {
			Thread.sleep(300);
			if (Thread.interrupted())
				throw new InterruptedException();
		}
		return getIfLoaded(id);
	}
	
	protected Loader getLoader(String id) {
		synchronized (loaders) {
			for (Loader ld : loaders) {
				if (ld.getID().equals(id))
					return ld;
			}
		}
		return null;
	}
	
	protected Saver getSaver(String id) {
		synchronized (savers) {
			for (Saver svr : savers) {
				if (svr.getID().equals(id))
					return svr;
			}
		}
		return null;
	}

	@Override
	public void loadEverythingStored() throws InterruptedException {
		Load(true);
	}

	@Override
	public void Load(boolean separateThread) throws InterruptedException {
		Collection<String> ids = getAllStoredIDs();
		for (String id : ids)
			if (id!=null && id.length()>0 && !id.equalsIgnoreCase("null"))
				loadIfStored(id, separateThread);
		waitFor(ids);
	}

	@Override
	public IdentifiableList<E, String> waitForEverythingStored()
			throws InterruptedException {
		return waitForAll();
	} 

	@Override
	public IdentifiableList<E,String> waitFor(Collection<String> ids) throws InterruptedException {
		if (ids==null)
			throw new NullPointerException("Must specify ids to wait for.");
		
		for (String id : ids) {
			if (id!=null && id.length()>0 && !id.equalsIgnoreCase("null"))
				waitFor(id);
		}
		return getAllThatAreLoaded(ids);
	}

	@Override
	public IdentifiableList<E,String> waitForAll() throws InterruptedException {
		Set<String> ids = new HashSet<String>(objects.size());
		synchronized (objects) {
			ids.addAll(objects.keySet());
		}
		synchronized (loaders) {
			for (Loader ldr : loaders) {
				ids.add(ldr.getID());
			}
		}
		return waitFor(ids);
	}

	@Override
	public abstract Class<E> getManagedClass();
	protected abstract IdentifiableList<E,String> createSetType();
}