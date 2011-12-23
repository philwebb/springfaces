package org.springframework.springfaces.ui;

import java.io.IOException;

import javax.faces.component.UIComponent;

import org.springframework.util.Assert;

/**
 * Convenient base class that wraps an existing {@link AspectInvocation}.
 * 
 * @author Phillip Webb
 */
public class AspectInvocationWrapper implements AspectInvocation {

	private AspectInvocation aspectInvocation;

	/**
	 * Create a new {@link AspectInvocationWrapper}.
	 * @param aspectInvocation the aspect invocation to wrap
	 */
	public AspectInvocationWrapper(AspectInvocation aspectInvocation) {
		Assert.notNull(aspectInvocation, "AspectInvocation must not be null");
		this.aspectInvocation = aspectInvocation;
	}

	public UIComponent getComponent() {
		return this.aspectInvocation.getComponent();
	}

	public void proceed() throws IOException {
		this.aspectInvocation.proceed();
	}
}
