/*
 * Copyright 2010-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.junit.After;
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
import org.springframework.springfaces.FacesContextSetter;
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

	private UIComponent parent = new UIPanel();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		SpringFacesMocks.setupSpringFacesIntegration(this.facesContext, this.applicationContext);
		given(this.facesContext.getViewRoot()).willReturn(this.viewRoot);
		given(this.facesContext.getExternalContext().getRequestMap()).willReturn(this.requestMap);
		this.uiMessageSource = new UIMessageSource();
		this.uiMessageSource.setVar("msg");
		this.uiMessageSource.setReturnStringsWhenPossible(false);
		FacesContextSetter.setCurrentInstance(this.facesContext);
	}

	@After
	public void cleanup() {
		FacesContextSetter.setCurrentInstance(null);
	}

	@Test
	public void shouldGetComponentFamily() throws Exception {
		assertThat(this.uiMessageSource.getFamily(), is(equalTo(UIMessageSource.COMPONENT_FAMILY)));
	}

	@Test
	public void shouldAddMessageMap() throws Exception {
		given(this.viewRoot.getViewId()).willReturn("/WEB-INF/pages/example/page.xhtml");
		MessageSourceMap messageSourceMap = callSetParent();
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
		given(this.viewRoot.getViewId()).willReturn("/WEB-INF/pages/example/page.xhtml");
		Object previous = new Object();
		this.requestMap.put("msg", previous);
		MessageSourceMap messageSourceMap = callSetParent();
		assertThat(messageSourceMap, is(not(previous)));
	}

	@Test
	public void shouldUseApplicationContextAsSource() throws Exception {
		given(this.viewRoot.getViewId()).willReturn("/WEB-INF/pages/example/page.xhtml");
		MessageSourceMap messageSourceMap = callSetParent();
		messageSourceMap.get("test").toString();
		verify(this.applicationContext).getMessage((MessageSourceResolvable) any(), (Locale) any());
	}

	@Test
	public void shouldUseDefinedSource() throws Exception {
		given(this.viewRoot.getViewId()).willReturn("/WEB-INF/pages/example/page.xhtml");
		MessageSource source = mock(MessageSource.class);
		this.uiMessageSource.setSource(source);
		MessageSourceMap messageSourceMap = callSetParent();
		messageSourceMap.get("test").toString();
		verify(source).getMessage((MessageSourceResolvable) any(), (Locale) any());
		verify(this.applicationContext, never()).getMessage((MessageSourceResolvable) any(), (Locale) any());
	}

	@Test
	public void shouldNeedSpringIntegration() throws Exception {
		SpringFacesMocks.removeSpringFacesIntegration(this.facesContext);
		given(this.viewRoot.getViewId()).willReturn("/WEB-INF/pages/example/page.xhtml");
		this.thrown.expect(IllegalStateException.class);
		this.thrown
				.expectMessage("Unable to find MessageSource, ensure that SpringFaces intergation is enabled or set the 'source' attribute");
		callSetParent();
	}

	@Test
	public void shouldSupportDefinedPrefixes() throws Exception {
		this.uiMessageSource.setPrefix("a.b,a.b.c , a.b.c.d");
		assertCodes("z", new String[] { "a.b.z", "a.b.c.z", "a.b.c.d.z" });
	}

	@Test
	public void shouldUseLocaleFromViewRoot() throws Exception {
		given(this.viewRoot.getViewId()).willReturn("/WEB-INF/pages/example/page.xhtml");
		Locale locale = Locale.CANADA_FRENCH;
		given(this.viewRoot.getLocale()).willReturn(locale);
		MessageSourceMap messageSourceMap = callSetParent();
		messageSourceMap.get("test").toString();
		verify(this.applicationContext).getMessage((MessageSourceResolvable) any(), eq(locale));
	}

	@Test
	public void shouldThrowOnMissingMessageWhenInProduction() throws Exception {
		given(this.viewRoot.getViewId()).willReturn("/WEB-INF/pages/example/page.xhtml");
		given(this.facesContext.isProjectStage(ProjectStage.Production)).willReturn(true);
		MessageSourceMap messageSourceMap = callSetParent();
		given(this.applicationContext.getMessage((MessageSourceResolvable) any(), (Locale) any())).willThrow(
				new NoSuchMessageException("test"));
		this.thrown.expect(NoSuchMessageException.class);
		messageSourceMap.get("test").toString();
	}

	@Test
	public void shouldAddFacesMessageOnMissingMessageWhenNotInProduction() throws Exception {
		given(this.viewRoot.getViewId()).willReturn("/WEB-INF/pages/example/page.xhtml");
		given(this.facesContext.isProjectStage(ProjectStage.Production)).willReturn(false);
		MessageSourceMap messageSourceMap = callSetParent();
		given(this.applicationContext.getMessage((MessageSourceResolvable) any(), (Locale) any())).willThrow(
				new NoSuchMessageException("test"));
		messageSourceMap.get("test").toString();
		verify(this.facesContext).addMessage(anyString(), this.messageCaptor.capture());
		assertThat(this.messageCaptor.getValue().getDetail(), is("No message found under code 'test' for locale '"
				+ Locale.getDefault().toString() + "'."));
	}

	@Test
	public void shouldWrapWithDefaultObjectMessageSource() throws Exception {
		given(this.viewRoot.getViewId()).willReturn("/WEB-INF/pages/example/page.xhtml");
		MessageSourceMap messageSourceMap = callSetParent();
		Convertable convertable = new Convertable();
		given(
				this.applicationContext.getMessage("org.springframework.springfaces.message.ui."
						+ "UIMessageSourceTest$Convertable", new Object[] {}, null)).willReturn("test");
		String actual = messageSourceMap.get(convertable).toString();
		assertThat(actual, is("test"));
	}

	@Test
	public void shouldDefaultToReturnStringsWhenPossible() throws Exception {
		this.uiMessageSource = new UIMessageSource();
		this.uiMessageSource.setVar("msg");
		given(this.viewRoot.getViewId()).willReturn("/WEB-INF/pages/example/page.xhtml");
		MessageSourceMap messageSourceMap = callSetParent();
		assertThat(this.uiMessageSource.isReturnStringsWhenPossible(), is(true));
		assertThat(messageSourceMap.returnStringsWhenPossible(), is(true));
	}

	private MessageSourceMap callSetParent() throws IOException {
		this.uiMessageSource.setParent(this.parent);
		MessageSourceMap msg = (MessageSourceMap) this.requestMap.get("msg");
		return msg;
	}

	private void assertBuildCode(String viewId, String expectedPrefix) throws IOException {
		given(this.viewRoot.getViewId()).willReturn(viewId);
		assertCodes("test", new String[] { expectedPrefix + "test" });
	}

	private void assertCodes(String key, String[] expectedCodes) throws IOException {
		MessageSourceMap messageSourceMap = callSetParent();
		MessageSourceResolvable resolvable = (MessageSourceResolvable) messageSourceMap.get(key);
		assertThat(resolvable.getCodes(), is(equalTo(expectedCodes)));
	}

	private static class Convertable {
	}
}
