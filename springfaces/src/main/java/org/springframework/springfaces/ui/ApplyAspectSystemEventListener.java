package org.springframework.springfaces.ui;

import java.util.HashSet;
import java.util.Set;

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

	private static final Set<Class<? extends UIComponent>> COMPONENTS_NOT_WRAPPED;
	static {
		COMPONENTS_NOT_WRAPPED = new HashSet<Class<? extends UIComponent>>();
		dontWrap("org.apache.myfaces.view.facelets.compiler.UILeaf");
		dontWrap("com.sun.faces.facelets.compiler.UILeaf");
		dontWrap(UIAspectGroup.class);
		dontWrap(UIApplyAspects.class);
	}

	@SuppressWarnings("unchecked")
	private static void dontWrap(Object classNameOrClass) {
		if (!(classNameOrClass instanceof Class)) {
			try {
				classNameOrClass = Class.forName(classNameOrClass.toString());
			} catch (ClassNotFoundException e) {
				classNameOrClass = null;
			}
		}
		if (classNameOrClass != null) {
			COMPONENTS_NOT_WRAPPED.add((Class<? extends UIComponent>) classNameOrClass);
		}
	}

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
		if (isToWrapWithUIApplyAspects(component)) {
			UIAspectGroup aspectGroup = getParentAspectGroupIfNotInUIAspect(component);
			if (aspectGroup != null) {
				int index = component.getParent().getChildren().indexOf(component);
				UIApplyAspects wrapper = new UIApplyAspects();
				component.getParent().getChildren().set(index, wrapper);
				wrapper.getChildren().add(component);
			}
		}
	}

	private boolean isToWrapWithUIApplyAspects(UIComponent component) {
		if (component.getParent() == null || component.getParent() instanceof UIApplyAspects) {
			return false;
		}
		for (Class<? extends UIComponent> componentClass : COMPONENTS_NOT_WRAPPED) {
			if (componentClass.isInstance(component)) {
				return false;
			}
		}
		return true;
	}

	private UIAspectGroup getParentAspectGroupIfNotInUIAspect(UIComponent component) {
		if (component == null || component instanceof UIAspect) {
			return null;
		}
		if (component instanceof UIAspectGroup) {
			return (UIAspectGroup) component;
		}
		return getParentAspectGroupIfNotInUIAspect(component.getParent());
	}
}
