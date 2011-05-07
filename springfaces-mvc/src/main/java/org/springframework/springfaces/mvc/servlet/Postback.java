package org.springframework.springfaces.mvc.servlet;

import org.springframework.springfaces.render.ViewArtifact;

public class Postback {

	// FIXME DC

	private ViewArtifact viewArtifact;
	private Object handler;

	public Postback(ViewArtifact viewArtifact, Object handler) {
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