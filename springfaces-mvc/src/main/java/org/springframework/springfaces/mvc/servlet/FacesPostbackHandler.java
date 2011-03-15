package org.springframework.springfaces.mvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.springfaces.mvc.FacesViewStateHandler;
import org.springframework.springfaces.mvc.SpringFacesContext;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.WebContentGenerator;

public class FacesPostbackHandler extends WebContentGenerator implements HandlerAdapter, HandlerMapping, Ordered {

	private FacesViewStateHandler stateHandler;

	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

	public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
		FacesView view = stateHandler.readState(request);
		if (view == null) {
			return null;
		}
		return new HandlerExecutionChain(new FacesPostback(view));
	}

	public boolean supports(Object handler) {
		return handler instanceof FacesPostback;
	}

	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//FIXME check supports
		FacesView view = ((FacesPostback) handler).getView();
		getApplicationContext().getAutowireCapableBeanFactory().initializeBean(view, view.getBeanName());
		SpringFacesContext.getCurrentInstance().render(view);
		return null;
	}

	public long getLastModified(HttpServletRequest request, Object handler) {
		//FIXME
		return -1;
	}

	private static class FacesPostback {

		private FacesView view;

		public FacesPostback(FacesView view) {
			this.view = view;
		}

		public FacesView getView() {
			return view;
		}
	}
}
