package org.springframework.springfaces.mvc.model;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIViewRoot;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests for {@link SpringFacesModel}.
 * 
 * @author Phillip Webb
 */
public class SpringFacesModelTest {

	@Rule
	public ExpectedException thown = ExpectedException.none();

	@Test
	public void shouldRequireExistingModel() throws Exception {
		thown.expect(IllegalArgumentException.class);
		thown.expectMessage("Source must not be null");
		new SpringFacesModel(null);
	}

	@Test
	public void shouldCreateFromExistingModel() throws Exception {
		Map<String, String> source = new HashMap<String, String>();
		source.put("k", "v");
		SpringFacesModel model = new SpringFacesModel(source);
		assertEquals("v", model.get("k"));
	}

	@Test
	public void shouldNeedViewRootForSave() throws Exception {
		thown.expect(IllegalArgumentException.class);
		thown.expectMessage("ViewRoot must not be null");
		SpringFacesModel.saveInViewScope(null, mock(SpringFacesModel.class));
	}

	@Test
	public void shouldNeedModelForSave() throws Exception {
		thown.expect(IllegalArgumentException.class);
		thown.expectMessage("Model must not be null");
		SpringFacesModel.saveInViewScope(mock(UIViewRoot.class), null);
	}

	@Test
	public void shouldSaveAndLoadUsingViewScope() throws Exception {
		SpringFacesModel model = new SpringFacesModel();
		model.put("k", "v");
		UIViewRoot viewRoot = mock(UIViewRoot.class);
		Map<String, Object> viewMap = new HashMap<String, Object>();
		given(viewRoot.getViewMap()).willReturn(viewMap);
		SpringFacesModel.saveInViewScope(viewRoot, model);
		SpringFacesModel loaded = SpringFacesModel.loadFromViewScope(viewRoot);
		assertEquals(model, loaded);
	}

	@Test
	public void shouldLoadNullFromNullViewRoot() throws Exception {
		assertNull(SpringFacesModel.loadFromViewScope(null));
	}
}
