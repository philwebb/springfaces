package org.springframework.springfaces.page.model;

/**
 * Exception to indicate that no row data is available.
 * 
 * @author Phillip Webb
 */
public class NoRowAvailableException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	public NoRowAvailableException() {
		super();
	}

	public NoRowAvailableException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoRowAvailableException(String s) {
		super(s);
	}

	public NoRowAvailableException(Throwable cause) {
		super(cause);
	}

}
