package org.springframework.springfaces.ui;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Constructor;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIPanel;
import javax.faces.event.PostAddToViewEvent;
import javax.faces.event.SystemEvent;

import org.junit.Test;

import com.sun.faces.facelets.compiler.UILiteralText;
import com.sun.faces.facelets.compiler.UIText;
import com.sun.faces.facelets.el.ELText;

/**
 * Tests for {@link ApplyAspectSystemEventListener}.
 * 
 * @author Phillip Webb
 */
public class ApplyAspectSystemEventListenerTest {

	private ApplyAspectSystemEventListener listener = new ApplyAspectSystemEventListener();

	private UIAspectGroup parent = new UIAspectGroup();

	@Test
	public void shouldListenForAllSource() throws Exception {
		Object source = new Object();
		boolean actual = listener.isListenerForSource(source);
		assertThat(actual, is(true));
	}

	@Test
	public void shouldWrapComponent() throws Exception {
		UIComponent component = addToParent(UIInput.class);
		SystemEvent event = new PostAddToViewEvent(component);
		listener.processEvent(event);
		assertThat(component.getParent(), is(UIApplyAspects.class));
		assertThat(component.getParent().getParent(), is((UIComponent) parent));
		assertThat(parent.getChildCount(), is(1));
	}

	@Test
	public void shouldNotDoubleWrap() throws Exception {
		UIComponent component = addToParent(UIInput.class);
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
	public void shouldNotWrapWithoutParent() throws Exception {
		UIInput component = new UIInput();
		SystemEvent event = new PostAddToViewEvent(component);
		listener.processEvent(event);
		assertThat(component.getParent(), is(nullValue()));
	}

	@Test
	public void shouldNotWrapUIAspects() throws Exception {
		shouldNotWrap(UIAspect.class);
	}

	@Test
	public void shouldNotWrapUIAspectGroup() throws Exception {
		shouldNotWrap(UIAspectGroup.class);
	}

	@Test
	public void shouldNotWrapUILiteralText() throws Exception {
		shouldNotWrap(UILiteralText.class.getConstructor(String.class), "text");
	}

	@Test
	public void shouldNotWrapUIText() throws Exception {
		String alias = "alais";
		ELText txt = mock(ELText.class);
		shouldNotWrap(UIText.class.getConstructor(String.class, ELText.class), alias, txt);
	}

	private <T extends UIComponent> void shouldNotWrap(Class<T> componentClass) throws Exception {
		shouldNotWrap(componentClass.getConstructor());
	}

	private <T extends UIComponent> void shouldNotWrap(Constructor<T> componentConstructor, Object... initArgs)
			throws Exception {
		T component = addToParent(componentConstructor, initArgs);
		SystemEvent event = new PostAddToViewEvent(component);
		listener.processEvent(event);
		assertThat(component.getParent(), is((UIComponent) parent));
	}

	private <T extends UIComponent> T addToParent(Class<T> componentClass) throws Exception {
		return addToParent(componentClass.getConstructor());
	}

	private <T extends UIComponent> T addToParent(Constructor<T> componentConstructor, Object... initArgs)
			throws Exception {
		T component = componentConstructor.newInstance(initArgs);
		parent.getChildren().add(component);
		return component;
	}

}
