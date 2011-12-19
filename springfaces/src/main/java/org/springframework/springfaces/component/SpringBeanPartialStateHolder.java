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
		return bean;
	}

	public Object saveState(FacesContext context) {
		Object beanState = null;
		if (bean instanceof StateHolder) {
			StateHolder stateHolder = (StateHolder) bean;
			beanState = stateHolder.saveState(context);
		}
		if (initialState) {
			return beanState;
		}
		return new SavedBeanState(beanName, beanState);
	}

	public void restoreState(FacesContext context, Object state) {
		Object beanState = state;
		if (state instanceof SavedBeanState) {
			SavedBeanState savedBeanState = (SavedBeanState) state;
			beanName = savedBeanState.getBeanName();
			loadBeanFromContext(context);
			beanState = savedBeanState.getBeanState();
		}
		if (beanState != null) {
			((StateHolder) bean).restoreState(context, beanState);
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
		this.bean = (T) applicationContext.getBean(beanName);
		Class<?> beanType = GenericTypeResolver.resolveTypeArgument(getClass(), SpringBeanPartialStateHolder.class);
		if (beanType != null) {
			Assert.isInstanceOf(beanType, bean, "Unable to load bean '" + beanName + "' ");
		}
		if (bean instanceof StateHolder) {
			Assert.state(applicationContext.isPrototype(beanName), "StateHolders must be declared as protoype beans");
		}
	}

	public boolean isTransient() {
		return transientValue;
	}

	public void setTransient(boolean newTransientValue) {
		transientValue = newTransientValue;
	}

	public void markInitialState() {
		if (bean instanceof PartialStateHolder) {
			((PartialStateHolder) bean).markInitialState();
		}
		initialState = true;
	}

	public boolean initialStateMarked() {
		if (bean instanceof PartialStateHolder) {
			return ((PartialStateHolder) bean).initialStateMarked();
		}
		return initialState;
	}

	public void clearInitialState() {
		if (bean instanceof PartialStateHolder) {
			((PartialStateHolder) bean).clearInitialState();
		}
		initialState = false;
	}

	static class SavedBeanState implements Serializable {

		private String beanName;
		private Object beanState;

		public SavedBeanState(String beanName, Object beanState) {
			this.beanName = beanName;
			this.beanState = beanState;
		}

		public String getBeanName() {
			return beanName;
		}

		public Object getBeanState() {
			return beanState;
		}
	}
}
