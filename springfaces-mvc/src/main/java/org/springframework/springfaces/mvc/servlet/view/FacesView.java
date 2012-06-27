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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.render.ViewArtifact;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

/**
 * A Spring {@link View} that can be used to render a JSF Page. In order to render the view a {@link SpringFacesContext}
 * must be active.
 * @see SpringFacesContext
 * @author Phillip Webb
 */
public class FacesView extends AbstractUrlBasedView {

	public FacesView() {
		super();
	}

	public FacesView(String url) {
		super();
		setUrl(url);
	}

	public FacesView(ViewArtifact viewArtifact) {
		super();
		Assert.notNull(viewArtifact, "ViewArtifact must not be null");
		setUrl(viewArtifact.toString());
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		SpringFacesContext.getCurrentInstance(true).render(this, removeBindingResults(model));

	}

	/**
	 * Remove {@link BindingResult}s from the model
	 * @param model
	 * @return
	 */
	private Map<String, Object> removeBindingResults(Map<String, Object> model) {
		model = new LinkedHashMap<String, Object>(model);
		for (Iterator<Map.Entry<String, Object>> iterator = model.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry<String, Object> entry = iterator.next();
			if (entry.getValue() instanceof BindingResult) {
				iterator.remove();
			}
		}
		return model;
	}

	@Override
	public void setUrl(String url) {
		Assert.hasLength(url, "URL must not be empty");
		super.setUrl(url);
	}

	public String getViewId() {
		return getUrl();
	}

	public ViewArtifact getViewArtifact() {
		return new ViewArtifact(getUrl());
	}
}
