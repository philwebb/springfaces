package org.springframework.springfaces.message.ui;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.application.ProjectStage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.springfaces.SpringFacesMocks;
import org.springframework.web.context.WebApplicationContext;

/**
 * Tests for {@link UIMessageSource}.
 * 
 * @author Phillip Webb
 */
public class UIMessageSourceTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private UIMessageSource uiMessageSource;

	@Mock
	private FacesContext facesContext;

	@Mock
	private WebApplicationContext applicationContext;

	@Mock
	private UIViewRoot viewRoot;

	private Map<String, Object> requestMap = new HashMap<String, Object>();

	@Captor
	private ArgumentCaptor<FacesMessage> messageCaptor;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		SpringFacesMocks.setupSpringFacesIntegration(facesContext, applicationContext);
		given(facesContext.getViewRoot()).willReturn(viewRoot);
		given(facesContext.getExternalContext().getRequestMap()).willReturn(requestMap);
		uiMessageSource = new UIMessageSource();
		uiMessageSource.setVar("msg");
	}

	@Test
	public void shouldGetComponentFamily() throws Exception {
		assertThat(uiMessageSource.getFamily(), is(equalTo(UIMessageSource.COMPONENT_FAMILY)));
	}

	@Test
	public void shouldAddMessageMap() throws Exception {
		given(viewRoot.getViewId()).willReturn("/WEB-INF/pages/example/page.xhtml");
		MessageSourceMap messageSourceMap = callEncodeEnd();
		assertThat(messageSourceMap, is(notNullValue()));
	}

	@Test
	public void shouldStripLeadingSlashFromBuiltCode() throws Exception {
		assertBuildCode("/page.xhtml", "page.");
	}

	@Test
	public void shouldStripWebInfFromBuiltCode() throws Exception {
		assertBuildCode("/WEB-INF/page.xhtml", "page.");
	}

	@Test
	public void shouldStripExtensionFromBuiltCode() throws Exception {
		assertBuildCode("/page.a.xhtml", "page.a.");
	}

	@Test
	public void shouldSupportNotExtensionForBuiltCode() throws Exception {
		assertBuildCode("/page", "page.");
	}

	@Test
	public void shouldLowerCaseBuiltCode() throws Exception {
		assertBuildCode("/WEB-INF/PAGES/a/PAth/page.xhtml", "pages.a.path.page.");
	}

	@Test
	public void shouldReplacePreviousVar() throws Exception {
		given(viewRoot.getViewId()).willReturn("/WEB-INF/pages/example/page.xhtml");
		Object previous = new Object();
		requestMap.put("msg", previous);
		MessageSourceMap messageSourceMap = callEncodeEnd();
		assertThat(messageSourceMap, is(not(previous)));
	}

	@Test
	public void shouldUseApplicationContextAsSource() throws Exception {
		given(viewRoot.getViewId()).willReturn("/WEB-INF/pages/example/page.xhtml");
		MessageSourceMap messageSourceMap = callEncodeEnd();
		messageSourceMap.get("test").toString();
		verify(applicationContext).getMessage((MessageSourceResolvable) any(), (Locale) any());
	}

	@Test
	public void shouldUseDefinedSource() throws Exception {
		given(viewRoot.getViewId()).willReturn("/WEB-INF/pages/example/page.xhtml");
		MessageSource source = mock(MessageSource.class);
		uiMessageSource.setSource(source);
		MessageSourceMap messageSourceMap = callEncodeEnd();
		messageSourceMap.get("test").toString();
		verify(source).getMessage((MessageSourceResolvable) any(), (Locale) any());
		verify(applicationContext, never()).getMessage((MessageSourceResolvable) any(), (Locale) any());
	}

	@Test
	public void shouldNeedSpringIntegration() throws Exception {
		SpringFacesMocks.removeSpringFacesIntegration(facesContext);
		given(viewRoot.getViewId()).willReturn("/WEB-INF/pages/example/page.xhtml");
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Unable to find MessageSource, ensure that SpringFaces intergation is enabled or set the 'source' attribute");
		callEncodeEnd();
	}

	@Test
	public void shouldSupportDefinedPrefixes() throws Exception {
		uiMessageSource.setPrefix("a.b,a.b.c , a.b.c.d");
		assertCodes("z", new String[] { "a.b.z", "a.b.c.z", "a.b.c.d.z" });
	}

	@Test
	public void shouldUseLocaleFromViewRoot() throws Exception {
		given(viewRoot.getViewId()).willReturn("/WEB-INF/pages/example/page.xhtml");
		Locale locale = Locale.CANADA_FRENCH;
		given(viewRoot.getLocale()).willReturn(locale);
		MessageSourceMap messageSourceMap = callEncodeEnd();
		messageSourceMap.get("test").toString();
		verify(applicationContext).getMessage((MessageSourceResolvable) any(), eq(locale));
	}

	@Test
	public void shouldThrowOnMissingMessageWhenInProduction() throws Exception {
		given(viewRoot.getViewId()).willReturn("/WEB-INF/pages/example/page.xhtml");
		given(facesContext.isProjectStage(ProjectStage.Production)).willReturn(true);
		MessageSourceMap messageSourceMap = callEncodeEnd();
		given(applicationContext.getMessage((MessageSourceResolvable) any(), (Locale) any())).willThrow(
				new NoSuchMessageException("test"));
		thrown.expect(NoSuchMessageException.class);
		messageSourceMap.get("test").toString();
	}

	@Test
	public void shouldAddFacesMessageOnMissingMessageWhenNotInProduction() throws Exception {
		given(viewRoot.getViewId()).willReturn("/WEB-INF/pages/example/page.xhtml");
		given(facesContext.isProjectStage(ProjectStage.Production)).willReturn(false);
		MessageSourceMap messageSourceMap = callEncodeEnd();
		given(applicationContext.getMessage((MessageSourceResolvable) any(), (Locale) any())).willThrow(
				new NoSuchMessageException("test"));
		messageSourceMap.get("test").toString();
		verify(facesContext).addMessage(anyString(), messageCaptor.capture());
		assertThat(messageCaptor.getValue().getDetail(), is("No message found under code 'test' for locale '"
				+ Locale.getDefault().toString() + "'."));
	}

	@Test
	public void shouldWrapWithDefaultObjectMessageSource() throws Exception {
		given(viewRoot.getViewId()).willReturn("/WEB-INF/pages/example/page.xhtml");
		MessageSourceMap messageSourceMap = callEncodeEnd();
		Convertable convertable = new Convertable();
		given(
				applicationContext.getMessage("org.springframework.springfaces.message.ui."
						+ "UIMessageSourceTest$Convertable", new Object[] {}, null)).willReturn("test");
		String actual = messageSourceMap.get(convertable).toString();
		assertThat(actual, is("test"));
	}

	private MessageSourceMap callEncodeEnd() throws IOException {
		uiMessageSource.encodeEnd(facesContext);
		MessageSourceMap msg = (MessageSourceMap) requestMap.get("msg");
		return msg;
	}

	private void assertBuildCode(String viewId, String expectedPrefix) throws IOException {
		given(viewRoot.getViewId()).willReturn(viewId);
		assertCodes("test", new String[] { expectedPrefix + "test" });
	}

	private void assertCodes(String key, String[] expectedCodes) throws IOException {
		MessageSourceMap messageSourceMap = callEncodeEnd();
		MessageSourceResolvable resolvable = (MessageSourceResolvable) messageSourceMap.get(key);
		assertThat(resolvable.getCodes(), is(equalTo(expectedCodes)));
	}

	private static class Convertable {
	}
}
