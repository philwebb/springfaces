package org.springframework.springfaces.mvc.view;


public class DefaultViewState implements ViewState {

	private String viewName;
	private String viewId;

	public DefaultViewState(String viewName, String viewId) {
		this.viewName = viewName;
		this.viewId = viewId;
	}

	public String getViewName() {
		return viewName;
	}

	public String getViewId() {
		return viewId;
	}
}
