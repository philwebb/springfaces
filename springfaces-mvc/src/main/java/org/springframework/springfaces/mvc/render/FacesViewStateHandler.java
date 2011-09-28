package org.springframework.springfaces.mvc.render;

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
	 * @param facesContext the faces context
	 * @param viewArtifact the view state to write
	 * @throws IOException on write error
	 */
	void write(FacesContext facesContext, ViewArtifact viewArtifact) throws IOException;

	/**
	 * Read previously saved view artifact. This method will be called during postback in order to restore state. NOTE:
	 * this method will be called for every JSF postback, implementations should take care to only restore artifacts
	 * that were previously {@link #write written}.
	 * @param request the request used to retrieve view state
	 * @return a ViewArtifact or <tt>null</tt> if the postback is not relevant
	 * @throws IOException on read error
	 */
	ViewArtifact read(HttpServletRequest request) throws IOException;
}
