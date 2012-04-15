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
package org.springframework.springfaces.mvc.expression.el;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import javax.el.ELContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.springfaces.mvc.MockELContext;
import org.springframework.springfaces.mvc.SpringFacesContextSetter;
import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.render.ModelAndViewArtifact;
import org.springframework.springfaces.mvc.render.ViewArtifact;
import org.springframework.web.context.WebApplicationContext;

/**
 * Tests for {@link SpringFacesBeanELResolver}.
 * 
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class SpringFacesBeanELResolverTest {

	@Mock
	private SpringFacesContext springFacesContext;

	private SpringFacesBeanELResolver resolver = new SpringFacesBeanELResolver();

	private ELContext elContext = new MockELContext();

	@Test
	public void shouldHaveEmptyBeanFactoryWhenActive() throws Exception {
		BeanFactory factory = this.resolver.getBeanFactory(this.elContext);
		assertEquals(0, ((ListableBeanFactory) factory).getBeanDefinitionCount());
	}

	@Test
	public void shouldGetBeanFactoryFromSpringFacesContext() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(this.springFacesContext);
		try {
			ViewArtifact viewArtifact = new ViewArtifact("artifact");
			ModelAndViewArtifact rendering = new ModelAndViewArtifact(viewArtifact, null);
			given(this.springFacesContext.getRendering()).willReturn(rendering);
			WebApplicationContext applicationContext = mock(WebApplicationContext.class);
			given(this.springFacesContext.getWebApplicationContext()).willReturn(applicationContext);
			assertSame(applicationContext, this.resolver.getBeanFactory(this.elContext));
		} finally {
			SpringFacesContextSetter.setCurrentInstance(null);
		}
	}
}
