package org.springframework.springfaces.page;

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
