package org.springframework.springfaces.mvc.view;

public class RenderablePostback implements Renderable {

	private String viewId;

	public RenderablePostback(String viewId) {
		this.viewId = viewId;
	}

	public String getViewId() {
		return viewId;
	}

	public String getActionUrl() {
		return null;
	}
}
