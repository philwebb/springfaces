package org.springframework.springfaces.sample.controller;

import java.io.Serializable;

public class NavigationBean implements Serializable {

	private String destination = "http://www.springsource.org";

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}
}
