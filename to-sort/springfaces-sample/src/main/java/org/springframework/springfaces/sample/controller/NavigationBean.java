package org.springframework.springfaces.sample.controller;

import java.io.Serializable;
import java.util.Date;

public class NavigationBean implements Serializable {

	private String destination = "http://www.springsource.org";

	private Date date;

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
