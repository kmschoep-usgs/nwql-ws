package gov.usgs.wma.nwql.webservice;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

import gov.usgs.wma.nwql.format.JsonStreamFormat;
import gov.usgs.wma.nwql.service.SpikeLotService;

/**
 * Webservice. Provides /json services for getting data
 * from any single database view.
 *
 * Also includes standard /count calls.
 *
 * @author kmschoep
 */
@Path("spikelot/data")
public class SpikeLotWebservice {

	public static final Integer DEFAULT_PAGE_SIZE = 50;

	public static final Integer MAX_PAGE_SIZE = 100;

	public static final Integer DEFAULT_STARTING_ROW = 0;

	/* Use this pattern in case of future dependency injection */
	private SpikeLotService spikeLotService = new SpikeLotService();

	/**
	 * Returns the record count based on all query parameters in the request
	 * object.
	 *
	 * @param request Request containing parameters
	 * @param type
	 * @return
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("count")
	public Integer getTotalCount(@Context final HttpServletRequest request) {
		@SuppressWarnings("unchecked")
			Integer result = spikeLotService.getRowCount();
			return result;
	}

	
	/**
	 * Pass through, for JSON format. See {@link #streamData}.
	 *
	 * @param request
	 * @param type
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("json")
	public StreamingOutput getJSON(
			@Context final HttpServletRequest request
 
	) {
		return streamData(request, new JsonStreamFormat());
	}

	/**
	 * Starts stream of filtered data.
	 *
	 * View type must be valid.
	 *
	 * @param request the service request which contains parameters
	 * @param type the service type
	 * @param format NWIS data formatter
	 *
	 * @return StreamingOutput object which does stream
	 */
	public StreamingOutput streamData(
			@Context final HttpServletRequest request,
			final JsonStreamFormat format
	) {

		return new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				spikeLotService.streamData(output, format);
			}
		};
	}
}
