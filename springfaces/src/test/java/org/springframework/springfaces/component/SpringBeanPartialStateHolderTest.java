/*
 * Copyright 2010-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
		SpringFacesMocks.setupSpringFacesIntegration(this.context, this.applicationContext);
		given(this.applicationContext.getBean(this.beanName)).willReturn(this.bean);
		given(this.applicationContext.getBean(this.stateHolderBeanName)).willReturn(this.stateHolderBean);
		given(this.applicationContext.isPrototype(this.stateHolderBeanName)).willReturn(true);
		given(this.applicationContext.getBean(this.integerBeanName)).willReturn(this.integerBean);
	}

	@Test
	public void shouldNeedFacesContext() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Context must not be null");
		new SpringBeanPartialStateHolder<Object>(null, this.beanName);
	}

	@Test
	public void shouldNeedBeanName() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("BeanName must not be null");
		new SpringBeanPartialStateHolder<Object>(this.context, null);
	}

	@Test
	public void shouldObtainBeanOnConstruct() throws Exception {
		SpringBeanPartialStateHolder<Object> holder = new SpringBeanPartialStateHolder<Object>(this.context,
				this.beanName);
		assertThat(holder.getBean(), is(this.bean));
	}

	@Test
	public void shouldOnlySupportStateHolderBeansIfPrototype() throws Exception {
		reset(this.applicationContext);
		given(this.applicationContext.getBean(this.stateHolderBeanName)).willReturn(this.stateHolderBean);
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("StateHolders must be declared as protoype beans");
		new SpringBeanPartialStateHolder<Object>(this.context, this.stateHolderBeanName);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldSaveAndRestore() throws Exception {
		SpringBeanPartialStateHolder<Object> holder = new SpringBeanPartialStateHolder<Object>(this.context,
				this.beanName);
		Object state = holder.saveState(this.context);
		holder = SpringBeanPartialStateHolder.class.newInstance();
		holder.restoreState(this.context, state);
		assertThat(holder.getBean(), is(this.bean));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldSaveAndRestoreBeansIfStateHolders() throws Exception {
		Object beanState = new Object();
		given(this.stateHolderBean.saveState(this.context)).willReturn(beanState);
		SpringBeanPartialStateHolder<Object> holder = new SpringBeanPartialStateHolder<Object>(this.context,
				this.stateHolderBeanName);
		Object state = holder.saveState(this.context);
		holder = SpringBeanPartialStateHolder.class.newInstance();
		holder.restoreState(this.context, state);
		verify(this.stateHolderBean).restoreState(this.context, beanState);
	}

	@Test
	public void shouldSupportTransient() throws Exception {
		SpringBeanPartialStateHolder<Object> holder = new SpringBeanPartialStateHolder<Object>(this.context,
				this.beanName);
		assertThat(holder.isTransient(), is(false));
		holder.setTransient(true);
		assertThat(holder.isTransient(), is(true));
	}

	@Test
	public void shouldSupportInitialState() throws Exception {
		SpringBeanPartialStateHolder<Object> holder = new SpringBeanPartialStateHolder<Object>(this.context,
				this.beanName);
		assertThat(holder.initialStateMarked(), is(false));
		holder.markInitialState();
		assertThat(holder.initialStateMarked(), is(true));
		holder.clearInitialState();
		assertThat(holder.initialStateMarked(), is(false));
	}

	@Test
	public void shouldDelegateInitialStateToBeanWhenPossible() throws Exception {
		SpringBeanPartialStateHolder<Object> holder = new SpringBeanPartialStateHolder<Object>(this.context,
				this.stateHolderBeanName);
		given(this.stateHolderBean.initialStateMarked()).willReturn(true);
		assertThat(holder.initialStateMarked(), is(true));
		verify(this.stateHolderBean).initialStateMarked();
		holder.markInitialState();
		verify(this.stateHolderBean).markInitialState();
		holder.clearInitialState();
		verify(this.stateHolderBean).clearInitialState();
	}

	@Test
	public void shouldCheckBeanType() throws Exception {
		new TypedToNumberHolder(this.context, this.integerBeanName);
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Unable to load bean 'integerBean' Object of class [java.lang.Integer] "
				+ "must be an instance of class java.lang.Long");
		new TypedToLongHolder(this.context, this.integerBeanName);
	}

	@Test
	public void shouldNotHaveNullStateIfMakeInitialStateAndNotStateHolderBean() throws Exception {
		SpringBeanPartialStateHolder<Object> holder = new SpringBeanPartialStateHolder<Object>(this.context,
				this.beanName);
		holder.markInitialState();
		Object state = holder.saveState(this.context);
		assertThat(state, is(nullValue()));
	}

	@Test
	public void shouldNotHaveDirectStateIfMakeInitialStateAndStateHolderBean() throws Exception {
		Object beanState = new Object();
		given(this.stateHolderBean.saveState(this.context)).willReturn(beanState);
		SpringBeanPartialStateHolder<Object> holder = new SpringBeanPartialStateHolder<Object>(this.context,
				this.stateHolderBeanName);
		holder.markInitialState();
		Object state = holder.saveState(this.context);
		holder.restoreState(this.context, state);
		assertThat(state, is(sameInstance(beanState)));
		verify(this.stateHolderBean).restoreState(this.context, beanState);
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
