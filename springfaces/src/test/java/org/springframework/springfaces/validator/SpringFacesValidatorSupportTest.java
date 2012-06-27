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
package org.springframework.springfaces.validator;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.faces.FacesWrapper;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.springfaces.FacesContextSetter;
import org.springframework.springfaces.SpringFacesMocks;
import org.springframework.springfaces.bean.ForClass;
import org.springframework.web.context.support.StaticWebApplicationContext;

/**
 * Tests for {@link SpringFacesValidatorSupport}.
 * @author Phillip Webb
 */
public class SpringFacesValidatorSupportTest {

	private static final String VALIDATOR_SUPPORT_BEAN = "validatorSupportBean";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private SpringFacesValidatorSupport validatorSupport;

	private StaticWebApplicationContext applicationContext = new StaticWebApplicationContext();

	@Mock
	private Application delegateApplication;

	@Mock
	private FacesContext facesContext;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		SpringFacesMocks.setupSpringFacesIntegration(this.facesContext, this.applicationContext);
		FacesContextSetter.setCurrentInstance(this.facesContext);
	}

	@After
	public void teardown() {
		FacesContextSetter.setCurrentInstance(null);
	}

	private Application createWrappedApplication() {
		this.applicationContext.registerSingleton(VALIDATOR_SUPPORT_BEAN, SpringFacesValidatorSupport.class);
		this.validatorSupport = this.applicationContext.getBean(SpringFacesValidatorSupport.class);
		this.validatorSupport.setApplicationContext(this.applicationContext);
		this.validatorSupport.onApplicationEvent(new ContextRefreshedEvent(this.applicationContext));
		return this.validatorSupport.newWrapper(Application.class, this.delegateApplication);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldGetWrapped() throws Exception {
		Application application = createWrappedApplication();
		assertThat(((FacesWrapper<Application>) application).getWrapped(), is(this.delegateApplication));
	}

	@Test
	public void shouldDelegateCreateValidatorById() throws Exception {
		Application application = createWrappedApplication();
		Validator validator = mock(Validator.class);
		given(this.delegateApplication.createValidator("id")).willReturn(validator);
		Validator actual = application.createValidator("id");
		assertThat(actual, is(sameInstance(validator)));
	}

	@Test
	public void shouldCreateValidatorByIdFromSpring() throws Exception {
		this.applicationContext.registerSingleton("bean", MockValidator.class);
		Application application = createWrappedApplication();
		Validator validator = application.createValidator("bean");
		assertThat(validator, is(instanceOf(SpringBeanValidator.class)));
		assertThat(getWrapped(validator), is(instanceOf(MockValidator.class)));
	}

	@Test
	public void shouldCreateFacesValidatorByIdFromSpring() throws Exception {
		this.applicationContext.registerSingleton("bean", MockFacesValidator.class);
		Application application = createWrappedApplication();
		Validator validator = application.createValidator("bean");
		assertThat(validator, is(instanceOf(SpringBeanFacesValidator.class)));
		assertThat(getWrapped(validator), is(instanceOf(MockFacesValidator.class)));
	}

	@Test
	public void shouldRegisterDefaultValidator() throws Exception {
		createWrappedApplication();
		verify(this.delegateApplication).addValidator(SpringFacesValidatorSupport.DefaultValidator.VALIDATOR_ID,
				SpringFacesValidatorSupport.DefaultValidator.class.getName());
		verify(this.delegateApplication).addDefaultValidatorId(
				SpringFacesValidatorSupport.DefaultValidator.VALIDATOR_ID);
	}

	@Test
	public void shouldCreateDefaultValidator() throws Exception {
		Application application = createWrappedApplication();
		Validator validator = application.createValidator(SpringFacesValidatorSupport.DefaultValidator.VALIDATOR_ID);
		assertThat(validator, is(instanceOf(SpringFacesValidatorSupport.DefaultValidator.class)));
	}

	@Test
	public void shouldUseForClassValidators() throws Exception {
		this.applicationContext.registerSingleton("b1", MockValidatorForClass.class);
		this.applicationContext.registerSingleton("b2", MockFacesValidatorForClass.class);
		Application application = createWrappedApplication();
		Validator validator = application.createValidator(SpringFacesValidatorSupport.DefaultValidator.VALIDATOR_ID);
		UIComponent component = mock(UIComponent.class);
		Object value = new Example();
		validator.validate(this.facesContext, component, value);
		assertThat(((MockValidatorForClass) this.applicationContext.getBean("b1")).isValidated(), is(true));
		assertThat(((MockFacesValidatorForClass) this.applicationContext.getBean("b2")).isValidated(), is(true));
	}

	@Test
	public void shouldDefaultValidateNull() throws Exception {
		Application application = createWrappedApplication();
		Validator validator = application.createValidator(SpringFacesValidatorSupport.DefaultValidator.VALIDATOR_ID);
		UIComponent component = mock(UIComponent.class);
		Object value = null;
		validator.validate(this.facesContext, component, value);
	}

	private Object getWrapped(Validator validator) {
		return ((FacesWrapper<?>) validator).getWrapped();
	}

	private static class Example {
	}

	private static class MockValidator implements org.springframework.springfaces.validator.Validator<Example> {
		private boolean validated;

		public void validate(FacesContext context, UIComponent component, Example value) throws ValidatorException {
			this.validated = true;
		}

		public boolean isValidated() {
			return this.validated;
		}
	}

	private static class MockFacesValidator implements Validator {
		private boolean validated;

		public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
			this.validated = true;
		}

		public boolean isValidated() {
			return this.validated;
		}
	}

	@ForClass
	private static class MockValidatorForClass extends MockValidator {
	}

	@ForClass(Example.class)
	private static class MockFacesValidatorForClass extends MockFacesValidator {
	}
}
