package org.springframework.springfaces.validator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.springfaces.SpringFacesMocks;
import org.springframework.web.context.WebApplicationContext;

/**
 * Tests for {@link SpringBeanFacesValidator}.
 * 
 * @author Phillip Webb
 */
public class SpringBeanFacesValidatorTest {

	@Mock
	private FacesContext facesContext;

	@Mock
	private WebApplicationContext applicationContext;

	private String beanName = "bean";

	@Mock
	private javax.faces.validator.Validator bean;

	@Mock
	private UIComponent component;

	private SpringBeanFacesValidator validator;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		SpringFacesMocks.setupSpringFacesIntegration(this.facesContext, this.applicationContext);
		given(this.applicationContext.getBean(this.beanName)).willReturn(this.bean);
		this.validator = new SpringBeanFacesValidator(this.facesContext, this.beanName);
	}

	@Test
	public void shouldDelegateValidate() throws Exception {
		Object value = new Object();
		this.validator.validate(this.facesContext, this.component, value);
		verify(this.bean).validate(this.facesContext, this.component, value);
	}
}
