package org.springframework.springfaces.ui;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

import org.springframework.util.Assert;

public class UIAspect extends UINamingContainer {

	public static final String COMPONENT_TYPE = "spring.faces.Aspect";

	public static final String COMPONENT_FAMILY = "spring.faces.Aspect";

	private UIAspectGroup aspectGroup;

	public UIAspect() {
		super();
	}

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
		// Children are not directly rendered
	}

	@Override
	public void setParent(UIComponent parent) {
		super.setParent(parent);
		if (this.aspectGroup != null) {
			this.aspectGroup.removeUIAspect(this);
		}
		this.aspectGroup = (parent == null ? null : getAspectGroup(parent));
		if (this.aspectGroup != null) {
			this.aspectGroup.addUIAspect(this);
		}
	}

	private UIAspectGroup getAspectGroup(UIComponent parent) {
		Assert.state(parent != null, "UIAspect must be contained within a UIAspectGroup");
		if (parent instanceof UIAspectGroup) {
			return (UIAspectGroup) parent;
		}
		return getAspectGroup(parent.getParent());
	}

	@Override
	public String getClientId(FacesContext context) {
		// FIXME include the active component ID
		return super.getClientId(context);
	}

	// FIXME make sure inside a UIAspectGroup
}
