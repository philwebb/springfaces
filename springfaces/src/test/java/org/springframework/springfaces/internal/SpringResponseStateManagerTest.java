package org.springframework.springfaces.internal;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.withSettings;

import javax.faces.render.ResponseStateManager;

import org.junit.Test;
import org.springframework.springfaces.FacesWrapperFactory;
import org.springframework.springfaces.render.RenderKitIdAware;

/**
 * Tests for {@link SpringResponseStateManager}.
 * 
 * @author Phillip Webb
 */
public class SpringResponseStateManagerTest extends
		AbstractFacesWrapperTest<ResponseStateManager, SpringResponseStateManager> {

	@Override
	protected SpringResponseStateManager newWrapper(ResponseStateManager delegate) throws Exception {
		return new SpringResponseStateManager("rid", delegate);
	}

	@Test
	public void shouldSetRenderKitId() throws Exception {
		@SuppressWarnings("unchecked")
		FacesWrapperFactory<ResponseStateManager> wrapperFactory = mock(FacesWrapperFactory.class);
		addFactoryWrapper("wrapper", wrapperFactory);
		ResponseStateManager delegate = mock(getTypeClass());
		SpringResponseStateManager wrapper = newWrapper(delegate);
		ResponseStateManager wrapped = mock(getTypeClass(), withSettings().extraInterfaces(RenderKitIdAware.class));
		given(wrapperFactory.newWrapper(getTypeClass(), delegate)).willReturn(wrapped);
		wrapper.getWrapped();
		verify((RenderKitIdAware) wrapped).setRenderKitId("rid");
	}
}
