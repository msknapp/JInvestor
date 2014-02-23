package com.KnappTech.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {
	
	public static void writeStringToFile(String fullPath,String text,boolean overWrite) {
		File file = new File(fullPath);
		File p = file.getParentFile();
		if (!p.exists())
			p.mkdirs();
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		writeStringToFile(file,text,overWrite);
	}
	
	public static void writeStringToFile(File file,String text,boolean overWrite) {
		FileWriter fw = null;
		if (file.exists() && !overWrite)
			return;
		try {
			File parent = file.getParentFile();
			if (!parent.exists()) {
				parent.mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			fw = new FileWriter(file);
			fw.write(text);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fw!=null) {
				try { fw.close(); } catch (Exception e) {}
			}
		}
	}

	public static String readStringFromFile(String filePath) {
		File file = new File(filePath);
		return readStringFromFile(file);
	}

	public static String readStringFromFile(File file) {
		if (file.exists()) {
			BufferedReader br = null;
			String text = "";
			try {
				br = new BufferedReader(new FileReader(file));
				String line = "";
				while ((line=br.readLine())!=null) {
					text+=line+"\n";
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return text;
		}
		return "";
	}
}
