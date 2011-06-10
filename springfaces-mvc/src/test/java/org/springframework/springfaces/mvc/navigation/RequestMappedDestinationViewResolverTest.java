package org.springframework.springfaces.mvc.navigation;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.security.Principal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.springfaces.mvc.FacesContextSetter;
import org.springframework.springfaces.mvc.servlet.view.BookmarkableView;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;

@RunWith(MockitoJUnitRunner.class)
public class RequestMappedDestinationViewResolverTest {

	@Mock
	private FacesContext facesContext;

	@Before
	public void setup() {
		FacesContextSetter.setCurrentInstance(facesContext);
	}

	@After
	public void teardown() {
		FacesContextSetter.setCurrentInstance(null);
	}

	@Test
	public void testname() throws Exception {
		RequestMappedDestinationViewResolver resolver = new RequestMappedDestinationViewResolver();
		ApplicationContext applicationContext = mock(ApplicationContext.class);
		Object bean = new HotelsController();
		given(applicationContext.getBean("bean")).willReturn(bean);
		resolver.setApplicationContext(applicationContext);
		View destination = resolver.resolveDestination("@bean.search", Locale.UK);
		HttpServletRequest request = mock(HttpServletRequest.class);
		Map<String, Object> model = new HashMap<String, Object>();
		SearchCriteria searchCriteria = new SearchCriteria();
		searchCriteria.setPage(10);
		searchCriteria.setSearchString("test");
		model.put("searchCriteria", searchCriteria);
		System.out.println(((BookmarkableView) destination).getBookmarkUrl(model, request));
	}

	// FIXME proper tests

	public static class HotelsController {
		@RequestMapping(value = "/hotels/search", method = RequestMethod.GET)
		public void search(SearchCriteria searchCriteria, Principal currentUser, Model model) {
		}

		@RequestMapping(value = "/hotels", method = RequestMethod.GET)
		public String list(SearchCriteria criteria, Model model) {
			return null;
		}

		@RequestMapping(value = "/hotels/{id}", method = RequestMethod.GET)
		public String show(@PathVariable Long id, Model model) {
			return null;
		}
	}

	public static class SearchCriteria {
		private String searchString;
		private int pageSize;
		private int page;

		public String getSearchString() {
			return searchString;
		}

		public void setSearchString(String searchString) {
			this.searchString = searchString;
		}

		public int getPageSize() {
			return pageSize;
		}

		public void setPageSize(int pageSize) {
			this.pageSize = pageSize;
		}

		public int getPage() {
			return page;
		}

		public void setPage(int page) {
			this.page = page;
		}
	}

}
