package org.springframework.springfaces.convert;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.springfaces.SpringFacesMocks;
import org.springframework.web.context.WebApplicationContext;

/**
 * Tests for {@link SpringBeanConverter}.
 * 
 * @author Phillip Webb
 */
public class SpringBeanConverterTest {

	@Mock
	private FacesContext facesContext;

	@Mock
	private WebApplicationContext applicationContext;

	private String beanName = "bean";

	@Mock
	private Converter<Object> bean;

	@Mock
	private UIComponent component;

	private SpringBeanConverter<Object> converter;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		SpringFacesMocks.setupSpringFacesIntegration(facesContext, applicationContext);
		given(applicationContext.getBean(beanName)).willReturn(bean);
		converter = new SpringBeanConverter<Object>(facesContext, beanName);
	}

	@Test
	public void shouldDelegateGetAsString() throws Exception {
		String value = "value";
		Object object = new Object();
		given(bean.getAsObject(facesContext, component, value)).willReturn(object);
		Object actual = converter.getAsObject(facesContext, component, value);
		assertThat(actual, is(object));
	}

	@Test
	public void shouldDelegateGetAsObject() throws Exception {
		Object value = new Object();
		String string = "string";
		given(bean.getAsString(facesContext, component, value)).willReturn(string);
		String actual = converter.getAsString(facesContext, component, value);
		assertThat(actual, is(string));
	}
}
