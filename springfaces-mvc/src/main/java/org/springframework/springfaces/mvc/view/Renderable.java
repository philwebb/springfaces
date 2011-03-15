package org.springframework.springfaces.mvc.view;

import javax.faces.context.ExternalContext;

public interface Renderable {

	/**
	 * Returns the view ID of the item being rendered.  The view ID usually refers to the location of a facelet file, for example: <pre>/WEB-INF/pages/page.xhtml</tt>.  Unlike
	 * the standard JSF implementation the view ID need not be related to current {@link ExternalContext#getRequestPathInfo() request path}.
	 *
	 * @return The view ID
	 */
	String getViewId();

	//DC the action URL or null to use the request
	String getActionUrl();
}
