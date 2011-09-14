package org.springframework.springfaces.message.code.resolver;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

/**
 * Tests for {@link EnumMessageCodeResolver}.
 * 
 * @author Phillip Webb
 */
public class EnumMessageCodeResolverTest {

	private static final String INNER = EnumMessageCodeResolverTest.class.getName() + "$";

	private EnumMessageCodeResolver resolver = new EnumMessageCodeResolver();

	@Test
	public void shouldGetMessageCodesForType() throws Exception {
		List<String> codes = resolver.getMessageCodesForType(ExampleEnum.class);
		assertThat(codes.size(), is(3));
		assertThat(codes.get(0), is(INNER + "ExampleEnum.ONE"));
		assertThat(codes.get(1), is(INNER + "ExampleEnum.TWO"));
		assertThat(codes.get(2), is(INNER + "ExampleEnum.THREE"));
	}

	@Test
	public void shouldGetMessageCodesForObject() throws Exception {
		List<String> codes = resolver.getMessageCodesForObject(ExampleEnum.TWO);
		assertThat(codes.size(), is(1));
		assertThat(codes.get(0), is(INNER + "ExampleEnum.TWO"));
	}

	private static enum ExampleEnum {
		ONE, TWO, THREE
	}
}
