package com.spider.core.parse.impl;

import com.spider.core.parse.AbstractElementEditable;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

public class CssEditable extends AbstractElementEditable {
	
	String cssExpr;
	String attrName;

	public CssEditable(String cssExpr, String attrName) {
		this.cssExpr=cssExpr;
		this.attrName=attrName;
	}
	
	public CssEditable(String cssExpr) {
		this(cssExpr,null);
	}

	private String getValue(Element element){
		StringBuilder sb = new StringBuilder();
		for(Node node : element.childNodes()){
			if(node instanceof TextNode){
				TextNode text = (TextNode)node;
				sb.append(text.text());
			}
		}
        return sb.toString();
	}
	private String getText(Element element){
		if(attrName ==null)
			return element.outerHtml();
		else if ("text".equalsIgnoreCase(attrName)){
			return getValue(element);
		}
		else if("innerHtml".equalsIgnoreCase(attrName)){
			return element.html();
		}
		else if("allText".equalsIgnoreCase(attrName))
			return  element.text();
		else
			return element.attr(attrName);
	}
	@Override
	public String cutStr(Element element) {
		List<Element> lists = cutEleList(element);
		if(lists.isEmpty())
			return null;
		return getText(lists.get(0));
	}

	@Override
	public List<String> cutStrList(Element element) {
		ArrayList<String> array = new ArrayList<>();
		List<Element> elements = element.select(cssExpr);
		for(Element ele: elements){
			String text = getText(ele);
			if(text != null)
			 array.add(text);
		}
		return array;
	}

	@Override
	public String cutStr(String str) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> cutStrList(String str) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Element cutElement(Element element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Element> cutEleList(Element element) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
