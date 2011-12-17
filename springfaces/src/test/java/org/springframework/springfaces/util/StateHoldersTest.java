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
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("StateHolder must not be null");
		stateHolders.add(null);
	}

	@Test
	public void shouldAdd() throws Exception {
		stateHolders.add(stateHolder);
		assertThat(stateHolders.asList().get(0), is(sameInstance(stateHolder)));
	}

	@Test
	public void shouldClearInitialStateOnAdd() throws Exception {
		stateHolders.markInitialState();
		stateHolders.add(stateHolder);
		assertThat(stateHolders.initialStateMarked(), is(false));
	}

	@Test
	public void shouldNotRemoveNull() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("StateHolder must not be null");
		stateHolders.remove(null);
	}

	@Test
	public void shouldRemove() throws Exception {
		stateHolders.add(stateHolder);
		boolean removed = stateHolders.remove(stateHolder);
		assertThat(removed, is(true));
		assertThat(stateHolders.asList().isEmpty(), is(true));
	}

	@Test
	public void shouldClearInitialStateOnRemove() throws Exception {
		stateHolders.add(stateHolder);
		stateHolders.markInitialState();
		stateHolders.remove(stateHolder);
		assertThat(stateHolders.initialStateMarked(), is(false));
	}

	@Test
	public void shouldSetTransient() throws Exception {
		assertThat(stateHolders.isTransient(), is(false));
		stateHolders.setTransient(true);
		assertThat(stateHolders.isTransient(), is(true));
	}

	@Test
	public void shouldPropagateTransient() throws Exception {
		stateHolders.add(stateHolder);
		stateHolders.setTransient(true);
		verify(stateHolder).setTransient(true);
	}

	@Test
	public void shouldMarkInitialState() throws Exception {
		assertThat(stateHolders.initialStateMarked(), is(false));
		stateHolders.markInitialState();
		assertThat(stateHolders.initialStateMarked(), is(true));
	}

	@Test
	public void shouldPropagateMarkInitialState() throws Exception {
		stateHolders.add(stateHolder);
		stateHolders.add(partialStateHolder);
		stateHolders.markInitialState();
		verify(partialStateHolder).markInitialState();
	}

	@Test
	public void shouldClearInitialState() throws Exception {
		stateHolders.markInitialState();
		assertThat(stateHolders.initialStateMarked(), is(true));
		stateHolders.clearInitialState();
		assertThat(stateHolders.initialStateMarked(), is(false));
	}

	@Test
	public void shouldPropagateClearInitialState() throws Exception {
		stateHolders.add(stateHolder);
		stateHolders.add(partialStateHolder);
		stateHolders.clearInitialState();
		verify(partialStateHolder).clearInitialState();
	}

	@Test
	public void shouldSaveAndRestoreWhenInitialState() throws Exception {
		stateHolders.add(partialStateHolder);
		Object state = new Object();
		given(partialStateHolder.saveState(context)).willReturn(state);
		stateHolders.markInitialState();
		Object saved = stateHolders.saveState(context);
		stateHolders.restoreState(context, saved);
		verify(partialStateHolder).restoreState(context, state);
	}

	@Test
	public void shouldNotSaveTransientStateHolder() throws Exception {
		stateHolders.add(partialStateHolder);
		given(partialStateHolder.isTransient()).willReturn(true);
		stateHolders.markInitialState();
		Object saved = stateHolders.saveState(context);
		assertThat(saved, is(nullValue()));
		verify(partialStateHolder, never()).saveState(context);
	}

	@Test
	public void shouldSaveAndRestoreWhenNotInitialState() throws Exception {
		RealStateHolder realStateHolder = new RealStateHolder();
		Object state = new Integer(1);
		realStateHolder.setState(state);
		stateHolders.add(realStateHolder);
		Object saved = stateHolders.saveState(context);
		stateHolders = new StateHolders<StateHolder>();
		stateHolders.restoreState(context, saved);
		RealStateHolder restored = (RealStateHolder) stateHolders.asList().get(0);
		assertThat(restored, is(not(sameInstance(realStateHolder))));
		assertThat(restored.getState(), is(equalTo(state)));
	}

	@Test
	public void shouldRestoreNull() throws Exception {
		stateHolders.restoreState(context, null);
		assertThat(stateHolders.asList().isEmpty(), is(true));

	}

	@Test
	public void shouldIterate() throws Exception {
		stateHolders.add(stateHolder);
		stateHolders.add(partialStateHolder);
		Iterator<StateHolder> iterator = stateHolders.iterator();
		assertThat(iterator.hasNext(), is(true));
		assertThat(iterator.next(), is(stateHolder));
		assertThat(iterator.hasNext(), is(true));
		assertThat(iterator.next(), is((StateHolder) partialStateHolder));
		assertThat(iterator.hasNext(), is(false));
	}

	@Test
	public void shouldGetAsList() throws Exception {
		stateHolders.add(stateHolder);
		stateHolders.add(partialStateHolder);
		List<StateHolder> asList = stateHolders.asList();
		assertThat(asList, is(equalTo(Arrays.asList(stateHolder, partialStateHolder))));
	}

	public static class RealStateHolder implements StateHolder {

		private boolean transientValue;
		private Object state;

		public Object getState() {
			return state;
		}

		public void setState(Object state) {
			this.state = state;
		}

		public Object saveState(FacesContext context) {
			return state;
		}

		public void restoreState(FacesContext context, Object state) {
			this.state = state;
		}

		public boolean isTransient() {
			return transientValue;
		}

		public void setTransient(boolean newTransientValue) {
			transientValue = newTransientValue;
		}
	}
}
