package org.springframework.springfaces.mvc.servlet.view;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.Assert;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

/**
 * A {@link Bookmarkable} version of {@link RedirectView} that also supports expansion of path variables using the
 * model.
 * 
 * @author Phillip Webb
 */
public class BookmarkableRedirectView extends RedirectView implements Bookmarkable {

	private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{([^/]+?)\\}");

	private boolean contextRelative;
	private boolean http10Compatible;
	private boolean exposeModelAttributes;

	private String encodingScheme;

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
		this.contextRelative = contextRelative;
		this.http10Compatible = http10Compatible;
		this.exposeModelAttributes = exposeModelAttributes;
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String targetUrl = getBookmarkUrl(model, request);
		sendRedirect(request, response, targetUrl, this.http10Compatible);
	}

	public String getBookmarkUrl(Map<String, ?> model, HttpServletRequest request) throws IOException {
		Map<String, Object> mergedModel = new HashMap<String, Object>();
		if (model != null) {
			mergedModel.putAll(model);
		}
		StringBuilder targetUrl = new StringBuilder();
		if (this.contextRelative && getUrl().startsWith("/")) {
			targetUrl.append(request.getContextPath());
		}

		StringBuffer variableExpandedUrl = new StringBuffer();
		Matcher matcher = VARIABLE_PATTERN.matcher(getUrl());
		while (matcher.find()) {
			String variableName = matcher.group(1);
			Object variableValue = mergedModel.remove(variableName);
			Assert.state(variableValue != null, "Unable to locate path variable '" + variableName
					+ "' from model for URL '" + getUrl() + "'");
			matcher.appendReplacement(variableExpandedUrl, variableValue.toString());
		}
		matcher.appendTail(variableExpandedUrl);
		targetUrl.append(variableExpandedUrl);

		if (this.exposeModelAttributes) {
			String enc = this.encodingScheme;
			if (enc == null) {
				enc = request.getCharacterEncoding();
			}
			if (enc == null) {
				enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
			}
			appendQueryProperties(targetUrl, mergedModel, enc);
		}
		return targetUrl.toString();
	}
}
