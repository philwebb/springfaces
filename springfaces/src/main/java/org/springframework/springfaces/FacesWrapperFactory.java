package org.springframework.springfaces;

import javax.faces.FacesWrapper;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.ViewHandler;
import javax.faces.render.RenderKit;
import javax.faces.render.ResponseStateManager;

import org.springframework.web.context.WebApplicationContext;

/**
 * A factory that to create wrappers around various JSF objects.  All factories
 * from the {@link WebApplicationContext web context} will be consulted when any of the following
 * JSF objects are created:
 * <ul>
 * <li>{@link ConfigurableNavigationHandler}</li>
 * <li>{@link RenderKit}</li>
 * <li>{@link ResponseStateManager}</li>
 * <li>{@link ViewHandler}</li>
 * <ul>
 * An <tt>FacesWrapperFactory</tt> can generically declare the JSF object type
 * that it wraps. Factories will be filtered accordingly, with {@link #newWrapper} only being invoked for matching
 * JSF objects.
 *
 * @author Phillip Webb
 *
 * @param <T> The type of class to be wrapped.
 */
public interface FacesWrapperFactory<T> {

	/**
	 * Factory method that can be used to wrap the specified JSF Object.
	 * @param typeClass The JSF Object type being wrapped
	 * @param delegate The existing JSF object that should be used as the delegate for the wrapper
	 * @return A {@link FacesWrapper wrapped} object that it is itself an implementation of <tt>typeClass</tt> or <tt>null</tt> if no wrapping is required.
	 */
	public T newWrapper(Class<?> typeClass, T delegate);

}
