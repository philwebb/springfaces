package org.springframework.springfaces.traveladvisor.integrationtest.page.selectitems;

public class SelectOption {

	private String value;
	private String selected;
	private String text;

	public SelectOption(String value, String selected, String text) {
		this.value = value;
		this.selected = selected;
		this.text = text;
	}

	public String getValue() {
		return value;
	}

	public String getSelected() {
		return selected;
	}

	public String getText() {
		return text;
	}
}
