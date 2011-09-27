package org.springframework.springfaces.convert;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockServletContext;
import org.springframework.springfaces.SpringFacesIntegration;
import org.springframework.web.context.WebApplicationContext;

/**
 * Tests for {@link SpringBeanConverter}.
 * 
 * @author Phillip Webb
 */
public class SpringBeanConverterTest {

	@Mock
	private FacesContext context;

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
		ExternalContext externalContext = mock(ExternalContext.class);
		SpringFacesIntegration springFacesIntegration = new SpringFacesIntegration();
		springFacesIntegration.setServletContext(new MockServletContext());
		springFacesIntegration.setApplicationContext(applicationContext);
		Map<String, Object> applicationMap = Collections.<String, Object> singletonMap(
				SpringFacesIntegration.class.getName(), springFacesIntegration);
		given(context.getExternalContext()).willReturn(externalContext);
		given(externalContext.getApplicationMap()).willReturn(applicationMap);
		given(applicationContext.getBean(beanName)).willReturn(bean);
		converter = new SpringBeanConverter<Object>(context, beanName);
	}

	@Test
	public void shouldDelegateGetAsString() throws Exception {
		String value = "value";
		Object object = new Object();
		given(bean.getAsObject(context, component, value)).willReturn(object);
		Object actual = converter.getAsObject(context, component, value);
		assertThat(actual, is(object));
	}

	@Test
	public void shouldDelegateGetAsObject() throws Exception {
		Object value = new Object();
		String string = "string";
		given(bean.getAsString(context, component, value)).willReturn(string);
		String actual = converter.getAsString(context, component, value);
		assertThat(actual, is(string));
	}
}
