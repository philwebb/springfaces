package org.springframework.springfaces.util;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Tests for {@link FacesVendor}.
 * 
 * @author Phillip Webb
 */
public class FacesVendorTest {

	@Test
	public void shouldHaveMyFacesPresent() throws Exception {
		assertThat(FacesVendor.MYFACES.isPresent(), is(true));
	}

	@Test
	public void shouldHaveMojarraPresent() throws Exception {
		assertThat(FacesVendor.MOJARRA.isPresent(), is(true));
	}

	@Test
	public void shouldHaveMojarraAsDefault() throws Exception {
		assertThat(FacesVendor.getCurrent(), is(FacesVendor.MOJARRA));
	}
}
