package org.springframework.springfaces.component;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import javax.faces.component.PartialStateHolder;
import javax.faces.context.FacesContext;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.springfaces.SpringFacesMocks;
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

	private String integerBeanName = "integerBean";

	private Integer integerBean = new Integer(5);

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		SpringFacesMocks.setupSpringFacesIntegration(context, applicationContext);
		given(applicationContext.getBean(beanName)).willReturn(bean);
		given(applicationContext.getBean(stateHolderBeanName)).willReturn(stateHolderBean);
		given(applicationContext.isPrototype(stateHolderBeanName)).willReturn(true);
		given(applicationContext.getBean(integerBeanName)).willReturn(integerBean);
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

	@Test
	public void shouldCheckBeanType() throws Exception {
		new TypedToNumberHolder(context, integerBeanName);
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Unable to load bean 'integerBean' Object of class [java.lang.Integer] "
				+ "must be an instance of class java.lang.Long");
		new TypedToLongHolder(context, integerBeanName);
	}

	@Test
	public void shouldNotHaveNullStateIfMakeInitialStateAndNotStateHolderBean() throws Exception {
		SpringBeanPartialStateHolder<Object> holder = new SpringBeanPartialStateHolder<Object>(context, beanName);
		holder.markInitialState();
		Object state = holder.saveState(context);
		assertThat(state, is(nullValue()));
	}

	@Test
	public void shouldNotHaveDirectStateIfMakeInitialStateAndStateHolderBean() throws Exception {
		Object beanState = new Object();
		given(stateHolderBean.saveState(context)).willReturn(beanState);
		SpringBeanPartialStateHolder<Object> holder = new SpringBeanPartialStateHolder<Object>(context,
				stateHolderBeanName);
		holder.markInitialState();
		Object state = holder.saveState(context);
		holder.restoreState(context, state);
		assertThat(state, is(sameInstance(beanState)));
		verify(stateHolderBean).restoreState(context, beanState);
	}

	private static class TypedToNumberHolder extends SpringBeanPartialStateHolder<Number> {
		public TypedToNumberHolder(FacesContext context, String beanName) {
			super(context, beanName);
		}
	}

	private static class TypedToLongHolder extends SpringBeanPartialStateHolder<Long> {
		public TypedToLongHolder(FacesContext context, String beanName) {
			super(context, beanName);
		}
	}
}
