package org.springframework.springfaces.selectitems.ui;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.ReflectionUtils.MethodCallback;

/**
 * Support class that is used to dynamically enhance functionality when JPA is available.
 * 
 * @author Phillip Webb
 */
abstract class SelectItemsJpaSupport {

	/**
	 * Return the entity ID of the specified <tt>value</tt> or </tt>null</tt> if the value is not an entity or does not
	 * contain an ID.
	 * @param value the value to get the ID from
	 * @return The entity ID or <tt>null</tt>
	 */
	public abstract Object getEntityId(Object value);

	private static boolean hasJpa = ClassUtils.isPresent("javax.persistence.Entity",
			SelectItemsJpaSupport.class.getClassLoader());

	private static SelectItemsJpaSupport instance;

	public static SelectItemsJpaSupport getInstance() {
		if (instance == null) {
			instance = (hasJpa ? new HasJpa() : new NoJpa());
		}
		return instance;
	}

	/**
	 * Override if JPA is available. This is primarily to aid testing.
	 * @param hasJpa if JPA is available.
	 */
	static void setHasJpa(boolean hasJpa) {
		SelectItemsJpaSupport.hasJpa = hasJpa;
		instance = null;
	}

	private static class NoJpa extends SelectItemsJpaSupport {
		@Override
		public String getEntityId(Object value) {
			return null;
		}
	}

	private static class HasJpa extends SelectItemsJpaSupport {

		private Map<Class<?>, Object> cache = new HashMap<Class<?>, Object>();

		@Override
		public Object getEntityId(Object value) {
			try {
				Class<? extends Object> valueClass = value.getClass();
				if (AnnotationUtils.findAnnotation(valueClass, Entity.class) != null) {
					addCacheOfMissing(valueClass);
					Object fieldOrMethod = this.cache.get(valueClass);
					if (fieldOrMethod instanceof Field) {
						return ReflectionUtils.getField((Field) fieldOrMethod, value);
					}
					if (fieldOrMethod instanceof Method) {
						return ReflectionUtils.invokeMethod((Method) fieldOrMethod, value);
					}
				}
			} catch (Exception e) {
			}
			return null;
		}

		private void addCacheOfMissing(final Class<?> valueClass) {
			if (!this.cache.containsKey(valueClass)) {
				addIdFieldToCache(valueClass);
			}
			if (!this.cache.containsKey(valueClass)) {
				addIdMethodToCache(valueClass);
			}
			if (!this.cache.containsKey(valueClass)) {
				this.cache.put(valueClass, null);
			}
		}

		private void addIdFieldToCache(final Class<?> valueClass) {
			ReflectionUtils.doWithFields(valueClass, new FieldCallback() {
				public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
					if (field.getAnnotation(Id.class) != null) {
						field.setAccessible(true);
						HasJpa.this.cache.put(valueClass, field);
					}
				}
			});
		}

		private void addIdMethodToCache(final Class<?> valueClass) {
			ReflectionUtils.doWithMethods(valueClass, new MethodCallback() {
				public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
					if (AnnotationUtils.getAnnotation(method, Id.class) != null) {
						method.setAccessible(true);
						HasJpa.this.cache.put(valueClass, method);
					}
				}
			});
		}
	}
}
