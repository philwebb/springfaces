package org.springframework.springfaces.mvc.model;

import static junit.framework.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

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
}
