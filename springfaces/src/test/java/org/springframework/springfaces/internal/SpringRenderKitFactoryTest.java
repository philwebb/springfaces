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
import org.springframework.springfaces.SpringFacesMocks;
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
		this.factory = new SpringRenderKitFactory(this.delegate);
		FacesContextSetter.setCurrentInstance(this.facesContext);
		SpringFacesMocks.setupSpringFacesIntegration(this.facesContext, this.applicationContext);
	}

	@After
	public void teardown() {
		FacesContextSetter.setCurrentInstance(null);
	}

	@Test
	public void shouldWrapDelegate() throws Exception {
		SpringRenderKitFactory factory = new SpringRenderKitFactory(this.delegate);
		assertSame(this.delegate, factory.getWrapped());
	}

	@Test
	public void shouldWrapExistingRenderKit() throws Exception {
		List<String> renderKitIds = Arrays.asList("a", "b");
		given(this.delegate.getRenderKitIds()).willReturn(renderKitIds.iterator());
		given(this.delegate.getRenderKit(this.facesContext, "b")).willReturn(this.renderKit);
		new SpringRenderKitFactory(this.delegate);
		ArgumentCaptor<RenderKit> renderKitCaptor = ArgumentCaptor.forClass(RenderKit.class);
		verify(this.delegate).addRenderKit(eq("b"), renderKitCaptor.capture());
		assertTrue(renderKitCaptor.getValue() instanceof SpringRenderKit);
		assertSame(this.renderKit, ((SpringRenderKit) renderKitCaptor.getValue()).getWrapped());
	}

	@Test
	public void shouldWrapAddedRenderKit() throws Exception {
		this.factory.addRenderKit(this.renderKitId, this.renderKit);
		ArgumentCaptor<RenderKit> renderKitCaptor = ArgumentCaptor.forClass(RenderKit.class);
		verify(this.delegate).addRenderKit(eq(this.renderKitId), renderKitCaptor.capture());
		assertTrue(renderKitCaptor.getValue() instanceof SpringRenderKit);
		assertSame(this.renderKit, ((SpringRenderKit) renderKitCaptor.getValue()).getWrapped());
	}

	@Test
	public void shouldNotDoubleWrapSpringRenderKits() throws Exception {
		SpringRenderKit springRenderKit = new SpringRenderKit(this.renderKitId, this.renderKit);
		this.factory.addRenderKit(this.renderKitId, springRenderKit);
		ArgumentCaptor<RenderKit> renderKitCaptor = ArgumentCaptor.forClass(RenderKit.class);
		verify(this.delegate).addRenderKit(eq(this.renderKitId), renderKitCaptor.capture());
		assertSame(this.renderKit, ((SpringRenderKit) renderKitCaptor.getValue()).getWrapped());
	}
}
