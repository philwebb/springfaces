package org.springframework.springfaces.mvc.internal;

import javax.faces.context.FacesContext;

import org.springframework.springfaces.render.ViewArtifact;
import org.springframework.util.Assert;

public class DefaultViewArtifactHolder implements ViewArtifactHolder {

	private static final String VIEW_ARTIFACT_ATTRIBUTE = DefaultViewArtifactHolder.class.getName() + ".ARTIFACT";

	private FacesContext getContext() {
		FacesContext context = FacesContext.getCurrentInstance();
		Assert.state(context != null, "Unable to obtain the FacesContext");
		return context;
	}

	public void put(ViewArtifact viewArtifact) {
		Assert.notNull(viewArtifact, "viewArtifact must not be null");
		getContext().getAttributes().put(VIEW_ARTIFACT_ATTRIBUTE, viewArtifact);
	}

	public ViewArtifact get() {
		return (ViewArtifact) getContext().getAttributes().get(VIEW_ARTIFACT_ATTRIBUTE);
	}

	public void clear() {
		getContext().getAttributes().remove(VIEW_ARTIFACT_ATTRIBUTE);
	}
}
