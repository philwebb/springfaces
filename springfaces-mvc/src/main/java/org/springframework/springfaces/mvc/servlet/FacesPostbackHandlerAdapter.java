package org.springframework.springfaces.mvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.springfaces.mvc.DefaultFacesView;
import org.springframework.springfaces.mvc.SpringFacesContext;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.WebContentGenerator;

public class FacesPostbackHandlerAdapter extends WebContentGenerator implements HandlerAdapter {

	public boolean supports(Object handler) {
		return FacesPostbackHandlerMapping.class.equals(handler);
	}

	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		//FIXME restore the view somehow
		String viewName = "test";
		DefaultFacesView view = new DefaultFacesView("/WEB-INF/pages/test.xhtml");
		getApplicationContext().getAutowireCapableBeanFactory().initializeBean(view, viewName);

		//FIXME make typesafe
		SpringFacesContext.getCurrentInstance().render(view);
		return null;
	}

	public long getLastModified(HttpServletRequest request, Object handler) {
		//FIXME
		return -1;
	}

}
