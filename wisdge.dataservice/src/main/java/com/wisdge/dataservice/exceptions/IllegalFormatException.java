package com.wisdge.dataservice.exceptions;

/**
 * Thrown to indicate that a method has been passed an illegal or inappropriate object format.
 * 
 * @author Kevin MOU
 */
public class IllegalFormatException extends Exception {
	private static final long serialVersionUID = 8529592923808370980L;

	/**
	 * Constructs an <code>IllegalFormatException</code> with no detail message.
	 */
	public IllegalFormatException() {
		super();
	}

	/**
	 * Constructs an <code>IllegalFormatException</code> with the specified detail message.
	 * 
	 * @param message
	 *            the detail message.
	 */
	public IllegalFormatException(String message) {
		super(message);
	}

	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * <p>
	 * Note that the detail message associated with <code>cause</code> is <i>not</i> automatically incorporated in this exception's detail message.
	 * 
	 * @param message
	 *            the detail message (which is saved for later retrieval by the {@link Throwable#getMessage()} method).
	 * @param cause
	 *            the cause (which is saved for later retrieval by the {@link Throwable#getCause()} method). (A <tt>null</tt> value is permitted, and indicates
	 *            that the cause is nonexistent or unknown.)
	 */
	public IllegalFormatException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new exception with the specified cause and a detail message of <tt>(cause==null ? null : cause.toString())</tt> (which typically contains
	 * the class and detail message of <tt>cause</tt>). This constructor is useful for exceptions that are little more than wrappers for other throwables.
	 * 
	 * @param cause
	 *            the cause (which is saved for later retrieval by the {@link Throwable#getCause()} method). (A <tt>null</tt> value is permitted, and indicates
	 *            that the cause is nonexistent or unknown.)
	 */
	public IllegalFormatException(Throwable cause) {
		super(cause);
	}
}
