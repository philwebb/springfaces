/*
 * Copyright 2010-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
