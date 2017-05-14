package com.spider.core.parse;

public enum CssType {
	
	InterHtml("interHtml"),Text("text"),AllText("allText"),NULL("none");
	
	private String attrName;
	private int index=1;
	private CssType(String attrName) {
		this.attrName =attrName;
	}
	
	@Override
	public String toString() {
		return "attrName :"+this.attrName+", index"+this.index;
	}
}
