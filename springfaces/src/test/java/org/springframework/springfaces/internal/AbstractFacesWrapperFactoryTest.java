package org.springframework.springfaces.internal;

import static org.mockito.BDDMockito.given;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.springfaces.FacesContextSetter;
import org.springframework.springfaces.FacesWrapperFactory;
import org.springframework.springfaces.SpringFacesMocks;
import org.springframework.web.context.WebApplicationContext;

/**
 * Abstract base class of tests that use {@link FacesWrapperFactory}.
 * 
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractFacesWrapperFactoryTest {

	@Mock
	private FacesContext facesContext;

	@Mock
	private WebApplicationContext applicationContext;

	@SuppressWarnings("rawtypes")
	private Map<String, FacesWrapperFactory> facesWrapperFactoryBeans = new LinkedHashMap<String, FacesWrapperFactory>();

	@Before
	public void setup() {
		SpringFacesMocks.setupSpringFacesIntegration(this.facesContext, this.applicationContext);
		FacesContextSetter.setCurrentInstance(this.facesContext);
		given(this.applicationContext.getBeansOfType(FacesWrapperFactory.class)).willReturn(
				this.facesWrapperFactoryBeans);
	}

	@After
	public void cleanupFacesContext() {
		FacesContextSetter.setCurrentInstance(null);
	}

	protected void addFactoryWrapper(String beanName, FacesWrapperFactory<?> wrapperFactory) {
		this.facesWrapperFactoryBeans.put(beanName, wrapperFactory);
	}
}
