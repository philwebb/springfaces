package org.springframework.springfaces.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.PartialStateHolder;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import org.springframework.util.Assert;

/**
 * Maintains a collection of {@link StateHolder}s, including support for partial state saving of the contained items.
 * 
 * @author Phillip Webb
 * 
 * @param <T> The state holder type
 */
public class StateHolders<T extends StateHolder> implements PartialStateHolder, Iterable<T> {

	private List<T> stateHolders = new ArrayList<T>();

	private boolean transientValue;

	private boolean initialState;

	/**
	 * Add a new state holder to the collection.
	 * @param stateHolder the state holder to add
	 */
	public void add(T stateHolder) {
		Assert.notNull(stateHolder, "StateHolder must not be null");
		clearInitialState();
		stateHolders.add(stateHolder);
	}

	/**
	 * Remove the specified state holder from the collection
	 * @param stateHolder the state holder to remove
	 * @return <tt>true</tt> if this list contained the specified element
	 */
	public boolean remove(T stateHolder) {
		Assert.notNull(stateHolder, "StateHolder must not be null");
		clearInitialState();
		return stateHolders.remove(stateHolder);
	}

	public boolean isTransient() {
		return transientValue;
	}

	public void setTransient(boolean newTransientValue) {
		transientValue = newTransientValue;
		for (StateHolder stateHolder : stateHolders) {
			stateHolder.setTransient(newTransientValue);
		}
	}

	public void markInitialState() {
		initialState = true;
		for (Iterator<PartialStateHolder> iterator = iterator(PartialStateHolder.class); iterator.hasNext();) {
			iterator.next().markInitialState();
		}
	}

	public void clearInitialState() {
		initialState = false;
		for (Iterator<PartialStateHolder> iterator = iterator(PartialStateHolder.class); iterator.hasNext();) {
			iterator.next().clearInitialState();
		}
	}

	public boolean initialStateMarked() {
		return initialState;
	}

	public Object saveState(FacesContext context) {
		boolean hasState = false;
		Object[] stateArray = new Object[stateHolders.size()];
		for (int i = 0; i < stateArray.length; i++) {
			StateHolder stateHolder = stateHolders.get(i);
			if (initialState) {
				if (!stateHolder.isTransient()) {
					stateArray[i] = stateHolder.saveState(context);
					hasState = true;
				}
			} else {
				stateArray[i] = new SavedAttachedState(UIComponentBase.saveAttachedState(context, stateHolder));
				hasState = true;
			}
		}
		if (!hasState) {
			return null;
		}
		return stateArray;
	}

	@SuppressWarnings("unchecked")
	public void restoreState(FacesContext context, Object state) {
		if (state != null) {
			Object[] stateArray = (Object[]) state;
			boolean savedFromInitialState = !(stateArray.length > 0 && stateArray[0] instanceof SavedAttachedState);
			if (savedFromInitialState) {
				for (int i = 0; i < stateArray.length; i++) {
					StateHolder stateHolder = stateHolders.get(i);
					stateHolder.restoreState(context, stateArray[i]);
				}
			} else {
				stateHolders.clear();
				for (int i = 0; i < stateArray.length; i++) {
					SavedAttachedState savedAttachedState = (SavedAttachedState) stateArray[i];
					Object restored = UIComponentBase.restoreAttachedState(context, savedAttachedState.getState());
					stateHolders.add((T) restored);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <C> Iterator<C> iterator(final Class<C> itemClass) {
		return (Iterator<C>) new FilteredIterator<T>(stateHolders.iterator()) {
			@Override
			protected boolean isElementFiltered(T element) {
				return !(itemClass.isInstance(element));
			}
		};
	}

	public Iterator<T> iterator() {
		return asList().iterator();
	}

	/**
	 * Returns the contained state holder as an unmodifiable {@link List}.
	 * @return an unmodifiable {@link List} of the contained state holders
	 */
	public List<T> asList() {
		return Collections.unmodifiableList(stateHolders);
	}

	/**
	 * Internal holder for fully saved state.
	 */
	static class SavedAttachedState implements Serializable {

		private Object state;

		public SavedAttachedState(Object state) {
			this.state = state;
		}

		public Object getState() {
			return state;
		}
	}

}
