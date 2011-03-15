package org.springframework.springfaces.mvc.view;

public interface ViewState {

	String getViewName();

	String getViewId();

	//DC the action URL or null to use the request
	String getActionUrl();
}
