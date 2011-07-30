package org.springframework.springfaces.ui;

import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

import org.springframework.util.Assert;

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

	@Override
	public void setParent(UIComponent parent) {
		removeFromParentAspectGroup();
		super.setParent(parent);
		addToParentAspectGroup();
	}

	private void removeFromParentAspectGroup() {
		UIAspectGroup aspectGroup = getAspectGroup(this);
		if (aspectGroup != null) {
			aspectGroup.removeAspect(this);
		}
	}

	private void addToParentAspectGroup() {
		UIAspectGroup aspectGroup = getAspectGroup(this);
		Assert.state(aspectGroup != null, "UIAspect must be contained within a UIAspectGroup");
		aspectGroup.addAspect(this);
	}

	private UIAspectGroup getAspectGroup(UIComponent component) {
		if (component == null || component instanceof UIAspectGroup) {
			return (UIAspectGroup) component;
		}
		return getAspectGroup(component.getParent());
	}

	@Override
	public String getClientId(FacesContext context) {
		// FIXME include the active component ID
		return super.getClientId(context);
	}

}
