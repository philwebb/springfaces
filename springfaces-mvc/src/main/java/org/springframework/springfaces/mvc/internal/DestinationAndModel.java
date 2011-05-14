package org.springframework.springfaces.mvc.internal;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;
import javax.faces.event.PreRenderComponentEvent;

import org.springframework.springfaces.mvc.navigation.NavigationOutcome;
import org.springframework.util.Assert;

class DestinationAndModel {

	private NavigationOutcome navigationOutcome;
	private UIComponent component;

	public DestinationAndModel(NavigationOutcome navigationOutcome, PreRenderComponentEvent preRenderComponentEvent) {
		Assert.notNull(navigationOutcome, "NavigationOutcome must not be null");
		this.navigationOutcome = navigationOutcome;
		if (preRenderComponentEvent != null) {
			component = preRenderComponentEvent.getComponent();
		}
	}

	public DestinationAndModel(NavigationOutcome navigationOutcome, ActionEvent actionEvent) {
		Assert.notNull(navigationOutcome, "NavigationOutcome must not be null");
		this.navigationOutcome = navigationOutcome;
		if (actionEvent != null) {
			component = actionEvent.getComponent();
		}
	}

	public Object getDestination() {
		return navigationOutcome.getDestination();
	}

	public Map<String, Object> getModel() {
		Map<String, Object> model = new HashMap<String, Object>();
		updateModelFromActionEvent(model);
		updateModelFromNavigationOutcome(model);
		return model;
	}

	private void updateModelFromActionEvent(Map<String, Object> model) {
		if (component != null) {
			for (UIComponent child : component.getChildren()) {
				if (child instanceof UIParameter) {
					UIParameter uiParam = (UIParameter) child;
					if (!uiParam.isDisable()) {
						Object value = uiParam.getValue();
						System.out.println(value);
						// FIXME we should not expand ELs here
						// FIXME what to do with params that have no name
						// FIXME a nice way to expose the existing MVC model
						// Param param = new Param(uiParam.getName(),
						// (value == null ? null :
						// value.toString()));
					}
				}
			}
		}
	}

	private void updateModelFromNavigationOutcome(Map<String, Object> model) {
		// TODO Auto-generated method stub

	}

}
