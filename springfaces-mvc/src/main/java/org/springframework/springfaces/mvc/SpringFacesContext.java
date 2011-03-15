package org.springframework.springfaces.mvc;

import javax.faces.context.FacesContext;

import org.springframework.core.NamedThreadLocal;
import org.springframework.springfaces.mvc.servlet.FacesView;
import org.springframework.util.Assert;

public abstract class SpringFacesContext {

	private static ThreadLocal<SpringFacesContext> instance = new NamedThreadLocal<SpringFacesContext>(
			"Spring Faces Context");

	public abstract boolean isRendering();

	public abstract View getRendering();

	public abstract <T> T doWithFacesContext(FacesContextCallback<T> fcc);

	public abstract <T> T doWithRequiredFacesContext(FacesContextCallback<T> fcc);

	//FIXME seems wrong here?
	public abstract void render(FacesView view);

	public static SpringFacesContext getCurrentInstance() {
		SpringFacesContext context = instance.get();
		return (context == null ? NullSpringFacesContext.INSTANCE : context);
	}

	protected static void setCurrentInstance(SpringFacesContext context) {
		//FIXME prevent double setup?
		if (context == null) {
			instance.remove();
		} else {
			instance.set(context);
		}
	}

	private static class NullSpringFacesContext extends SpringFacesContext {
		public static final NullSpringFacesContext INSTANCE = new NullSpringFacesContext();

		@Override
		public boolean isRendering() {
			return false;
		}

		@Override
		public View getRendering() {
			throw new IllegalStateException();
		}

		@Override
		public <T> T doWithFacesContext(FacesContextCallback<T> fcc) {
			return doWithFacesContext(fcc, false);
		}

		@Override
		public <T> T doWithRequiredFacesContext(FacesContextCallback<T> fcc) {
			return doWithFacesContext(fcc, true);
		}

		private <T> T doWithFacesContext(FacesContextCallback<T> fcc, boolean required) {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			Assert.state(facesContext != null && required, "Unable to obtain FacesContext");
			if (facesContext != null) {
				return fcc.doWithFacesContext(facesContext);
			}
			return null;
		}

		@Override
		public void render(FacesView view) {
			//FIXME
		}
	}

	public static interface FacesContextCallback<T> {
		public T doWithFacesContext(FacesContext facesContext);
	}
}
