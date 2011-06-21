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
		FacesContextSetter.setCurrentInstance(context);
		navigationHandler = new MvcNavigationHandler(delegate, navigationOutcomeResolver);
		navigationHandler.setDestinationAndModelRegistry(destinationAndModelRegistry);
		given(destinationAndModelRegistry.put(any(FacesContext.class), any(DestinationAndModel.class))).willReturn(
				"viewId");
		given(springFacesContext.getHandler()).willReturn(handler);
		given(springFacesContext.getController()).willReturn(controller);
		given(context.getApplication()).willReturn(application);
		given(application.getViewHandler()).willReturn(viewHandler);
		Map<Object, Object> attributes = new HashMap<Object, Object>();
		given(context.getAttributes()).willReturn(attributes);
		given(context.getMessageList()).willReturn(messageList);
		given(context.getMessages()).willAnswer(new Answer<Iterator<FacesMessage>>() {
			public Iterator<FacesMessage> answer(InvocationOnMock invocation) throws Throwable {
				return messageList.iterator();
			}
		});
	}

	@After
	public void cleanup() {
		SpringFacesContextSetter.setCurrentInstance(null);
		FacesContextSetter.setCurrentInstance(null);
	}

	private void handleOutcome() throws Exception {
		given(navigationOutcomeResolver.canResolve(any(FacesContext.class), navigationContext.capture())).willReturn(
				true);
		given(navigationOutcomeResolver.resolve(any(FacesContext.class), any(NavigationContext.class))).willReturn(
				navigationOutcome);
	}

	@Test
	public void shouldDelegateGetNavigationCaseWithoutSpringFacesContext() throws Exception {
		navigationHandler.getNavigationCase(context, fromAction, outcome);
		verify(delegate).getNavigationCase(context, fromAction, outcome);
	}

	@Test
	public void shouldDelegateGetNavigationCaseWhenNoResolve() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(springFacesContext);
		navigationHandler.getNavigationCase(context, fromAction, outcome);
		verify(navigationOutcomeResolver).canResolve(any(FacesContext.class), navigationContext.capture());
		verify(delegate, atLeastOnce()).getNavigationCase(context, fromAction, outcome);
	}

	@Test
	public void shouldNeedNavigationOutcomeForGetNavigationCase() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(springFacesContext);
		given(navigationOutcomeResolver.canResolve(any(FacesContext.class), navigationContext.capture())).willReturn(
				true);
		thrown.equals(IllegalStateException.class);
		thrown.expectMessage("Unable to resolve required navigation outcome 'outcome'");
		navigationHandler.getNavigationCase(context, fromAction, outcome);
	}

	@Test
	public void shouldGetNavigationCase() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(springFacesContext);
		UIComponent component = mock(UIComponent.class);
		new MvcNavigationSystemEventListener().processEvent(new PreRenderComponentEvent(component));
		handleOutcome();
		NavigationCase navigationCase = navigationHandler.getNavigationCase(context, fromAction, outcome);
		assertNotNull(navigationCase);
		verify(destinationAndModelRegistry).put(eq(context), destinationAndModel.capture());
		assertEquals(navigationOutcome.getDestination(), destinationAndModel.getValue().getDestination());
		// FIXME test model?
		NavigationContext navigationContext = this.navigationContext.getValue();
		assertEquals(outcome, navigationContext.getOutcome());
		assertEquals(fromAction, navigationContext.getFromAction());
		assertTrue(navigationContext.isPreemptive());
		assertSame(component, navigationContext.getComponent());
		assertSame(handler, navigationContext.getHandler());
		assertSame(controller, navigationContext.getController());
	}

	@Test
	public void shouldDelegateHandleNavigationWithoutSpringFacesContext() throws Exception {
		navigationHandler.handleNavigation(context, fromAction, outcome);
		verify(delegate).handleNavigation(context, fromAction, outcome);
	}

	@Test
	public void shouldDelegateHandleNavigationWhenCantResolve() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(springFacesContext);
		navigationHandler.handleNavigation(context, fromAction, outcome);
		verify(navigationOutcomeResolver).canResolve(any(FacesContext.class), navigationContext.capture());
		verify(delegate).handleNavigation(context, fromAction, outcome);
	}

	@Test
	public void shouldDelegateHandleNavigationWhenResolvesNull() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(springFacesContext);
		given(navigationOutcomeResolver.canResolve(any(FacesContext.class), navigationContext.capture())).willReturn(
				true);
		navigationHandler.handleNavigation(context, fromAction, outcome);
		verify(navigationOutcomeResolver).canResolve(any(FacesContext.class), navigationContext.capture());
		verify(delegate).handleNavigation(context, fromAction, outcome);
	}

	@Test
	public void shouldHandleNavigation() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(springFacesContext);
		UIViewRoot viewRoot = mock(UIViewRoot.class);
		ActionEvent actionEvent = mock(ActionEvent.class);
		UIComponent component = mock(UIComponent.class);
		handleOutcome();
		given(viewHandler.createView(context, "viewId")).willReturn(viewRoot);
		given(actionEvent.getComponent()).willReturn(component);

		// Simulate the action event listener
		MvcNavigationActionListener actionLister = new MvcNavigationActionListener(mock(ActionListener.class));
		actionLister.processAction(actionEvent);

		navigationHandler.handleNavigation(context, fromAction, outcome);

		verify(destinationAndModelRegistry).put(eq(context), destinationAndModel.capture());
		assertEquals(navigationOutcome.getDestination(), destinationAndModel.getValue().getDestination());
		// FIXME verify model?
		verify(context).setViewRoot(viewRoot);
		NavigationContext navigationContext = this.navigationContext.getValue();
		assertEquals(outcome, navigationContext.getOutcome());
		assertEquals(fromAction, navigationContext.getFromAction());
		assertFalse(navigationContext.isPreemptive());
		assertSame(component, navigationContext.getComponent());
		assertSame(handler, navigationContext.getHandler());
		assertSame(controller, navigationContext.getController());
	}

	@Test
	public void shouldRemoveSlashFromDefaultDestinationViewId() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(springFacesContext);
		handleOutcome();
		NavigationCase defaultNavigationCase = mock(NavigationCase.class);
		given(delegate.getNavigationCase(context, fromAction, outcome)).willReturn(defaultNavigationCase);
		given(defaultNavigationCase.getToViewId(context)).willReturn("/example");
		navigationHandler.getNavigationCase(context, fromAction, outcome);
		assertEquals("example", navigationContext.getValue().getDefaultDestinationViewId());
	}

	@Test
	public void shouldRemoveSuperflousWarningFacesMessages() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(springFacesContext);
		handleOutcome();
		messageList.add(new FacesMessage("existing"));
		given(delegate.getNavigationCase(context, fromAction, outcome)).willAnswer(new Answer<NavigationCase>() {
			public NavigationCase answer(InvocationOnMock invocation) throws Throwable {
				messageList.add(new FacesMessage("new warning"));
				return null;
			}
		});
		navigationHandler.getNavigationCase(context, fromAction, outcome);
		assertEquals(1, messageList.size());
		assertEquals("existing", messageList.get(0).getSummary());
	}

}
