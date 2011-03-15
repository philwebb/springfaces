package org.springframework.springfaces.mvc;

import org.springframework.web.servlet.View;

public interface FacesView extends View {

	String getViewName();

	String getViewId();

}
