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
package org.springframework.springfaces.expression.el;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Tests for {@link FacesStandardEvaluationContextPostProcessor}
 * 
 * @author Phillip Webb
 */
public class FacesStandardEvaluationContextPostProcessorTest {

	private FacesStandardEvaluationContextPostProcessor postProcessor = new FacesStandardEvaluationContextPostProcessor();

	@Test
	public void shouldApplyFacesPropertyAccessor() throws Exception {
		StandardEvaluationContext evaluationContext = mock(StandardEvaluationContext.class);
		this.postProcessor.postProcessStandardEvaluationContext(evaluationContext);
		verify(evaluationContext).addPropertyAccessor(isA(FacesPropertyAccessor.class));
	}
}
