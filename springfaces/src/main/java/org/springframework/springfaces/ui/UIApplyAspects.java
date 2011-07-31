package org.springframework.springfaces.ui;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import org.springframework.springfaces.util.FacesUtils;
import org.springframework.util.Assert;

/**
 * A wrapper for {@link UIComponent components} contained within an {@link UIAspectGroup} that is responsible for
 * applying the relevant {@link UIAspect}s. This component is dynamically added via the
 * {@link ApplyAspectSystemEventListener} and should not be used directly.
 * 
 * @author Phillip Webb
 */
public class UIApplyAspects extends UIComponentBase {

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
	public void encodeChildren(final FacesContext context) throws IOException {
		Assert.state(getChildCount() == 1, "Aspects can only be applied to a single child");
		final UIComponent child = getChildren().get(0);
		AspectInvocation invocation = new AspectInvocation() {
			public void proceed() throws IOException {
				UIApplyAspects.super.encodeChildren(context);
			}

			public UIComponent getComponent() {
				return child;
			}
		};
		UIAspectGroup aspectGroup = FacesUtils.findParentOfType(this, UIAspectGroup.class);
		Assert.state(aspectGroup != null, "Unable to locate parent UIAspectGroup");
		aspectGroup.applyAspects(context, invocation);
	}
}
