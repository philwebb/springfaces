package org.springframework.springfaces.mvc.s;

import javax.faces.render.ResponseStateManager;
import javax.servlet.http.HttpServletRequest;

import org.springframework.core.Ordered;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;

public class FacesHandlerMapping extends AbstractHandlerMapping implements Ordered {

	public FacesHandlerMapping() {
		super();
		setOrder(HIGHEST_PRECEDENCE);
	}

	@Override
	protected Object getHandlerInternal(HttpServletRequest request) throws Exception {

		if (request.getParameter(ResponseStateManager.VIEW_STATE_PARAM) != null) {
			//FIXME this is not enough, we need our own state manager and our own parameter.  Also possibly check if post
			return FacesHandlerMapping.class;
		}
		return null;
	}

}
