package com.spider.core.parse.impl;

import com.spider.core.parse.AbstractElementEditable;
import java.util.List;
import org.jsoup.nodes.Element;
import us.codecraft.xsoup.XPathEvaluator;
import us.codecraft.xsoup.Xsoup;

public class XpathEditable extends AbstractElementEditable {

	private XPathEvaluator xpathEvaluater;
	
	public XpathEditable(String xpathExpr){
		this.xpathEvaluater= (XPathEvaluator) Xsoup.compile(xpathExpr);
	}
	
	@Override
	public String cutStr(Element element){
		return xpathEvaluater.evaluate(element).get();
	}
	
	@Override
	public List<String> cutStrList(Element element){
		return xpathEvaluater.evaluate(element).list();
	}
	
	@Override
	public Element cutElement(Element element) {
		List<Element> eleList = cutEleList(element);
		if(!eleList.isEmpty()){
			return eleList.get(0);
		}
		return null;
	}
	
	@Override
	public List<Element> cutEleList(Element element) {
		return xpathEvaluater.evaluate(element).getElements();
	}

}
