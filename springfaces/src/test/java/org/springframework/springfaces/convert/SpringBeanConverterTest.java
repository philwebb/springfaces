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
		SpringFacesMocks.setupSpringFacesIntegration(this.facesContext, this.applicationContext);
		given(this.applicationContext.getBean(this.beanName)).willReturn(this.bean);
		this.converter = new SpringBeanConverter<Object>(this.facesContext, this.beanName);
	}

	@Test
	public void shouldDelegateGetAsString() throws Exception {
		String value = "value";
		Object object = new Object();
		given(this.bean.getAsObject(this.facesContext, this.component, value)).willReturn(object);
		Object actual = this.converter.getAsObject(this.facesContext, this.component, value);
		assertThat(actual, is(object));
	}

	@Test
	public void shouldDelegateGetAsObject() throws Exception {
		Object value = new Object();
		String string = "string";
		given(this.bean.getAsString(this.facesContext, this.component, value)).willReturn(string);
		String actual = this.converter.getAsString(this.facesContext, this.component, value);
		assertThat(actual, is(string));
	}
}
