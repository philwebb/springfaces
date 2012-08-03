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
import org.springframework.core.io.ClassPathResource;
import org.springframework.springfaces.SpringFacesIntegration;
import org.springframework.springfaces.convert.SpringFacesConverterSupport;
import org.springframework.springfaces.exceptionhandler.ObjectMessageExceptionHandler;
import org.springframework.springfaces.exceptionhandler.SpringFacesExceptionHandlerSupport;
import org.springframework.springfaces.expression.el.FacesStandardEvaluationContextPostProcessor;
import org.springframework.springfaces.validator.SpringFacesValidatorSupport;
import org.springframework.web.context.support.StaticWebApplicationContext;

/**
 * Tests for {@link SpringFacesMvcNamespaceHandler}.
 * 
 * @author Phillip Webb
 */
public class SpringFacesMvcNamespaceHandlerTest extends AbstractNamespaceTest {

	@Test
	public void shouldSetupIntegration() throws Exception {
		StaticWebApplicationContext applicationContext = loadApplicationContext(new ClassPathResource(
				"testSpringFacesMvcNamespace.xml", getClass()));
		assertThat(applicationContext.getBeanFactory().getBeanDefinitionCount(), is(30));
		assertHasBean(applicationContext, SpringFacesIntegration.class);
		assertHasBean(applicationContext, SpringFacesValidatorSupport.class);
		assertHasBean(applicationContext, SpringFacesConverterSupport.class);
		assertHasBean(applicationContext, SpringFacesExceptionHandlerSupport.class);
		assertHasBean(applicationContext, ObjectMessageExceptionHandler.class);
		assertHasBean(applicationContext, FacesStandardEvaluationContextPostProcessor.class);
	}
}
