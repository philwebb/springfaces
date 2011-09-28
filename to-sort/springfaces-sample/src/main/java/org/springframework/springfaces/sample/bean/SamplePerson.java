package org.springframework.springfaces.sample.bean;

import org.springframework.stereotype.Component;

@Component
public class SamplePerson {

	public String getFirstName() {
		return "Phil";
	}

	public String getLastName() {
		return "Webb";
	}

}
