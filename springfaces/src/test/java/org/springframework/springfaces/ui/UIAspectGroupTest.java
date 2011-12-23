package org.springframework.springfaces.ui;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;

import javax.faces.component.UIPanel;

import org.junit.Test;

/**
 * Tests for {@link UIAspectGroup}.
 * 
 * @author Phillip Webb
 */
public class UIAspectGroupTest {

	private UIAspectGroup aspectGroup = new UIAspectGroup();

	private UIAspect aspect = new UIAspect();

	@Test
	public void shouldGetChildAspects() throws Exception {
		this.aspectGroup.getChildren().add(this.aspect);
		assertThat(this.aspectGroup.getAllAspects(), is(Collections.singletonList(this.aspect)));
	}

	@Test
	public void shouldGetNestedChildAspects() throws Exception {
		UIPanel parent = new UIPanel();
		this.aspectGroup.getChildren().add(parent);
		parent.getChildren().add(this.aspect);
		assertThat(this.aspectGroup.getAllAspects(), is(Collections.singletonList(this.aspect)));
	}

	@Test
	public void shouldIncludeAspectsFromParentAspectGroup() throws Exception {
		UIAspect parentAspect = new UIAspect();
		UIAspectGroup parentAspectGroup = new UIAspectGroup();
		parentAspectGroup.getChildren().add(parentAspect);
		parentAspectGroup.getChildren().add(this.aspectGroup);
		this.aspectGroup.getChildren().add(this.aspect);
		assertThat(this.aspectGroup.getAllAspects(), is(Arrays.asList(parentAspect, this.aspect)));

	}
}
