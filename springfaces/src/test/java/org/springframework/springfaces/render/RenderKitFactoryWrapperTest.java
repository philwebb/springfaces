package org.springframework.springfaces.render;

import static org.mockito.Mockito.verify;

import javax.faces.context.FacesContext;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests for {@link RenderKitFactoryWrapper}.
 * 
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class RenderKitFactoryWrapperTest {

	@Mock
	private RenderKitFactory wrapped;

	private RenderKitFactoryWrapper wrapper = new MockRenderKitFactoryWrapper();

	private String renderKitId = "renderKitId";

	@Mock
	private FacesContext context;

	@Mock
	private RenderKit renderKit;

	@Test
	public void shouldWrapAddRenderKit() {
		this.wrapper.addRenderKit(this.renderKitId, this.renderKit);
		verify(this.wrapped).addRenderKit(this.renderKitId, this.renderKit);
	}

	@Test
	public void shouldWrapGetRenderKit() {
		this.wrapper.getRenderKit(this.context, this.renderKitId);
		verify(this.wrapped).getRenderKit(this.context, this.renderKitId);
	}

	@Test
	public void shouldWrapGetRenderKitIds() {
		this.wrapper.getRenderKitIds();
		verify(this.wrapped).getRenderKitIds();
	}

	private class MockRenderKitFactoryWrapper extends RenderKitFactoryWrapper {
		@Override
		public RenderKitFactory getWrapped() {
			return RenderKitFactoryWrapperTest.this.wrapped;
		}
	}
}
