package org.springframework.springfaces.internal;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.Order;
import org.springframework.springfaces.FacesWrapperFactory;
import org.springframework.web.context.WebApplicationContext;

/**
 * Utility class that can wrap JSF objects by consulting all {@link FacesWrapperFactory} objects registered with Spring.
 * 
 * @author Phillip Webb
 * 
 * @param <T> The JSF type being managed
 */
class WrapperHandler<T> {

	/**
	 * Request attribute to hold the current web application context. This attribute is recreated here from
	 * <tt>DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE</tt> to save a hard dependency to Spring MVC.
	 */
	private static final String DISPATCHER_SERVLET_WEB_APPLICATION_CONTEXT_ATTRIBUTE = "org.springframework."
			+ "web.servlet.DispatecherServlet.CONTEXT";

	/**
	 * The type of JSF object being managed.
	 */
	private Class<?> typeClass;

	/**
	 * The root delegate.
	 */
	private T delegate;

	/**
	 * The fully wrapped implementation. This is late binding.
	 * @see #getWrapped()
	 */
	private T wrapped;

	/**
	 * Constructor.
	 * @param typeClass The JSF type being wrapped
	 * @param delegate The root delegate
	 */
	public WrapperHandler(Class<T> typeClass, T delegate) {
		this.typeClass = typeClass;
		this.delegate = delegate;
	}

	/**
	 * Creates a fully wrapped implementation of the delegate by consulting all {@link FacesWrapperFactory factories}
	 * registered with {@link #getApplicationContext() Spring}.
	 * @return A wrapped implementation
	 */
	public T getWrapped() {
		if (wrapped == null) {
			wrapped = wrap(delegate);
		}
		return wrapped;
	}

	/**
	 * Wrap the specified delegate by consulting all {@link FacesWrapperFactory factories} registered with
	 * {@link #getApplicationContext() Spring}.
	 * @param delegate The root delegate
	 * @return A wrapped implementation
	 */
	private T wrap(T delegate) {
		ApplicationContext applicationContext = getApplicationContext();
		if (applicationContext == null) {
			// FIXME log a warning
			return delegate;
		}

		SortedSet<Map.Entry<String, FacesWrapperFactory>> orderdBans = new TreeSet(new OrderedMapEntryComparator());
		orderdBans.addAll(BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, FacesWrapperFactory.class)
				.entrySet());
		T rtn = delegate;
		for (Map.Entry<String, FacesWrapperFactory> entry : orderdBans) {
			FacesWrapperFactory factory = entry.getValue();
			if (isFactorySupported(factory)) {
				T wrapper = (T) factory.newWrapper(typeClass, rtn);
				// FIXME assert T
				if (wrapper != null) {
					// FIXME log
					postProcessWrapper(wrapper);
					rtn = wrapper;
				}
			}
		}
		return rtn;
	}

	/**
	 * Returns the Spring {@link ApplicationContext} that will be used to locate {@link FacesWrapperFactory factories}.
	 * @return The application context or <tt>null</tt>.
	 */
	protected ApplicationContext getApplicationContext() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		WebApplicationContext applicationContext = asWebApplicationContext(externalContext.getRequestMap().get(
				DISPATCHER_SERVLET_WEB_APPLICATION_CONTEXT_ATTRIBUTE));
		if (applicationContext == null) {
			applicationContext = asWebApplicationContext(externalContext.getApplicationMap().get(
					WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE));
		}
		return applicationContext;
	}

	private static WebApplicationContext asWebApplicationContext(Object object) {
		if (object == null) {
			return null;
		}
		if (object instanceof RuntimeException) {
			throw (RuntimeException) object;
		}
		if (object instanceof Error) {
			throw (Error) object;
		}
		if (!(object instanceof WebApplicationContext)) {
			throw new IllegalStateException("Root context attribute is not of type WebApplicationContext: " + object);
		}
		return (WebApplicationContext) object;
	}

	/**
	 * Determine if a given {@link FacesWrapperFactory} is suitable by resolving generic arguments.
	 * @param factory The factory to test
	 * @return <tt>true</tt> if the <tt>factory</tt> is supported, otherwise <tt>false</tt>
	 */
	private boolean isFactorySupported(FacesWrapperFactory factory) {
		Class typeArg = GenericTypeResolver.resolveTypeArgument(factory.getClass(), FacesWrapperFactory.class);
		if (typeArg == null) {
			Class targetClass = AopUtils.getTargetClass(this.wrapped);
			if (targetClass != this.wrapped.getClass()) {
				typeArg = GenericTypeResolver.resolveTypeArgument(targetClass, FacesWrapperFactory.class);
			}
		}
		return (typeArg == null || typeArg.isAssignableFrom(typeClass));
	}

	/**
	 * Strategy method called after a wrapped instance has been created. Subclasses can implement custom post-processing
	 * as required.
	 * @param wrapped The newly created wrapped instance.
	 */
	protected void postProcessWrapper(T wrapped) {
	}

	/**
	 * Convenience factory method to create a {@link WrapperHandler} with the generic type obtained from
	 * <tt>typeClass</tt>
	 * @param <T> The JSF type being managed
	 * @param typeClass The JSF type being managed
	 * @param delegate The delegate
	 * @return A {@link WrapperHandler}
	 */
	public static <T> WrapperHandler<T> get(Class<T> typeClass, T delegate) {
		return new WrapperHandler<T>(typeClass, delegate);
	}

	/**
	 * {@link Comparator} implementation to sort {@link Map.Entry} values by {@link org.springframework.core.Ordered} as
	 * well as the {@link Order} annotation.
	 */
	private static class OrderedMapEntryComparator extends AnnotationAwareOrderComparator {
		@Override
		public int compare(Object o1, Object o2) {
			return super.compare(((Map.Entry<?, ?>) o1).getValue(), ((Map.Entry<?, ?>) o2).getValue());
		}
	}
}
