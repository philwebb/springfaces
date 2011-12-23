package org.springframework.springfaces.ui;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.faces.component.UIComponent;

import org.junit.Test;

/**
 * Tests for {@link AspectInvocationWrapper}.
 * 
 * @author Phillip Webb
 */
public class AspectInvocationWrapperTest {

	private AspectInvocation wrapped = mock(AspectInvocation.class);

	private AspectInvocationWrapper wrapper = new AspectInvocationWrapper(this.wrapped);

	@Test
	public void shouldWrapGetComponent() throws Exception {
		UIComponent component = mock(UIComponent.class);
		given(this.wrapped.getComponent()).willReturn(component);
		assertThat(this.wrapper.getComponent(), is(sameInstance(component)));
	}

	@Test
	public void shouldWrapProceed() throws Exception {
		this.wrapper.proceed();
		verify(this.wrapped).proceed();
	}
}
