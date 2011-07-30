package org.springframework.springfaces.ui;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEventListener;

//FIXME look into generating meta-data like myfaces
//Use cases:
// Decorate components to include label
// Set value of outputText based on ID
// Change UIInput to UIOutput
public class UIAspectGroup extends UIComponentBase implements ComponentSystemEventListener {

	public static final String COMPONENT_TYPE = "spring.faces.AspectGroup";

	public static final String COMPONENT_FAMILY = "spring.faces.Aspect";

	public UIAspectGroup() {
		super();
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public void applyAspects(FacesContext context, AspectInvocation invocation) {
		// FIXME
	}
}
