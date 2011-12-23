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
		applyAspects.getChildren().add(this.child);
		assertThat(applyAspects.getRendersChildren(), is(true));
	}

	@Test
	public void shouldApplyUsingAspectGroup() throws Exception {
		this.applyAspects.getChildren().add(this.child);
		this.aspectGroup.getChildren().add(this.applyAspects);
		this.applyAspects.encodeChildren(this.context);
		InOrder ordered = inOrder(this.aspectGroup, this.child);
		ordered.verify(this.aspectGroup).applyAspects(eq(this.context), this.invocation.capture());
		ordered.verify(this.child).encodeAll(this.context);
	}

	@Test
	public void shouldNeedAspectGroupParent() throws Exception {
		this.applyAspects.getChildren().add(this.child);
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("Unable to locate parent UIAspectGroup");
		this.applyAspects.encodeChildren(this.context);
	}

	@Test
	public void shouldFindAspectGroupGrandParent() throws Exception {
		UIPanel parent = new UIPanel();
		this.applyAspects.getChildren().add(this.child);
		this.aspectGroup.getChildren().add(parent);
		parent.getChildren().add(this.applyAspects);
		this.applyAspects.encodeChildren(this.context);
		verify(this.aspectGroup).applyAspects(eq(this.context), this.invocation.capture());
	}

	@Test
	public void shouldAllowAccessToChildComponent() throws Exception {
		this.applyAspects.getChildren().add(this.child);
		this.aspectGroup.getChildren().add(this.applyAspects);
		this.applyAspects.encodeChildren(this.context);
		verify(this.aspectGroup).applyAspects(eq(this.context), this.invocation.capture());
		assertThat(this.invocation.getValue().getComponent(), is(this.child));
	}

	@Test
	public void shouldThrowWithTwoOrMoreChildren() throws Exception {
		UIComponent secondChild = mock(UIComponent.class);
		this.applyAspects.getChildren().add(this.child);
		this.applyAspects.getChildren().add(secondChild);
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("Aspects can only be applied to a single child");
		this.applyAspects.encodeChildren(this.context);
	}

	@Test
	public void shouldThrowWithZeroChildren() throws Exception {
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("Aspects can only be applied to a single child");
		this.applyAspects.encodeChildren(this.context);
	}

	@Test
	public void shouldBeInAspectFamily() throws Exception {
		String family = this.applyAspects.getFamily();
		assertThat(family, is("spring.faces.Aspect"));
	}
}
