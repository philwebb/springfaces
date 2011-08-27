package org.springframework.springfaces.mvc.servlet.view;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriTemplate;
import org.springframework.web.util.WebUtils;

/**
 * A {@link BookmarkableView} version of {@link RedirectView} that also supports expansion of path variables using the
 * model.
 * 
 * @author Phillip Webb
 */
public class BookmarkableRedirectView extends RedirectView implements BookmarkableView {

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
		String targetUrl = doCreateTargetUrl(model, request);
		sendRedirect(request, response, targetUrl, this.http10Compatible);
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

		// Work around Spring encoding bug
		String enc = this.encodingScheme;
		if (enc == null) {
			enc = request.getCharacterEncoding();
		}
		if (enc == null) {
			enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
		}
		UriTemplate uriTemplate = createUriTemplate(getUrl(), enc);
		for (String variable : uriTemplate.getVariableNames()) {
			Object value = mutableModel.get(variable);
			value = urlEncode(value == null ? "" : value.toString(), enc);
			mutableModel.put(variable, value);
		}

		return createTargetUrl(mutableModel, request);
	}

	@SuppressWarnings("serial")
	private UriTemplate createUriTemplate(String targetUrl, final String encoding) {
		return new UriTemplate(targetUrl.toString()) {
			@Override
			protected URI encodeUri(String uri) {
				try {
					return new URI(uri);
				} catch (URISyntaxException ex) {
					throw new IllegalArgumentException("Could not create URI from [" + uri + "]: " + ex, ex);
				}
			}
		};
	}
}
