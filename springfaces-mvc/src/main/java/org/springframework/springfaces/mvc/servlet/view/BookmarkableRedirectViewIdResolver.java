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

import java.util.Locale;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

//FIXME DC

public class BookmarkableRedirectViewIdResolver extends UrlBasedViewResolver {

	public BookmarkableRedirectViewIdResolver() {
		setOrder(LOWEST_PRECEDENCE - 1);
	}

	@Override
	protected View createView(String viewName, Locale locale) throws Exception {
		if (!canHandle(viewName, locale)) {
			return null;
		}
		if (viewName.startsWith(REDIRECT_URL_PREFIX)) {
			String redirectUrl = viewName.substring(REDIRECT_URL_PREFIX.length());
			return new BookmarkableRedirectView(redirectUrl, isRedirectContextRelative(), isRedirectHttp10Compatible());
		}
		return super.createView(viewName, locale);
	}

	@Override
	protected View loadView(String viewName, Locale locale) throws Exception {
		AbstractUrlBasedView view = buildView(viewName);
		if (view == null) {
			return null;
		}
		View result = (View) getApplicationContext().getAutowireCapableBeanFactory().initializeBean(view, viewName);
		return (view.checkResource(locale) ? result : null);
	}

	@Override
	protected AbstractUrlBasedView buildView(String viewName) throws Exception {
		if (getViewClass() == null) {
			return null;
		}
		return super.buildView(viewName);
	}

}
