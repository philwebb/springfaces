package org.springframework.springfaces.mvc.servlet.view;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.RedirectView;

public class BookmarkableRedirectView extends RedirectView implements Bookmarkable {

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
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		super.renderMergedOutputModel(model, request, response);
	}

	public String getBookmarkUrl(Map<String, Object> model, HttpServletRequest request) {
		return null;
	}
}
