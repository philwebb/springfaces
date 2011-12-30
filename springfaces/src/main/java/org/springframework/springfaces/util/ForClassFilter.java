package org.springframework.springfaces.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.springfaces.bean.ConditionalForClass;
import org.springframework.springfaces.bean.ForClass;
import org.springframework.util.Assert;

/**
 * Filters objects based on the {@link ForClass @ForClass} annotation or the {@link ConditionalForClass} interface. This
 * object can filter {@link #apply(Collection, Class) collections}, {@link #apply(Iterator, Class) iterators} ,
 * {@link #apply(Map, Class) maps} or {@link #match(Object, Class) single objects}. If not explicitly defined the
 * <tt>{@link ForClass#value() @ForClass.value}</tt> can be deduced either from a {@link #ForClassFilter(Class) generic}
 * or using a {@link #ForClassFilter(Deducer) custom} {@link Deducer}.
 * 
 * @author Phillip Webb
 */
public class ForClassFilter {

	private Deducer deducer;

	/**
	 * Create a new {@link ForClassFilter} instance. No deduction will be attempted if the {@link ForClass} annotation
	 * does not define a value.
	 * @see #ForClassFilter(Deducer)
	 */
	public ForClassFilter() {
		this.deducer = new NullForClassDeducer();
	}

	/**
	 * Create a new {@link ForClassFilter} instance. If not specified the {@link ForClass#value() @ForClass.value} will
	 * be deduced from the specified generic type.
	 * <p>
	 * For example:
	 * 
	 * <pre>
	 * &#064;ForClass 
	 * ForClass public class BeanForClass implements GenericType&lt;Linked&gt; {
	 * }
	 * 
	 * filter = new ForClassFilter(GenericType.class)
	 * </pre>
	 * 
	 * @param genericType the generic type to match
	 * @see #ForClassFilter(Class, int)
	 * @see #ForClassFilter(Deducer)
	 */
	public ForClassFilter(Class<?> genericType) {
		this(genericType, 0);
	}

	/**
	 * Create a new {@link ForClassFilter} instance. If not specified the {@link ForClass#value() @ForClass.value} will
	 * be deduced from indexed parameter the specified generic type. See {@link #ForClassFilter(Class)} for an example.
	 * 
	 * @param genericType the generic type to match
	 * @param parameterIndex the index of the generic parameter to match
	 * @see #ForClassFilter(Class)
	 * @see #ForClassFilter(Deducer)
	 */
	public ForClassFilter(Class<?> genericType, int parameterIndex) {
		Assert.notNull(genericType, "GenericType must not be null");
		this.deducer = new GenericTypeForClassDeducer(genericType, parameterIndex);
	}

	/**
	 * Create a new {@link ForClassFilter} instance. If not specified the {@link ForClass#value() @ForClass.value} will
	 * be deduced using the specified {@link Deducer}.
	 * 
	 * @param deducer the deducer used to determine the <tt>forClass</tt> value
	 */
	public ForClassFilter(Deducer deducer) {
		Assert.notNull(deducer, "Deducer must not be null");
		this.deducer = deducer;
	}

	/**
	 * Apply the filter to the specified {@link Collection} returning only {@link #match(Object, Class) matching}
	 * objects. If the collection contains {@link java.util.Map.Entry map entries} filtering will be applied against the
	 * {@link java.util.Map.Entry#getValue() entry value}.
	 * @param collection the collection to filter
	 * @param targetClass the target class to match
	 * @return a filtered collection
	 */
	public <E> Collection<E> apply(Collection<E> collection, Class<?> targetClass) {
		Assert.notNull(collection, "Collection must not be null");
		Assert.notNull(targetClass, "TargetClass must not be null");
		List<E> matched = new ArrayList<E>();
		Iterator<E> iterator = apply(collection.iterator(), targetClass);
		while (iterator.hasNext()) {
			matched.add(iterator.next());
		}
		return Collections.unmodifiableCollection(matched);
	}

	/**
	 * Apply the filter to the specified {@link Map} returning only {@link #match(Object, Class) matching} values.
	 * @param map the map to filter
	 * @param targetClass the target class to match
	 * @return a filtered map
	 */
	public <K, V> Map<K, V> apply(Map<K, V> map, Class<?> targetClass) {
		Assert.notNull(map, "Map must not be null");
		Assert.notNull(targetClass, "TargetClass must not be null");
		Map<K, V> matched = new LinkedHashMap<K, V>();
		Iterator<Map.Entry<K, V>> iterator = apply(map.entrySet().iterator(), targetClass);
		while (iterator.hasNext()) {
			Entry<K, V> entry = iterator.next();
			matched.put(entry.getKey(), entry.getValue());
		}
		return matched;
	}

	/**
	 * Apply the filter to the specified {@link Iterator} returning only {@link #match(Object, Class) matching} objects.
	 * If the iterator contains {@link java.util.Map.Entry map entries} filtering will be applied against the
	 * {@link java.util.Map.Entry#getValue() entry value}.
	 * @param iterator the iterator to filter
	 * @param targetClass the target class to match
	 * @return a filtered iterator
	 */
	public <E> Iterator<E> apply(Iterator<E> iterator, final Class<?> targetClass) {
		Assert.notNull(iterator, "Iterator must not be null");
		Assert.notNull(targetClass, "TargetClass must not be null");
		return new FilteredIterator<E>(iterator) {
			@Override
			protected boolean isElementFiltered(E element) {
				Object object = element;
				if (object instanceof Map.Entry) {
					object = ((Map.Entry) object).getValue();
				}
				return !match(object, targetClass);
			};
		};
	}

	/**
	 * Returns <tt>true</tt> if object is for the specified targetClass.
	 * @param object the object to test (can be <tt>null</tt>)
	 * @param targetClass the target class to match
	 * @return <tt>true</tt> if the object is for the target class
	 */
	public boolean match(Object object, Class<?> targetClass) {
		Assert.notNull(targetClass, "TargetClass must not be null");
		if (object == null) {
			return false;
		}
		Set<Class<?>> classes = getForClasses(object);
		boolean isForClass = isAssignableFromAny(targetClass, classes);

		if (!(object instanceof ConditionalForClass)) {
			return isForClass;
		}

		ConditionalForClass conditionalForClass = (ConditionalForClass) object;
		// Only call the conditional if we match one of the classes or there was no specific classes to match against
		if (isForClass || classes.isEmpty()) {
			return conditionalForClass.isForClass(targetClass);
		}
		return false;
	}

	/**
	 * Returns all classes (deduced or specified) that the object is "for".
	 * @param object the source object
	 * @return a set of classes
	 */
	private Set<Class<?>> getForClasses(Object object) {
		ForClass annotation = AnnotationUtils.findAnnotation(object.getClass(), ForClass.class);
		if (annotation == null) {
			return Collections.emptySet();
		}
		Set<Class<?>> forClasses = new HashSet<Class<?>>();
		forClasses.addAll(Arrays.asList(annotation.value()));
		if (forClasses.isEmpty()) {
			Class<?> deduced = this.deducer.getForClass(object);
			if (deduced != null) {
				forClasses.add(deduced);
			}
		}
		Assert.state(forClasses.size() > 0, "Unable to determine classes for use with " + object.getClass().getName());
		return forClasses;
	}

	/**
	 * Returns <tt>true</tt> if the specified type is assignable from any of the candidates.
	 * @param type the type to test
	 * @param candidates candidate types
	 * @return <tt>true</tt> if the type is assignable from any of the candidates
	 */
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

	/**
	 * Strategy interface that can be used to deduce the class that an object is "for".
	 */
	public static interface Deducer {
		Class<?> getForClass(Object object);
	}

	/**
	 * Internal "null" {@link Deducer}.
	 */
	private static class NullForClassDeducer implements Deducer {
		public Class<?> getForClass(Object bean) {
			return null;
		}
	}

	/**
	 * {@link Deducer} that inspect generic types to determine the class that object is "for".
	 */
	private static class GenericTypeForClassDeducer implements Deducer {

		private Class<?> genericType;

		private int parameterIndex;

		public GenericTypeForClassDeducer(Class<?> genericType, int parameterIndex) {
			this.genericType = genericType;
			this.parameterIndex = parameterIndex;
		}

		public Class<?> getForClass(Object bean) {
			try {
				Class[] arguments = GenericTypeResolver.resolveTypeArguments(bean.getClass(), this.genericType);
				if (this.parameterIndex < arguments.length) {
					return arguments[this.parameterIndex];
				}
				return null;
			} catch (Exception e) {
				return null;
			}
		}
	}
}
