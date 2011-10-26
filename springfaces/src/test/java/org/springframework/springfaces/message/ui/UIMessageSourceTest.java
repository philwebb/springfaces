package org.springframework.springfaces.message.ui;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UIMessageSourceTest {

	private UIMessageSource uiMessageSource;

	@Mock
	private FacesContext context;

	@Mock
	private ExternalContext externalContext;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		given(context.getExternalContext()).willReturn(externalContext);
		uiMessageSource = new UIMessageSource();
	}

	@Test
	public void shouldGetComponentFamily() throws Exception {
		assertThat(uiMessageSource.getFamily(), is(equalTo(UIMessageSource.COMPONENT_FAMILY)));
	}

	@Test
	@Ignore
	public void shouldAddMessageMap() throws Exception {
		uiMessageSource.encodeEnd(context);
	}
}
