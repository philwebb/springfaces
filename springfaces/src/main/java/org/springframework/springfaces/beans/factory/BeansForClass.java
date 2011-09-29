package org.springframework.springfaces.beans.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.springfaces.util.FilteredIterator;
import org.springframework.util.Assert;

public class BeansForClass {

	private ForClassDeducer deducer;

	public BeansForClass() {
		this.deducer = new NullForClassDeducer();
	}

	public BeansForClass(Class<?> genericType) {
		this(genericType, 0);
	}

	public BeansForClass(Class<?> genericType, int parameterIndex) {
		Assert.notNull(genericType, "GenericType must not be null");
		this.deducer = new GenericTypeForClassDeducer(genericType, parameterIndex);
	}

	public BeansForClass(ForClassDeducer deducer) {
		Assert.notNull(deducer, "Deducer must not be null");
		this.deducer = deducer;
	}

	public <E> Collection<E> getForClass(Collection<E> beanCollection, Class<?> targetClass) {
		Assert.notNull(beanCollection, "BeanCollection must not be null");
		Assert.notNull(targetClass, "TargetClass must not be null");
		List<E> beansForClass = new ArrayList<E>();
		Iterator<E> iterator = getForClass(beanCollection.iterator(), targetClass);
		while (iterator.hasNext()) {
			beansForClass.add(iterator.next());
		}
		return beansForClass;
	}

	public <K, V> Map<K, V> getForClass(Map<K, V> beanMap, Class<?> targetClass) {
		Assert.notNull(beanMap, "BeanMap must not be null");
		Assert.notNull(targetClass, "TargetClass must not be null");
		Map<K, V> beansForClass = new LinkedHashMap<K, V>();
		Iterator<Map.Entry<K, V>> iterator = getForClass(beanMap.entrySet().iterator(), targetClass);
		while (iterator.hasNext()) {
			Entry<K, V> entry = iterator.next();
			beansForClass.put(entry.getKey(), entry.getValue());
		}
		return beansForClass;
	}

	public <E> Iterator<E> getForClass(Iterator<E> beanIterator, final Class<?> targetClass) {
		Assert.notNull(beanIterator, "Beans must not be null");
		Assert.notNull(targetClass, "TargetClass must not be null");
		return new FilteredIterator<E>(beanIterator) {
			protected boolean isElementFiltered(E element) {
				return isForClass(element, targetClass);
			};
		};
	}

	public boolean isForClass(Object bean, Class<?> targetClass) {
		if (bean == null) {
			return false;
		}

		Set<Class<?>> classes = getForClasses(bean);
		boolean beanIsForClass = isAssignableFromAny(targetClass, classes);

		if (!(bean instanceof ConditionalForClass)) {
			return beanIsForClass;
		}

		ConditionalForClass conditionalForClass = (ConditionalForClass) bean;
		// Only call the conditional if we match one of the classes or there was no specific classes to match against
		if (beanIsForClass || classes.isEmpty()) {
			return conditionalForClass.isForClass(targetClass);
		}
		return false;
	}

	private Set<Class<?>> getForClasses(Object bean) {
		Set<Class<?>> forClasses = new HashSet<Class<?>>();

		Class<?> deduced = deducer.deduce(bean);
		if (deduced != null) {
			forClasses.add(deduced);
		}

		ForClass annotation = AnnotationUtils.findAnnotation(bean.getClass(), ForClass.class);
		if (annotation != null) {
			forClasses.addAll(Arrays.asList(annotation.value()));
			Assert.state(forClasses.size() > 0, "Unable to determine classes for use with " + bean.getClass().getName());
		}
		return forClasses;
	}

	private boolean isAssignableFromAny(Class<?> type, Set<Class<?>> candidates) {
		if (candidates.contains(type)) {
			return true;
		}
		for (Class<?> candidate : candidates) {
			if (candidate.isAssignableFrom(type)) {
				return true;
			}
		}
		return false;
	}

	public static interface ForClassDeducer {
		Class<?> deduce(Object bean);
	}

	private static class NullForClassDeducer implements ForClassDeducer {
		public Class<?> deduce(Object bean) {
			return null;
		}
	}

	private static class GenericTypeForClassDeducer implements ForClassDeducer {

		private Class<?> genericType;

		private int parameterIndex;

		public GenericTypeForClassDeducer(Class<?> genericType, int parameterIndex) {
			this.genericType = genericType;
			this.parameterIndex = parameterIndex;
		}

		public Class<?> deduce(Object bean) {
			try {
				Class[] arguments = GenericTypeResolver.resolveTypeArguments(bean.getClass(), genericType);
				if (parameterIndex < arguments.length) {
					return arguments[parameterIndex];
				}
				return null;
			} catch (Exception e) {
				return null;
			}
		}
	}
}
