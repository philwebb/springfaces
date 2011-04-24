package org.springframework.springfaces;

import javax.el.CompositeELResolver;
import javax.faces.FacesWrapper;
import javax.faces.application.Application;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.ViewHandler;
import javax.faces.render.RenderKit;
import javax.faces.render.ResponseStateManager;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.WebApplicationContext;

/**
 * A factory that to create wrappers around various JSF objects. All factories from the Spring
 * {@link WebApplicationContext context} containing the {@link SpringFacesIntegration} bean will be consulted when any
 * of the following JSF objects are created:
 * <ul>
 * <li>{@link Application}</li>
 * <li>{@link ConfigurableNavigationHandler}</li>
 * <li>{@link RenderKit}</li>
 * <li>{@link ResponseStateManager}</li>
 * <li>{@link ViewHandler}</li>
 * <li>{@link CompositeELResolver}</li>
 * </ul>
 * A <tt>FacesWrapperFactory</tt> can generically declare the JSF object type that it wraps. Factories will be filtered
 * accordingly, with {@link #newWrapper} only being invoked for matching JSF objects.
 * <p>
 * Factories can implement the {@link Ordered} interface or use the {@link Order} annotation if a specific invocation
 * order is required.
 * <p>
 * When working with {@link CompositeELResolver}s the {@link CompositeELResolver#add add} method of the delegate should
 * be used and the original delegate returned.
 * 
 * @param <T> The type of class to be wrapped.
 * @see FacesWrapperFactory
 * 
 * @author Phillip Webb
 */
public interface FacesWrapperFactory<T> {

	/**
	 * Factory method that can be used to wrap the specified JSF object.
	 * @param typeClass The JSF Object type being wrapped
	 * @param delegate The existing JSF object that should be used as the delegate for the wrapper
	 * @return A {@link FacesWrapper wrapped} object that it is itself an implementation of <tt>typeClass</tt> or
	 * <tt>null</tt> if no wrapping is required.
	 */
	public T newWrapper(Class<?> typeClass, T delegate);

	// FIXME callback interface when all done?
}
