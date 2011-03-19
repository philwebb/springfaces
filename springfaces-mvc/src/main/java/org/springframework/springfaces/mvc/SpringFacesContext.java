package org.springframework.springfaces.mvc;

import javax.faces.context.FacesContext;
import javax.faces.lifecycle.Lifecycle;

import org.springframework.core.NamedThreadLocal;

public abstract class SpringFacesContext {

	private static ThreadLocal<SpringFacesContext> instance = new NamedThreadLocal<SpringFacesContext>(
			"Spring Faces Context");

	public abstract Lifecycle getLifecycle();

	public abstract Object getHandler();

	//FIXME DC ref counted
	public abstract FacesContext getFacesContext(boolean required);

	public static SpringFacesContext getCurrentInstance() {
		return getCurrentInstance(false);
	}

	public static SpringFacesContext getCurrentInstance(boolean required) {
		return instance.get();
	}

	protected static void setCurrentInstance(SpringFacesContext context) {
		//FIXME prevent double setup?
		if (context == null) {
			instance.remove();
		} else {
			instance.set(context);
		}
	}

	public enum FacesContextCallbackMode {
		REQUIRED, IGNORE_IF_MISSING
	}
}
