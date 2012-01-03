package org.springframework.springfaces.message;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Locale;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * Tests for {@link ObjectMessageSourceUtils}.
 * 
 * @author Phillip Webb
 */
public class ObjectMessageSourceUtilsTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	private String code = "code";
	private Object[] args = new Object[] {};
	private Locale locale = Locale.UK;

	@Test
	public void shouldNeedMessageSourceOrApplicationContext() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("ApplicationContext must not be null");
		ObjectMessageSourceUtils.getObjectMessageSource(null, null);
	}

	@Test
	public void shouldUseMessageSourceAsObjectMessageSource() throws Exception {
		ObjectMessageSource messageSource = mock(ObjectMessageSource.class);
		ObjectMessageSource objectMessageSource = ObjectMessageSourceUtils.getObjectMessageSource(messageSource, null);
		assertThat(objectMessageSource, is(sameInstance(messageSource)));
	}

	@Test
	public void shouldWrapMessageSourceInDefaultObjectMessageSource() throws Exception {
		MessageSource messageSource = mock(MessageSource.class);
		ObjectMessageSource objectMessageSource = ObjectMessageSourceUtils.getObjectMessageSource(messageSource, null);
		assertThat(objectMessageSource, is(DefaultObjectMessageSource.class));
		objectMessageSource.getMessage(this.code, this.args, this.locale);
		verify(messageSource).getMessage(this.code, this.args, this.locale);
	}

	@Test
	public void shouldUseMessageSourceBean() throws Exception {
		ApplicationContext applicationContext = mock(ApplicationContext.class);
		ObjectMessageSource messageSource = mock(ObjectMessageSource.class);
		given(applicationContext.containsBean(AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME)).willReturn(true);
		given(applicationContext.getBean(AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME))
				.willReturn(messageSource);
		ObjectMessageSource objectMessageSource = ObjectMessageSourceUtils.getObjectMessageSource(null,
				applicationContext);
		assertThat(objectMessageSource, is(sameInstance(messageSource)));
	}

	@Test
	public void shouldUseApplicationContext() throws Exception {
		ApplicationContext applicationContext = mock(ApplicationContext.class);
		ObjectMessageSource objectMessageSource = ObjectMessageSourceUtils.getObjectMessageSource(null,
				applicationContext);
		assertThat(objectMessageSource, is(DefaultObjectMessageSource.class));
		objectMessageSource.getMessage(this.code, this.args, this.locale);
		verify(applicationContext).getMessage(this.code, this.args, this.locale);
	}

}
