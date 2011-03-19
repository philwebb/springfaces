package org.springframework.springfaces.mvc.servlet;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.internal.MvcViewHandler;
import org.springframework.springfaces.render.FacesViewStateHandler;
import org.springframework.springfaces.render.ViewArtifact;
import org.springframework.util.Assert;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.handler.MappedInterceptor;

public class FacesPostbackHandler extends AbstractHandlerMapping implements HandlerAdapter, HandlerMapping, Ordered,
		ApplicationListener<ContextRefreshedEvent> {

	private static final String DISABLE_ATTRIBUTE = FacesPostbackHandler.class.getName() + ".DISABLE";

	private DelegateDispatcherServlet delegateDispatcherServlet = new DelegateDispatcherServlet();

	private FacesViewStateHandler stateHandler;

	private FacesHandlerInterceptor facesInterceptor;

	public FacesPostbackHandler() {
		super();
		setOrder(HIGHEST_PRECEDENCE);
	}

	@Override
	protected void initInterceptors() {
		super.initInterceptors();
		findFacesInterceptor();
	}

	private void findFacesInterceptor() {
		Map<String, MappedInterceptor> mappedInterceptors = BeanFactoryUtils.beansOfTypeIncludingAncestors(
				getApplicationContext(), MappedInterceptor.class, true, false);
		for (MappedInterceptor mappedInterceptor : mappedInterceptors.values()) {
			if (mappedInterceptor.getInterceptor() instanceof FacesHandlerInterceptor) {
				Assert.state(facesInterceptor == null, "Multiple " + FacesHandlerInterceptor.class.getSimpleName()
						+ " registered within the web context");
				facesInterceptor = (FacesHandlerInterceptor) mappedInterceptor.getInterceptor();
			}
		}
		Assert.state(facesInterceptor != null, "No" + FacesHandlerInterceptor.class.getSimpleName()
				+ " registered within the web context");
	}

	@Override
	protected Object getHandlerInternal(HttpServletRequest request) throws Exception {
		if (request.getAttribute(DISABLE_ATTRIBUTE) != null) {
			return null;
		}
		ViewArtifact viewArtifact = getStateHandler().read(request);
		if (viewArtifact == null) {
			return null;
		}
		//FIXME get the delegate handler here
		return new HandlerExecutionChain(new Postback(viewArtifact), new HandlerInterceptor[] { facesInterceptor });

	}

	public boolean supports(Object handler) {
		return handler instanceof Postback;
	}

	public ModelAndView handle(final HttpServletRequest request, HttpServletResponse response, final Object handler)
			throws Exception {
		request.setAttribute(DISABLE_ATTRIBUTE, Boolean.TRUE);
		try {
			//FIXME check supports

			HandlerExecutionChain chain = delegateDispatcherServlet.getHandler(request);
			System.out.println(chain.getHandler());

			SpringFacesContext springFacesContext = SpringFacesContext.getCurrentInstance();
			Assert.state(springFacesContext != null, "Unable to locate the SpringFacesContext.  Ensure that a "
					+ FacesHandlerInterceptor.class.getSimpleName() + " is registered in the web context");
			FacesContext facesContext = springFacesContext.getFacesContext(true);
			try {
				ViewArtifact viewArtifact = ((Postback) handler).getViewArtifact();
				//FIXME model?
				MvcViewHandler.prepare(facesContext, viewArtifact, null);
				springFacesContext.getLifecycle().execute(facesContext);
				springFacesContext.getLifecycle().render(facesContext);
			} finally {
				facesContext.release();
			}

		} finally {
			request.removeAttribute(DISABLE_ATTRIBUTE);
		}
		return null;
	}

	public long getLastModified(HttpServletRequest request, Object handler) {
		//FIXME
		return -1;
	}

	public void onApplicationEvent(ContextRefreshedEvent event) {
		delegateDispatcherServlet.onApplicationEvent(event);
	}

	public FacesViewStateHandler getStateHandler() {
		return stateHandler;
	}

	public void setStateHandler(FacesViewStateHandler stateHandler) {
		this.stateHandler = stateHandler;
	}

	/**
	 * Set whether to detect all HandlerMapping beans in this servlet's context. Otherwise,
	 * just a single bean with name "handlerMapping" will be expected.
	 * <p>Default is "true". Turn this off if you want this servlet to use a single
	 * HandlerMapping, despite multiple HandlerMapping beans being defined in the context.
	 */
	public void setDetectAllHandlerMappings(boolean detectAllHandlerMappings) {
		this.delegateDispatcherServlet.setDetectAllHandlerMappings(detectAllHandlerMappings);
	}

	private static class Postback {

		private ViewArtifact viewArtifact;

		public Postback(ViewArtifact viewArtifact) {
			this.viewArtifact = viewArtifact;
		}

		public ViewArtifact getViewArtifact() {
			return viewArtifact;
		}
	}

	//FIXME can we put this somewhere common, we also need support for viewListeners
	private static class DelegateDispatcherServlet extends DispatcherServlet {
		private static final long serialVersionUID = 1L;

		@Override
		public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
			return super.getHandler(request);
		}
	}

}
