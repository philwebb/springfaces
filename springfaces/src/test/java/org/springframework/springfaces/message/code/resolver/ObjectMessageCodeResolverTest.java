package org.springframework.springfaces.message.code.resolver;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

/**
 * Tests for {@link ObjectMessageCodeResolver}.
 * 
 * @author Phillip Webb
 */
public class ObjectMessageCodeResolverTest {

	private static final String INNER = ObjectMessageCodeResolverTest.class.getName() + "$";

	private ObjectMessageCodeResolver resolver = new ObjectMessageCodeResolver();

	@Test
	public void shouldGetMessageCodesForType() throws Exception {
		List<String> codes = resolver.getMessageCodesForType(ExampleClass.class);
		assertThat(codes.size(), is(1));
		assertThat(codes.get(0), is(INNER + "ExampleClass"));
	}

	@Test
	public void shouldGetMessageCodesForObject() throws Exception {
		List<String> codes = resolver.getMessageCodesForObject(new ExampleClass());
		assertThat(codes.size(), is(1));
		assertThat(codes.get(0), is(INNER + "ExampleClass"));
	}

	private static class ExampleClass {
	}
}
