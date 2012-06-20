/*
 * Copyright 2010-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.springfaces.message;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Locale;

import org.junit.Test;

/**
 * Tests for {@link NoSuchObjectMessageException}.
 * 
 * @author Phillip Webb
 */
public class NoSuchObjectMessageExceptionTest {

	private Object object = new Object();

	private Locale locale = Locale.CANADA;

	@Test
	public void shouldCreateWithObjectAndLocale() throws Exception {
		NoSuchObjectMessageException e = new NoSuchObjectMessageException(this.object, this.locale);
		assertThat(e.getObject(), is(this.object));
		assertThat(e.getLocale(), is(this.locale));
		assertThat(e.getMessage(), is("Unable to convert object of type java.lang.Object to a "
				+ "message for locale en_CA"));
		assertThat(e.getCause(), is(nullValue()));
	}

	@Test
	public void shouldCreateWithObjectLocalAndCause() throws Exception {
		Throwable cause = new Exception();
		NoSuchObjectMessageException e = new NoSuchObjectMessageException(this.object, this.locale, cause);
		assertThat(e.getObject(), is(this.object));
		assertThat(e.getLocale(), is(this.locale));
		assertThat(e.getMessage(), is("Unable to convert object of type java.lang.Object to a "
				+ "message for locale en_CA"));
		assertThat(e.getCause(), is(cause));
	}

}
