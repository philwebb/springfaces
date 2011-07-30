package org.springframework.springfaces.ui;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

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
	public boolean getRendersChildren() {
		return true;
	}

	@Override
	public void encodeChildren(FacesContext context) throws IOException {
		// Never directly rendered
	}

	@Override
	public void setParent(UIComponent parent) {
		removeFromParentAspectGroup();
		super.setParent(parent);
		if (parent != null) {
			addToParentAspectGroup();
		}
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

	public void apply(FacesContext context, AspectInvocation invocation) throws IOException {
		// FIXME watch for proceed
		// FIXME stash the invocation
		// FIXME render the children
		// FIXME if we did not call proceed then proceed
		ResponseWriter writer = context.getResponseWriter();
		writer.write("(");
		invocation.proceed();
		writer.write(")");
	}

}
