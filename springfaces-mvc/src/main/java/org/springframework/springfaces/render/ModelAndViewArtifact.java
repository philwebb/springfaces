package org.springframework.springfaces.render;

import java.util.Map;

import org.springframework.util.Assert;

public final class ModelAndViewArtifact {

	private ViewArtifact viewArtifact;
	private Map<String, Object> model;

	public ModelAndViewArtifact(ViewArtifact viewArtifact, Map<String, Object> model) {
		super();
		Assert.notNull(viewArtifact, "ViewArtifact must not be null");
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
