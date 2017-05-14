package com.spider.core.parse;

import java.util.List;
import org.jsoup.nodes.Element;

public interface CutElement {

	public String cutStr(Element element);
	
	public List<String> cutStrList(Element element);
}
