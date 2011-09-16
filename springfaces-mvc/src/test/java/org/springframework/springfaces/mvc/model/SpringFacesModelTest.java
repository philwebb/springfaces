package org.springframework.springfaces.mvc.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;

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
	@SuppressWarnings("cast")
	public void shouldSupportSpringTypes() throws Exception {
		SpringFacesModel model = new SpringFacesModel();
		assertTrue(model instanceof ExtendedModelMap);
		assertTrue(model instanceof ModelMap);
		assertTrue(model instanceof Model);
		assertTrue(model instanceof Map);
	}
}
