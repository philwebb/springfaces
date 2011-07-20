package org.springframework.springfaces.ui;

import javax.faces.component.UINamingContainer;

public class UIAspect extends UINamingContainer {

	public static final String COMPONENT_TYPE = "spring.faces.Aspect";

	public static final String COMPONENT_FAMILY = "spring.faces.Aspect";

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}

	// FIXME make sure inside a UIAspectGroup

}
