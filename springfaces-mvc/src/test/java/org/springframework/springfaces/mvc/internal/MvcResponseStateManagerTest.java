package org.springframework.springfaces.mvc.internal;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import javax.faces.context.FacesContext;
import javax.faces.render.ResponseStateManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.springfaces.mvc.SpringFacesContextSetter;
import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.render.FacesViewStateHandler;
import org.springframework.springfaces.render.ModelAndViewArtifact;
import org.springframework.springfaces.render.ViewArtifact;

/**
 * Tests for {@link MvcResponseStateManager}.
 * 
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class MvcResponseStateManagerTest {

	private MvcResponseStateManager stateManager;

	@Mock
	private ResponseStateManager delegate;

	@Mock
	private FacesViewStateHandler stateHandler;

	@Mock
	private Object state;

	@Mock
	private FacesContext context;

	@Mock
	private SpringFacesContext springFacesContext;

	@Before
	public void setup() {
		stateManager = new MvcResponseStateManager(delegate, stateHandler);
	}

	@After
	public void cleanup() {
		SpringFacesContextSetter.setCurrentInstance(null);
	}

	@Test
	public void shouldDelegateWriteState() throws Exception {
		stateManager.writeState(context, state);
		verify(delegate).writeState(context, state);
	}

	@Test
	public void shouldCallStateHandler() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(springFacesContext);
		ViewArtifact viewArtifact = new ViewArtifact("page.xhtml");
		ModelAndViewArtifact rendering = new ModelAndViewArtifact(viewArtifact, null);
		given(springFacesContext.getRendering()).willReturn(rendering);
		stateManager.setRenderKitId("HTML_BASIC");
		stateManager.writeState(context, state);
		verify(stateHandler).write(context, viewArtifact);
		verify(delegate).writeState(context, state);
	}
}
