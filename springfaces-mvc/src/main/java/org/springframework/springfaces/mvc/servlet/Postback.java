package org.springframework.springfaces.mvc.servlet;

import org.springframework.springfaces.render.ViewArtifact;

/**
 * Used to pass JSF postback data from the {@link FacesHandlerInterceptor} to the {@link FacesPostbackHandler}.
 * 
 * @see FacesHandlerInterceptor
 * @see FacesPostbackHandler
 * 
 * @author Phillip Webb
 */
public class Postback {

	private ViewArtifact viewArtifact;
	private Object handler;

	public Postback(ViewArtifact viewArtifact, Object handler) {
		// FIXME ANN
		this.viewArtifact = viewArtifact;
		this.handler = handler;
	}

	public ViewArtifact getViewArtifact() {
		return viewArtifact;
	}

	public Object getHandler() {
		return handler;
	}
}