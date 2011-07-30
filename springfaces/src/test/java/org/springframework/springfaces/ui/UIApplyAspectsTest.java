package org.springframework.springfaces.ui;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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

	@Test
	public void shouldRenderChildren() throws Exception {
		UIApplyAspects applyAspects = new UIApplyAspects();
		assertThat(applyAspects.getRendersChildren(), is(true));
	}

	@Test
	public void shouldApplyUsingAspectGroup() throws Exception {
		UIAspectGroup aspectGroup = spy(new UIAspectGroup());
		UIApplyAspects applyAspects = new UIApplyAspects();
		UIComponent child = mock(UIComponent.class);
		aspectGroup.getChildren().add(applyAspects);
		applyAspects.getChildren().add(child);
		applyAspects.encodeChildren(context);

		// We should use the group to apply the aspect
		verify(aspectGroup).applyAspects(eq(context), invocation.capture());
		verify(child, never()).encodeAll(context);

		// On proceed the child should be encoded
		invocation.getValue().proceed();
		verify(child).encodeAll(context);
	}

	@Test
	public void shouldNeedAspectGroupParent() throws Exception {
		UIApplyAspects applyAspects = new UIApplyAspects();
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Unable to locate parent UIAspectGroup");
		applyAspects.encodeChildren(context);
	}

	@Test
	public void shouldFindAspectGroupGrandParent() throws Exception {
		UIAspectGroup aspectGroup = spy(new UIAspectGroup());
		UIPanel parent = new UIPanel();
		UIApplyAspects applyAspects = new UIApplyAspects();
		aspectGroup.getChildren().add(parent);
		parent.getChildren().add(applyAspects);
		applyAspects.encodeChildren(context);
		verify(aspectGroup).applyAspects(eq(context), invocation.capture());
	}
}
