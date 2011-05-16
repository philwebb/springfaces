package org.springframework.springfaces.render;

import javax.faces.render.ResponseStateManager;

import org.springframework.springfaces.FacesWrapperFactory;

/**
 * Interface to be implemented by {@link ResponseStateManager}s that wish to be aware of the JSF <tt>renderKitId</tt>
 * being used. NOTE: Only {@link ResponseStateManager}s created from a {@link FacesWrapperFactory} will receive this
 * callback.
 * 
 * @author Phillip Webb
 */
public interface RenderKitIdAware {

	/**
	 * Callback that supplies the <tt>renderKitId</tt>.
	 * @param renderKitId the render kit ID
	 */
	void setRenderKitId(String renderKitId);
}
