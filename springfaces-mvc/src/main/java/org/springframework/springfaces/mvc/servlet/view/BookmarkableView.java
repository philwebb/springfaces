/*
 * Copyright 2010-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
