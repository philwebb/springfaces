package org.springframework.springfaces.internal;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.springfaces.FacesContextSetter;
import org.springframework.springfaces.component.SpringFacesMocks;
import org.springframework.web.context.WebApplicationContext;

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
	private FacesContext facesContext;

	@Mock
	private RenderKit renderKit;

	@Mock
	private WebApplicationContext applicationContext;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		factory = new SpringRenderKitFactory(delegate);
		FacesContextSetter.setCurrentInstance(facesContext);
		SpringFacesMocks.setupSpringFacesIntegration(facesContext, applicationContext);
	}

	@After
	public void teardown() {
		FacesContextSetter.setCurrentInstance(null);
	}

	@Test
	public void shouldWrapDelegate() throws Exception {
		SpringRenderKitFactory factory = new SpringRenderKitFactory(delegate);
		assertSame(delegate, factory.getWrapped());
	}

	@Test
	public void shouldWrapExistingRenderKit() throws Exception {
		List<String> renderKitIds = Arrays.asList("a", "b");
		given(delegate.getRenderKitIds()).willReturn(renderKitIds.iterator());
		given(delegate.getRenderKit(facesContext, "b")).willReturn(renderKit);
		new SpringRenderKitFactory(delegate);
		ArgumentCaptor<RenderKit> renderKitCaptor = ArgumentCaptor.forClass(RenderKit.class);
		verify(delegate).addRenderKit(eq("b"), renderKitCaptor.capture());
		assertTrue(renderKitCaptor.getValue() instanceof SpringRenderKit);
		assertSame(renderKit, ((SpringRenderKit) renderKitCaptor.getValue()).getWrapped());
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
