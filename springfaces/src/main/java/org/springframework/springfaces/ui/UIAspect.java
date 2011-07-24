package org.springframework.springfaces.ui;

import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

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

	@Override
	public String getClientId(FacesContext context) {
		// FIXME include the active component ID
		return super.getClientId(context);
	}

	// FIXME make sure inside a UIAspectGroup
}
