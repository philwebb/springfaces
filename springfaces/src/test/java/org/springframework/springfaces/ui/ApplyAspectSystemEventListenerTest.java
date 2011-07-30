package org.springframework.springfaces.ui;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIPanel;
import javax.faces.event.PostAddToViewEvent;
import javax.faces.event.SystemEvent;

import org.junit.Test;

/**
 * Tests for {@link ApplyAspectSystemEventListener}.
 * 
 * @author Phillip Webb
 */
public class ApplyAspectSystemEventListenerTest {

	private ApplyAspectSystemEventListener listener = new ApplyAspectSystemEventListener();

	@Test
	public void shouldListenForAllSource() throws Exception {
		Object source = new Object();
		boolean actual = listener.isListenerForSource(source);
		assertThat(actual, is(true));
	}

	@Test
	public void shouldWrapComponent() throws Exception {
		UIAspectGroup parent = new UIAspectGroup();
		UIComponent component = new UIInput();
		parent.getChildren().add(component);
		SystemEvent event = new PostAddToViewEvent(component);
		listener.processEvent(event);
		assertThat(component.getParent(), is(UIApplyAspects.class));
		assertThat(component.getParent().getParent(), is((UIComponent) parent));
		assertThat(parent.getChildCount(), is(1));
	}

	@Test
	public void shouldNotDoubleWrap() throws Exception {
		UIAspectGroup parent = new UIAspectGroup();
		UIComponent component = new UIInput();
		parent.getChildren().add(component);
		SystemEvent event = new PostAddToViewEvent(component);
		listener.processEvent(event);
		listener.processEvent(event);
		assertThat(component.getParent().getParent(), is((UIComponent) parent));
	}

	@Test
	public void shouldOnlyWrapWhenInUIAspectGroup() throws Exception {
		UIPanel parent = new UIPanel();
		UIComponent component = new UIInput();
		parent.getChildren().add(component);
		SystemEvent event = new PostAddToViewEvent(component);
		listener.processEvent(event);
		assertThat(component.getParent(), is((UIComponent) parent));
	}

	@Test
	public void shouldWrapChildren() throws Exception {
		UIAspectGroup parent = new UIAspectGroup();
		UIComponent component = new UIPanel();
		UIInput child = new UIInput();
		parent.getChildren().add(component);
		component.getChildren().add(child);
		SystemEvent event = new PostAddToViewEvent(component);
		listener.processEvent(event);
		assertThat(child.getParent(), is(UIApplyAspects.class));
		assertThat(child.getParent().getParent(), is(component));
	}

	@Test
	public void shouldNotWrapUIAspects() throws Exception {
		UIAspectGroup parent = new UIAspectGroup();
		UIComponent component = new UIAspect();
		parent.getChildren().add(component);
		SystemEvent event = new PostAddToViewEvent(component);
		listener.processEvent(event);
		assertThat(component.getParent(), is((UIComponent) parent));
	}

}
