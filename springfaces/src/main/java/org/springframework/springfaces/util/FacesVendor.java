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
package org.springframework.springfaces.util;

import org.springframework.util.ClassUtils;

/**
 * Supported JSF vendors including detection of the {@link #getCurrent() currently running implementation}.
 * 
 * @author Phillip Webb
 * @see #getCurrent()
 */
public enum FacesVendor {

	/**
	 * Oracle Mojarra reference JSF implementation.
	 */
	MOJARRA("com.sun.faces.application.ApplicationFactoryImpl"),

	/**
	 * Apache MyFaces JSF implementation.
	 */
	MYFACES("org.apache.myfaces.application.ApplicationFactoryImpl"),

	/**
	 * Unknown JSF implementation.
	 */
	UNKNOWN(null);

	private boolean present;

	private FacesVendor(String implementationSpecificClass) {
		if (implementationSpecificClass != null) {
			this.present = ClassUtils.isPresent(implementationSpecificClass, getClass().getClassLoader());
		}
	}

	/**
	 * Returns <tt>true</tt> if the JSF vendor is on the classpath for the currently running application.
	 * @return <tt>true</tt> if this vendor is present
	 */
	public boolean isPresent() {
		return this.present;
	}

	private static FacesVendor current;

	/**
	 * Returns the vendor for the currently running application.
	 * @return the JSF vendor
	 */
	public static FacesVendor getCurrent() {
		if (current == null) {
			for (FacesVendor vendor : FacesVendor.values()) {
				if (vendor.isPresent()) {
					current = vendor;
					break;
				}
			}
		}
		return current;
	}

}
