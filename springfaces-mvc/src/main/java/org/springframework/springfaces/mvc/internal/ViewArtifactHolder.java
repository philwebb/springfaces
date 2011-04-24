package org.springframework.springfaces.mvc.internal;

import org.springframework.springfaces.render.ViewArtifact;

public interface ViewArtifactHolder {

	void put(ViewArtifact viewArtifact);

	ViewArtifact get();

	void clear();
}
