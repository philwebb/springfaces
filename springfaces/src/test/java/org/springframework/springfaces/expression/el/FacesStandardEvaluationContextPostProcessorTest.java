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
		postProcessor.postProcessStandardEvaluationContext(evaluationContext);
		verify(evaluationContext).addPropertyAccessor(isA(FacesPropertyAccessor.class));
	}
}
