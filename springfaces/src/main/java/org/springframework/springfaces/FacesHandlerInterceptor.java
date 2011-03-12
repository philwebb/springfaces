package org.springframework.springfaces;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class FacesHandlerInterceptor extends HandlerInterceptorAdapter {

	static final String KEY = FacesHandlerInterceptor.class.getName();

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		//FIXME better as a thread local perhaps
		request.setAttribute(KEY, modelAndView.getModelMap());
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		request.removeAttribute(KEY);
	}

}
