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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.application.Application;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationCase;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.PreRenderComponentEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.springfaces.mvc.FacesContextSetter;
import org.springframework.springfaces.mvc.SpringFacesContextSetter;
import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.navigation.NavigationContext;
import org.springframework.springfaces.mvc.navigation.NavigationOutcome;
import org.springframework.springfaces.mvc.navigation.NavigationOutcomeResolver;

/**
 * Tests for {@link MvcNavigationHandler}.
 * 
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class MvcNavigationHandlerTest {

	private MvcNavigationHandler navigationHandler;

	@Mock
	private ConfigurableNavigationHandler delegate;

	@Mock
	private NavigationOutcomeResolver navigationOutcomeResolver;

	@Mock
	private FacesContext context;

	@Mock
	private Application application;

	@Mock
	private ViewHandler viewHandler;

	private String fromAction = "fromAction";

	private String outcome = "outcome";

	@Mock
	private SpringFacesContext springFacesContext;

	@Mock
	private DestinationAndModelRegistry destinationAndModelRegistry;

	@Mock
	private Object handler;

	@Mock
	private Object controller;

	@Captor
	private ArgumentCaptor<NavigationContext> navigationContext;

	@Captor
	private ArgumentCaptor<DestinationAndModel> destinationAndModel;

	private NavigationOutcome navigationOutcome = new NavigationOutcome("destination");

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private List<FacesMessage> messageList = new ArrayList<FacesMessage>();

	@Before
	public void setup() {
		FacesContextSetter.setCurrentInstance(this.context);
		this.navigationHandler = new MvcNavigationHandler(this.delegate, this.navigationOutcomeResolver);
		this.navigationHandler.setDestinationAndModelRegistry(this.destinationAndModelRegistry);
		given(this.destinationAndModelRegistry.put(any(FacesContext.class), any(DestinationAndModel.class)))
				.willReturn("viewId");
		given(this.springFacesContext.getHandler()).willReturn(this.handler);
		given(this.springFacesContext.getController()).willReturn(this.controller);
		given(this.context.getApplication()).willReturn(this.application);
		given(this.application.getViewHandler()).willReturn(this.viewHandler);
		Map<Object, Object> attributes = new HashMap<Object, Object>();
		given(this.context.getAttributes()).willReturn(attributes);
		given(this.context.getMessageList()).willReturn(this.messageList);
		given(this.context.getMessages()).willAnswer(new Answer<Iterator<FacesMessage>>() {
			public Iterator<FacesMessage> answer(InvocationOnMock invocation) throws Throwable {
				return MvcNavigationHandlerTest.this.messageList.iterator();
			}
		});
	}

	@After
	public void cleanup() {
		SpringFacesContextSetter.setCurrentInstance(null);
		FacesContextSetter.setCurrentInstance(null);
	}

	private void handleOutcome() throws Exception {
		given(this.navigationOutcomeResolver.canResolve(any(FacesContext.class), this.navigationContext.capture()))
				.willReturn(true);
		given(this.navigationOutcomeResolver.resolve(any(FacesContext.class), any(NavigationContext.class)))
				.willReturn(this.navigationOutcome);
	}

	@Test
	public void shouldDelegateGetNavigationCaseWithoutSpringFacesContext() throws Exception {
		this.navigationHandler.getNavigationCase(this.context, this.fromAction, this.outcome);
		verify(this.delegate).getNavigationCase(this.context, this.fromAction, this.outcome);
	}

	@Test
	public void shouldDelegateGetNavigationCaseWhenNoResolve() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(this.springFacesContext);
		this.navigationHandler.getNavigationCase(this.context, this.fromAction, this.outcome);
		verify(this.navigationOutcomeResolver).canResolve(any(FacesContext.class), this.navigationContext.capture());
		verify(this.delegate, atLeastOnce()).getNavigationCase(this.context, this.fromAction, this.outcome);
	}

	@Test
	public void shouldNeedNavigationOutcomeForGetNavigationCase() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(this.springFacesContext);
		given(this.navigationOutcomeResolver.canResolve(any(FacesContext.class), this.navigationContext.capture()))
				.willReturn(true);
		this.thrown.equals(IllegalStateException.class);
		this.thrown.expectMessage("Unable to resolve required navigation outcome 'outcome'");
		this.navigationHandler.getNavigationCase(this.context, this.fromAction, this.outcome);
	}

	@Test
	public void shouldGetNavigationCase() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(this.springFacesContext);
		UIComponent component = mock(UIComponent.class);
		new MvcNavigationSystemEventListener().processEvent(new PreRenderComponentEvent(component));
		handleOutcome();
		NavigationCase navigationCase = this.navigationHandler.getNavigationCase(this.context, this.fromAction,
				this.outcome);
		assertNotNull(navigationCase);
		verify(this.destinationAndModelRegistry).put(eq(this.context), this.destinationAndModel.capture());
		assertEquals(this.navigationOutcome.getDestination(), this.destinationAndModel.getValue().getDestination());
		assertEquals(component, this.destinationAndModel.getValue().getComponent());
		NavigationContext navigationContext = this.navigationContext.getValue();
		assertEquals(this.outcome, navigationContext.getOutcome());
		assertEquals(this.fromAction, navigationContext.getFromAction());
		assertTrue(navigationContext.isPreemptive());
		assertSame(component, navigationContext.getComponent());
		assertSame(this.handler, navigationContext.getHandler());
		assertSame(this.controller, navigationContext.getController());
	}

	@Test
	public void shouldDelegateHandleNavigationWithoutSpringFacesContext() throws Exception {
		this.navigationHandler.handleNavigation(this.context, this.fromAction, this.outcome);
		verify(this.delegate).handleNavigation(this.context, this.fromAction, this.outcome);
	}

	@Test
	public void shouldDelegateHandleNavigationWhenCantResolve() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(this.springFacesContext);
		this.navigationHandler.handleNavigation(this.context, this.fromAction, this.outcome);
		verify(this.navigationOutcomeResolver).canResolve(any(FacesContext.class), this.navigationContext.capture());
		verify(this.delegate).handleNavigation(this.context, this.fromAction, this.outcome);
	}

	@Test
	public void shouldReRenderCurrentScreenWhenCanResolveAndNullOutcome() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(this.springFacesContext);
		given(this.navigationOutcomeResolver.canResolve(any(FacesContext.class), this.navigationContext.capture()))
				.willReturn(true);
		this.navigationHandler.handleNavigation(this.context, this.fromAction, this.outcome);
		verify(this.navigationOutcomeResolver).canResolve(any(FacesContext.class), this.navigationContext.capture());
		verify(this.delegate, never()).handleNavigation(this.context, this.fromAction, this.outcome);
	}

	@Test
	public void shouldHandleNavigation() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(this.springFacesContext);
		UIViewRoot viewRoot = mock(UIViewRoot.class);
		ActionEvent actionEvent = mock(ActionEvent.class);
		UIComponent component = mock(UIComponent.class);
		handleOutcome();
		given(this.viewHandler.createView(this.context, "viewId")).willReturn(viewRoot);
		given(actionEvent.getComponent()).willReturn(component);

		// Simulate the action event listener
		MvcNavigationActionListener actionLister = new MvcNavigationActionListener(mock(ActionListener.class));
		actionLister.processAction(actionEvent);

		this.navigationHandler.handleNavigation(this.context, this.fromAction, this.outcome);

		verify(this.destinationAndModelRegistry).put(eq(this.context), this.destinationAndModel.capture());
		assertEquals(this.navigationOutcome.getDestination(), this.destinationAndModel.getValue().getDestination());
		assertEquals(component, this.destinationAndModel.getValue().getComponent());
		verify(this.context).setViewRoot(viewRoot);
		NavigationContext navigationContext = this.navigationContext.getValue();
		assertEquals(this.outcome, navigationContext.getOutcome());
		assertEquals(this.fromAction, navigationContext.getFromAction());
		assertFalse(navigationContext.isPreemptive());
		assertSame(component, navigationContext.getComponent());
		assertSame(this.handler, navigationContext.getHandler());
		assertSame(this.controller, navigationContext.getController());
	}

	@Test
	public void shouldRemoveSlashFromDefaultDestinationViewId() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(this.springFacesContext);
		handleOutcome();
		NavigationCase defaultNavigationCase = mock(NavigationCase.class);
		given(this.delegate.getNavigationCase(this.context, this.fromAction, this.outcome)).willReturn(
				defaultNavigationCase);
		given(defaultNavigationCase.getToViewId(this.context)).willReturn("/example");
		this.navigationHandler.getNavigationCase(this.context, this.fromAction, this.outcome);
		assertEquals("example", this.navigationContext.getValue().getDefaultDestinationViewId());
	}

	@Test
	public void shouldRemoveSuperflousWarningFacesMessages() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(this.springFacesContext);
		handleOutcome();
		this.messageList.add(new FacesMessage("existing"));
		given(this.delegate.getNavigationCase(this.context, this.fromAction, this.outcome)).willAnswer(
				new Answer<NavigationCase>() {
					public NavigationCase answer(InvocationOnMock invocation) throws Throwable {
						MvcNavigationHandlerTest.this.messageList.add(new FacesMessage("new warning"));
						return null;
					}
				});
		this.navigationHandler.getNavigationCase(this.context, this.fromAction, this.outcome);
		assertEquals(1, this.messageList.size());
		assertEquals("existing", this.messageList.get(0).getSummary());
	}

}
