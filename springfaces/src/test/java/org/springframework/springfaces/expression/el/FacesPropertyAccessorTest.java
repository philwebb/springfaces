package org.springframework.springfaces.expression.el;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import javax.el.ELContext;
import javax.faces.context.FacesContext;

import org.junit.After;
import org.junit.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.springfaces.FacesContextSetter;

/**
 * Test for {@link FacesPropertyAccessor}.
 * 
 * @author Phillip Webb
 */
public class FacesPropertyAccessorTest {

	private FacesPropertyAccessor facesPropertyAccessor = new FacesPropertyAccessor();
	private EvaluationContext context = mock(EvaluationContext.class);
	private Object target = new Object();

	@After
	public void cleanupFacesContext() {
		FacesContextSetter.setCurrentInstance(null);
	}

	@Test
	public void shouldWorkWithoutFacesContext() throws Exception {
		assertNull(this.facesPropertyAccessor.getElContext(this.context, this.target));
	}

	@Test
	public void shouldGetElContextFromFacesContext() throws Exception {
		FacesContext facesContext = mock(FacesContext.class);
		FacesContextSetter.setCurrentInstance(facesContext);
		ELContext elContext = mock(ELContext.class);
		given(facesContext.getELContext()).willReturn(elContext);
		assertSame(elContext, this.facesPropertyAccessor.getElContext(this.context, elContext));
	}
}
