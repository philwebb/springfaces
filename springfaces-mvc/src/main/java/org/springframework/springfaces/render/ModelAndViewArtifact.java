package org.springframework.springfaces.render;

import java.util.Map;

public final class ModelAndViewArtifact {

	private ViewArtifact viewArtifact;
	private Map<String, Object> model;

	public ModelAndViewArtifact(ViewArtifact viewArtifact, Map<String, Object> model) {
		super();
		this.viewArtifact = viewArtifact;
		this.model = model;
	}

	public ViewArtifact getViewArtifact() {
		return viewArtifact;
	}

	public Map<String, Object> getModel() {
		return model;
	}
}
