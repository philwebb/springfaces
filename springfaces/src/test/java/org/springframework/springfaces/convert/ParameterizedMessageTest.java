package org.springframework.springfaces.convert;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ParameterizedMessageTest {

	private ParameterizedMessage.VariableResolver resolver = new ParameterizedMessage.VariableResolver() {
		public String resolve(String variable) {
			return variable;
		}
	};

	@Test
	public void shouldMatchVariables() throws Exception {
		ParameterizedMessage m = ParameterizedMessage.get("a {b} c {d}");
		assertThat(m.resolve(resolver), is("a b c d"));
	}

}
