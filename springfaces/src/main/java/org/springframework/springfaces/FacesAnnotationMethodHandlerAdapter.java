package org.springframework.springfaces;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

public class FacesAnnotationMethodHandlerAdapter extends AnnotationMethodHandlerAdapter {
	//FIXME may not need

	@Override
	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		System.out.println("Hello");
		return super.handle(request, response, handler);
	}

}
