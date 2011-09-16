package org.springframework.springfaces.expression.el;

import java.beans.FeatureDescriptor;
import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.PropertyNotWritableException;

/**
 * Abstract convenience base class for {@link ELResolver}s. This class provides an easier to use base for simple EL
 * resolver implementations. The {@link #isAvailable()} method will be called to determine if the EL resolver can be
 * used with the {@link #get(String)} method used to perform the actual value resolve. By default this resolver will be
 * read-only, if the resolver should support mutable values the {@link #set(String, Object)} and
 * {@link #isReadOnly(String)} methods should be overridden. Resolving <tt>null</tt> values can also be supported by
 * Overriding {@link #handles(String)}.
 * 
 * @see #isAvailable()
 * @see #handles(String)
 * @see #get(String)
 * @see #isReadOnly(String)
 * @see #set(String, Object)
 * 
 * @author Phillip Webb
 */
public abstract class AbstractELResolver extends ELResolver {

	/**
	 * Determine if the resolver is available for use.
	 * @return <tt>true</tt> if the resolver is available or <tt>false</tt> if the resolver should not be used. Defaults
	 * to <tt>true</tt>
	 */
	protected boolean isAvailable() {
		return true;
	}

	/**
	 * Determine if the resolve handles the specified property. This method will only be called is
	 * {@link #isAvailable()} returns <tt>true</tt>. By default this method will return <tt>true</tt> if
	 * {@link #get(String)} returns a non <tt>null</tt> value and the property itself is not null.
	 * @param property The property
	 * @return <tt>true</tt> if resolver handles the specified property, otherwise <tt>false</tt>.
	 */
	protected boolean handles(String property) {
		return (property != null) && (get(property) != null);
	}

	/**
	 * Called to gets the value of the specified property. This method will only be called is {@link #isAvailable()}
	 * returns <tt>true</tt>.
	 * @param property The property
	 * @return The value of the property or <tt>null</tt>.
	 */
	protected abstract Object get(String property);

	/**
	 * Called to determine if the property is read only. This method will only be called is {@link #isAvailable()}
	 * returns <tt>true</tt>. By default this method returns <tt>true</tt>
	 * @param property The property
	 * @return <tt>true</tt> if the property is read-only or <tt>false</tt> if the property is mutable.
	 * @see #set(String, Object)
	 */
	protected boolean isReadOnly(String property) {
		return true;
	}

	/**
	 * Called to set the property value. This method will only be called is {@link #isAvailable()} and
	 * {@link #isReadOnly(String)} return <tt>true</tt>. By default his method will throw a
	 * {@link PropertyNotWritableException}.
	 * @param property The property
	 * @param value The value to set
	 * @throws PropertyNotWritableException if the property is not writable.
	 * @see #isReadOnly(String)
	 */
	protected void set(String property, Object value) throws PropertyNotWritableException {
		throw new PropertyNotWritableException("The property " + property + " is not writable.");
	}

	/**
	 * Template method used to handle searching for the property and invoking some operation on the scope.
	 * @param <T> the property type
	 * @param elContext the EL context
	 * @param base the base object
	 * @param property the property
	 * @param operation Callback interface used to execute the operation
	 * @return Result of the operation
	 */
	protected <T> T handle(ELContext elContext, Object base, Object property, ELOperation<T> operation) {
		if (base != null || !isAvailable()) {
			return null;
		}
		String propertyString = (property == null ? null : property.toString());
		if (handles(propertyString)) {
			elContext.setPropertyResolved(true);
			return operation.execute(propertyString);
		}
		return null;
	}

	public Class<?> getCommonPropertyType(ELContext elContext, Object base) {
		if (base == null) {
			return Object.class;
		}
		return null;
	}

	public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext elContext, Object base) {
		return null;
	}

	public Class<?> getType(ELContext elContext, Object base, Object property) {
		return handle(elContext, base, property, new ELOperation<Class<?>>() {
			public Class<?> execute(String property) {
				return get(property).getClass();
			}
		});
	}

	public Object getValue(ELContext elContext, Object base, Object property) {
		return handle(elContext, base, property, new ELOperation<Object>() {
			public Object execute(String property) {
				return get(property);
			}
		});
	}

	public boolean isReadOnly(ELContext elContext, Object base, Object property) {
		Boolean readOnly = handle(elContext, base, property, new ELOperation<Boolean>() {
			public Boolean execute(String property) {
				return new Boolean(isReadOnly(property));
			}
		});
		return (readOnly != null ? readOnly.booleanValue() : false);
	}

	public void setValue(ELContext elContext, Object base, Object property, final Object value) {
		handle(elContext, base, property, new ELOperation<Object>() {
			public Object execute(String property) {
				set(property, value);
				return null;
			}
		});
	}

	/**
	 * Internal callback interface used to perform a scope operation.
	 * @param <T> The data type
	 */
	protected static interface ELOperation<T> {
		public T execute(String property);
	}
}