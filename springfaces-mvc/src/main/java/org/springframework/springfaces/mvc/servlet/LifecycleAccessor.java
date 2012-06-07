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
package org.springframework.springfaces.mvc.servlet;

import javax.faces.FactoryFinder;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.webapp.FacesServlet;
import javax.servlet.ServletContext;

import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Provides cached access to the JSF {@link Lifecycle}. The {@link Lifecycle} is obtained in the same way as standard
 * JSF implementations (respecting any <tt>javax.faces.LIFECYCLE_ID</tt> initiation parameter). The
 * {@link #setLifecycleId(String)} method can be used to override the lifecycle ID.
 * 
 * @author Phillip Webb
 */
public class LifecycleAccessor implements ServletContextAware {

	private ServletContext servletContext;

	private String lifecycleId;

	private Lifecycle cachedLifecycle;

	public void setServletContext(ServletContext servletContext) {
		this.cachedLifecycle = null;
		this.servletContext = servletContext;
	}

	public Lifecycle getLifecycle() {
		Assert.state(this.servletContext != null, "ServletContext has not been set");
		if (this.cachedLifecycle == null) {
			String lifecycleIdToUse = this.lifecycleId;
			LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
					.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
			if (lifecycleIdToUse == null) {
				lifecycleIdToUse = this.servletContext.getInitParameter(FacesServlet.LIFECYCLE_ID_ATTR);
			}
			if (lifecycleIdToUse == null) {
				lifecycleIdToUse = LifecycleFactory.DEFAULT_LIFECYCLE;
			}
			this.cachedLifecycle = lifecycleFactory.getLifecycle(lifecycleIdToUse);
		}
		return this.cachedLifecycle;
	}

	/**
	 * Set the lifecycle identifier to use when {@link LifecycleFactory#getLifecycle(String) creating} the JSF
	 * {@link Lifecycle}. When not specified the <tt>javax.faces.LIFECYCLE_ID</tt> initiation parameter of the
	 * {@link DispatcherServlet} will be used. If no explicit initialization parameter is set the
	 * {@link LifecycleFactory#DEFAULT_LIFECYCLE default} lifecycle identifier will be used.
	 * @param lifecycleId The lifecycle id or <tt>null</tt>
	 */
	public void setLifecycleId(String lifecycleId) {
		this.cachedLifecycle = null;
		this.lifecycleId = lifecycleId;
	}
}
