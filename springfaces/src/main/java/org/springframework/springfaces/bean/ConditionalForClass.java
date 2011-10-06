package org.springframework.springfaces.bean;

/**
 * Used to indicate that a bean is conditionally for use with a specific class. This interface allows for more advanced
 * bindings than standard {@link ForClass} annotation.
 * 
 * @see ForClass
 * @author Phillip Webb
 */
public interface ConditionalForClass {

	/**
	 * Determine if the bean is for the given target class.
	 * @param targetClass the target class
	 * @return if the bean is for the given target class
	 */
	boolean isForClass(Class<?> targetClass);

}
