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
import org.springframework.springfaces.mvc.render.FacesViewStateHandler;
import org.springframework.springfaces.mvc.render.ModelAndViewArtifact;
import org.springframework.springfaces.mvc.render.ViewArtifact;

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
		this.stateManager = new MvcResponseStateManager(this.delegate, this.stateHandler);
	}

	@After
	public void cleanup() {
		SpringFacesContextSetter.setCurrentInstance(null);
	}

	@Test
	public void shouldDelegateWriteState() throws Exception {
		this.stateManager.writeState(this.context, this.state);
		verify(this.delegate).writeState(this.context, this.state);
	}

	@Test
	public void shouldCallStateHandler() throws Exception {
		SpringFacesContextSetter.setCurrentInstance(this.springFacesContext);
		ViewArtifact viewArtifact = new ViewArtifact("page.xhtml");
		ModelAndViewArtifact rendering = new ModelAndViewArtifact(viewArtifact, null);
		given(this.springFacesContext.getRendering()).willReturn(rendering);
		this.stateManager.setRenderKitId("HTML_BASIC");
		this.stateManager.writeState(this.context, this.state);
		verify(this.stateHandler).write(this.context, viewArtifact);
		verify(this.delegate).writeState(this.context, this.state);
	}
}
