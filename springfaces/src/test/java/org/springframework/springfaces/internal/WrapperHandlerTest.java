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
package org.springframework.springfaces.internal;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.withSettings;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.Ordered;
import org.springframework.springfaces.FacesContextSetter;
import org.springframework.springfaces.FacesWrapperFactory;
import org.springframework.springfaces.SpringFacesIntegration;
import org.springframework.web.context.WebApplicationContext;

/**
 * Tests for {@link WrapperHandler}.
 * 
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class WrapperHandlerTest {

	private static final String SPRING_FACES_INTEGRATION_ATTRIBUTE = SpringFacesIntegration.class.getName();
	private static final String LAST_REFRESHED_DATE_ATTRIBUTE = SpringFacesIntegration.class.getName() + ".DATE";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private FacesContext facesContext;

	@Mock
	private ExternalContext externalContext;

	private Map<String, Object> applicationMap = new HashMap<String, Object>();

	@Mock
	private WebApplicationContext applicationContext;

	@SuppressWarnings("rawtypes")
	private Map<String, FacesWrapperFactory> facesWrapperFactoryBeans = new LinkedHashMap<String, FacesWrapperFactory>();

	@Mock
	private FacesWrapperFactory<Object> factory;

	private Object delegate;

	private WrapperHandler<Object> wrapperHandler;

	@Before
	public void setup() {
		given(this.facesContext.getExternalContext()).willReturn(this.externalContext);
		given(this.externalContext.getApplicationMap()).willReturn(this.applicationMap);
		given(this.applicationContext.getBeansOfType(FacesWrapperFactory.class)).willReturn(
				this.facesWrapperFactoryBeans);
		this.delegate = new Object();
		this.wrapperHandler = WrapperHandler.get(Object.class, this.delegate);
	}

	@After
	public void cleanupFacesContext() {
		FacesContext fc = FacesContext.getCurrentInstance();
		if (fc != null) {
			fc.release();
		}
		FacesContextSetter.setCurrentInstance(null);
	}

	private void setupApplicationContext(WebApplicationContext webContext) {
		SpringFacesIntegration integration = new SpringFacesIntegration();
		integration.setServletContext(mock(ServletContext.class));
		integration.setApplicationContext(webContext);
		this.applicationMap.put(SPRING_FACES_INTEGRATION_ATTRIBUTE, integration);
		this.applicationMap.put(LAST_REFRESHED_DATE_ATTRIBUTE, new Date());
	}

	private Object setupWrapperFactory() {
		FacesContextSetter.setCurrentInstance(this.facesContext);
		setupApplicationContext(this.applicationContext);
		this.facesWrapperFactoryBeans.put("bean", this.factory);
		Object wrapped = new Object();
		given(this.factory.newWrapper(Object.class, this.delegate)).willReturn(wrapped);
		return wrapped;
	}

	@Test
	public void shouldNeedTypeClass() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("TypeClass must not be null");
		new WrapperHandler<Object>(null, this.delegate);
	}

	@Test
	public void shouldNeedDelegate() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Delegate must not be null");
		new WrapperHandler<Object>(Object.class, null);
	}

	@Test
	public void shouldReturnDelegateWithoutApplicationContext() throws Exception {
		FacesContextSetter.setCurrentInstance(this.facesContext);
		Object actual = this.wrapperHandler.getWrapped();
		assertThat(actual, is(sameInstance(this.delegate)));
	}

	@Test
	public void shouldReturnDelegateWithoutFacesContext() throws Exception {
		cleanupFacesContext();
		Object actual = this.wrapperHandler.getWrapped();
		assertThat(actual, is(sameInstance(this.delegate)));
	}

	@Test
	public void shouldWrap() throws Exception {
		Object wrapped = setupWrapperFactory();
		Object actual = this.wrapperHandler.getWrapped();
		assertThat(actual, sameInstance(wrapped));
	}

	@Test
	public void shouldCacheWrapped() throws Exception {
		setupWrapperFactory();
		this.wrapperHandler.getWrapped();
		this.wrapperHandler.getWrapped();
		verify(this.factory).newWrapper(Object.class, this.delegate);
		verifyNoMoreInteractions(this.factory);
	}

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void shouldWrapInOrder() throws Exception {
		FacesContextSetter.setCurrentInstance(this.facesContext);
		setupApplicationContext(this.applicationContext);

		FacesWrapperFactory f1 = mock(FacesWrapperFactory.class, withSettings().extraInterfaces(Ordered.class));
		FacesWrapperFactory f2 = mock(FacesWrapperFactory.class, withSettings().extraInterfaces(Ordered.class));
		FacesWrapperFactory f3 = mock(FacesWrapperFactory.class, withSettings().extraInterfaces(Ordered.class));
		given(((Ordered) f1).getOrder()).willReturn(1);
		given(((Ordered) f2).getOrder()).willReturn(2);
		given(((Ordered) f3).getOrder()).willReturn(3);

		// Insert in reverse order
		this.facesWrapperFactoryBeans.put("f3", f3);
		this.facesWrapperFactoryBeans.put("f2", f2);
		this.facesWrapperFactoryBeans.put("f1", f1);

		this.wrapperHandler.getWrapped();

		InOrder inOrder = Mockito.inOrder(f1, f2, f3);
		inOrder.verify(f1).newWrapper(any(Class.class), any());
		inOrder.verify(f2).newWrapper(any(Class.class), any());
		inOrder.verify(f3).newWrapper(any(Class.class), any());
	}

	@Test
	public void shouldFilterByGenerics() throws Exception {
		FacesContextSetter.setCurrentInstance(this.facesContext);
		setupApplicationContext(this.applicationContext);
		this.facesWrapperFactoryBeans.put("long", new LongFacesWrapperFactory());
		this.facesWrapperFactoryBeans.put("integer", new IntegerFacesWrapperFactory());

		WrapperHandler<Integer> wrapperHandler = WrapperHandler.get(Integer.class, 0);
		Integer actual = wrapperHandler.getWrapped();

		assertThat(actual, is(equalTo(1)));
	}

	@Test
	public void shouldPostProcessWrapper() throws Exception {
		Object wrapped = setupWrapperFactory();
		WrapperHandler<Object> spy = spy(this.wrapperHandler);
		spy.getWrapped();
		verify(spy).postProcessWrapper(wrapped);
	}

	@Test
	public void shouldRewrapIfAppicationContextRefreshed() throws Exception {
		setupWrapperFactory();
		this.wrapperHandler.getWrapped();
		Calendar c = Calendar.getInstance();
		c.add(Calendar.SECOND, 1);
		this.applicationMap.put(LAST_REFRESHED_DATE_ATTRIBUTE, c.getTime());
		this.wrapperHandler.getWrapped();
		verify(this.factory, times(2)).newWrapper(Object.class, this.delegate);
	}

	private static class LongFacesWrapperFactory implements FacesWrapperFactory<Long> {
		public Long newWrapper(Class<?> typeClass, Long delegate) {
			return delegate + 1;
		}
	}

	private static class IntegerFacesWrapperFactory implements FacesWrapperFactory<Integer> {
		public Integer newWrapper(Class<?> typeClass, Integer delegate) {
			return delegate + 1;
		}
	}

}
