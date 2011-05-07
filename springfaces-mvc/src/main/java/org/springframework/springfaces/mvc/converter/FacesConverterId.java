package org.springframework.springfaces.mvc.converter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.faces.application.Application;

/**
 * Indicates that the {@link GenericFacesConverter} should use a specific JSF converter.
 * 
 * @see GenericFacesConverter
 * 
 * @author Phillip Webb
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FacesConverterId {

	/**
	 * The ID of the JSF converter. The converter must have been registered to the JSF {@link Application} using any of
	 * the standard JSF mechanisms.
	 */
	String value();
}
