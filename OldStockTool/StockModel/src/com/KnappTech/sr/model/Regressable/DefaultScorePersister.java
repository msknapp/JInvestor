package com.KnappTech.sr.model.Regressable;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.KnappTech.model.LiteDate;
import com.KnappTech.util.FileUtil;

public class DefaultScorePersister implements IERScorePersister {
	public static final String DATEFORMAT = "yyyyMMdd";
	private static final String SCOREKEEPERTAG = "ScoreKeeper";
	private static final String SCORESTAG = "Scores";
	private static final String BESTORDERTAG = "Best";
	private static final String LASTUPDATETAG = "LastUpdate";
	private final String saveFilePath;
	
	public DefaultScorePersister(String path) {
		this.saveFilePath = path;
	}

	@Override
	public void lazyLoad() {
		File f = new File(saveFilePath);
		if (!f.exists()) {
			System.err.println("Never found scores file to load from.");
			return;
		}
		try {
			Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
			Element scoreKeeperElement = d.getDocumentElement();
			try {
				String s = scoreKeeperElement.getElementsByTagName(BESTORDERTAG).item(0)
					.getFirstChild().getNodeValue();
				if (s.endsWith(","))
					s = s.substring(0,s.length()-1);
				String[] p = s.split(",");
				for (String b : p) {
					ERScoreKeeper.addToBestOrder(b);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				ERScoreKeeper.setLastUpdate(LiteDate.getOrCreate(scoreKeeperElement.getElementsByTagName(LASTUPDATETAG)
						.item(0).getFirstChild().getNodeValue(),DATEFORMAT));
			} catch (Exception e) {
				e.printStackTrace();
			}
			Element scoresElement = (Element) scoreKeeperElement.getElementsByTagName(SCORESTAG).item(0);
			NodeList nl=scoresElement.getElementsByTagName(ERScore.SCORETAG);
			for (int i=0;i<nl.getLength();i++) {
				try {
					Element sc = (Element)nl.item(i);
					ERScore score = new ERScore(sc);
					ERScoreKeeper.put(score.getID(), score);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void save() {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		sb.append("<"+SCOREKEEPERTAG+">");
		sb.append("<"+LASTUPDATETAG+">");
		sb.append(ERScoreKeeper.getLastUpdate().getFormatted(DATEFORMAT));
		sb.append("</"+LASTUPDATETAG+">");
		sb.append("<"+BESTORDERTAG+">");
		for (String b : ERScoreKeeper.getBestOrder(Integer.MAX_VALUE)) {
			sb.append(b+",");
		}
		sb.append("</"+BESTORDERTAG+">");
		sb.append("<"+SCORESTAG+">");
		for (ERScore score : ERScoreKeeper.getInstance().getItems()) {
			sb.append(score.toXML());
		}
		sb.append("</"+SCORESTAG+">");
		sb.append("</"+SCOREKEEPERTAG+">");
		String xml = sb.toString();
		// write to file.
		String fn = saveFilePath;
		FileUtil.writeStringToFile(fn, xml,true);
	}
}