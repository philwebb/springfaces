package org.springframework.springfaces.expression.el;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ValueExpression;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.convert.Property;
import org.springframework.core.convert.TypeDescriptor;

import com.sun.el.lang.ExpressionBuilder;
import com.sun.faces.el.ELContextImpl;

/**
 * Tests for {@link ELUtils}.
 * 
 * @author Phillip Webb
 */
public class ELUtilsTest {

	private Bean bean = new Bean();
	private CompositeELResolver resolver;
	private ELContextImpl context;

	@Before
	public void setup() {
		this.resolver = new CompositeELResolver();
		this.resolver.add(new TestBeanResolver());
		this.resolver.add(new BeanELResolver());
		this.context = new ELContextImpl(this.resolver);
	}

	@Test
	public void shouldGetTypeDescriptorForSimpleType() throws Exception {
		ValueExpression valueExpression = newValueExpression("integer", Object.class);
		TypeDescriptor typeDescriptor = ELUtils.getTypeDescriptor(valueExpression, this.context);
		assertThat(typeDescriptor.getType(), is(equalTo((Class) Integer.class)));
	}

	@Test
	public void shouldGetTypeDescriptorForArray() throws Exception {
		ValueExpression valueExpression = newValueExpression("arrayOfString", Object.class);
		TypeDescriptor typeDescriptor = ELUtils.getTypeDescriptor(valueExpression, this.context);
		assertThat(typeDescriptor.isArray(), is(true));
		assertThat(typeDescriptor.getElementTypeDescriptor(), is(TypeDescriptor.valueOf(String.class)));

	}

	@Test
	public void shouldGetTypeDescriptorForCollectionWithGeneric() throws Exception {
		ValueExpression valueExpression = newValueExpression("setOfLong", Object.class);
		TypeDescriptor typeDescriptor = ELUtils.getTypeDescriptor(valueExpression, this.context);
		assertThat(typeDescriptor.getType(), is(equalTo((Class) Set.class)));
		assertThat(typeDescriptor.isCollection(), is(true));
		assertThat(typeDescriptor.getElementTypeDescriptor(), is(TypeDescriptor.valueOf(Long.class)));
	}

	@Test
	public void shouldGetTypeDescriptorForMapWithGenerics() throws Exception {
		ValueExpression valueExpression = newValueExpression("mapOfStringInteger", Object.class);
		TypeDescriptor typeDescriptor = ELUtils.getTypeDescriptor(valueExpression, this.context);
		assertThat(typeDescriptor.getType(), is(equalTo((Class) Map.class)));
		assertThat(typeDescriptor.isMap(), is(true));
		assertThat(typeDescriptor.getMapKeyTypeDescriptor(), is(TypeDescriptor.valueOf(String.class)));
		assertThat(typeDescriptor.getMapValueTypeDescriptor(), is(TypeDescriptor.valueOf(Integer.class)));
	}

	@Test
	public void shouldGetTypeDescriptorForNested() throws Exception {
		ValueExpression valueExpression = newValueExpression("nested.collectionOfInteger", Object.class);
		TypeDescriptor typeDescriptor = ELUtils.getTypeDescriptor(valueExpression, this.context);
		assertThat(typeDescriptor.getType(), is(equalTo((Class) Collection.class)));
		assertThat(typeDescriptor.isCollection(), is(true));
		assertThat(typeDescriptor.getElementTypeDescriptor(), is(TypeDescriptor.valueOf(Integer.class)));
	}

	@Test
	public void shouldGetPropertyForNull() throws Exception {
		Property property = ELUtils.getProperty(null, this.context);
		assertThat(property, is(nullValue()));
	}

	@Test
	public void shouldGetProperty() throws Exception {
		ValueExpression valueExpression = newValueExpression("integer", Object.class);
		Property property = ELUtils.getProperty(valueExpression, this.context);
		assertThat(property.getName(), is(equalTo("integer")));
		assertThat(property.getObjectType(), is(equalTo((Class) Bean.class)));
	}

	@Test
	public void shouldGetNestedProperty() throws Exception {
		ValueExpression valueExpression = newValueExpression("nested.collectionOfInteger", Object.class);
		Property property = ELUtils.getProperty(valueExpression, this.context);
		assertThat(property.getName(), is(equalTo("collectionOfInteger")));
		assertThat(property.getObjectType(), is(equalTo((Class) NestedBean.class)));
	}

	@Test
	public void shouldGetNullPropertyIfMissing() throws Exception {
		ValueExpression valueExpression = new ExpressionBuilder("#{bean}", this.context)
				.createValueExpression(Object.class);
		Property property = ELUtils.getProperty(valueExpression, this.context);
		assertThat(property, is(nullValue()));
	}

	private ValueExpression newValueExpression(String propery, Class<?> expectedType) {
		return new ExpressionBuilder("#{bean." + propery + "}", this.context).createValueExpression(expectedType);
	}

	private class TestBeanResolver extends AbstractELResolver {
		@Override
		protected Object get(String property) {
			if ("bean".equals(property)) {
				return ELUtilsTest.this.bean;
			}
			return null;
		}
	}

	public static class Bean {
		private Integer integer;
		private String[] arrayOfString;
		private Set<Long> setOfLong;
		private Map<String, Integer> mapOfStringInteger;
		private NestedBean nested = new NestedBean();

		public Integer getInteger() {
			return this.integer;
		}

		public void setInteger(Integer integer) {
			this.integer = integer;
		}

		public String[] getArrayOfString() {
			return this.arrayOfString;
		}

		public void setArrayOfString(String[] arrayOfString) {
			this.arrayOfString = arrayOfString;
		}

		public Set<Long> getSetOfLong() {
			return this.setOfLong;
		}

		public void setSetOfLong(Set<Long> setOfLong) {
			this.setOfLong = setOfLong;
		}

		public Map<String, Integer> getMapOfStringInteger() {
			return this.mapOfStringInteger;
		}

		public void setMapOfStringInteger(Map<String, Integer> mapOfStringInteger) {
			this.mapOfStringInteger = mapOfStringInteger;
		}

		public NestedBean getNested() {
			return this.nested;
		}

		public void setNested(NestedBean nested) {
			this.nested = nested;
		}

	}

	public static class NestedBean {
		private Collection<Integer> collectionOfInteger;

		public Collection<Integer> getCollectionOfInteger() {
			return this.collectionOfInteger;
		}

		public void setCollectionOfInteger(Collection<Integer> collectionOfInteger) {
			this.collectionOfInteger = collectionOfInteger;
		}

	}

}
