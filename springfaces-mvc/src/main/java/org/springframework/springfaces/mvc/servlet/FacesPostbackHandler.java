package org.springframework.springfaces.mvc.servlet;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.core.Ordered;
import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.render.FacesViewStateHandler;
import org.springframework.springfaces.mvc.render.ModelAndViewArtifact;
import org.springframework.springfaces.mvc.render.ViewArtifact;
import org.springframework.util.Assert;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.handler.MappedInterceptor;

/**
 * MVC {@link HandlerAdapter} used to handle JSF postbacks.
 * 
 * @see FacesHandlerInterceptor
 * @see Postback
 * @author Phillip Webb
 */
public class FacesPostbackHandler extends AbstractHandlerMapping implements HandlerAdapter, HandlerMapping, Ordered {

	private static final String DISABLE_ATTRIBUTE = FacesPostbackHandler.class.getName() + ".DISABLE";

	protected static final String METHOD_GET = "GET";

	private OriginalHandlerLocator originalHandlerLocator;

	/**
	 * State handler used to obtain the {@link ViewArtifact} if the request is a JSF/MVC postback.
	 */
	private FacesViewStateHandler stateHandler;

	/**
	 * The {@link FacesHandlerInterceptor} used by the handler to ensure the {@link SpringFacesContext} is available.
	 */
	private HandlerInterceptor facesHandlerInterceptor;

	/**
	 * Create a new FacesPostbackHandler.
	 * @param stateHandler the state handler
	 * @param originalHandlerLocator used to locate the original handler.
	 */
	public FacesPostbackHandler(FacesViewStateHandler stateHandler, OriginalHandlerLocator originalHandlerLocator) {
		super();
		this.stateHandler = stateHandler;
		this.originalHandlerLocator = originalHandlerLocator;
		setOrder(HIGHEST_PRECEDENCE);
	}

	@Override
	protected void initInterceptors() {
		super.initInterceptors();
		facesHandlerInterceptor = findFacesHandlerInterceptor();
	}

	/**
	 * Obtain the {@link FacesHandlerInterceptor} by searching all mapped interceptors.
	 * @return the interceptor
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
	 * @return the interceptors to apply
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
	 * @return the original handler
	 */
	private Object getOriginalHandler(HttpServletRequest request) {
		request.setAttribute(DISABLE_ATTRIBUTE, Boolean.TRUE);
		try {
			// Change the method to GET to mimic the original request
			request = new HttpServletRequestWrapper(request) {
				public String getMethod() {
					return METHOD_GET;
				}
			};
			HandlerExecutionChain chain = originalHandlerLocator.getOriginalHandler(request);
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
		ViewArtifact viewArtifact = ((Postback) handler).getViewArtifact();
		SpringFacesContext.getCurrentInstance(true).render(new ModelAndViewArtifact(viewArtifact, null));
		return null;
	}

	public long getLastModified(HttpServletRequest request, Object handler) {
		return -1;
	}
}
