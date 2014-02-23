package com.KnappTech.sr.persistence;

import java.io.File;

import com.KnappTech.model.Identifiable;
import com.KnappTech.model.IdentifiableList;
import com.KnappTech.sr.persist.AbstractFilePM;
import com.KnappTech.sr.persist.IPersistenceManager;
import com.KnappTech.util.FileUtil;
import com.KnappTech.xml.INode;
import com.KnappTech.xml.NodeExpressable;
import com.KnappTech.xml.NodeParser;

public abstract class PersistenceManager<E extends Identifiable<String>> 
extends AbstractFilePM<E> implements IPersistenceManager<E> {

	private static final String DOCSTART = "<?xml version=\"1.0\"?>";
	
	protected void doSave(E obj) {
		if (obj==null) 
			throw new NullPointerException("Tried saving a null object.");
		if (obj.getID()==null || obj.getID().length()<1 || obj.getID().equals("null")) 
			throw new IllegalArgumentException("Tried saving an object with an invalid id.");
		NodeExpressable bean = getBean(obj);
		if (bean==null){
			System.err.println("Failed to produce a bean from object: "+obj.getID());
			return;
		}
		INode node = bean.toNode();
		if (node==null){
			System.err.println("Failed to produce a node from a bean: "+obj.getID());
			return;
		}
		String s = node.toXML();
		if (s==null || s.length()<1){
			System.err.println("Failed to produce xml text from a node: "+obj.getID());
			return;
		}
		String text = DOCSTART+s;
		String path = getBasePath();
		if (!path.endsWith(File.separator))
			path+= File.separator;
		path+=obj.getID()+".xml";
		File file = new File(path);
		FileUtil.writeStringToFile(file, text, true);
	}
	
	protected E doLoad(String id) {
		String path = getBasePath();
		if (!path.endsWith(File.separator))
			path+= File.separator;
		path+=id+".xml";
		File file = new File(path);
		if (!file.exists()){ 
			System.err.println("Cannot load from this file, it does not exist: "+file);
			return null;
		}
		String text = FileUtil.readStringFromFile(file);
		if (text==null || text.length()<1 || text.equals("null")) {
			System.err.println("There was no text in this file to load from: "+file);
			return null;
		}
		text = text.substring(text.indexOf(">")+1);
		if (text==null || text.length()<1 || text.equals("null")) {
			System.err.println("There was no material in this file to load from: "+file);
			return null;
		}
		INode node = NodeParser.parse(text);
		if (node==null) {
			System.err.println("Parsed a null node from text.");
			return null;
		}
		E obj = getObj(node);
		if (obj==null && hasStored(id)) {
			System.err.println("Persistence Manager doLoad Failed to load an id we have stored. "+id);
			getObj(node);
		}
		return obj;
	}

	@Override
	public abstract Class<E> getManagedClass();
	protected abstract String getBasePath();
	protected abstract IdentifiableList<E,String> createSetType();
	protected abstract NodeExpressable getBean(E obj);
	protected abstract E getObj(INode node);
}