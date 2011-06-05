package org.springframework.springfaces.mvc.internal;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
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
		context = mock(FacesContext.class);
		Map<Object, Object> attributes = new HashMap<Object, Object>();
		given(context.getAttributes()).willReturn(attributes);
		FacesContextSetter.setCurrentInstance(context);
	}

	@After
	public void cleanup() {
		FacesContextSetter.setCurrentInstance(null);
	}

	@Test
	public void shouldListenForAllSources() throws Exception {
		Object source = new Object();
		assertTrue(listener.isListenerForSource(source));
	}

	@Test
	public void shouldCapturePreRenderComponentEvent() throws Exception {
		UIComponent component = mock(UIComponent.class);
		SystemEvent event = new PreRenderComponentEvent(component);
		listener.processEvent(event);
		assertSame(event, MvcNavigationSystemEventListener.getLastPreRenderComponentEvent(context));
	}

	@Test
	public void shouldNotCaptureOtherEvent() throws Exception {
		UIComponent component = mock(UIComponent.class);
		SystemEvent event = new PreRemoveFromViewEvent(component);
		listener.processEvent(event);
		assertNull(MvcNavigationSystemEventListener.getLastPreRenderComponentEvent(context));
	}
}
