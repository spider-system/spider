package com.spider.core.parse;

public interface Editable {
	
	public Editable xpath(String xpath);
	
	public Editable css(String css);
	
	public Editable css(String css, String attrName);
	
	public Editable regex(String regex);
	
	public Editable regex(String regex, int groupIndex);

}
