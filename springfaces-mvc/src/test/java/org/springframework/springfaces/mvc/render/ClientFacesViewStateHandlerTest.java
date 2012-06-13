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
package org.springframework.springfaces.mvc.render;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Matchers.anyString;

import java.io.IOException;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.mock.web.MockHttpSession;

/**
 * Tests for {@link ClientFacesViewStateHandler}.
 * 
 * @author Phillip Webb
 */
public class ClientFacesViewStateHandlerTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private ClientFacesViewStateHandler handler = new ClientFacesViewStateHandler();

	@Mock
	private FacesContext facesContext;

	@Mock
	private ExternalContext externalContext;

	@Mock
	private HttpServletRequest request;

	@Mock
	private ResponseWriter responseWriter;

	private MockHttpSession session = new MockHttpSession();

	private ViewArtifact viewState = new ViewArtifact("/WEB-INF/pages/test.xhtml");

	private StringBuilder output = new StringBuilder();

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		given(this.facesContext.getExternalContext()).willReturn(this.externalContext);
		given(this.externalContext.getRequest()).willReturn(this.request);
		given(this.request.getSession()).willReturn(this.session);
		given(this.facesContext.getResponseWriter()).willReturn(this.responseWriter);
		willAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				ClientFacesViewStateHandlerTest.this.output.append(invocation.getArguments()[0]);
				return null;
			}
		}).given(this.responseWriter).write(anyString());
	}

	@Test
	public void shouldWrite() throws Exception {
		this.handler.write(this.facesContext, this.viewState);
		assertThat(this.output.toString(), startsWith("<input type=\"hidden\" "
				+ "name=\"org.springframework.springfaces.id\" " + "id=\"org.springframework.springfaces.id\" "
				+ "value=\""));
		assertThat(this.output.toString(), endsWith("\"\\>"));
	}

	@Test
	public void shouldRead() throws Exception {
		String value = writeAndGetValue(this.viewState);
		given(this.request.getParameter("org.springframework.springfaces.id")).willReturn(value);
		ViewArtifact read = this.handler.read(this.request);
		assertThat(read, is(equalTo(this.viewState)));
	}

	@Test
	public void shouldVerifyMac() throws Exception {
		String value = writeAndGetValue(this.viewState);
		value = new StringBuilder(value).reverse().toString();
		given(this.request.getParameter("org.springframework.springfaces.id")).willReturn(value);
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("Unable to decrypt input value");
		this.handler.read(this.request);
	}

	@Test
	public void shouldEncryptPerSessionKey() throws Exception {
		String value = writeAndGetValue(this.viewState);
		session = new MockHttpSession();
		value = new StringBuilder(value).reverse().toString();
		given(this.request.getParameter("org.springframework.springfaces.id")).willReturn(value);
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("Unable to decrypt input value");
		this.handler.read(this.request);
	}

	private String writeAndGetValue(ViewArtifact viewState) throws IOException {
		this.output.setLength(0);
		this.handler.write(this.facesContext, viewState);
		String value = this.output.toString();
		value = value.substring(value.indexOf("value"));
		value = value.substring(value.indexOf("\"") + 1);
		value = value.substring(0, value.lastIndexOf("\""));
		return value;
	}

}
