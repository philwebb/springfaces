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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.PartialStateHolder;
import javax.faces.component.StateHolder;
import javax.faces.context.FacesContext;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StateHoldersTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private StateHolders<StateHolder> stateHolders = new StateHolders<StateHolder>();

	@Mock
	private PartialStateHolder partialStateHolder;

	@Mock
	private StateHolder stateHolder;

	@Mock
	private FacesContext context;

	@Test
	public void shouldNotAddNull() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("StateHolder must not be null");
		this.stateHolders.add(null);
	}

	@Test
	public void shouldAdd() throws Exception {
		this.stateHolders.add(this.stateHolder);
		assertThat(this.stateHolders.asList().get(0), is(sameInstance(this.stateHolder)));
	}

	@Test
	public void shouldClearInitialStateOnAdd() throws Exception {
		this.stateHolders.markInitialState();
		this.stateHolders.add(this.stateHolder);
		assertThat(this.stateHolders.initialStateMarked(), is(false));
	}

	@Test
	public void shouldNotRemoveNull() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("StateHolder must not be null");
		this.stateHolders.remove(null);
	}

	@Test
	public void shouldRemove() throws Exception {
		this.stateHolders.add(this.stateHolder);
		boolean removed = this.stateHolders.remove(this.stateHolder);
		assertThat(removed, is(true));
		assertThat(this.stateHolders.asList().isEmpty(), is(true));
	}

	@Test
	public void shouldClearInitialStateOnRemove() throws Exception {
		this.stateHolders.add(this.stateHolder);
		this.stateHolders.markInitialState();
		this.stateHolders.remove(this.stateHolder);
		assertThat(this.stateHolders.initialStateMarked(), is(false));
	}

	@Test
	public void shouldSetTransient() throws Exception {
		assertThat(this.stateHolders.isTransient(), is(false));
		this.stateHolders.setTransient(true);
		assertThat(this.stateHolders.isTransient(), is(true));
	}

	@Test
	public void shouldPropagateTransient() throws Exception {
		this.stateHolders.add(this.stateHolder);
		this.stateHolders.setTransient(true);
		verify(this.stateHolder).setTransient(true);
	}

	@Test
	public void shouldMarkInitialState() throws Exception {
		assertThat(this.stateHolders.initialStateMarked(), is(false));
		this.stateHolders.markInitialState();
		assertThat(this.stateHolders.initialStateMarked(), is(true));
	}

	@Test
	public void shouldPropagateMarkInitialState() throws Exception {
		this.stateHolders.add(this.stateHolder);
		this.stateHolders.add(this.partialStateHolder);
		this.stateHolders.markInitialState();
		verify(this.partialStateHolder).markInitialState();
	}

	@Test
	public void shouldClearInitialState() throws Exception {
		this.stateHolders.markInitialState();
		assertThat(this.stateHolders.initialStateMarked(), is(true));
		this.stateHolders.clearInitialState();
		assertThat(this.stateHolders.initialStateMarked(), is(false));
	}

	@Test
	public void shouldPropagateClearInitialState() throws Exception {
		this.stateHolders.add(this.stateHolder);
		this.stateHolders.add(this.partialStateHolder);
		this.stateHolders.clearInitialState();
		verify(this.partialStateHolder).clearInitialState();
	}

	@Test
	public void shouldSaveAndRestoreWhenInitialState() throws Exception {
		this.stateHolders.add(this.partialStateHolder);
		Object state = new Object();
		given(this.partialStateHolder.saveState(this.context)).willReturn(state);
		this.stateHolders.markInitialState();
		Object saved = this.stateHolders.saveState(this.context);
		this.stateHolders.restoreState(this.context, saved);
		verify(this.partialStateHolder).restoreState(this.context, state);
	}

	@Test
	public void shouldNotSaveTransientStateHolder() throws Exception {
		this.stateHolders.add(this.partialStateHolder);
		given(this.partialStateHolder.isTransient()).willReturn(true);
		this.stateHolders.markInitialState();
		Object saved = this.stateHolders.saveState(this.context);
		assertThat(saved, is(nullValue()));
		verify(this.partialStateHolder, never()).saveState(this.context);
	}

	@Test
	public void shouldSaveAndRestoreWhenNotInitialState() throws Exception {
		RealStateHolder realStateHolder = new RealStateHolder();
		Object state = new Integer(1);
		realStateHolder.setState(state);
		this.stateHolders.add(realStateHolder);
		Object saved = this.stateHolders.saveState(this.context);
		this.stateHolders = new StateHolders<StateHolder>();
		this.stateHolders.restoreState(this.context, saved);
		RealStateHolder restored = (RealStateHolder) this.stateHolders.asList().get(0);
		assertThat(restored, is(not(sameInstance(realStateHolder))));
		assertThat(restored.getState(), is(equalTo(state)));
	}

	@Test
	public void shouldRestoreNull() throws Exception {
		this.stateHolders.restoreState(this.context, null);
		assertThat(this.stateHolders.asList().isEmpty(), is(true));

	}

	@Test
	public void shouldIterate() throws Exception {
		this.stateHolders.add(this.stateHolder);
		this.stateHolders.add(this.partialStateHolder);
		Iterator<StateHolder> iterator = this.stateHolders.iterator();
		assertThat(iterator.hasNext(), is(true));
		assertThat(iterator.next(), is(this.stateHolder));
		assertThat(iterator.hasNext(), is(true));
		assertThat(iterator.next(), is((StateHolder) this.partialStateHolder));
		assertThat(iterator.hasNext(), is(false));
	}

	@Test
	public void shouldGetAsList() throws Exception {
		this.stateHolders.add(this.stateHolder);
		this.stateHolders.add(this.partialStateHolder);
		List<StateHolder> asList = this.stateHolders.asList();
		assertThat(asList, is(equalTo(Arrays.asList(this.stateHolder, this.partialStateHolder))));
	}

	public static class RealStateHolder implements StateHolder {

		private boolean transientValue;
		private Object state;

		public Object getState() {
			return this.state;
		}

		public void setState(Object state) {
			this.state = state;
		}

		public Object saveState(FacesContext context) {
			return this.state;
		}

		public void restoreState(FacesContext context, Object state) {
			this.state = state;
		}

		public boolean isTransient() {
			return this.transientValue;
		}

		public void setTransient(boolean newTransientValue) {
			this.transientValue = newTransientValue;
		}
	}
}
