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
