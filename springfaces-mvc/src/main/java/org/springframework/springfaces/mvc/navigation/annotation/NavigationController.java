/*
 * Copyright 2010-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
