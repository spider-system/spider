package com.spider.core.parse.impl;

import com.jayway.jsonpath.JsonPath;
import com.spider.core.parse.Editor;
import java.util.ArrayList;
import java.util.List;

public class JsonPathEditable implements Editor {

	private String jsonPathExpr;
	
	private JsonPath jsonPath;
	
	
	public JsonPathEditable(String jsonPathExpr) {
		this.jsonPathExpr = jsonPathExpr;
        this.jsonPath = JsonPath.compile(this.jsonPathExpr);
	}

	@Override
	public String cutStr(String str) {
		Object obj = jsonPath.read(str);
		if(obj == null)
			return null;
		if(obj instanceof List){
			List list = (List)obj;
			if(list != null && list.size() > 0)
				return list.iterator().next().toString();
		}
		return obj.toString();
	}

	@Override
	public List<String> cutStrList(String str) {
		Object obj = jsonPath.read(str);
		List<String> list = new ArrayList<>();
		if(obj == null)
			return list;
		if(obj instanceof List){
			List<Object> objList =(List<Object>)obj;
			for(Object oo: objList)
				list.add(String.valueOf(oo));
		}
		else
			list.add(String.valueOf(obj));
		return list;
	}

}
