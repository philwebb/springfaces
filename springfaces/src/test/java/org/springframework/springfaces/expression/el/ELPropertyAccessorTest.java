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
		elContext = mock(ELContext.class);
		elResolver = mock(ELResolver.class);
		given(elContext.getELResolver()).willReturn(elResolver);
		elPropertyAccessor = new ELPropertyAccessor() {
			@Override
			protected ELContext getElContext(EvaluationContext context, Object target) {
				return elContext;
			}

			protected Object getResolveBase(EvaluationContext context, Object target, String name) {
				if (resolveBase != null) {
					return resolveBase;
				}
				return super.getResolveBase(context, target, name);
			};

			protected Object getResolveProperty(EvaluationContext context, Object target, String name) {
				if (resolveProperty != null) {
					return resolveProperty;
				}
				return super.getResolveProperty(context, target, name);

			};
		};
	}

	@SuppressWarnings("unchecked")
	private void willResolve(Object base, Object property, Object value) {
		given(elResolver.getType(elContext, base, property)).willReturn((Class) value.getClass());
		given(elResolver.getValue(elContext, base, property)).willReturn(value);
		given(elContext.isPropertyResolved()).willReturn(true);
	}

	@Test
	public void shouldNotReadIfNotInEl() throws Exception {
		assertFalse(elPropertyAccessor.canRead(context, target, name));
		assertNull(elPropertyAccessor.read(context, target, name));
	}

	@Test
	public void shouldReadIfInEl() throws Exception {
		willResolve(null, name, value);
		assertTrue(elPropertyAccessor.canRead(context, target, name));
		assertSame(value, elPropertyAccessor.read(context, target, name).getValue());
	}

	@Test
	public void shouldNotWrite() throws Exception {
		assertFalse(elPropertyAccessor.canWrite(context, target, name));
		elPropertyAccessor.write(context, target, name, value);
	}

	@Test
	public void shouldDefaultToBeanExpressionContext() throws Exception {
		assertTrue(Arrays.equals(new Class<?>[] { BeanExpressionContext.class },
				elPropertyAccessor.getSpecificTargetClasses()));
	}

	@Test
	public void shouldSupportResolveOverrides() throws Exception {
		resolveBase = new Object();
		resolveProperty = "changed";
		Object expected = new Object();
		willResolve(null, name, value);
		willResolve(resolveBase, resolveProperty, expected);
		assertTrue(elPropertyAccessor.canRead(context, target, name));
		assertSame(expected, elPropertyAccessor.read(context, target, name).getValue());
	}

	@Test
	public void shouldWorkWithoutElContext() throws Exception {
		elContext = null;
		assertFalse(elPropertyAccessor.canRead(context, target, name));
		assertNull(elPropertyAccessor.read(context, target, name));
	}
}
