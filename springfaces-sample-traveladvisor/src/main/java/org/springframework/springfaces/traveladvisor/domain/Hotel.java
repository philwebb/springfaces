package org.springframework.springfaces.traveladvisor.domain;

public class Hotel {

	private String key;
	private String name;

	public Hotel(String key, String name) {
		this.key = key;
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public String getName() {
		return name;
	}
}
