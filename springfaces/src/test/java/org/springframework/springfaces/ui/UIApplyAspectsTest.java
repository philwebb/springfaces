package org.springframework.springfaces.ui;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests for {@link UIApplyAspects}.
 * 
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class UIApplyAspectsTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private FacesContext context;

	@Captor
	private ArgumentCaptor<AspectInvocation> invocation;

	@Mock
	private UIComponent child;

	private UIAspectGroup aspectGroup = spy(new UIAspectGroup());

	private UIApplyAspects applyAspects = new UIApplyAspects();

	@Test
	public void shouldRenderChildren() throws Exception {
		UIApplyAspects applyAspects = new UIApplyAspects();
		applyAspects.getChildren().add(child);
		assertThat(applyAspects.getRendersChildren(), is(true));
	}

	@Test
	public void shouldApplyUsingAspectGroup() throws Exception {
		applyAspects.getChildren().add(child);
		aspectGroup.getChildren().add(applyAspects);
		applyAspects.encodeChildren(context);
		InOrder ordered = inOrder(aspectGroup, child);
		ordered.verify(aspectGroup).applyAspects(eq(context), invocation.capture());
		ordered.verify(child).encodeAll(context);
	}

	@Test
	public void shouldNeedAspectGroupParent() throws Exception {
		applyAspects.getChildren().add(child);
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Unable to locate parent UIAspectGroup");
		applyAspects.encodeChildren(context);
	}

	@Test
	public void shouldFindAspectGroupGrandParent() throws Exception {
		UIPanel parent = new UIPanel();
		applyAspects.getChildren().add(child);
		aspectGroup.getChildren().add(parent);
		parent.getChildren().add(applyAspects);
		applyAspects.encodeChildren(context);
		verify(aspectGroup).applyAspects(eq(context), invocation.capture());
	}

	@Test
	public void shouldAllowAccessToChildComponent() throws Exception {
		applyAspects.getChildren().add(child);
		aspectGroup.getChildren().add(applyAspects);
		applyAspects.encodeChildren(context);
		verify(aspectGroup).applyAspects(eq(context), invocation.capture());
		assertThat(invocation.getValue().getComponent(), is(child));
	}

	@Test
	public void shouldThrowWithTwoOrMoreChildren() throws Exception {
		UIComponent secondChild = mock(UIComponent.class);
		applyAspects.getChildren().add(child);
		applyAspects.getChildren().add(secondChild);
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Aspects can only be applied to a single child");
		applyAspects.encodeChildren(context);
	}

	@Test
	public void shouldThrowWithZeroChildren() throws Exception {
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Aspects can only be applied to a single child");
		applyAspects.encodeChildren(context);
	}

	@Test
	public void shouldBeInAspectFamily() throws Exception {
		String family = applyAspects.getFamily();
		assertThat(family, is("spring.faces.Aspect"));
	}
}
