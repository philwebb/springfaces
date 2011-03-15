package org.springframework.springfaces.mvc.servlet;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.core.Ordered;
import org.springframework.springfaces.mvc.SpringFacesContext;
import org.springframework.springfaces.mvc.view.FacesViewStateHandler;
import org.springframework.springfaces.mvc.view.ViewState;
import org.springframework.util.Assert;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.handler.MappedInterceptor;

public class FacesPostbackHandler extends AbstractHandlerMapping implements HandlerAdapter, HandlerMapping, Ordered {

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
		ViewState view = getStateHandler().readViewState(request);
		if (view == null) {
			return null;
		}
		return new HandlerExecutionChain(new FacesPostback(view), new HandlerInterceptor[] { facesInterceptor });
	}

	public boolean supports(Object handler) {
		return handler instanceof FacesPostback;
	}

	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//FIXME check supports
		ViewState view = ((FacesPostback) handler).getView();
		getApplicationContext().getAutowireCapableBeanFactory().initializeBean(view, view.getViewName());
		SpringFacesContext springFacesContext = SpringFacesContext.getCurrentInstance();
		Assert.state(springFacesContext != null, "Unable to locate the SpringFacesContext.  Ensure that a "
				+ FacesHandlerInterceptor.class.getSimpleName() + " is registered in the web context");
		springFacesContext.render(view);
		return null;
	}

	public long getLastModified(HttpServletRequest request, Object handler) {
		//FIXME
		return -1;
	}

	public FacesViewStateHandler getStateHandler() {
		return stateHandler;
	}

	public void setStateHandler(FacesViewStateHandler stateHandler) {
		this.stateHandler = stateHandler;
	}

	private static class FacesPostback {

		private ViewState view;

		public FacesPostback(ViewState view) {
			this.view = view;
		}

		public ViewState getView() {
			return view;
		}
	}
}
