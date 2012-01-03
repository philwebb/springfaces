package org.springframework.springfaces.expression.el;

import java.beans.PropertyDescriptor;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ValueExpression;

import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.Property;
import org.springframework.core.convert.TypeDescriptor;

/**
 * Miscellaneous <tt>Unified EL</tt> utility methods.
 * 
 * @author Phillip Webb
 */
public abstract class ELUtils {

	/**
	 * Returns a {@link TypeDescriptor} for the given <tt>valueExpression</tt>. This method is similar to
	 * {@link ValueExpression#getType(ELContext)} except that generic type information will be deduced when possible.
	 * @param valueExpression the value expression
	 * @param elContext the el context
	 * @return a {@link TypeDescriptor} for the given expression
	 */
	public static TypeDescriptor getTypeDescriptor(ValueExpression valueExpression, ELContext elContext) {
		TrackedELContext trackedContext = new TrackedELContext(elContext);
		Class<?> type = valueExpression.getType(trackedContext);
		if (type == null) {
			return null;
		}
		TypeDescriptor typeDescriptor = TypeDescriptor.valueOf(type);
		if (typeDescriptor.isCollection() || typeDescriptor.isMap()) {
			// We may be able to obtain the generic type info by resolving the property directly
			if (trackedContext.hasValues()) {
				try {
					Class<? extends Object> baseClass = trackedContext.getBase().getClass();
					PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(baseClass, trackedContext
							.getProperty().toString());
					Property property = new Property(baseClass, propertyDescriptor.getReadMethod(),
							propertyDescriptor.getWriteMethod());
					typeDescriptor = new TypeDescriptor(property);
				} catch (Exception e) {
				}
			}
		}
		return typeDescriptor;
	}

	/**
	 * Internal {@link ELContext} decorator that tracks {@link #getELResolver() resolver} calls to
	 * {@link ELResolver#getType getType()} in order to allow direct access to bean properties.
	 */
	private static class TrackedELContext extends ELContextDecorator {

		private TrackingELResolver resolver;

		private Object base;
		private Object property;

		public TrackedELContext(ELContext elContext) {
			super(elContext);
		}

		@Override
		public ELResolver getELResolver() {
			if (this.resolver == null) {
				this.resolver = new TrackingELResolver(super.getELResolver());
			}
			return this.resolver;
		}

		public boolean hasValues() {
			return ((this.base != null) && (this.property != null));
		}

		public Object getBase() {
			return this.base;
		}

		public Object getProperty() {
			return this.property;
		}

		private class TrackingELResolver extends ELResolverDecorator {
			public TrackingELResolver(ELResolver resolver) {
				super(resolver);
			}

			@Override
			public Class<?> getType(ELContext context, Object base, Object property) {
				TrackedELContext.this.base = base;
				TrackedELContext.this.property = property;
				return super.getType(context, base, property);
			}
		}

	}

}
