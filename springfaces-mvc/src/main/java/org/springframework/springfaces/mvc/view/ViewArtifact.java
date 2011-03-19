package org.springframework.springfaces.mvc.view;

import java.io.Serializable;

import javax.faces.view.ViewDeclarationLanguage;

/**
 * A reference to an artifact that contains the {@link ViewDeclarationLanguage VDL} syntax of a JSF view. In most cases
 * a <tt>ViewArtifact</tt> will refer to the location of a facelet file, for example: <tt>/WEB-INF/pages/page.xhtml</tt>
 * .
 * 
 * @author Phillip Webb
 */
public final class ViewArtifact implements Serializable {

	private static final long serialVersionUID = 1L;

	private String artifact;

	public ViewArtifact(String artifact) {
		super();
		// FIXME ANN
		this.artifact = artifact;
	}

	@Override
	public String toString() {
		return artifact;
	}
}
