package org.springframework.springfaces.ui;

import javax.faces.component.UIComponentBase;

public class UIApplyAspects extends UIComponentBase {

	public static final String COMPONENT_FAMILY = "spring.faces.Aspect";

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}
}
