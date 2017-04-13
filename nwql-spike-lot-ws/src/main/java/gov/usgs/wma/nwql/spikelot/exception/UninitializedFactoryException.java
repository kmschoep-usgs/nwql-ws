package gov.usgs.wma.nwql.spikelot.exception;

/**
 * This exception is thrown when requests for a null session factory are made.
 * See {@link gov.usgs.nwis.reporting.dao.SessionFactory}
 *
 * @author kmschoep
 */
public class UninitializedFactoryException extends RuntimeException {

	/**
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Message constructer.
	 *
	 * @param requestedView
	 */
	public UninitializedFactoryException(String message) {
		super(message);
	}
}
