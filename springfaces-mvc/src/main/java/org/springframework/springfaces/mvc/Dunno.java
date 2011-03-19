package org.springframework.springfaces.mvc;

import org.springframework.web.servlet.View;

public interface Dunno {

	public boolean isSupported(String viewId);

	public View getView(String viewName);

}
