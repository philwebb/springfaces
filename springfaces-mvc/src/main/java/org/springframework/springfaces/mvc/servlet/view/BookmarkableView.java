package org.springframework.springfaces.mvc.servlet.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.View;

/**
 * A MVC {@link View} that can be {@link #getBookmarkUrl bookmarked}.
 * 
 * @author Phillip Webb
 */
public interface BookmarkableView extends View {

	/**
	 * Return a bookmark URL for the view given the specified model.
	 * @param model Map with name Strings as keys and corresponding model objects as values (Map can also be
	 * <code>null</code> in case of empty model)
	 * @param request current HTTP request
	 * @return a bookmark URL
	 * @throws Exception if rendering failed
	 */
	String getBookmarkUrl(Map<String, ?> model, HttpServletRequest request) throws Exception;
}
