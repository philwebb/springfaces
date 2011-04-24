package org.springframework.springfaces.mvc.servlet;

import java.util.Locale;

import org.springframework.web.servlet.View;

//FIXME replace with Implicit version
@Deprecated
public interface ViewIdResolver {

	public boolean isResolvable(String viewId);

	public View resolveViewId(String viewId, Locale locale);

}
