package org.springframework.springfaces.mvc.expression.el;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import javax.el.ELContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.springfaces.mvc.MockELContext;
import org.springframework.springfaces.mvc.SpringFacesContextSetter;
import org.springframework.springfaces.mvc.context.SpringFacesContext;
import org.springframework.springfaces.mvc.render.ModelAndViewArtifact;
import org.springframework.springfaces.mvc.render.ViewArtifact;
import org.springframework.web.context.WebApplicationContext;

/**
 * Tests for {@link SpringFacesBeanELResolver}.
 * 
 * @author Phillip Webb
 */
@RunWith(MockitoJUnitRunner.class)
public class SpringFacesBeanELResolverTest {

	@Mock
	private SpringFacesContext springFacesContext;

	private SpringFacesBeanELResolver resolver = new SpringFacesBeanELResolver();

	private ELContext elContext = new MockELContext();

	@Before
	public void setup() {
		SpringFacesContextSetter.setCurrentInstance(this.springFacesContext);
	}

	@After
	public void cleanup() {
		SpringFacesContextSetter.setCurrentInstance(null);
	}

	@Test
	public void shouldHaveEmptyBeanFactoryWhenNotRendering() throws Exception {
		BeanFactory factory = this.resolver.getBeanFactory(this.elContext);
		assertEquals(0, ((ListableBeanFactory) factory).getBeanDefinitionCount());
	}

	@Test
	public void shouldGetBeanFactoryFromSpringFacesContext() throws Exception {
		ViewArtifact viewArtifact = new ViewArtifact("artifact");
		ModelAndViewArtifact rendering = new ModelAndViewArtifact(viewArtifact, null);
		given(this.springFacesContext.getRendering()).willReturn(rendering);
		WebApplicationContext applicationContext = mock(WebApplicationContext.class);
		given(this.springFacesContext.getWebApplicationContext()).willReturn(applicationContext);
		assertSame(applicationContext, this.resolver.getBeanFactory(this.elContext));
	}
}
