package org.springframework.springfaces.mvc.expression.el;

import javax.el.ELContext;
import javax.el.ELResolver;

import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;

/**
 * Spring EL property accessor that can be used to traverse properties from a Java {@link ELContext}.
 * 
 * @author Phillip Webb
 */
public abstract class ELPropertyAccessor implements PropertyAccessor {

	public boolean canRead(EvaluationContext context, Object target, String name) throws AccessException {
		return (resolveValue(context, target, name) != null);
	}

	public TypedValue read(EvaluationContext context, Object target, String name) throws AccessException {
		return resolveValue(context, target, name);
	}

	public boolean canWrite(EvaluationContext context, Object target, String name) throws AccessException {
		return false;
	}

	public void write(EvaluationContext context, Object target, String name, Object newValue) throws AccessException {
	}

	public Class<?>[] getSpecificTargetClasses() {
		return new Class[] { BeanExpressionContext.class };
	}

	private TypedValue resolveValue(EvaluationContext context, Object target, String name) throws AccessException {
		ELContext elContext = getElContext(context, target);
		if (elContext != null) {
			ELResolver resolver = elContext.getELResolver();
			Object base = getResolveBase(context, target);
			Object property = getResolveProperty(context, target);
			Class<?> type = resolver.getType(elContext, base, property);
			Object value = resolver.getValue(elContext, base, property);
			if (elContext.isPropertyResolved()) {
				return new TypedValue(value, TypeDescriptor.valueOf(type));
			}
		}
		return null;
	}

	protected abstract ELContext getElContext(EvaluationContext context, Object target);

	protected Object getResolveBase(EvaluationContext context, Object target) {
		return null;
	}

	protected Object getResolveProperty(EvaluationContext context, Object target) {
		return target;
	}

}
