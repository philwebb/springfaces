package org.springframework.springfaces.mvc.view;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 * Strategy interface use to read and write {@link ViewArtifact} data.
 * 
 * @author Phillip Webb
 */
public interface FacesViewStateHandler {

	/**
	 * Write the specified view artifact.
	 * @param facesContext The faces context
	 * @param viewArtifact The view state to write
	 */
	public void write(FacesContext facesContext, ViewArtifact viewArtifact) throws IOException;

	/**
	 * Read previously saved view artifact. This method will be called during postback in order to restore state. NOTE:
	 * this method will be called for every JSF postback, implementations should take care to only restore artifacts
	 * that were previously {@link #write written}.
	 * @param request The request used to retrieve view state
	 * @return ViewArtifact or <tt>null</tt> if the postback is not relevant
	 */
	public ViewArtifact read(HttpServletRequest request) throws IOException;
}
