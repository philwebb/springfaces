package org.springframework.springfaces.mvc.expression.el;

import java.util.HashMap;
import java.util.Map;

import javax.el.BeanELResolver;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;

/**
 * Base class for any read-only EL resolver that is backed against java bean properties. This EL resolver can be used to
 * map EL properties to java bean getters. Subclasses should call the {@link #map(String, String)} method on
 * construction to setup mappings between EL and bean properties.
 * 
 * @see #map(String, String)
 * @see #getBean()
 * 
 * @author Phillip Webb
 */
public abstract class BeanBackedELResolver extends AbstractELResolver {

	private static final BeanELResolver elPropertyResolver = new BeanELResolver();

	/**
	 * Mapping or EL properties to java bean properties.
	 */
	private Map<String, String> properties = new HashMap<String, String>();

	/**
	 * Returns the underlying bean that will be used to resolve properties. If this method returns <tt>null</tt> the
	 * resolver will not be used.
	 * @return The bean instance or <tt>null</tt>
	 */
	protected abstract Object getBean();

	/**
	 * Map a specified EL property to a bean property. This method should be called when the EL resolver is constructed
	 * in order to map EL properties to bean properties. Mapped properties should be available as getter methods on the
	 * bean returned from the {@link #getBean()} method.
	 * <p>
	 * For example: <code>
	 * map('currentUser','loggedInUser')
	 * </code> Will resolve an <tt>currentUser</tt> to <tt>bean.getLoggedInUser()</tt>.
	 * <p>
	 * If your EL property and bean property are identical the {@link #map(String)} convenience method can be used.
	 * @param elProperty The EL property
	 * @param beanProperty The bean property exposed from {@link #getBean()}
	 * @see #map(String)
	 */
	protected void map(String elProperty, String beanProperty) {
		properties.put(elProperty, beanProperty);
	}

	/**
	 * Map a EL property to a bean property. This is a convenience method that can be used when the EL property and bean
	 * property. Functionally equivalent to <code>map(property,property)</code>, see {@link #map(String, String)} for
	 * details.
	 * @param property The EL and bean property to map.
	 */
	protected void map(String property) {
		map(property, property);
	}

	protected boolean isAvailable() {
		return getBean() != null;
	}

	protected boolean handles(String property) {
		return properties.containsKey(property);
	}

	protected Object get(String property) {
		String beanProperty = (String) properties.get(property);
		if (beanProperty != null) {
			ELContext elContext = new ELContext() {
				@Override
				public VariableMapper getVariableMapper() {
					return null;
				}

				@Override
				public FunctionMapper getFunctionMapper() {
					return null;
				}

				@Override
				public ELResolver getELResolver() {
					return elPropertyResolver;
				}
			};
			return elContext.getELResolver().getValue(elContext, getBean(), beanProperty);
		}
		return null;
	}
}
