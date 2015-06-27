package com.jumppi.frwk.sql;

public class QueryFilter {
	private String ord = "";
	private String ascDesc = "";
	private String searchField = "";
	private String searchString = "";
	private String searchOper = "";  // eq (=), cn (contained => like)
	private String wildCard = "%";
	private String quote = "'";
	
	public String getOrd() {
		return ord;
	}
	
	public void setOrd(String ord) {
		this.ord = ord;
	}
	
	public String getAscDesc() {
		return ascDesc;
	}
	
	public void setAscDesc(String ascDesc) {
		this.ascDesc = ascDesc;
	}
	
	public String getSearchField() {
		return searchField;
	}
	
	public void setSearchField(String searchField) {
		this.searchField = searchField;
	}
	
	public String getSearchString() {
		return searchString;
	}
	
	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}
	
	public String getSearchOper() {
		return searchOper;    // eq (=), cn (contained => like)
	}
	
	public void setSearchOper(String searchOper) {
		this.searchOper = searchOper;
	}
	
	public String getJoker() {
		return wildCard;
	}
	
	public void setJoker(String joker) {
		this.wildCard = joker;
	}
	
	public String getQuote() {
		return quote;
	}
	
	public void setQuote(String quote) {
		this.quote = quote;
	}
}

