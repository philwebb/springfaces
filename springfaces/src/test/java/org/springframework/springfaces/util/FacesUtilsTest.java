package org.springframework.springfaces.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.Locale;

import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests for {@link FacesUtils}.
 * 
 * @author Phillip Webb
 */
public class FacesUtilsTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void shouldNeedFacesContextForFindLocale() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("FacesContext must not be null");
		FacesUtils.getLocale(null);
	}

	@Test
	public void shouldFindLocaleFromViewRoot() throws Exception {
		FacesContext facesContext = mock(FacesContext.class);
		UIViewRoot viewRoot = mock(UIViewRoot.class);
		ExternalContext externalContext = mock(ExternalContext.class);
		given(facesContext.getViewRoot()).willReturn(viewRoot);
		given(facesContext.getExternalContext()).willReturn(externalContext);
		given(viewRoot.getLocale()).willReturn(Locale.CANADA);
		given(externalContext.getRequestLocale()).willReturn(Locale.GERMAN);
		assertEquals(Locale.CANADA, FacesUtils.getLocale(facesContext));
	}

	@Test
	public void shouldFindLocaleFromRequestWhenNoViewRoot() throws Exception {
		FacesContext facesContext = mock(FacesContext.class);
		ExternalContext externalContext = mock(ExternalContext.class);
		given(facesContext.getExternalContext()).willReturn(externalContext);
		given(externalContext.getRequestLocale()).willReturn(Locale.GERMAN);
		assertEquals(Locale.GERMAN, FacesUtils.getLocale(facesContext));
	}
}
