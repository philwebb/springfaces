package org.springframework.springfaces.ui;

import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PostAddToViewEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

/**
 * A {@link SystemEventListener} that will ensure that an {@link UIComponent components} that are added to a
 * {@link UIAspectGroup} are wrapped with {@link UIApplyAspects}.
 * 
 * @author Phillip Webb
 */
public class ApplyAspectSystemEventListener implements SystemEventListener {

	public boolean isListenerForSource(Object source) {
		return true;
	}

	public void processEvent(SystemEvent event) throws AbortProcessingException {
		if (event instanceof PostAddToViewEvent) {
			processPostAddToViewEvent((PostAddToViewEvent) event);
		}
	}

	private void processPostAddToViewEvent(PostAddToViewEvent event) {
		UIComponent component = event.getComponent();
		wrapComponentAndChildrenWithUIApplyAspects(component);
	}

	private void wrapComponentAndChildrenWithUIApplyAspects(UIComponent component) {
		wrapComponentWithUIApplyAspects(component);
		if (component.getChildCount() > 0) {
			for (UIComponent child : component.getChildren()) {
				wrapComponentAndChildrenWithUIApplyAspects(child);
			}
		}
	}

	private void wrapComponentWithUIApplyAspects(UIComponent component) {
		if (component.getParent() instanceof UIApplyAspects || component instanceof UIAspect) {
			return;
		}
		UIAspectGroup aspectGroup = getParentAspectGroup(component);
		if (aspectGroup != null) {
			int index = component.getParent().getChildren().indexOf(component);
			UIApplyAspects wrapper = new UIApplyAspects();
			component.getParent().getChildren().set(index, wrapper);
			wrapper.getChildren().add(component);
		}
	}

	private UIAspectGroup getParentAspectGroup(UIComponent component) {
		if (component == null) {
			return null;
		}
		if (component instanceof UIAspectGroup) {
			return (UIAspectGroup) component;
		}
		return getParentAspectGroup(component.getParent());
	}
}
