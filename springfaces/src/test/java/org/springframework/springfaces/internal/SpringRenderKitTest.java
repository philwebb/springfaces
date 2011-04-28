package org.springframework.springfaces.internal;

import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.faces.render.RenderKit;
import javax.faces.render.ResponseStateManager;

import org.junit.Test;

/**
 * Tests for {@link SpringRenderKit}.
 * 
 * @author Phillip Webb
 */
public class SpringRenderKitTest extends AbstractFacesWrapperTest<RenderKit, SpringRenderKit> {

	// Tests are inherited from AbstractWrapperTest

	@Override
	protected SpringRenderKit newWrapper(RenderKit delegate) throws Exception {
		return new SpringRenderKit("rid", delegate);
	}

	@Test
	public void shouldCreateWrappedResponseStateManager() throws Exception {
		RenderKit delegate = mock(RenderKit.class);
		ResponseStateManager responseStateManager = mock(ResponseStateManager.class);
		given(delegate.getResponseStateManager()).willReturn(responseStateManager);
		SpringRenderKit springRenderKit = new SpringRenderKit("rid", delegate);
		ResponseStateManager actual = springRenderKit.getResponseStateManager();
		assertTrue(actual instanceof SpringResponseStateManager);
		verify(delegate).getResponseStateManager();
	}
}
