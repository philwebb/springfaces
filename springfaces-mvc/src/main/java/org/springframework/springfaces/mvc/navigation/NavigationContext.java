package org.springframework.springfaces.mvc.navigation;

import javax.faces.context.FacesContext;

public interface NavigationContext {

	FacesContext getFacesContext();

	Object getHandler();

	String getFromAction();

	String getOutcome();

	boolean isPreEmptive();
}
