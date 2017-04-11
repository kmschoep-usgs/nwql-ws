package gov.usgs.wma.nwql.spikelot.webservice.providers;

import javax.ws.rs.core.MediaType;

/**
 * Extend mediatype to add other string descriptors for additional formats.
 *
 * @author kmschoep
 */
public class NwqlOutputFormat extends MediaType {

	/**
	 * XML download type
	 */
	public final static String XML_DOWNLOAD = "application/x-xml";

	/**
	 * JSON download type
	 */
	public final static String JSON_DOWNLOAD = "application/x-json";

}
