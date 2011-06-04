package org.springframework.springfaces.internal;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SpringRenderKitFactoryTest {

	@Mock
	private RenderKitFactory delegate;

	@Test
	public void shouldWrapDelegate() throws Exception {
		SpringRenderKitFactory factory = new SpringRenderKitFactory(delegate);
		assertSame(delegate, factory.getWrapped());
	}

	@Test
	public void shouldWrapAddedRenderKit() throws Exception {
		SpringRenderKitFactory factory = new SpringRenderKitFactory(delegate);
		String renderKitId = "renderKitId";
		RenderKit renderKit = mock(RenderKit.class);
		factory.addRenderKit(renderKitId, renderKit);
		ArgumentCaptor<RenderKit> renderKitCaptor = ArgumentCaptor.forClass(RenderKit.class);
		verify(delegate).addRenderKit(eq(renderKitId), renderKitCaptor.capture());
		assertTrue(renderKitCaptor.getValue() instanceof SpringRenderKit);
		assertSame(renderKit, ((SpringRenderKit) renderKitCaptor.getValue()).getWrapped());
	}
}
