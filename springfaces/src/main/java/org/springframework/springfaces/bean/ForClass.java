package org.springframework.springfaces.bean;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that can be used to indicate that a bean is "for" a specific class.
 * 
 * @see ConditionalForClass
 * 
 * @author Phillip Webb
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.TYPE)
public @interface ForClass {

	/**
	 * Returns the classes that the bean is "for". If not specified an attempt will be made to deduce the value (for
	 * example from a generic type).
	 * @return the class types that the bean is for
	 */
	Class<?>[] value() default {};
}
