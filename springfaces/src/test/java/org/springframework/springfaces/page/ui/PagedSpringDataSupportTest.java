package org.springframework.springfaces.page.ui;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Tests for {@link PagedSpringDataSupport}.
 * 
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class PagedSpringDataSupportTest {

	@Mock
	private PageRequest pageRequest;

	@Mock
	private Page<String> page;

	@Mock
	private Object notPage;

	@After
	public void resetHasSpringData() {
		PagedSpringDataSupport.setHasSpringData(true);
	}

	@Test
	public void shouldMakePageableWithSpringData() throws Exception {
		PageRequest pageable = PagedSpringDataSupport.getInstance().makePageable(this.pageRequest);
		assertThat(pageable, is(instanceOf(Pageable.class)));
	}

	@Test
	public void shouldNotMakePageableWithoutSpringData() throws Exception {
		PagedSpringDataSupport.setHasSpringData(false);
		PageRequest pageable = PagedSpringDataSupport.getInstance().makePageable(this.pageRequest);
		assertThat(pageable, is(not(instanceOf(Pageable.class))));
	}

	@Test
	public void shouldGetContentFromPageWithSpringData() throws Exception {
		List<String> pageContent = Collections.singletonList("a");
		given(this.page.getContent()).willReturn(pageContent);
		Object content = PagedSpringDataSupport.getInstance().getContentFromPage(this.page);
		assertThat(content, is(sameInstance((Object) pageContent)));
	}

	@Test
	public void shouldNotGetContentIfNotPageWithSpringData() throws Exception {
		Object content = PagedSpringDataSupport.getInstance().getContentFromPage(this.notPage);
		assertThat(content, is(sameInstance(this.notPage)));
	}

	@Test
	public void shouldNotGetContentFromPageWithoutSpringData() throws Exception {
		PagedSpringDataSupport.setHasSpringData(false);
		Object content = PagedSpringDataSupport.getInstance().getContentFromPage(this.notPage);
		assertThat(content, is(sameInstance(this.notPage)));
	}

	@Test
	public void shouldGetRowCountFromPageWithSpringData() throws Exception {
		Long numberOfElements = 100L;
		given(this.page.getTotalElements()).willReturn(numberOfElements);
		Object content = PagedSpringDataSupport.getInstance().getRowCountFromPage(this.page);
		assertThat(content, is(equalTo((Object) numberOfElements)));
	}

	@Test
	public void shouldNotGetRowCountIfNotPageWithSpringData() throws Exception {
		Object content = PagedSpringDataSupport.getInstance().getRowCountFromPage(this.notPage);
		assertThat(content, is(nullValue()));
	}

	@Test
	public void shouldNotGetRowCountFromPageWithoutSpringData() throws Exception {
		PagedSpringDataSupport.setHasSpringData(false);
		Object content = PagedSpringDataSupport.getInstance().getRowCountFromPage(this.notPage);
		assertThat(content, is(nullValue()));
	}
}
