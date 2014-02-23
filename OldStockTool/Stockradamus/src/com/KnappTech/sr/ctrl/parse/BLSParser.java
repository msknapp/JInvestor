package com.KnappTech.sr.ctrl.parse;

import java.util.Calendar;
import java.util.List;

import com.KnappTech.model.LiteDate;
import com.KnappTech.sr.model.constants.SourceAgency;
import com.KnappTech.util.ConnectionCreater;
import com.KnappTech.util.DefaultConnectionCreater;
import com.KnappTech.util.Util;
import com.KnappTech.xml.INode;
import com.KnappTech.xml.NodeParser;
import com.KnappTech.xml.SimpleImmutableNode;

public class BLSParser extends ERParser {
//	private static final transient String STARTTEXT = "<PRE STYLE=\"csv-output\">";
	
	@Override
	protected ConnectionCreater getConnectionCreater() {
		return DefaultConnectionCreater.create(createURL());
	}
	
	protected String createURL() {
		String from_year = "";
		if (record.getEndDate()!=null){
			from_year = Integer.toString(record.getEndDate().getYear());
		} else {
			from_year = "1988";
		}
		String to_year = Integer.toString(LiteDate.getOrCreate().getYear());
		String str = "http://data.bls.gov/pdq/SurveyOutputServlet?"+
		"request_option=get_data&"+
		"reformat=true&"+
		"from_results_page=true&"+
		"initial_request=false&"+
		"output_type=default&"+
		"years_option=specific_years&"+
		"from_year="+from_year+"&"+
		"to_year="+to_year+"&"+
		"output_view=data|&"+
		"periods_option=all_periods&"+
		"output_format=text&"+
		"delimiter=comma&"+
		"include_graphs=false&"+
		"data_tool=surveymost&"+
		"series_id="+record.getID()+"&"+
		"original_output_type=default";
		return str;
	}
	
	@Override
	public SourceAgency getSourceAgency() {
		return SourceAgency.BLS;
	}
	
	protected void updateER() throws InterruptedException {
		data = data.toLowerCase();
		while (wrongTable(data))
			data = data.substring(data.indexOf("<table",1));
		data = data.substring(0,data.indexOf("/table>")+7);
		// limited to the table.
		while (data.contains("&nbsp;"))
			data = data.replace("&nbsp;", "");
		while (data.contains("<br>"))
			data = data.replace("<br>", "");
		if (!data.contains("<thead") || !data.contains("<tbody"))
			return; // abort.
		String headData = getHeadData(data);
		String bodyData = getBodyData(data);
		SimpleImmutableNode headNode = new SimpleImmutableNode(NodeParser.parse(headData));
		SimpleImmutableNode bodyNode = new SimpleImmutableNode(NodeParser.parse(bodyData));
		int startMonth = 0;
		int rate = 1;
		List<INode> headingNodes = headNode.getSubNode("tr").getSubNodes("th");
		INode col1 = headingNodes.get(1);
		String str = col1.getValue();
		if (str.equals("annual")) {
			rate = 12;
		} else if (str.contains("qtr")) {
			int i = Integer.parseInt(str.substring(3,4));
			startMonth = (i-1)*3;
			if (startMonth<Calendar.JANUARY || startMonth>Calendar.DECEMBER) {
				System.err.println("invalid month parsed, see BLS Parser.");
			}
			rate = 3;
		} else if (Util.isMonthAbbreviation(str)) {
			startMonth = Util.getMonthFromAbbreviation(str);
		} else {
			System.err.println("Could not recognize the update frequency type for "+str);
		}
		if (Thread.interrupted()) {
			throw new InterruptedException();
		}
		List<INode> rowNodes = bodyNode.getSubNodes("tr");
		String sYear = "";
		int year=0;
		LiteDate d=null;
		for (INode rowNode : rowNodes) {
			sYear = rowNode.getSubNode("th").getValue();
			sYear = sYear.trim();
			year = Integer.parseInt(sYear);
			List<INode> cells = rowNode.getSubNodes("td");
			int currentMonth = startMonth;
			String val = "";
			double vlu = 0;
			for (INode cell : cells) {
				if (currentMonth>11)
					break;
				d = LiteDate.getOrCreate(year, (byte)currentMonth, (byte)1);
				currentMonth+=rate;
				val = cell.getValue();
				if (val==null)
					continue;
				val = val.trim();
				if (val.equals("&nbsp;"))
					continue;
				try {
					vlu = Double.parseDouble(val);
					record.addOrReplace(d, vlu);
				} catch (Exception e) {
				}
				if (Thread.interrupted())
					throw new InterruptedException();
			}
		}
	}
	
	private String getBodyData(String data) {
		String s = "<tbody";
		String end = "</tbody>";
		return data.substring(data.indexOf(s),data.indexOf(end)+end.length());
	}

	private String getHeadData(String data) {
		String s = "<thead";
		String end = "</thead>";
		return data.substring(data.indexOf(s),data.indexOf(end)+end.length());
	}

	private String fixInputs(String data) {
		String s = "<input";
		int i = -1;
		int j = -1;
		while ((i=data.indexOf(s,i+1))>=0) {
			j = data.indexOf(">",i);
			char c = data.charAt(j-1);
			if (c=='/')
				continue;
			data = data.substring(0,j)+"/"+data.substring(j);
		}
		return data;
	}

	private boolean wrongTable(String data) {
		int first = data.indexOf("<");
		int next = data.indexOf(">",first);
		String s = data.substring(first,next+1);
		return (!s.contains("class=\"regular-data\""));
	}

	private static String everythingBeforeWhiteSpace(String str) {
		// TODO Auto-generated method stub
		Character ch = 'a';
		int endIndex = -1;
		for (int i = 0;i<str.length();i++) {
			ch = new Character(str.charAt(i));
			if (!Character.isDigit(ch) && 
				ch!='(' && ch!=')' && ch!='.')
			{
				endIndex = i;
				break;
			}
		}
		String temp = str;
		if (endIndex>=0) {
			temp = str.substring(0, endIndex);
		}
		return temp;
	}

	@Override
	protected boolean isConstantURL() {
		return false;
	}
}
