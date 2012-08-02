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
package org.springframework.springfaces.mvc.config;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter;

/**
 * Tests for {@link ResourcesBeanDefinitionParser}.
 * 
 * @author Phillip Webb
 */
public class ResourcesBeanDefinitionParserTest {

	@Test
	public void shouldNotRegisterHandlerAdapterIfAlreadyPresent() throws Exception {
		StaticWebApplicationContext applicationContext = SpringFacesMvcNamespaceHandlerTest
				.loadApplicationContext("<faces:resources/>");
		assertThat(applicationContext.getBean(HttpRequestHandlerAdapter.class.getName()),
				is(HttpRequestHandlerAdapter.class));
	}

	@Test
	public void shouldRegisterHandlerAdapterIfNotAlreadyPresent() throws Exception {
		// Sanity check to ensure the MVC context has the adapter
		StaticWebApplicationContext applicationContext = SpringFacesMvcNamespaceHandlerTest
				.loadMvcApplicationContext("");
		assertThat(applicationContext.getBean(HttpRequestHandlerAdapter.class.getName()),
				is(HttpRequestHandlerAdapter.class));

		// Actual test
		applicationContext = SpringFacesMvcNamespaceHandlerTest.loadMvcApplicationContext("<faces:resources/>");
		assertThat(applicationContext.getBeansOfType(HttpRequestHandlerAdapter.class).size(), is(1));
	}

	@Test
	public void shouldRegisterWithSpecificOrder() throws Exception {
		StaticWebApplicationContext applicationContext = SpringFacesMvcNamespaceHandlerTest
				.loadApplicationContext("<faces:resources order=\"5\"/>");
		assertThat(applicationContext.getBean(SimpleUrlHandlerMapping.class).getOrder(), is(5));
	}
}
