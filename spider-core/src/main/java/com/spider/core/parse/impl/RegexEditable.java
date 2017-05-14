package com.spider.core.parse.impl;


import com.spider.core.parse.Editor;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.commons.lang3.StringUtils;

public class RegexEditable implements Editor {
	
	private String regexExpr;
	private int group;
	private Pattern regex;

	public RegexEditable(String regexExpr, int group) {
		if(regexExpr == null)
			throw new IllegalArgumentException("regex couldn't be null");
		 if (StringUtils.countMatches(regexExpr, "(") - StringUtils.countMatches(regexExpr, "\\(") ==
	                StringUtils.countMatches(regexExpr, "(?:") - StringUtils.countMatches(regexExpr, "\\(?:")) {
			 regexExpr = "(" + regexExpr + ")";
	        }
		this.regexExpr = regexExpr;
		try{
            regex = Pattern.compile(regexExpr, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

		}
		catch(PatternSyntaxException e){
            throw new IllegalArgumentException("invalid regex", e);
		}

		this.group = group;
	}

	public RegexEditable(String regexExpr) {
		this(regexExpr,1);
	}

	public String[] cutGroup(String text){
		Matcher matcher = regex.matcher(text);
		if(matcher.find()){
			int len = matcher.groupCount()+1;
			String[] resultGroup = new String[ matcher.groupCount()+1];

			for(int i=0 ;i<resultGroup.length;i++){
				resultGroup[i] = matcher.group(i);
			}
            return resultGroup;
		}
		return null;
	}
	
	public List<String[]> cutGroupList(String text){
		Matcher matcher = regex.matcher(text);
		List<String[]> list = new ArrayList<>();
		while(matcher.find()){
			int len= matcher.groupCount()+1;
			String[] group = new String[len];
			for(int i=0;i< len;i++){
				group[i]= matcher.group(i);
			}
			list.add(group);
		}
		return list;
	}

	@Override
	public String cutStr(String str) {
		return cutGroup(str)[group];
	}

	@Override
	public List<String> cutStrList(String str) {
		ArrayList<String> list = new ArrayList<>();
		List<String[]> resultList =cutGroupList(str);
		for(String[] result : resultList)
			list.add(result[group]);
		return list;
	}



	@Override
	public String toString() {
		return "RegexEditable [regexExpr=" + regexExpr + ", group=" + group + ", regex=" + regex + "]";
	}
	
	

}
