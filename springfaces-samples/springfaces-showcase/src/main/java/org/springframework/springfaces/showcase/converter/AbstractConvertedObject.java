package org.springframework.springfaces.showcase.converter;

/**
 * Example object used to demonstrate conversion.
 * 
 * @author Phillip Webb
 */
public abstract class AbstractConvertedObject {

	private String from;
	private long value;

	public AbstractConvertedObject(String from, long value) {
		this.from = from;
		this.value = value;
	}

	@Override
	public String toString() {
		return String.valueOf(value) + " from " + from;
	}

	public String getFrom() {
		return from;
	}

	public long getValue() {
		return value;
	}

}
