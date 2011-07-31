package org.springframework.springfaces.ui;

import java.io.IOException;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UniqueIdVendor;
import javax.faces.context.FacesContext;

import org.springframework.springfaces.util.FacesUtils;
import org.springframework.util.Assert;

//FIXME filter
//FIXME visitChildren
//FIXME save state

public class UIAspect extends UINamingContainer {

	public static final String COMPONENT_FAMILY = "spring.faces.Aspect";

	private AspectInvocation invocation;

	private boolean proceedCalled;

	private String clientId = null;

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
		// Aspects are not directly rendered
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
		if (invocation != null) {
			this.clientId = getId();
			if (this.clientId == null) {
				setId(generateCliendId(context));
				this.clientId = getId();
			}
			return new StringBuilder(invocation.getComponent().getClientId(context))
					.append(UINamingContainer.getSeparatorChar(context)).append(clientId).toString();

		}
		return super.getClientId(context);
	}

	private String generateCliendId(FacesContext context) {
		NamingContainer namingContainer = FacesUtils.findParentOfType(this, NamingContainer.class);
		if (namingContainer != null && namingContainer instanceof UniqueIdVendor) {
			return ((UniqueIdVendor) namingContainer).createUniqueId(context, null);
		}
		return context.getViewRoot().createUniqueId();
	}

	public void apply(FacesContext context, AspectInvocation invocation) throws IOException {
		setInvocation(invocation);
		try {
			this.proceedCalled = false;
			super.encodeChildren(context);
			if (!proceedCalled) {
				invocation.proceed();
			}
		} finally {
			clearInvocation();
		}
	}

	private void setInvocation(AspectInvocation invocation) {
		this.invocation = invocation;
	}

	private void clearInvocation() {
		this.invocation = null;
	}

	void encodeUIProceed() throws IOException {
		this.invocation.proceed();
		this.proceedCalled = true;
	}
}
