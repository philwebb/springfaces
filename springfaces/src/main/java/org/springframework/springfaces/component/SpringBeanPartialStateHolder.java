package org.springframework.springfaces.component;

import javax.faces.component.PartialStateHolder;
import javax.faces.component.StateHolder;
import javax.faces.context.FacesContext;

import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;

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
		if (bean instanceof StateHolder) {
			StateHolder stateHolder = (StateHolder) bean;
			return new Object[] { beanName, stateHolder.saveState(context) };
		}
		return new Object[] { beanName };
	}

	public void restoreState(FacesContext context, Object state) {
		Object[] stateArray = (Object[]) state;
		beanName = (String) stateArray[0];
		loadBeanFromContext(context);
		if (stateArray.length > 1) {
			((StateHolder) bean).restoreState(context, stateArray[1]);
		}
	}

	/**
	 * Loads the bean from the {@link WebApplicationContext} associated with the specified {@link FacesContext}.
	 * @param context the faces context.
	 */
	@SuppressWarnings("unchecked")
	private void loadBeanFromContext(FacesContext context) {
		WebApplicationContext applicationContext = FacesContextUtils.getRequiredWebApplicationContext(context);
		this.bean = (T) applicationContext.getBean(beanName);
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
}
