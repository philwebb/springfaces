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
import org.springframework.springfaces.SpringFacesIntegration;

/**
 * Tests utilities that can be used to setup mocks.
 * 
 * @author Phillip Webb
 */
public class SpringFacesMocks {

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
		applicationMap.put(SpringFacesIntegration.class.getName(), springFacesIntegration);
		applicationMap.put(SpringFacesIntegration.class.getName() + ".DATE", new Date());
		given(facesContext.getExternalContext()).willReturn(externalContext);
		given(externalContext.getApplicationMap()).willReturn(applicationMap);
	}
}
