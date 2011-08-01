package org.springframework.springfaces.ui;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import org.springframework.util.Assert;

public class UIProceed extends UIComponentBase {

	public static final String COMPONENT_TYPE = "spring.faces.Proceed";

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
	public void encodeChildren(FacesContext context) throws IOException {
		Assert.state(getChildCount() == 0, "UIProceed component should not include children");
		UIAspect aspect = getParentAspect(getParent());
		aspect.encodeUIProceed();
	}

	private UIAspect getParentAspect(UIComponent component) {
		Assert.state(component != null, "UIProceed component must be contained within a UIAspect");
		if (component instanceof UIAspect) {
			return (UIAspect) component;
		}
		return getParentAspect(component.getParent());
	}
}
