package org.springframework.springfaces.showcase.bean;

import org.springframework.stereotype.Component;

@Component
public class TimeBean {

	public long getCurrentTimeMillis() {
		return System.currentTimeMillis();
	}

}
