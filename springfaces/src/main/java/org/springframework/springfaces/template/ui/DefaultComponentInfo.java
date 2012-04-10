package org.springframework.springfaces.template.ui;

import java.util.Collections;
import java.util.List;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;

import org.springframework.util.Assert;

public class DefaultComponentInfo implements ComponentInfo {

	private List<UIComponent> components;

	public DefaultComponentInfo(List<UIComponent> components) {
		Assert.notNull(components, "Components must not be null");
		this.components = Collections.unmodifiableList(components);
	}

	public UIComponent getComponent() {
		if (this.components.isEmpty()) {
			return null;
		}
		return this.components.get(0);
	}

	public List<UIComponent> getComponents() {
		return this.components;
	}

	public boolean isValid() {
		for (UIComponent component : this.components) {
			if (!isValid(component)) {
				return false;
			}
		}
		return true;
	}

	private boolean isValid(UIComponent component) {
		if (component instanceof EditableValueHolder) {
			return ((EditableValueHolder) component).isValid();
		}
		// FIXME test messages
		// FIXME test @Valid
		return false;
	}

	public boolean isRequired() {
		for (UIComponent component : this.components) {
			if (isRequired(component)) {
				return true;
			}
		}
		return false;
	}

	private boolean isRequired(UIComponent component) {
		if (component instanceof EditableValueHolder) {
			return ((EditableValueHolder) component).isRequired();
		}
		return false;
	}

	public String getLabel() {
		UIComponent component = getComponent();
		return (String) (component == null ? null : component.getAttributes().get("label"));
	}

	public String getFor() {
		UIComponent component = getComponent();
		return (component == null ? null : component.getClientId());
	}
}
