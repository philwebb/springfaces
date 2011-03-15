package org.springframework.springfaces.mvc.servlet;

import javax.faces.render.ResponseStateManager;
import javax.servlet.http.HttpServletRequest;

import org.springframework.core.Ordered;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;

public class FacesPostbackHandlerMapping extends AbstractHandlerMapping implements Ordered {

	public FacesPostbackHandlerMapping() {
		super();
		setOrder(HIGHEST_PRECEDENCE);
	}

	@Override
	protected Object getHandlerInternal(HttpServletRequest request) throws Exception {

		if (request.getParameter(ResponseStateManager.VIEW_STATE_PARAM) != null) {
			//FIXME this is not enough, we need our own state manager and our own parameter.  Also possibly check if post
			return FacesPostbackHandlerMapping.class;
		}
		return null;
	}

}
