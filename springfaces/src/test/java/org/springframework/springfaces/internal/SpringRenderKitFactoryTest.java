package org.springframework.springfaces.internal;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Tests for {@link SpringRenderKitFactory}.
 * 
 * @author Phillip Webb
 */
public class SpringRenderKitFactoryTest {

	@Mock
	private RenderKitFactory delegate;

	private SpringRenderKitFactory factory;

	private String renderKitId = "renderKitId";

	@Mock
	private RenderKit renderKit;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		factory = new SpringRenderKitFactory(delegate);
	}

	@Test
	public void shouldWrapDelegate() throws Exception {
		SpringRenderKitFactory factory = new SpringRenderKitFactory(delegate);
		assertSame(delegate, factory.getWrapped());
	}

	@Test
	public void shouldWrapAddedRenderKit() throws Exception {
		factory.addRenderKit(renderKitId, renderKit);
		ArgumentCaptor<RenderKit> renderKitCaptor = ArgumentCaptor.forClass(RenderKit.class);
		verify(delegate).addRenderKit(eq(renderKitId), renderKitCaptor.capture());
		assertTrue(renderKitCaptor.getValue() instanceof SpringRenderKit);
		assertSame(renderKit, ((SpringRenderKit) renderKitCaptor.getValue()).getWrapped());
	}

	@Test
	public void shouldNotDoubleWrapSpringRenderKits() throws Exception {
		SpringRenderKit springRenderKit = new SpringRenderKit(renderKitId, renderKit);
		factory.addRenderKit(renderKitId, springRenderKit);
		ArgumentCaptor<RenderKit> renderKitCaptor = ArgumentCaptor.forClass(RenderKit.class);
		verify(delegate).addRenderKit(eq(renderKitId), renderKitCaptor.capture());
		assertSame(renderKit, ((SpringRenderKit) renderKitCaptor.getValue()).getWrapped());
	}
}
