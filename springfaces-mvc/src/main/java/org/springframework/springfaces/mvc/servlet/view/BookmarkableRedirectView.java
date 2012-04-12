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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.RedirectView;

/**
 * A {@link BookmarkableView} version of {@link RedirectView} that also supports expansion of path variables using the
 * model.
 * 
 * @author Phillip Webb
 */
public class BookmarkableRedirectView extends RedirectView implements BookmarkableView, FacesRenderedView {

	private boolean http10Compatible;

	public BookmarkableRedirectView() {
		super();
	}

	public BookmarkableRedirectView(String url) {
		this(url, false);
	}

	public BookmarkableRedirectView(String url, boolean contextRelative) {
		this(url, contextRelative, true);
	}

	public BookmarkableRedirectView(String url, boolean contextRelative, boolean http10Compatible) {
		this(url, contextRelative, http10Compatible, true);
	}

	public BookmarkableRedirectView(String url, boolean contextRelative, boolean http10Compatible,
			boolean exposeModelAttributes) {
		super(url, contextRelative, http10Compatible, exposeModelAttributes);
		this.http10Compatible = http10Compatible;
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String targetUrl = doCreateTargetUrl(model, request);
		sendRedirect(request, response, targetUrl, this.http10Compatible);
	}

	// FIXME test
	public void render(Map<String, ?> model, FacesContext facesContext) throws Exception {
		HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
		String targetUrl = doCreateTargetUrl(model, request);
		facesContext.getExternalContext().redirect(targetUrl);
	}

	public String getBookmarkUrl(Map<String, ?> model, HttpServletRequest request) throws IOException {
		return doCreateTargetUrl(model, request);
	}

	private String doCreateTargetUrl(Map<String, ?> model, HttpServletRequest request)
			throws UnsupportedEncodingException {
		Map<String, Object> mutableModel = new HashMap<String, Object>();
		if (model != null) {
			mutableModel.putAll(model);
		}
		return createTargetUrl(mutableModel, request);
	}
}
