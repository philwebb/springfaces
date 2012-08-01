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
package org.springframework.springfaces.config.util;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;

/**
 * Tests for {@link RegisteredBeanDefinition}.
 * 
 * @author Phillip Webb
 */
public class RegisteredBeanDefinitionTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private BeanDefinition beanDefinition;

	private String name = "name";

	private RegisteredBeanDefinition registeredBeanDefinition;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.registeredBeanDefinition = new RegisteredBeanDefinition(this.beanDefinition, this.name);
	}

	@Test
	public void shouldNeedBeanDefinition() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("BeanDefinition must not be null");
		new RegisteredBeanDefinition(null, this.name);
	}

	@Test
	public void shouldNeedName() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Name must not be null");
		new RegisteredBeanDefinition(this.beanDefinition, null);
	}

	@Test
	public void shouldGetBeanDefinition() throws Exception {
		assertThat(this.registeredBeanDefinition.getBeanDefinition(), is(this.beanDefinition));
	}

	@Test
	public void shouldGetName() throws Exception {
		assertThat(this.registeredBeanDefinition.getName(), is(this.name));
	}

	@Test
	public void shouldGetAsReference() throws Exception {
		RuntimeBeanReference reference = this.registeredBeanDefinition.asReference();
		assertThat(reference.getBeanName(), is(this.name));
	}
}
