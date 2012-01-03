package org.springframework.springfaces.showcase.selectitems;

import java.util.Set;

public class SampleBean {

	private SampleEnum sampleEnum;
	private Set<SampleEnum> sampleEnums;

	public SampleEnum getSampleEnum() {
		return sampleEnum;
	}

	public void setSampleEnum(SampleEnum sampleEnum) {
		this.sampleEnum = sampleEnum;
	}

	public Set<SampleEnum> getSampleEnums() {
		return sampleEnums;
	}

	public void setSampleEnums(Set<SampleEnum> sampleEnums) {
		this.sampleEnums = sampleEnums;
	}

}
