package org.springframework.springfaces.mvc;

import javax.faces.context.ResponseWriter;
import javax.servlet.http.HttpServletRequest;

public interface FacesViewStateHandler {

	public void writeState(FacesView view, ResponseWriter responseWriter);

	//FIXME DC restored view must have view name set
	public FacesView readState(HttpServletRequest request);
}
