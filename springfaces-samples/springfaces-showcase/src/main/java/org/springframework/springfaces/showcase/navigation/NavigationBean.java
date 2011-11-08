package org.springframework.springfaces.showcase.navigation;

import java.io.Serializable;
import java.util.Date;

public class NavigationBean implements Serializable {

	private String destination = "/spring/navigation/destination?s=value";

	private Date date;

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getDate() {
		return String.valueOf(date);
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
