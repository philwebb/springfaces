package org.springframework.springfaces;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.WebContentGenerator;

public class FacesHandlerAdapter extends WebContentGenerator implements HandlerAdapter {

	public boolean supports(Object handler) {
		return FacesHandlerMapping.class.equals(handler);
	}

	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String viewName = "test";
		FacesView view = new FacesView("/WEB-INF/pages/test.xhtml");
		getApplicationContext().getAutowireCapableBeanFactory().initializeBean(view, viewName);
		view.render(request, response, true);
		return null;
	}

	public long getLastModified(HttpServletRequest request, Object handler) {
		//FIXME
		return -1;
	}

}
