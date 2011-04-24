package org.springframework.springfaces.mvc.internal;

public interface DestinationRegistry {

	public String put(Object destination);

	public Object get(String key);

}
