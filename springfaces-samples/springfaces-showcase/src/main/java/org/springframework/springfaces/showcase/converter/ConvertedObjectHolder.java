package org.springframework.springfaces.showcase.converter;

import java.io.Serializable;

public class ConvertedObjectHolder implements Serializable {

	private ConvertedObject value;

	public ConvertedObject getValue() {
		return value;
	}

	public void setValue(ConvertedObject value) {
		this.value = value;
	}
}
