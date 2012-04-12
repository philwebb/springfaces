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
		this.stateHolders.add(stateHolder);
	}

	/**
	 * Remove the specified state holder from the collection
	 * @param stateHolder the state holder to remove
	 * @return <tt>true</tt> if this list contained the specified element
	 */
	public boolean remove(T stateHolder) {
		Assert.notNull(stateHolder, "StateHolder must not be null");
		clearInitialState();
		return this.stateHolders.remove(stateHolder);
	}

	public boolean isTransient() {
		return this.transientValue;
	}

	public void setTransient(boolean newTransientValue) {
		this.transientValue = newTransientValue;
		for (StateHolder stateHolder : this.stateHolders) {
			stateHolder.setTransient(newTransientValue);
		}
	}

	public void markInitialState() {
		this.initialState = true;
		for (Iterator<PartialStateHolder> iterator = iterator(PartialStateHolder.class); iterator.hasNext();) {
			iterator.next().markInitialState();
		}
	}

	public void clearInitialState() {
		this.initialState = false;
		for (Iterator<PartialStateHolder> iterator = iterator(PartialStateHolder.class); iterator.hasNext();) {
			iterator.next().clearInitialState();
		}
	}

	public boolean initialStateMarked() {
		return this.initialState;
	}

	public Object saveState(FacesContext context) {
		boolean hasState = false;
		Object[] stateArray = new Object[this.stateHolders.size()];
		for (int i = 0; i < stateArray.length; i++) {
			StateHolder stateHolder = this.stateHolders.get(i);
			if (this.initialState) {
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
					StateHolder stateHolder = this.stateHolders.get(i);
					stateHolder.restoreState(context, stateArray[i]);
				}
			} else {
				this.stateHolders.clear();
				for (int i = 0; i < stateArray.length; i++) {
					SavedAttachedState savedAttachedState = (SavedAttachedState) stateArray[i];
					Object restored = UIComponentBase.restoreAttachedState(context, savedAttachedState.getState());
					this.stateHolders.add((T) restored);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <C> Iterator<C> iterator(final Class<C> itemClass) {
		return (Iterator<C>) new FilteredIterator<T>(this.stateHolders.iterator()) {
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
		return Collections.unmodifiableList(this.stateHolders);
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
			return this.state;
		}
	}

}
