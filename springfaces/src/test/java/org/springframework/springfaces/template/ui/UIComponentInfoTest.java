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
package org.springframework.springfaces.template.ui;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIOutput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.render.RenderKit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.springfaces.FacesContextSetter;

import com.sun.faces.component.visit.VisitContextFactoryImpl;

/**
 * Tests for {@link UIComponentInfo}.
 * 
 * @author Phillip Webb
 */
public class UIComponentInfoTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private UIComponentInfo uiComponentInfo = new TestUIComponentInfo();

	private FacesContext context;

	private Map<String, Object> requestMap = new HashMap<String, Object>();

	private Map<String, Object> requestMapAtEncode;

	private Map<Object, Object> attributes = new HashMap<Object, Object>();

	@Before
	public void setup() {
		this.context = mock(FacesContext.class);
		ExternalContext externalContext = mock(ExternalContext.class);
		Application application = mock(Application.class);
		RenderKit renderKit = mock(RenderKit.class);
		given(this.context.getExternalContext()).willReturn(externalContext);
		given(this.context.getAttributes()).willReturn(this.attributes);
		given(this.context.getApplication()).willReturn(application);
		given(this.context.getRenderKit()).willReturn(renderKit);
		given(externalContext.getRequestMap()).willReturn(this.requestMap);
		FacesContextSetter.setCurrentInstance(this.context);
	}

	@Test
	public void shouldGetFamily() throws Exception {
		assertThat(this.uiComponentInfo.getFamily(), is("spring.faces.ComponentInfo"));
	}

	@Test
	public void shouldDefaultToNoFor() throws Exception {
		assertThat(this.uiComponentInfo.getFor(), is(nullValue()));
	}

	@Test
	public void shouldGetForIfSpecified() throws Exception {
		this.uiComponentInfo.setFor("forId");
		assertThat(this.uiComponentInfo.getFor(), is("forId"));
	}

	@Test
	public void shouldNeedVar() throws Exception {
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("Please specify a 'var' for the ComponentInfo");
		this.uiComponentInfo.getVar();
	}

	@Test
	public void shouldGetVar() throws Exception {
		this.uiComponentInfo.setVar("info");
		assertThat(this.uiComponentInfo.getVar(), is("info"));
	}

	@Test
	public void shouldRenderChildren() throws Exception {
		assertThat(this.uiComponentInfo.getRendersChildren(), is(true));
	}

	@Test
	public void shouldSetComponentInfoOnEncode() throws Exception {
		FactoryFinder.setFactory(FactoryFinder.VISIT_CONTEXT_FACTORY, VisitContextFactoryImpl.class.getName());
		UIInput child = spy(new UIInput());
		this.uiComponentInfo.getChildren().add(child);
		this.uiComponentInfo.setVar("info");
		this.uiComponentInfo.encodeChildren(this.context);
		ComponentInfo componentInfo = (ComponentInfo) this.requestMapAtEncode.get("info");
		verify(child).encodeAll(this.context);
		assertThat(componentInfo, is(instanceOf(DefaultComponentInfo.class)));
		assertThat(componentInfo.getComponent(), is((UIComponent) child));
	}

	@Test
	public void shouldSupportMultipleEditableValueHolderComponents() throws Exception {
		FactoryFinder.setFactory(FactoryFinder.VISIT_CONTEXT_FACTORY, VisitContextFactoryImpl.class.getName());
		UIInput child1 = new UIInput();
		this.uiComponentInfo.getChildren().add(child1);
		UIOutput child2 = new UIOutput();
		this.uiComponentInfo.getChildren().add(child2);
		UIInput child3 = new UIInput();
		this.uiComponentInfo.getChildren().add(child3);
		this.uiComponentInfo.setVar("info");
		this.uiComponentInfo.encodeChildren(this.context);
		ComponentInfo componentInfo = (ComponentInfo) this.requestMapAtEncode.get("info");
		assertThat(componentInfo.getComponent(), is((UIComponent) child1));
		assertThat(componentInfo.getComponents(), is(Arrays.<UIComponent> asList(child1, child3)));
	}

	@Test
	public void shouldEncodeWithCustomFor() throws Exception {
		FactoryFinder.setFactory(FactoryFinder.VISIT_CONTEXT_FACTORY, VisitContextFactoryImpl.class.getName());
		UIInput child1 = new UIInput();
		child1.setId("for");
		this.uiComponentInfo.getChildren().add(child1);
		UIInput child2 = new UIInput();
		child2.setId("notFor");
		this.uiComponentInfo.setVar("info");
		this.uiComponentInfo.setFor("for");
		this.uiComponentInfo.encodeChildren(this.context);
		ComponentInfo componentInfo = (ComponentInfo) this.requestMapAtEncode.get("info");
		assertThat(componentInfo.getComponents(), is(Arrays.<UIComponent> asList(child1)));
	}

	private class TestUIComponentInfo extends UIComponentInfo {
		@Override
		protected void doEncodeChildren(FacesContext context) throws IOException {
			UIComponentInfoTest.this.requestMapAtEncode = new HashMap<String, Object>(
					UIComponentInfoTest.this.requestMap);
			super.doEncodeChildren(context);
		}
	}
}
