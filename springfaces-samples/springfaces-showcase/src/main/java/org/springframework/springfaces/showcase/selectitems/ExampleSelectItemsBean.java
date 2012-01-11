package org.springframework.springfaces.showcase.selectitems;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExampleSelectItemsBean {

	private Boolean booleanValue;

	private Set<Technology> technologies = new HashSet<Technology>();

	private List<String> strings = new ArrayList<String>();

	private Author author;

	public Boolean getBooleanValue() {
		return booleanValue;
	}

	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	public Set<Technology> getTechnologies() {
		return technologies;
	}

	public void setTechnologies(Set<Technology> technologies) {
		this.technologies = technologies;
	}

	public List<String> getStrings() {
		return strings;
	}

	public void setStrings(List<String> strings) {
		this.strings = strings;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}
}
