package org.springframework.springfaces.internal;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.springfaces.FacesWrapperFactoryBean;
import org.springframework.web.context.WebApplicationContext;

class WrapperHandler<T> {

	private static final String DISPATECHER_SERVLET_WEB_APPLICATION_CONTEXT_ATTRIBUTE = "org.springframework.web.servlet.DispatecherServlet.CONTEXT";

	private Class<?> typeClass;
	private T delegate;
	private T wrapped;
	private ApplicationContext applicationContext;

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
		ApplicationContext applicationContext = getApplicationContext();
		if (applicationContext == null) {
			//FIXME log a warning
			return delegate;
		}

		Map<String, FacesWrapperFactoryBean> beans = BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext,
				FacesWrapperFactoryBean.class);
		Set<Entry<String, FacesWrapperFactoryBean>> orderdBans = MapEntryValueComparator.entrySet(beans,
				new AnnotationAwareOrderComparator());

		T rtn = delegate;
		for (Entry<String, FacesWrapperFactoryBean> entry : orderdBans) {
			FacesWrapperFactoryBean factory = entry.getValue();
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

	private ApplicationContext getApplicationContext() {
		if (applicationContext == null) {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();
			applicationContext = asWebApplicationContext(externalContext.getRequestMap().get(
					DISPATECHER_SERVLET_WEB_APPLICATION_CONTEXT_ATTRIBUTE));
			if (applicationContext == null) {
				applicationContext = asWebApplicationContext(externalContext.getApplicationMap().get(
						WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE));
			}
			//FIXME assert not null
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

	private boolean isFactorySupported(FacesWrapperFactoryBean factory) {
		Class typeArg = GenericTypeResolver.resolveTypeArgument(factory.getClass(), FacesWrapperFactoryBean.class);
		if (typeArg == null) {
			Class targetClass = AopUtils.getTargetClass(this.wrapped);
			if (targetClass != this.wrapped.getClass()) {
				typeArg = GenericTypeResolver.resolveTypeArgument(targetClass, FacesWrapperFactoryBean.class);
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
