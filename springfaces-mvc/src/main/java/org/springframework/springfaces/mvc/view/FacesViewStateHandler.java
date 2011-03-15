package org.springframework.springfaces.mvc.view;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 * Strategy interface use to read and write {@link Renderable}.
 *
 * @author Phillip Webb
 */
public interface FacesViewStateHandler {

	/**
	 * Write the specified view state.
	 * @param facesContext The faces context
	 * @param viewState The view state to write
	 */
	public void writeViewState(FacesContext facesContext, Renderable viewState) throws IOException;

	/**
	 * Read previously saved view state.  This method will be called during postback in order to restore state.  NOTE: this method
	 * will be called for every JSF postback, implementations should take care to only restore state that was
	 * previously {@link #writeViewState written}.
	 * @param request The request used to retrieve view state
	 * @return ViewState or <tt>null</tt> if the postback is not relevant
	 */
	public Renderable readViewState(HttpServletRequest request) throws IOException;
}
