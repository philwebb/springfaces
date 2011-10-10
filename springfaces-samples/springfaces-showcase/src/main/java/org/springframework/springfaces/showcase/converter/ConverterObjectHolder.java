package org.springframework.springfaces.showcase.converter;

import java.io.Serializable;

/**
 * Simple holder model object used to contain converted values.
 * 
 * @author Phillip Webb
 */
public class ConverterObjectHolder implements Serializable {

	private ConvertedObject value;

	private ConvertableEnum enumValue = ConvertableEnum.TWO;

	public ConvertedObject getValue() {
		return value;
	}

	public void setValue(ConvertedObject value) {
		this.value = value;
	}

	public ConvertableEnum getEnumValue() {
		return enumValue;
	}

	public void setEnumValue(ConvertableEnum enumValue) {
		this.enumValue = enumValue;
	}
}
