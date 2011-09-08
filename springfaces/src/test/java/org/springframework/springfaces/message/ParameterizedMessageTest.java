package org.springframework.springfaces.message;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ParameterizedMessageTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private ParameterizedMessage.ParamaterResolver resolver = new ParameterizedMessage.ParamaterResolver() {
		public String resolve(String variable) {
			return variable;
		}
	};

	private ParameterizedMessage.ParamaterResolver nullResolver = new ParameterizedMessage.ParamaterResolver() {
		public String resolve(String variable) {
			return null;
		}
	};

	@Test
	public void shouldDetectParameters() throws Exception {
		assertFalse(ParameterizedMessage.isParameterized("a { b"));
		assertFalse(ParameterizedMessage.isParameterized("a {} b"));
		assertFalse(ParameterizedMessage.isParameterized("a } b"));
		assertFalse(ParameterizedMessage.isParameterized(null));
		assertTrue(ParameterizedMessage.isParameterized("a {b} c"));
	}

	@Test
	public void shouldGetEventWhenNotParameterized() throws Exception {
		ParameterizedMessage m = ParameterizedMessage.get("a {} b");
		assertThat(m.resolve(resolver), is("a {} b"));
	}

	@Test
	public void shouldResolve() throws Exception {
		ParameterizedMessage m = ParameterizedMessage.get("a {b} c {d}");
		assertThat(m.resolve(resolver), is("a b c d"));
	}

	@Test
	public void shouldNeedToGet() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Message must not be null");
		ParameterizedMessage.get(null);
	}

	@Test
	public void shouldNeedResolverToResolve() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Resolver must not be null");
		ParameterizedMessage.get("a b").resolve(null);
	}

	@Test
	public void shouldReturnUnchangedIfCannotResolve() throws Exception {
		ParameterizedMessage m = ParameterizedMessage.get("a {b} c {d}");
		assertThat(m.resolve(nullResolver), is("a {b} c {d}"));
	}

}
