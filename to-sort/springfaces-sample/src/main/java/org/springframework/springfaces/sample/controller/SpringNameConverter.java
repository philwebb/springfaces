package org.springframework.springfaces.sample.controller;

import org.springframework.core.convert.converter.Converter;

public class SpringNameConverter implements Converter<String, Name> {

	public Name convert(String source) {
		return new Name(0, source);
	}

}
