package org.springframework.springfaces.ui;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponentBase;

//FIXME look into generating meta-data like myfaces
//Use cases:
// Decorate components to include label
// Set value of outputText based on ID
// Change UIInput to UIOutput

//
//Plan:
// 1) UIAspects attach to group             DONE
// 2) Render each aspect before child
// 3) Support NamingContainer
// 4) Support for proceed
// 5) Support for filtering
// 6) Support for state saving
// 7) Support for visitTree
// 8) Create FacesAdvice interface

public class UIAspectGroup extends UIComponentBase {

	public static final String COMPONENT_TYPE = "spring.faces.AspectGroup";

	public static final String COMPONENT_FAMILY = "spring.faces.Aspect";

	private List<UIAspect> aspects = new ArrayList<UIAspect>();

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}

	public void addUIAspect(UIAspect uiAspect) {
		aspects.add(uiAspect);
	}

	public void removeUIAspect(UIAspect uiAspect) {
		aspects.remove(uiAspect);
	}
}
