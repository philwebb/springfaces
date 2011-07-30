package org.springframework.springfaces.ui;

import javax.faces.component.UINamingContainer;

public class UIAspect extends UINamingContainer {

	public static final String COMPONENT_TYPE = "spring.faces.Aspect";

	public static final String COMPONENT_FAMILY = "spring.faces.Aspect";

	public UIAspect() {
		super();
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

}
