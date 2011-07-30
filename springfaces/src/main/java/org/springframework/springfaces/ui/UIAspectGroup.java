package org.springframework.springfaces.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.component.UIComponent;
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

	private List<UIAspect> aspects = new ArrayList<UIAspect>();

	public UIAspectGroup() {
		super();
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	void addAspect(UIAspect aspect) {
		aspects.add(aspect);
	}

	void removeAspect(UIAspect aspect) {
		aspects.remove(aspect);
	}

	public List<UIAspect> getAllAspects() {
		List<UIAspect> allAspects = aspects;
		UIAspectGroup parentAspectGroup = getParentAspectGroup(getParent());
		if (parentAspectGroup != null) {
			allAspects = new ArrayList<UIAspect>(parentAspectGroup.getAllAspects());
			allAspects.addAll(aspects);
		}
		return Collections.unmodifiableList(allAspects);
	}

	private UIAspectGroup getParentAspectGroup(UIComponent component) {
		if (component == null || component instanceof UIAspectGroup) {
			return (UIAspectGroup) component;
		}
		return getParentAspectGroup(component.getParent());
	}

	public void applyAspects(FacesContext context, AspectInvocation invocation) {
		// FIXME
	}

}
