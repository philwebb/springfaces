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
package org.springframework.springfaces.mvc.internal;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.PreRemoveFromViewEvent;
import javax.faces.event.PreRenderComponentEvent;
import javax.faces.event.SystemEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.springfaces.mvc.FacesContextSetter;

/**
 * Tests for {@link MvcNavigationSystemEventListener}.
 * 
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class MvcNavigationSystemEventListenerTest {

	private FacesContext context;

	private MvcNavigationSystemEventListener listener = new MvcNavigationSystemEventListener();

	@Before
	public void setup() {
		this.context = mock(FacesContext.class);
		Map<Object, Object> attributes = new HashMap<Object, Object>();
		given(this.context.getAttributes()).willReturn(attributes);
		FacesContextSetter.setCurrentInstance(this.context);
	}

	@After
	public void cleanup() {
		FacesContextSetter.setCurrentInstance(null);
	}

	@Test
	public void shouldListenForAllSources() throws Exception {
		Object source = new Object();
		assertThat(this.listener.isListenerForSource(source), is(true));
	}

	@Test
	public void shouldCapturePreRenderComponentEvent() throws Exception {
		UIComponent component = mock(UIComponent.class);
		SystemEvent event = new PreRenderComponentEvent(component);
		this.listener.processEvent(event);
		assertThat(MvcNavigationSystemEventListener.getLastPreRenderComponentEvent(this.context),
				is(sameInstance(event)));
	}

	@Test
	public void shouldNotCaptureOtherEvent() throws Exception {
		UIComponent component = mock(UIComponent.class);
		SystemEvent event = new PreRemoveFromViewEvent(component);
		this.listener.processEvent(event);
		assertThat(MvcNavigationSystemEventListener.getLastPreRenderComponentEvent(this.context), is(nullValue()));
	}
}
