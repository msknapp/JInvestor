package com.KnappTech.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.util.Properties;

import com.KnappTech.model.KTObject;

public class PersistenceHelper {

	private static String propLoc = "props.properties";
	private static Properties properties;
	
	static {
		InputStream is = null;
		try {
			is = PersistenceHelper.class.getResourceAsStream(propLoc);
			properties = new Properties();
			properties.load(is);
		} catch (Exception e) {
			
		}
	}
	
	public static KTObject load(Class<? extends Object> loadClass, String metaData) {
		String fullPath = getFullPath(loadClass,metaData);
		ObjectInputStream ois = null;
        FileInputStream fis = null;
        KTObject myObject = null;
        File file = null;
        try {
	        try {
	        	file = setFile(fullPath);
	            fis = new FileInputStream(file);
	        	ois = new ObjectInputStream(fis);
	            myObject = (KTObject)ois.readObject();
	        } catch (InvalidClassException e) {
	        	String msg = "The class found was invalid when trying to load the object from: "+'\n' + 
	        			fullPath + '\n' + 
	        			"Processing will resume with a null object instead." + 
	        			"\nThe error message is:\n"+e.getMessage()+"\nThrown from DataStorage loadObject method.";
	        	// Runner.stockTrendLog.log(Level.SEVERE, msg, e);
	        	System.err.println(msg);
	        } catch (IOException e) {
	        	String msg = "There was an IO Exception when trying to load the object from: " + '\n' +
	        			fullPath+'\n' +
	        			"Processing will resume with a null object instead." + 
	        			"\nThe error message is:\n"+e.getMessage()+"\nThrown from DataStorage loadObject method.";
	        	// Runner.stockTrendLog.log(Level.SEVERE, msg, e);
	        	System.err.println(msg);
	        } catch (ClassNotFoundException e) {
	        	String msg = "The class was not found when trying to load the object from: " + '\n' +
	        			fullPath + "\nThe error message is:\n"+e.getMessage()+
	        			"\nThrown from DataStorage loadObject method.";
	        	// Runner.stockTrendLog.log(Level.SEVERE, msg, e);
	        	System.err.println(msg);
	        } catch (ClassCastException e) {
	        	String msg = "There was a mismatch between a stored data type and its specified type (ClassCastException)\n" +
	        			"when trying to load the object from: " + '\n' +
	        			fullPath + "\nThe error message is:\n"+e.getMessage()+
		    			"\nThrown from DataStorage loadObject method.";
		    	// Runner.stockTrendLog.log(Level.SEVERE, msg, e);
	        	System.err.println(msg);
		    	myObject = null;
	        } finally {
	            if (fis != null) {
	            	fis.close();
	            }
	            if (ois!= null) {
	            	ois.close();
	            }
	        }
        } catch (IOException e) {
            // does nothing here.
        }
        return myObject;
	}
	
    private static File setFile(String relativeFile){
        File myFile = new File(relativeFile);
        if (!myFile.exists()){
            try {
                myFile.createNewFile();
            } catch (IOException e) {
            	String msg = "Problem while setting the file " + relativeFile;
            	System.err.println(msg);
            }
        }
        return myFile;
    }
    
    public static void saveStringToFile(String contents, String fullPath) {
    	File file = new File(fullPath);
    	File parentDir = file.getParentFile();
    	if (!parentDir.exists()) {
    		parentDir.mkdirs();
    	}
    	FileWriter fw = null;
    	BufferedWriter bw = null;
    	try {
    		fw = new FileWriter(file);
    		bw = new BufferedWriter(fw);
    		bw.write(contents);
    		bw.close();
    		fw.close();
    	} catch (IOException e) {
    		
    	} finally {
    		try {
    			bw.close();
    			fw.close();
    		} catch (Exception e) {}
    	}
    }
    
    public static String loadStringFromFile(String fullPath) {
		File file = new File(fullPath);
		FileReader fr = null;
		BufferedReader br =null;
		StringBuilder page = new StringBuilder();
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			int i = -1;
			while ((i=br.read())>=0) {
				page.append((char)i);
			}
			br.close();
			fr.close();
		} catch (IOException e) {
			
		} finally {
			try {
				br.close();
				fr.close();
			} catch (Exception e) {}
		}
		return page.toString();
    }
	
//	public static void save(Persistable object) {
//		String metaData = "";
//		if (object instanceof SRCompanySet) {
//			SRCompanySet srcs = (SRCompanySet)object;
//			metaData = String.valueOf(srcs.getLetter());
//		}
//		String fullPath = getFullPath(object.getClass(),metaData);
//		ObjectOutputStream oos = null;
//        FileOutputStream fos = null;
//        try {
//	        try {
//	        	File myFile = setFile(fullPath);
//	        	fos = new FileOutputStream(myFile);
//	            oos = new ObjectOutputStream(fos);
//	            oos.writeObject(object);
//	        } catch (IOException e) {
//	        	String msg = "Problem while saving the object";
//	        	Runner.stockTrendLog.log(Level.SEVERE, msg, e);
//	        	e.printStackTrace();
//	        } finally {
//	            if (fos != null) {
//	            	fos.close();
//	            }
//	            if (oos!=null){
//	                oos.close();
//	            }
//	        }
//        } catch (IOException e) {
//        	String msg = "Problem while saving the object";
//        	Runner.stockTrendLog.log(Level.SEVERE, msg, e);
//        	e.printStackTrace();
//        }
//	}
	
	private static String getFullPath(Class<? extends Object> loadClass, String metaData){
		String objectPropName = loadClass.getName()+"_dir";
		String projectPath = properties.getProperty("base_dir");
		String relativePath = properties.getProperty(objectPropName);
		if (loadClass.getName().contains("SRCompanySet")) {
			relativePath += File.pathSeparator+metaData;
		}
		String fullPath = projectPath + relativePath;
		return fullPath;
	}
}
