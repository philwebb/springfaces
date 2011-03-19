package org.springframework.springfaces.mvc;

import org.springframework.web.servlet.View;

public interface FacesViewResolver {

	public boolean isSupported(String viewId);

	public View getView(String viewId);

}
