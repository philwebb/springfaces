package org.springframework.springfaces.convert;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.support.StaticMessageSource;

public class ObjectMessageConverterTest {

	private static final String INNER = "org.springframework.springfaces.convert.ObjectMessageConverterTest$";

	private StaticMessageSource messageSource;

	private ObjectMessageConverter<Object> converter = new ObjectMessageConverter<Object>();

	@Mock
	private FacesContext context;

	@Mock
	private UIComponent component;

	@Before
	public void setup() {
		this.messageSource = new StaticMessageSource();
		setupMessage(INNER + "Mapped", "mapped");
		setupMessage(INNER + "MappedArguments", "a {name} b");
		converter.setMessageSource(messageSource);
		setupMocks();
	}

	private void setupMocks() {
		MockitoAnnotations.initMocks(this);
		UIViewRoot viewRoot = mock(UIViewRoot.class);
		given(context.getViewRoot()).willReturn(viewRoot);
		given(viewRoot.getLocale()).willReturn(Locale.getDefault());
	}

	private void setupMessage(String code, String msg) {
		messageSource.addMessage(code, Locale.getDefault(), msg);
	}

	@Test
	public void shouldBeForMappedClass() throws Exception {
		assertThat(converter.isForClass(Mapped.class), is(true));
		assertThat(converter.isForClass(NotMapped.class), is(false));
	}

	@Test
	public void shouldBeForMappedClassWhenUsingDefaults() throws Exception {
		messageSource.setUseCodeAsDefaultMessage(true);
		assertThat(converter.isForClass(Mapped.class), is(true));
		assertThat(converter.isForClass(NotMapped.class), is(false));
	}

	@Test
	public void shouldGetMappedAsString() throws Exception {
		String actual = converter.getAsString(context, component, new Mapped());
		assertThat(actual, is("mapped"));
	}

	@Test
	public void shouldConvertNullToNull() throws Exception {
		String actual = converter.getAsString(context, component, null);
		assertThat(actual, is(nullValue()));
	}

	@Test
	@Ignore
	public void shouldGetWithExpandedArguments() throws Exception {
		// FIXME
		String actual = converter.getAsString(context, component, new MappedArguments("Phil"));
		assertThat(actual, is("a Phil b"));
	}

	static class NotMapped {
	}

	static class Mapped {
	}

	static class MappedArguments {
		private String name;

		public MappedArguments(String name) {
		}

		public String getName() {
			return name;
		}
	}

}
