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
package org.springframework.springfaces;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockServletContext;

/**
 * Tests utilities that can be used to setup mocks.
 * 
 * @author Phillip Webb
 */
public class SpringFacesMocks {

	private static final String SPRING_FACES_ATTR = SpringFacesIntegration.class.getName();
	private static final String SPRING_FACES_DATE_ATTR = SpringFacesIntegration.class.getName() + ".DATE";

	/**
	 * Update the mock FacesContext to include support for {@link SpringFacesIntegration}.
	 * 
	 * @param facesContext the faces context to update (should not have an external context mocked)
	 * @param applicationContext the spring application context
	 */
	public static void setupSpringFacesIntegration(FacesContext facesContext, ApplicationContext applicationContext) {
		ExternalContext externalContext = mock(ExternalContext.class);
		SpringFacesIntegration springFacesIntegration = new SpringFacesIntegration();
		springFacesIntegration.setServletContext(new MockServletContext());
		springFacesIntegration.setApplicationContext(applicationContext);
		Map<String, Object> applicationMap = new HashMap<String, Object>();
		applicationMap.put(SPRING_FACES_ATTR, springFacesIntegration);
		applicationMap.put(SPRING_FACES_DATE_ATTR, new Date());
		given(facesContext.getExternalContext()).willReturn(externalContext);
		given(externalContext.getApplicationMap()).willReturn(applicationMap);
	}

	/**
	 * Remove previously {@link #setupSpringFacesIntegration(FacesContext, ApplicationContext) setup} support for
	 * {@link SpringFacesIntegration}.
	 * @param facesContext the faces context previously setup
	 */
	public static void removeSpringFacesIntegration(FacesContext facesContext) {
		facesContext.getExternalContext().getApplicationMap().remove(SPRING_FACES_ATTR);
	}
}
