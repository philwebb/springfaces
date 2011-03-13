package org.springframework.springfaces.integration;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.springfaces.FacesWrapperFactory;
import org.springframework.springfaces.mvc.SpringFacesContext;
import org.springframework.springfaces.util.MapEntryValueComparator;

class WrapperHandler<T> {

	private Class<?> typeClass;
	private T delegate;
	private T wrapped;

	public WrapperHandler(Class<T> typeClass, T delegate) {
		this.typeClass = typeClass;
		this.delegate = delegate;
	}

	public T getWrapped() {
		if (wrapped == null) {
			wrapped = wrap(delegate);
		}
		return wrapped;
	}

	protected T wrap(T delegate) {
		ApplicationContext applicationContext = SpringFacesContext.getCurrentInstance().getApplicationContext();
		if (applicationContext == null) {
			//FIXME log a warning
			return delegate;
		}

		Map<String, FacesWrapperFactory> beans = BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext,
				FacesWrapperFactory.class);
		Set<Entry<String, FacesWrapperFactory>> orderdBans = MapEntryValueComparator.entrySet(beans,
				new AnnotationAwareOrderComparator());

		T rtn = delegate;
		for (Entry<String, FacesWrapperFactory> entry : orderdBans) {
			FacesWrapperFactory factory = entry.getValue();
			if (isFactorySupported(factory)) {
				T wrapper = (T) factory.newWrapper(rtn);
				//FIXME assert T
				//FIXME possibly asst is a wrapper
				if (wrapper != null) {
					//FIXME log
					postProcessWrapper(wrapper);
					rtn = wrapper;
				}
			}
		}
		return rtn;
	}

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

	protected void postProcessWrapper(T wrapped) {
	}

	public static <T> WrapperHandler<T> get(Class<T> typeClass, T delegate) {
		return new WrapperHandler<T>(typeClass, delegate);
	}
}
