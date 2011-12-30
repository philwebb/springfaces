package org.springframework.springfaces.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.springfaces.bean.ForClass;
import org.springframework.util.ClassUtils;

/**
 * Tests for {@link ForClassFilter}.
 * 
 * @author Phillip Webb
 */
public class ForClassFilterTest {

	private Map<String, Object> beans;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setup() throws Exception {
		this.beans = new HashMap<String, Object>();
		addBean(ForAnimals.class);
		addBean(ForDog.class);
		addBean(ForCat.class);
		addBean(ForDogAndCat.class);
		addBean(ForAnimalFromGeneric.class);
		addBean(ForDogFromMultiGeneric.class);
		addBean(DeducedForDog.class);
	}

	@Test
	public void shouldFilterLeafClassAnnotatedBeans() throws Exception {
		this.beans.remove("ForAnimalFromGeneric");
		this.beans.remove("ForDogFromMultiGeneric");
		this.beans.remove("DeducedForDog");
		ForClassFilter filter = new ForClassFilter();
		testFilter(filter, Dog.class, "ForAnimals", "ForDog", "ForDogAndCat");
	}

	@Test
	public void shouldFilterSuperClassAnnotatedBeans() throws Exception {
		this.beans.remove("ForAnimalFromGeneric");
		this.beans.remove("ForDogFromMultiGeneric");
		this.beans.remove("DeducedForDog");
		ForClassFilter filter = new ForClassFilter();
		testFilter(filter, Animal.class, "ForAnimals");
	}

	@Test
	public void shouldFilterByGeneric() throws Exception {
		this.beans.remove("DeducedForDog");
		this.beans.remove("ForDogFromMultiGeneric");
		ForClassFilter filter = new ForClassFilter(Generic.class);
		testFilter(filter, Cat.class, "ForAnimals", "ForCat", "ForDogAndCat", "ForAnimalFromGeneric");
	}

	@Test
	public void shouldNotIncludeItemsWithoutAnnotationWhenFilterByGeneric() throws Exception {
		this.beans.remove("DeducedForDog");
		this.beans.remove("ForDogFromMultiGeneric");
		addBean(ForAnimalFromGenericWithoutAnnotation.class);
		ForClassFilter filter = new ForClassFilter(Generic.class);
		testFilter(filter, Cat.class, "ForAnimals", "ForCat", "ForDogAndCat", "ForAnimalFromGeneric");
	}

	@Test
	public void shouldFilterByMulitGeneric() throws Exception {
		this.beans.remove("ForAnimalFromGeneric");
		this.beans.remove("DeducedForDog");
		ForClassFilter filter = new ForClassFilter(MultiGeneric.class, 1);
		testFilter(filter, Dog.class, "ForAnimals", "ForDog", "ForDogAndCat", "ForDogFromMultiGeneric");
	}

	@Test
	public void shouldFilterFromCustomDeducer() throws Exception {
		this.beans.remove("ForAnimalFromGeneric");
		this.beans.remove("ForDogFromMultiGeneric");
		ForClassFilter filter = new ForClassFilter(new ForClassFilter.Deducer() {
			public Class<?> getForClass(Object bean) {
				if (getName(bean).equals("DeducedForDog")) {
					return Dog.class;
				}
				return null;
			}
		});
		testFilter(filter, Dog.class, "ForAnimals", "ForDog", "ForDogAndCat", "DeducedForDog");
	}

	@Test
	public void shouldFilterNullBeans() throws Exception {
		ForClassFilter filter = new ForClassFilter(Generic.class);
		Collection<Object> collection = filter.apply(Arrays.asList(new Object[] { null }), Dog.class);
		Map<String, Object> map = filter.apply(Collections.singletonMap("bean", null), Dog.class);
		boolean single = filter.match(null, Dog.class);
		assertThat(collection.size(), is(0));
		assertThat(map.size(), is(0));
		assertThat(single, is(false));
	}

	@Test
	public void shouldNeedGenericTypeOnConstructor() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("GenericType must not be null");
		new ForClassFilter((Class) null);
	}

	@Test
	public void shouldNeedDeducerOnConstructor() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Deducer must not be null");
		new ForClassFilter((ForClassFilter.Deducer) null);
	}

	@Test
	public void shouldNeedCollection() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Collection must not be null");
		new ForClassFilter().apply((Collection<?>) null, Dog.class);
	}

	@Test
	public void shouldNeedTargetOnCollection() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("TargetClass must not be null");
		new ForClassFilter().apply(Collections.emptySet(), null);
	}

	@Test
	public void shouldNeedMap() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Map must not be null");
		new ForClassFilter().apply((Map<?, ?>) null, Dog.class);
	}

	@Test
	public void shouldNeedTargetOnMap() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("TargetClass must not be null");
		new ForClassFilter().apply(Collections.emptyMap(), null);
	}

	@Test
	public void shouldNeedIterator() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Iterator must not be null");
		new ForClassFilter().apply((Iterator<?>) null, Dog.class);
	}

	@Test
	public void shouldNeedTargetOnIterator() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("TargetClass must not be null");
		new ForClassFilter().apply(Collections.emptySet().iterator(), null);
	}

	@Test
	public void shouldNeedTargetOnBean() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("TargetClass must not be null");
		new ForClassFilter().match(new Object(), null);
	}

	private void testFilter(ForClassFilter filter, Class<?> targetClass, String... expected) {
		testFilterCollection(filter, targetClass, expected);
		testFilterCollectionOfMapEntry(filter, targetClass, expected);
		testFilterMap(filter, targetClass, expected);
		testFilterIterator(filter, targetClass, expected);
		testFilterIteratorOfMapEntry(filter, targetClass, expected);
		testFilterObject(filter, targetClass, expected);
	}

	private void testFilterCollection(ForClassFilter filter, Class<?> targetClass, String... expected) {
		Collection<Object> actual = filter.apply(this.beans.values(), targetClass);
		Set<String> actualNames = new HashSet<String>();
		for (Object bean : actual) {
			actualNames.add(getName(bean));
		}
		assertThat(actualNames, is(equalToSetOf(expected)));
	}

	private void testFilterCollectionOfMapEntry(ForClassFilter filter, Class<?> targetClass, String... expected) {
		Collection<Map.Entry<String, Object>> actual = filter.apply(this.beans.entrySet(), targetClass);
		Set<String> actualNames = new HashSet<String>();
		for (Map.Entry<String, Object> entry : actual) {
			actualNames.add(entry.getKey());
		}
		assertThat(actualNames, is(equalToSetOf(expected)));
	}

	private void testFilterMap(ForClassFilter filter, Class<?> targetClass, String... expected) {
		Map<String, Object> actual = filter.apply(this.beans, targetClass);
		assertThat(actual.keySet(), is(equalToSetOf(expected)));
	}

	private void testFilterIterator(ForClassFilter filter, Class<?> targetClass, String... expected) {
		Iterator<Object> actual = filter.apply(this.beans.values().iterator(), targetClass);
		Set<String> actualNames = new HashSet<String>();
		while (actual.hasNext()) {
			actualNames.add(getName(actual.next()));
		}
		assertThat(actualNames, is(equalToSetOf(expected)));
	}

	private void testFilterIteratorOfMapEntry(ForClassFilter filter, Class<?> targetClass, String... expected) {
		Iterator<Map.Entry<String, Object>> actual = filter.apply(this.beans.entrySet().iterator(), targetClass);
		Set<String> actualNames = new HashSet<String>();
		while (actual.hasNext()) {
			actualNames.add(actual.next().getKey());
		}
		assertThat(actualNames, is(equalToSetOf(expected)));
	}

	private void testFilterObject(ForClassFilter filter, Class<?> targetClass, String... expected) {
		Set<String> actualNames = new HashSet<String>();
		for (Map.Entry<String, Object> entry : this.beans.entrySet()) {
			if (filter.match(entry.getValue(), targetClass)) {
				actualNames.add(entry.getKey());
			}
		}
		assertThat(actualNames, is(equalToSetOf(expected)));
	}

	private void addBean(Class<?> beanClass) throws Exception {
		this.beans.put(getName(beanClass), beanClass.newInstance());
	}

	private String getName(Object bean) {
		return getName(bean.getClass());
	}

	private String getName(Class<?> beanClass) {
		String name = ClassUtils.getShortName(beanClass);
		return name.substring(name.indexOf(".") + 1);
	}

	private static Matcher<Set<String>> equalToSetOf(String... expected) {
		Set<String> expectedSet = new HashSet<String>(Arrays.asList(expected));
		return equalTo((expectedSet));
	}

	static class Animal {
	}

	static class Cat extends Animal {
	}

	static class Dog extends Animal {
	}

	static interface Generic<T> {
	}

	static interface MultiGeneric<X, Y, Z> {
	}

	@ForClass(Animal.class)
	static class ForAnimals {
	}

	@ForClass(Dog.class)
	static class ForDog {
	}

	@ForClass(Cat.class)
	static class ForCat {
	}

	@ForClass({ Dog.class, Cat.class })
	static class ForDogAndCat {
	}

	@ForClass
	static class ForAnimalFromGeneric implements Generic<Animal> {
	}

	@ForClass
	static class ForDogFromMultiGeneric implements MultiGeneric<String, Animal, String> {
	}

	@ForClass
	static class DeducedForDog {
	}

	static class ForAnimalFromGenericWithoutAnnotation implements Generic<Animal> {
	}
}
