package org.springframework.springfaces.internal;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.springfaces.FacesContextSetter;
import org.springframework.springfaces.FacesWrapperFactory;
import org.springframework.springfaces.SpringFacesIntegration;
import org.springframework.web.context.WebApplicationContext;

/**
 * Abstract base class of tests that use {@link FacesWrapperFactory}.
 * 
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractFacesWrapperFactoryTest {

	private static final String SPRING_FACES_INTEGRATION_ATTRIBUTE = SpringFacesIntegration.class.getName();
	private static final String LAST_REFRESHED_DATE_ATTRIBUTE = SpringFacesIntegration.class.getName() + ".DATE";

	@Mock
	private FacesContext facesContext;

	@Mock
	private ExternalContext externalContext;

	@Mock
	private WebApplicationContext applicationContext;

	private Map<String, Object> applicationMap = new HashMap<String, Object>();

	@SuppressWarnings("rawtypes")
	private Map<String, FacesWrapperFactory> facesWrapperFactoryBeans = new LinkedHashMap<String, FacesWrapperFactory>();

	@Before
	public void setup() {
		given(facesContext.getExternalContext()).willReturn(externalContext);
		given(externalContext.getApplicationMap()).willReturn(applicationMap);
		given(applicationContext.getBeansOfType(FacesWrapperFactory.class)).willReturn(facesWrapperFactoryBeans);
		SpringFacesIntegration integration = new SpringFacesIntegration();
		integration.setServletContext(mock(ServletContext.class));
		integration.setApplicationContext(applicationContext);
		applicationMap.put(SPRING_FACES_INTEGRATION_ATTRIBUTE, integration);
		applicationMap.put(LAST_REFRESHED_DATE_ATTRIBUTE, new Date());
		FacesContextSetter.setCurrentInstance(facesContext);
	}

	@After
	public void cleanupFacesContext() {
		FacesContextSetter.setCurrentInstance(null);
	}

	protected void addFactoryWrapper(String beanName, FacesWrapperFactory<?> wrapperFactory) {
		facesWrapperFactoryBeans.put(beanName, wrapperFactory);
	}
}
