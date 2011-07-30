package org.springframework.springfaces.ui;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;

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
}
