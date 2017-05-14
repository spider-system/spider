package com.spider.core.parse;

import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public abstract class AbstractElementEditable implements Editor,CutElement {
	
	@Override
	public String cutStr(String str) {
		if(null != str){
			return cutStr(Jsoup.parse(str));
		}
		return null;
	}
	
	@Override
	public List<String> cutStrList(String str) {
		if(null != str){
			return cutStrList(Jsoup.parse(str));
		}
		return null;
	}
	public Element cutElement(String str){
		if(null != str){
			return cutElement(Jsoup.parse(str));
		}
		return null;
	}
	
	public List<Element> cutEleList(String str){
		if(null != str){
			cutEleList(Jsoup.parse(str));
		}
		return new ArrayList<Element>();
	}
	public abstract Element cutElement(Element element);
	
	public abstract List<Element> cutEleList(Element element);
	
}
