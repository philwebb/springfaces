package org.springframework.springfaces.mvc.servlet;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.core.Ordered;
import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.internal.MvcViewHandler;
import org.springframework.springfaces.render.FacesViewStateHandler;
import org.springframework.springfaces.render.ViewArtifact;
import org.springframework.util.Assert;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.handler.MappedInterceptor;

public class FacesPostbackHandler extends AbstractHandlerMapping implements HandlerAdapter, HandlerMapping, Ordered {

	private static final String DISABLE_ATTRIBUTE = FacesPostbackHandler.class.getName() + ".DISABLE";

	protected static final String METHOD_GET = "GET";

	private FacesPostbackOrginalHandlerLocator originalHandlerLocator;

	/**
	 * State handler used to obtain the {@link ViewArtifact} if the request is a JSF/MVC postback.
	 */
	private FacesViewStateHandler stateHandler;

	/**
	 * The {@link FacesHandlerInterceptor} used by the handler to ensure the {@link SpringFacesContext} is available.
	 */
	private HandlerInterceptor facesHandlerInterceptor;

	/**
	 * Constructor
	 * @param stateHandler The state handler
	 */
	public FacesPostbackHandler(FacesViewStateHandler stateHandler,
			FacesPostbackOrginalHandlerLocator orginalHandlerLocator) {
		super();
		this.stateHandler = stateHandler;
		this.originalHandlerLocator = orginalHandlerLocator;
		setOrder(HIGHEST_PRECEDENCE);
	}

	@Override
	protected void initInterceptors() {
		super.initInterceptors();
		facesHandlerInterceptor = findFacesHandlerInterceptor();
	}

	/**
	 * Obtain the {@link FacesHandlerInterceptor} by searching all mapped interceptors.
	 */
	protected HandlerInterceptor findFacesHandlerInterceptor() {
		Map<String, MappedInterceptor> mappedInterceptors = BeanFactoryUtils.beansOfTypeIncludingAncestors(
				getApplicationContext(), MappedInterceptor.class, true, false);
		for (MappedInterceptor mappedInterceptor : mappedInterceptors.values()) {
			if (mappedInterceptor.getInterceptor() instanceof FacesHandlerInterceptor) {
				Assert.state(facesHandlerInterceptor == null,
						"Multiple " + FacesHandlerInterceptor.class.getSimpleName()
								+ " registered within the web context");
				return mappedInterceptor.getInterceptor();
			}
		}
		throw new IllegalStateException("No" + FacesHandlerInterceptor.class.getSimpleName()
				+ " registered within the web context");

	}

	/**
	 * Return the handler interceptors that should be applied to the postback handler.
	 * @return The interceptors to apply
	 */
	protected HandlerInterceptor[] getHandlerInterceptors() {
		return new HandlerInterceptor[] { facesHandlerInterceptor };
	}

	@Override
	protected Object getHandlerInternal(HttpServletRequest request) throws Exception {
		if (request.getAttribute(DISABLE_ATTRIBUTE) != null) {
			return null;
		}
		ViewArtifact viewArtifact = stateHandler.read(request);
		if (viewArtifact == null) {
			return null;
		}
		Object orginalHandler = getOriginalHandler(request);
		return new HandlerExecutionChain(new Postback(viewArtifact, orginalHandler), getHandlerInterceptors());
	}

	/**
	 * Return the handler that would have processed the request if it were not a postback.
	 * @param request The current request
	 * @return The original handler
	 */
	private Object getOriginalHandler(HttpServletRequest request) {
		request.setAttribute(DISABLE_ATTRIBUTE, Boolean.TRUE);
		try {
			// Change the method to GET to mimic the orginal request
			request = new HttpServletRequestWrapper(request) {
				public String getMethod() {
					return METHOD_GET;
				}
			};
			HandlerExecutionChain chain = originalHandlerLocator.getHandler(request);
			return chain.getHandler();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		} finally {
			request.removeAttribute(DISABLE_ATTRIBUTE);
		}
	}

	public boolean supports(Object handler) {
		return handler instanceof Postback;
	}

	public ModelAndView handle(final HttpServletRequest request, HttpServletResponse response, final Object handler)
			throws Exception {
		Assert.state(supports(handler), "The specified handler is not supported");
		SpringFacesContext springFacesContext = SpringFacesContext.getCurrentInstance();
		Assert.state(springFacesContext != null, "Unable to locate the SpringFacesContext.  Ensure that a "
				+ FacesHandlerInterceptor.class.getSimpleName() + " is registered in the web context");
		FacesContext facesContext = springFacesContext.getFacesContext();
		try {
			ViewArtifact viewArtifact = ((Postback) handler).getViewArtifact();
			// FIXME model?
			MvcViewHandler.prepare(facesContext, viewArtifact, null);
			springFacesContext.getLifecycle().execute(facesContext);
			springFacesContext.getLifecycle().render(facesContext);
		} finally {
			facesContext.release();
		}

		return null;
	}

	public long getLastModified(HttpServletRequest request, Object handler) {
		// FIXME
		return -1;
	}
}
