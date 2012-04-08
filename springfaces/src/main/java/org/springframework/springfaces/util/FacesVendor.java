package org.springframework.springfaces.util;

/**
 * Supported JSF vendors including detection of the {@link #getCurrent() currently running implementation}.
 * 
 * @see #getCurrent()
 * @author Phillip Webb
 */
public enum FacesVendor {

	/**
	 * Oracle Mojarra reference JSF implementation.
	 */
	MOJARRA("org.apache.myfaces.application.ApplicationFactoryImpl"),

	/**
	 * Apache MyFaces JSF implementation.
	 */
	MYFACES("com.sun.faces.application.ApplicationFactoryImpl"),

	/**
	 * Unknown JSF implementation.
	 */
	UNKNOWN(null);

	private boolean present;

	private FacesVendor(String implementationSpecificClass) {
		if (implementationSpecificClass != null) {
			try {
				Class.forName(implementationSpecificClass);
				this.present = true;
			} catch (ClassNotFoundException e) {
			}
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
