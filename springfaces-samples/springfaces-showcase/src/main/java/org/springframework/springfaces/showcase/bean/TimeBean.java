package org.springframework.springfaces.showcase.bean;

import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class TimeBean {

	public Date getCurrentTime() {
		return new Date();
	}

}
