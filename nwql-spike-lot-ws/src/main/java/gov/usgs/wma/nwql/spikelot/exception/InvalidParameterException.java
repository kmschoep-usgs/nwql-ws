package gov.usgs.wma.nwql.spikelot.exception;

/**
 * This exception is thrown when a passed in query parameter is invalid, either
 * due to a non-existent view, or bad expression and data type specifiers. May
 * also be thrown if the field name is not found in the database.
 *
 * @author thongsav
 */
public class InvalidParameterException extends RuntimeException {

	private String field = "";

	/**
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Message constructor, expect to take the requested and invalid view as a
	 * message.
	 *
	 * @param message
	 */
	public InvalidParameterException(String message) {
		super(message);
	}

	/**
	 * Message constructor, expect to take the requested and invalid view as a
	 * message.
	 *
	 * @param message The message to include
	 * @param t The causing exception or error
	 */
	public InvalidParameterException(String message, Throwable t) {
		super(message, t);
	}

	/**
	 * Message constructor, expect to take the requested and invalid view as a
	 * message.
	 *
	 * @param message The message to include
	 * @param field The field that caused the error to include
	 * @param t The causing exception or error
	 */
	public InvalidParameterException(String message, String field, Throwable t) {
		super(message, t);
		this.field = field;
	}

	public String getField() {
		return this.field;
	}
}
