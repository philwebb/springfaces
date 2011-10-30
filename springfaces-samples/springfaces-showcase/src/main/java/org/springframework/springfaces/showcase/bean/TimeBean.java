package org.springframework.springfaces.showcase.bean;

import org.springframework.stereotype.Component;

/**
 * Simple Spring bean that can be used to obtain the current time.
 * 
 * @author Phillip Webb
 */
@Component
public class TimeBean {

	/**
	 * Returns the time.
	 * @return the current time in milliseconds
	 */
	public long getCurrentTimeMillis() {
		return System.currentTimeMillis();
	}

}
