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
package org.springframework.springfaces.component;

import java.io.Serializable;

import javax.faces.component.PartialStateHolder;
import javax.faces.component.StateHolder;
import javax.faces.context.FacesContext;

import org.springframework.context.ApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.springfaces.SpringFacesIntegration;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

/**
 * A {@link PartialStateHolder} that can be used to hold a reference to a Spring Bean. Using this class allows Spring
 * Beans to be used anywhere that the standard {@link StateHolder} is used. The name of the bean stored when state is
 * {@link #saveState saved} and the bean instance will be re-obtained from the {@link ApplicationContext} when state is
 * {@link #restoreState restored}. Referenced prototype beans can also be {@link StateHolder}s if required.
 * 
 * @author Phillip Webb
 * 
 * @param <T> The bean type
 */
public class SpringBeanPartialStateHolder<T> implements PartialStateHolder {

	private String beanName;

	private T bean;

	private boolean transientValue;

	private boolean initialState;

	/**
	 * Constructor to satisfy the {@link StateHolder}. This constructor should not be used directly.
	 * @deprecated use alternative constructor
	 */
	@Deprecated
	public SpringBeanPartialStateHolder() {
	}

	/**
	 * Create a new {@link SpringBeanPartialStateHolder} instance.
	 * @param context
	 * @param beanName
	 */
	public SpringBeanPartialStateHolder(FacesContext context, String beanName) {
		Assert.notNull(context, "Context must not be null");
		Assert.notNull(beanName, "BeanName must not be null");
		this.beanName = beanName;
		loadBeanFromContext(context);
	}

	/**
	 * Returns the Spring bean instance.
	 * @return the bean instance
	 */
	protected final T getBean() {
		return this.bean;
	}

	public Object saveState(FacesContext context) {
		Object beanState = null;
		if (this.bean instanceof StateHolder) {
			StateHolder stateHolder = (StateHolder) this.bean;
			beanState = stateHolder.saveState(context);
		}
		if (this.initialState) {
			return beanState;
		}
		return new SavedBeanState(this.beanName, beanState);
	}

	public void restoreState(FacesContext context, Object state) {
		Object beanState = state;
		if (state instanceof SavedBeanState) {
			SavedBeanState savedBeanState = (SavedBeanState) state;
			this.beanName = savedBeanState.getBeanName();
			loadBeanFromContext(context);
			beanState = savedBeanState.getBeanState();
		}
		if (beanState != null) {
			((StateHolder) this.bean).restoreState(context, beanState);
		}
	}

	/**
	 * Loads the bean from the {@link WebApplicationContext} associated with the specified {@link FacesContext}.
	 * @param context the faces context.
	 */
	@SuppressWarnings("unchecked")
	private void loadBeanFromContext(FacesContext context) {
		ApplicationContext applicationContext = SpringFacesIntegration.getCurrentInstance(context.getExternalContext())
				.getApplicationContext();
		this.bean = (T) applicationContext.getBean(this.beanName);
		Class<?> beanType = GenericTypeResolver.resolveTypeArgument(getClass(), SpringBeanPartialStateHolder.class);
		if (beanType != null) {
			Assert.isInstanceOf(beanType, this.bean, "Unable to load bean '" + this.beanName + "' ");
		}
		if (this.bean instanceof StateHolder) {
			Assert.state(applicationContext.isPrototype(this.beanName),
					"StateHolders must be declared as protoype beans");
		}
	}

	public boolean isTransient() {
		return this.transientValue;
	}

	public void setTransient(boolean newTransientValue) {
		this.transientValue = newTransientValue;
	}

	public void markInitialState() {
		if (this.bean instanceof PartialStateHolder) {
			((PartialStateHolder) this.bean).markInitialState();
		}
		this.initialState = true;
	}

	public boolean initialStateMarked() {
		if (this.bean instanceof PartialStateHolder) {
			return ((PartialStateHolder) this.bean).initialStateMarked();
		}
		return this.initialState;
	}

	public void clearInitialState() {
		if (this.bean instanceof PartialStateHolder) {
			((PartialStateHolder) this.bean).clearInitialState();
		}
		this.initialState = false;
	}

	static class SavedBeanState implements Serializable {

		private String beanName;
		private Object beanState;

		public SavedBeanState(String beanName, Object beanState) {
			this.beanName = beanName;
			this.beanState = beanState;
		}

		public String getBeanName() {
			return this.beanName;
		}

		public Object getBeanState() {
			return this.beanState;
		}
	}
}
