package org.springframework.springfaces.mvc.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.springfaces.mvc.SpringFacesMocks.mockUIViewRootWithModelSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests for {@link SpringFacesModelHolder}.
 * 
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class SpringFacesModelHolderTest {

	private SpringFacesModelHolder h = new SpringFacesModelHolder(null);

	@Mock
	FacesContext context;

	@Test
	public void shouldSupportNullModel() throws Exception {
		assertNull(new SpringFacesModelHolder(null).getModel());
	}

	@Test
	public void shouldCopyModel() throws Exception {
		Map<String, String> m = Collections.singletonMap("k", "v");
		assertEquals("v", new SpringFacesModelHolder(m).getModel().get("k"));
	}

	@Test
	public void shouldHaveStaticComponentId() throws Exception {
		String componentId = h.getId();
		h.setId("madeup");
		assertEquals(componentId, h.getId());
	}

	@Test
	public void shouldHaveSameClientIdAsComponentId() throws Exception {
		assertEquals(h.getId(), h.getClientId());
	}

	@Test
	public void shouldBeParameterFamily() throws Exception {
		assertEquals("javax.faces.Parameter", h.getFamily());
	}

	@Test
	public void shouldHaveNullRenderer() throws Exception {
		assertNull(h.getRenderer());
	}

	@Test
	public void shouldSupportTransient() throws Exception {
		assertFalse(h.isTransient());
		h.setTransient(true);
		assertTrue(h.isTransient());
	}

	@Test
	public void shouldSaveAndRestoreState() throws Exception {
		Map<String, String> m = Collections.singletonMap("k", "v");
		SpringFacesModelHolder h1 = new SpringFacesModelHolder(m);
		Object state = h1.saveState(context);
		h.restoreState(context, state);
		assertEquals("v", new SpringFacesModelHolder(m).getModel().get("k"));
	}

	@Test
	public void shouldAttach() throws Exception {
		Map<String, String> m = Collections.singletonMap("k", "v");
		UIViewRoot viewRoot = mock(UIViewRoot.class);
		List<UIComponent> children = new ArrayList<UIComponent>();
		given(viewRoot.getChildren()).willReturn(children);
		SpringFacesModelHolder.attach(context, viewRoot, m);
		assertEquals("v", ((SpringFacesModelHolder) children.get(0)).getModel().get("k"));
	}

	@Test
	public void shouldGetModelFromNullViewRoot() throws Exception {
		assertNull(SpringFacesModelHolder.getModel(null));
	}

	@Test
	public void shouldGetModelFromEmptyViewRoot() throws Exception {
		UIViewRoot viewRoot = mock(UIViewRoot.class);
		assertNull(SpringFacesModelHolder.getModel(viewRoot));
	}

	@Test
	public void shouldGetModelWhenAttached() throws Exception {
		UIViewRoot viewRoot = mockUIViewRootWithModelSupport();
		Map<String, String> m = Collections.singletonMap("k", "v");
		SpringFacesModelHolder.attach(context, viewRoot, m);
		assertEquals("v", SpringFacesModelHolder.getModel(viewRoot).get("k"));
	}
}
