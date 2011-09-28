package org.springframework.springfaces.convert;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.springfaces.message.ObjectMessageSource;

/**
 * Tests for {@link ObjectMessageConverter}.
 * 
 * @author Phillip Webb
 */
public class ObjectMessageConverterTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ObjectMessageSource messageSource;

	@Mock
	private FacesContext context;

	@Mock
	private UIComponent component;

	private Locale locale = Locale.getDefault();

	private ObjectMessageConverter converter;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.converter = new ObjectMessageConverter(messageSource);
		UIViewRoot viewRoot = mock(UIViewRoot.class);
		given(context.getViewRoot()).willReturn(viewRoot);
		given(viewRoot.getLocale()).willReturn(locale);
	}

	@Test
	public void shouldRequireMessageSource() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("MessageSource must not be null");
		new ObjectMessageConverter(null);
	}

	@Test
	public void shouldDelegateIsForClassToMessageSource() throws Exception {
		given(messageSource.containsMessage(Mapped.class)).willReturn(true);
		given(messageSource.containsMessage(NotMapped.class)).willReturn(false);
		assertThat(converter.isForClass(Mapped.class), is(true));
		assertThat(converter.isForClass(NotMapped.class), is(false));
		verify(messageSource).containsMessage(Mapped.class);
		verify(messageSource).containsMessage(NotMapped.class);
	}

	@Test
	public void shouldDelegateGetAsStringToMessageSource() throws Exception {
		Mapped mapped = new Mapped();
		NotMapped notMapped = new NotMapped();
		given(messageSource.getMessage(mapped, locale)).willReturn("mapped");
		given(messageSource.getMessage(notMapped, locale)).willReturn("not mapped");
		assertThat(converter.getAsString(context, component, mapped), is("mapped"));
		assertThat(converter.getAsString(context, component, notMapped), is("not mapped"));
		verify(messageSource).getMessage(mapped, locale);
		verify(messageSource).getMessage(notMapped, locale);
	}

	@Test
	public void shouldNotSupportGetAsObject() throws Exception {
		thrown.expect(UnsupportedOperationException.class);
		converter.getAsObject(context, component, "");
	}

	private static class Mapped {
	}

	private static class NotMapped {
	}
}
