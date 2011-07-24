package org.springframework.springfaces.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

//FIXME look into generating meta-data like myfaces
//Use cases:
// Decorate components to include label
// Set value of outputText based on ID
// Change UIInput to UIOutput

//
//Plan:
// - UIAspects attach to group             DONE
// - Encode children                       DONE
// - Render each aspect before child
// - Support NamingContainer
// - Support for proceed
// - Support for filtering
// - Support for state saving
// - Support for visitTree
// - Create FacesAdvice interface

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

	@Override
	public void encodeChildren(FacesContext context) throws IOException {
		if (isRendered() && (getChildCount() > 0)) {
			for (UIComponent child : getChildren()) {
				encodeWithAspects(context, child);
			}
		}
	}

	private static void encodeWithAspects(FacesContext context, UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		component.encodeBegin(context);
		if (component.getRendersChildren()) {
			component.encodeChildren(context);
		} else if (component.getChildCount() > 0) {
			for (UIComponent child : component.getChildren()) {
				encodeWithAspects(context, child);
			}
		}
		component.encodeEnd(context);
	}

	public void addUIAspect(UIAspect uiAspect) {
		aspects.add(uiAspect);
	}

	public void removeUIAspect(UIAspect uiAspect) {
		aspects.remove(uiAspect);
	}
}
