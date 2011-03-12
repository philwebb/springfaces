package org.springframework.springfaces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class SpringFacesUtils {

	public static <T> Collection<T> getBeans(Class<T> type) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext == null) {
			//FIXME Log warning
			return Collections.emptySet();
		}
		List<T> beans = new ArrayList<T>();

		//Add beans from the DispatchServer context
		Object request = facesContext.getExternalContext().getRequest();
		if (request instanceof HttpServletRequest) {
			Object context = ((HttpServletRequest) request)
					.getAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE);
			beans.addAll(getBeans(context, type));
		}

		//Add beans from the root web application context
		Object context = facesContext.getExternalContext().getApplicationMap()
				.get(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		beans.addAll(getBeans(context, type));

		Collections.sort(beans, new AnnotationAwareOrderComparator());
		return Collections.unmodifiableCollection(beans);
	}

	private static <T> Collection<T> getBeans(Object context, Class<T> type) {
		WebApplicationContext webApplicationContext = asWebApplicationContext(context);
		if (webApplicationContext == null) {
			return Collections.emptySet();
		}
		return BeanFactoryUtils.beansOfTypeIncludingAncestors(webApplicationContext, type).values();
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

}