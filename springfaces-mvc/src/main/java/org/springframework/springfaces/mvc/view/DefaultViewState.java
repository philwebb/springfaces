package org.springframework.springfaces.mvc.view;

public class DefaultViewState implements ViewState {

	private String viewName;
	private String viewId;
	private String actionUrl;

	public DefaultViewState(String viewName, String viewId, String actionUrl) {
		this.viewName = viewName;
		this.viewId = viewId;
		this.actionUrl = actionUrl;
	}

	public String getViewName() {
		return viewName;
	}

	public String getViewId() {
		return viewId;
	}

	public String getActionUrl() {
		return actionUrl;
	}
}
