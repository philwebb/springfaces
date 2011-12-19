package org.springframework.springfaces.showcase.validator;

import java.io.Serializable;
import java.math.BigInteger;

public class ValidatorObjectHolder implements Serializable {

	private Integer integerValue;

	private BigInteger bigIntegerValue;

	public Integer getIntegerValue() {
		return integerValue;
	}

	public void setIntegerValue(Integer integerValue) {
		this.integerValue = integerValue;
	}

	public BigInteger getBigIntegerValue() {
		return bigIntegerValue;
	}

	public void setBigIntegerValue(BigInteger bigIntegerValue) {
		this.bigIntegerValue = bigIntegerValue;
	}

}
