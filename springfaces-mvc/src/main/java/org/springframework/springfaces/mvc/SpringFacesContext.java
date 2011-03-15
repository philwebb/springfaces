package org.springframework.springfaces.mvc;

import java.io.IOException;

import javax.faces.context.FacesContext;

import org.springframework.core.NamedThreadLocal;
import org.springframework.springfaces.mvc.view.Renderable;

public abstract class SpringFacesContext {

	private static ThreadLocal<SpringFacesContext> instance = new NamedThreadLocal<SpringFacesContext>(
			"Spring Faces Context");

	public abstract <T> T doWithFacesContext(FacesContextCallbackMode mode, FacesContextCallback<T> fcc);

	public abstract void render(Renderable renderable);

	public abstract Renderable getRendering();

	public abstract void writeState(FacesContext context, Object state) throws IOException;

	public static SpringFacesContext getCurrentInstance() {
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

	public static interface FacesContextCallback<T> {
		public T doWithFacesContext(FacesContext facesContext);
	}
}
