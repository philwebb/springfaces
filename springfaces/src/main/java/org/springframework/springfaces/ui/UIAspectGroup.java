package org.springframework.springfaces.ui;

import javax.faces.component.UIComponentBase;
import javax.faces.event.ComponentSystemEventListener;
import javax.faces.event.ListenerFor;
import javax.faces.event.PostAddToViewEvent;

//FIXME look into generating meta-data like myfaces
//Use cases:
// Decorate components to include label
// Set value of outputText based on ID
// Change UIInput to UIOutput

//
//Plan:
@ListenerFor(systemEventClass = PostAddToViewEvent.class)
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

}
