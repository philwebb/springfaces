package org.springframework.springfaces.mvc.method.support;

import java.util.Locale;
import java.util.concurrent.Callable;

import javax.faces.application.Application;
import javax.faces.application.ResourceHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;

import org.springframework.web.method.support.HandlerMethodArgumentResolver;

/**
 * {@link HandlerMethodArgumentResolver} that can resolve parameters using the current {@link FacesContext} instance.
 * This resolver supports the following parameter types:
 * <ul>
 * <li>{@link FacesContext}</li>
 * <li>{@link ExternalContext}</li>
 * <li>{@link PartialViewContext}</li>
 * <li>{@link Application}</li>
 * <li>{@link ResourceHandler}</li>
 * <li>{@link ExceptionHandler}</li>
 * <li>{@link UIViewRoot}</li>
 * <li>{@link Locale}</li>
 * </ul>
 * Parameters are only resolve when the {@link FacesContext#getCurrentInstance() current} {@link FacesContext} is not
 * <tt>null</tt>.
 * 
 * @author Phillip Webb
 */
public class FacesContextMethodArgumentResolver extends ImplicitObjectMethodArgumentResolver {

	private static final Callable<Boolean> HAS_FACES_CONTEXT = new Callable<Boolean>() {
		public Boolean call() throws Exception {
			return FacesContext.getCurrentInstance() != null;
		}
	};

	private static final Callable<Boolean> HAS_VIEW_ROOT = new Callable<Boolean>() {
		public Boolean call() throws Exception {
			return HAS_FACES_CONTEXT.call() && FacesContext.getCurrentInstance().getViewRoot() != null;
		}
	};

	/**
	 * Create a new {@link FacesContextMethodArgumentResolver}.
	 */
	public FacesContextMethodArgumentResolver() {
		add(FacesContext.class, HAS_FACES_CONTEXT, new Callable<FacesContext>() {
			public FacesContext call() throws Exception {
				return FacesContext.getCurrentInstance();
			}
		});

		add(ExternalContext.class, HAS_FACES_CONTEXT, new Callable<ExternalContext>() {
			public ExternalContext call() throws Exception {
				return FacesContext.getCurrentInstance().getExternalContext();
			}
		});

		add(PartialViewContext.class, HAS_FACES_CONTEXT, new Callable<PartialViewContext>() {
			public PartialViewContext call() throws Exception {
				return FacesContext.getCurrentInstance().getPartialViewContext();
			}
		});

		add(Application.class, HAS_FACES_CONTEXT, new Callable<Application>() {
			public Application call() throws Exception {
				return FacesContext.getCurrentInstance().getApplication();
			}
		});
		add(ResourceHandler.class, HAS_FACES_CONTEXT, new Callable<ResourceHandler>() {
			public ResourceHandler call() throws Exception {
				return FacesContext.getCurrentInstance().getApplication().getResourceHandler();
			}
		});

		add(ExceptionHandler.class, HAS_FACES_CONTEXT, new Callable<ExceptionHandler>() {
			public ExceptionHandler call() throws Exception {
				return FacesContext.getCurrentInstance().getExceptionHandler();
			}
		});

		add(UIViewRoot.class, HAS_VIEW_ROOT, new Callable<UIViewRoot>() {
			public UIViewRoot call() throws Exception {
				return FacesContext.getCurrentInstance().getViewRoot();
			}
		});

		add(Locale.class, HAS_VIEW_ROOT, new Callable<Locale>() {
			public Locale call() throws Exception {
				return FacesContext.getCurrentInstance().getViewRoot().getLocale();
			}
		});
	}
}
