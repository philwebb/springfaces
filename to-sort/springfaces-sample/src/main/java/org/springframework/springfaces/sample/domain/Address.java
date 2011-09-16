package org.springframework.springfaces.sample.domain;

public class Address {

	private long id;
	private String line1;
	private String line2;
	private String postcode;

	public Address(long id, String line1, String line2, String postCode) {
		super();
		this.id = id;
		this.line1 = line1;
		this.line2 = line2;
		this.postcode = postCode;
	}

	public long getId() {
		return id;
	}

	public String getLine1() {
		return line1;
	}

	public String getLine2() {
		return line2;
	}

	public String getPostcode() {
		return postcode;
	}

}
