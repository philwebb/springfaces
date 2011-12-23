package org.springframework.springfaces.expression.el;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.Arrays;

import javax.el.ELContext;
import javax.el.ELResolver;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.expression.EvaluationContext;

/**
 * Tests for {@link ELPropertyAccessor}.
 * 
 * @author Phillip Webb
 */
public class ELPropertyAccessorTest {

	private ELPropertyAccessor elPropertyAccessor;
	private ELContext elContext;
	private ELResolver elResolver;
	private EvaluationContext context = mock(EvaluationContext.class);
	private Object target = new Object();
	private String name = "name";
	private Object value = new Object();
	private Object resolveBase;
	private Object resolveProperty;

	@Before
	public void setup() {
		this.elContext = mock(ELContext.class);
		this.elResolver = mock(ELResolver.class);
		given(this.elContext.getELResolver()).willReturn(this.elResolver);
		this.elPropertyAccessor = new ELPropertyAccessor() {
			@Override
			protected ELContext getElContext(EvaluationContext context, Object target) {
				return ELPropertyAccessorTest.this.elContext;
			}

			@Override
			protected Object getResolveBase(EvaluationContext context, Object target, String name) {
				if (ELPropertyAccessorTest.this.resolveBase != null) {
					return ELPropertyAccessorTest.this.resolveBase;
				}
				return super.getResolveBase(context, target, name);
			};

			@Override
			protected Object getResolveProperty(EvaluationContext context, Object target, String name) {
				if (ELPropertyAccessorTest.this.resolveProperty != null) {
					return ELPropertyAccessorTest.this.resolveProperty;
				}
				return super.getResolveProperty(context, target, name);

			};
		};
	}

	@SuppressWarnings("unchecked")
	private void willResolve(Object base, Object property, Object value) {
		given(this.elResolver.getType(this.elContext, base, property)).willReturn((Class) value.getClass());
		given(this.elResolver.getValue(this.elContext, base, property)).willReturn(value);
		given(this.elContext.isPropertyResolved()).willReturn(true);
	}

	@Test
	public void shouldNotReadIfNotInEl() throws Exception {
		assertFalse(this.elPropertyAccessor.canRead(this.context, this.target, this.name));
		assertNull(this.elPropertyAccessor.read(this.context, this.target, this.name));
	}

	@Test
	public void shouldReadIfInEl() throws Exception {
		willResolve(null, this.name, this.value);
		assertTrue(this.elPropertyAccessor.canRead(this.context, this.target, this.name));
		assertSame(this.value, this.elPropertyAccessor.read(this.context, this.target, this.name).getValue());
	}

	@Test
	public void shouldNotWrite() throws Exception {
		assertFalse(this.elPropertyAccessor.canWrite(this.context, this.target, this.name));
		this.elPropertyAccessor.write(this.context, this.target, this.name, this.value);
	}

	@Test
	public void shouldDefaultToBeanExpressionContext() throws Exception {
		assertTrue(Arrays.equals(new Class<?>[] { BeanExpressionContext.class },
				this.elPropertyAccessor.getSpecificTargetClasses()));
	}

	@Test
	public void shouldSupportResolveOverrides() throws Exception {
		this.resolveBase = new Object();
		this.resolveProperty = "changed";
		Object expected = new Object();
		willResolve(null, this.name, this.value);
		willResolve(this.resolveBase, this.resolveProperty, expected);
		assertTrue(this.elPropertyAccessor.canRead(this.context, this.target, this.name));
		assertSame(expected, this.elPropertyAccessor.read(this.context, this.target, this.name).getValue());
	}

	@Test
	public void shouldWorkWithoutElContext() throws Exception {
		this.elContext = null;
		assertFalse(this.elPropertyAccessor.canRead(this.context, this.target, this.name));
		assertNull(this.elPropertyAccessor.read(this.context, this.target, this.name));
	}
}
