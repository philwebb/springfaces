package org.springframework.springfaces.component;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.Map;

import javax.faces.component.PartialStateHolder;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.context.WebApplicationContext;

/**
 * Tests for {@link SpringBeanPartialStateHolder}.
 * 
 * @author Phillip Webb
 */
public class SpringBeanPartialStateHolderTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private FacesContext context;

	@Mock
	private WebApplicationContext applicationContext;

	private String beanName = "bean";

	@Mock
	private Object bean;

	private String stateHolderBeanName = "stateHolderBean";

	@Mock
	private PartialStateHolder stateHolderBean;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		ExternalContext externalContext = mock(ExternalContext.class);
		Map<String, Object> applicationMap = Collections.<String, Object> singletonMap(
				WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);
		given(context.getExternalContext()).willReturn(externalContext);
		given(externalContext.getApplicationMap()).willReturn(applicationMap);
		given(applicationContext.getBean(beanName)).willReturn(bean);
		given(applicationContext.getBean(stateHolderBeanName)).willReturn(stateHolderBean);
		given(applicationContext.isPrototype(stateHolderBeanName)).willReturn(true);
	}

	@Test
	public void shouldNeedFacesContext() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Context must not be null");
		new SpringBeanPartialStateHolder<Object>(null, beanName);
	}

	@Test
	public void shouldNeedBeanName() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("BeanName must not be null");
		new SpringBeanPartialStateHolder<Object>(context, null);
	}

	@Test
	public void shouldObtainBeanOnConstruct() throws Exception {
		SpringBeanPartialStateHolder<Object> holder = new SpringBeanPartialStateHolder<Object>(context, beanName);
		assertThat(holder.getBean(), is(bean));
	}

	@Test
	public void shouldOnlySupportStateHolderBeansIfPrototype() throws Exception {
		reset(applicationContext);
		given(applicationContext.getBean(stateHolderBeanName)).willReturn(stateHolderBean);
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("StateHolders must be declared as protoype beans");
		new SpringBeanPartialStateHolder<Object>(context, stateHolderBeanName);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldSaveAndRestore() throws Exception {
		SpringBeanPartialStateHolder<Object> holder = new SpringBeanPartialStateHolder<Object>(context, beanName);
		Object state = holder.saveState(context);
		holder = SpringBeanPartialStateHolder.class.newInstance();
		holder.restoreState(context, state);
		assertThat(holder.getBean(), is(bean));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldSaveAndRestoreBeansIfStateHolders() throws Exception {
		Object beanState = new Object();
		given(stateHolderBean.saveState(context)).willReturn(beanState);
		SpringBeanPartialStateHolder<Object> holder = new SpringBeanPartialStateHolder<Object>(context,
				stateHolderBeanName);
		Object state = holder.saveState(context);
		holder = SpringBeanPartialStateHolder.class.newInstance();
		holder.restoreState(context, state);
		verify(stateHolderBean).restoreState(context, beanState);
	}

	@Test
	public void shouldSupportTransient() throws Exception {
		SpringBeanPartialStateHolder<Object> holder = new SpringBeanPartialStateHolder<Object>(context, beanName);
		assertThat(holder.isTransient(), is(false));
		holder.setTransient(true);
		assertThat(holder.isTransient(), is(true));
	}

	@Test
	public void shouldSupportInitialState() throws Exception {
		SpringBeanPartialStateHolder<Object> holder = new SpringBeanPartialStateHolder<Object>(context, beanName);
		assertThat(holder.initialStateMarked(), is(false));
		holder.markInitialState();
		assertThat(holder.initialStateMarked(), is(true));
		holder.clearInitialState();
		assertThat(holder.initialStateMarked(), is(false));
	}

	@Test
	public void shouldDelegateInitialStateToBeanWhenPossible() throws Exception {
		SpringBeanPartialStateHolder<Object> holder = new SpringBeanPartialStateHolder<Object>(context,
				stateHolderBeanName);
		given(stateHolderBean.initialStateMarked()).willReturn(true);
		assertThat(holder.initialStateMarked(), is(true));
		verify(stateHolderBean).initialStateMarked();
		holder.markInitialState();
		verify(stateHolderBean).markInitialState();
		holder.clearInitialState();
		verify(stateHolderBean).clearInitialState();
	}
}
