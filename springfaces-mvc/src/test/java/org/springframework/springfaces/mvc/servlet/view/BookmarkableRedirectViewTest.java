package org.springframework.springfaces.mvc.servlet.view;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BookmarkableRedirectViewTest {

	@Mock
	private HttpServletRequest request;

	@Before
	public void setupMocks() {
		given(request.getContextPath()).willReturn("/context");
	}

	@Test
	public void shouldExpandPathVariables() throws Exception {
		BookmarkableRedirectView view = new BookmarkableRedirectView("/ab/{cd}/ef/{gh}");
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("cd", "CD");
		model.put("gh", "GH");
		String actual = view.getBookmarkUrl(model, request);
		assertEquals("/ab/CD/ef/GH", actual);
	}

	@Test
	@Ignore
	public void shouldEncodePathVariables() throws Exception {
		BookmarkableRedirectView view = new BookmarkableRedirectView("/ab/{cd}");
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("cd", "C D");
		String actual = view.getBookmarkUrl(model, request);
		assertEquals("/ab/C%20D", actual);
	}

	@Test
	public void shouldNotAddQueryParamForPathVariable() throws Exception {
		BookmarkableRedirectView view = new BookmarkableRedirectView("/ab/{cd}");
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("cd", "CD");
		model.put("gh", "GH");
		String actual = view.getBookmarkUrl(model, request);
		assertEquals("/ab/CD?gh=GH", actual);
	}

	@Test
	public void shouldFailIfPathVariableNotInModel() throws Exception {

	}

	@Test
	public void shouldAddContextPath() throws Exception {
		BookmarkableRedirectView view = new BookmarkableRedirectView("/ab", true);
		String actual = view.getBookmarkUrl(null, request);
		assertEquals("/context/ab", actual);
	}

	@Test
	public void shouldNotAddContextPathIfNotSlashPrefixed() throws Exception {
		BookmarkableRedirectView view = new BookmarkableRedirectView("ab", true);
		String actual = view.getBookmarkUrl(null, request);
		assertEquals("ab", actual);
	}

	@Test
	public void shouldRedirectWithPathVariables() throws Exception {
		BookmarkableRedirectView view = new BookmarkableRedirectView("/ab/{cd}");
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("cd", "CD");
		String expected = "/ab/CD";
		HttpServletResponse response = mock(HttpServletResponse.class);
		given(response.encodeRedirectURL(expected)).willReturn(expected);
		view.render(model, request, response);
		verify(response).sendRedirect(expected);
	}

}
