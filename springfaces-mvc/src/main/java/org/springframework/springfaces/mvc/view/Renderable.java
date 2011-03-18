package org.springframework.springfaces.mvc.view;

import java.io.Serializable;

import javax.faces.context.ExternalContext;

public final class Renderable implements Serializable {

	private static final long serialVersionUID = 1L;

	private String viewId;

	private String actionUrl;

	public Renderable(String viewId) {
		this(viewId, null);
	}

	public Renderable(String viewId, String actionUrl) {
		super();
		//FIXME ANN
		this.viewId = viewId;
		this.actionUrl = actionUrl;
	}

	/**
	 * Returns the view ID of the item being rendered.  The view ID usually refers to the location of a facelet file, for example: <pre>/WEB-INF/pages/page.xhtml</tt>.  Unlike
	 * the standard JSF implementation the view ID need not be related to current {@link ExternalContext#getRequestPathInfo() request path}.
	 *
	 * @return The view ID
	 */
	public String getViewId() {
		return viewId;
	}

	//DC the action URL or null to use the request
	//FIXME support this
	public String getActionUrl() {
		return actionUrl;
	}
}
