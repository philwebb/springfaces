package org.springframework.springfaces.mvc.navigation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * Indicates that an annotated class contains JSF navigation mappings.
 * <p>
 * This annotation serves as a specialization of {@link Component @Component}, allowing for implementation classes to be
 * autodetected through classpath scanning. It is typically used in combination with annotated navigation methods based
 * on the {@link org.springframework.springfaces.mvc.navigation.annotation.NavigationMapping} annotation.
 * 
 * @see Component
 * @see NavigationMapping
 * @see NavigationMethodOutcomeResolver
 * 
 * @author Phillip Webb
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface NavigationController {

	/**
	 * The value may indicate a suggestion for a logical component name, to be turned into a Spring bean in case of an
	 * autodetected component.
	 * @return the suggested component name, if any
	 */
	String value() default "";
}
