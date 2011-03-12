package org.springframework.springfaces.sample.controller;

public class Name {

	private String name;

	public Name(int index, String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

}
