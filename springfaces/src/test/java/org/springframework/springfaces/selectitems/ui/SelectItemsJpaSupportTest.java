package org.springframework.springfaces.selectitems.ui;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.junit.After;
import org.junit.Test;

public class SelectItemsJpaSupportTest {

	@After
	public void resetHasJpa() {
		SelectItemsJpaSupport.setHasJpa(true);
	}

	@Test
	public void shouldReturnNullIfNotEntity() throws Exception {
		Object value = new NotEntity();
		Object entityId = SelectItemsJpaSupport.getInstance().getEntityId(value);
		assertThat(entityId, is(nullValue()));
	}

	@Test
	public void shouldReturnNullIfEntityWithoutId() throws Exception {
		Object value = new EntityWithoutId();
		Object entityId = SelectItemsJpaSupport.getInstance().getEntityId(value);
		assertThat(entityId, is(nullValue()));
	}

	@Test
	public void shouldReturnIdField() throws Exception {
		Object value = new EntityWithIdField();
		Object entityId = SelectItemsJpaSupport.getInstance().getEntityId(value);
		assertThat(entityId, is(equalTo((Object) 100L)));
	}

	@Test
	public void shouldReturnIdMethod() throws Exception {
		Object value = new EntityWithIdMethod();
		Object entityId = SelectItemsJpaSupport.getInstance().getEntityId(value);
		assertThat(entityId, is(equalTo((Object) 100)));
	}

	@Test
	public void shouldReturnNullIfNoJpa() throws Exception {
		Object value = new EntityWithIdField();
		SelectItemsJpaSupport.setHasJpa(false);
		Object entityId = SelectItemsJpaSupport.getInstance().getEntityId(value);
		assertThat(entityId, is(nullValue()));
	}

	static class NotEntity {
	}

	@Entity
	static class EntityWithoutId {
	}

	@Entity
	static class EntityWithIdField {
		@Id
		@SuppressWarnings("unused")
		private Long id = 100L;
	}

	@Entity
	static class EntityWithIdMethod {
		@Id
		public int getId() {
			return 100;
		}

	}

}
