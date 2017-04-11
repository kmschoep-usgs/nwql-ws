package gov.usgs.wma.nwql.spikelot.webservice;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

import gov.usgs.wma.nwql.spikelot.api.format.INwqlStreamFormat;
import gov.usgs.wma.nwql.spikelot.api.service.IDataViewService;
import gov.usgs.wma.nwql.spikelot.format.JsonStreamFormat;
import gov.usgs.wma.nwql.spikelot.format.XmlStreamFormat;
import gov.usgs.wma.nwql.spikelot.model.ResultRow;
import gov.usgs.wma.nwql.spikelot.model.ResultRowCollection;
import gov.usgs.wma.nwql.spikelot.service.DataViewService;
import gov.usgs.wma.nwql.spikelot.webservice.providers.NwqlOutputFormat;

/**
 * Webservice. Provides /json, /xml, and /rdb services for getting paged data
 * from any single database view.
 *
 * Also includes standard /count and /column calls.
 *
 * @author thongsav-usgs
 */
@Path("data/view")
public class DataViewWebservice {

	public static final Integer DEFAULT_PAGE_SIZE = 50;

	public static final Integer MAX_PAGE_SIZE = 100;

	public static final Integer DEFAULT_STARTING_ROW = 0;

	/* Use this pattern in case of future dependency injection */
	private IDataViewService singleViewService = new DataViewService();

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
	@Path("{type}/count")
	public Integer getTotalCount(@Context final HttpServletRequest request,
			@PathParam("type") String type) {
		@SuppressWarnings("unchecked")
			Integer result = singleViewService.getRowCount();
			return result;
	}

	/**
	 * Produces the columns included for a given type.
	 *
	 * @param type type of view being requested. must be valid
	 * @return PojoCollection<String> containing all column names as strings.
	 */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("{type}/columns")
	public PojoCollection<String> getColumnNames(@PathParam("type") String type) {

		try {
			PojoCollection<String> result = new PojoCollection<String>();
			result.setCollection(singleViewService.getColumnNames(type));
			return result;
		} catch (IllegalArgumentException e) {
			throw new ViewNotFoundException(type, e);
		}
	}

	/**
	 * Returns the sitefile count for sites related to all type records based on
	 * all query parameters in the request object.
	 *
	 * @param request Request containing parameters
	 * @param type must be a valid type
	 * @return
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("relatedsites/{type}/count")
	public Integer getRelatedSitesTotalCount(@Context final HttpServletRequest request,
			@PathParam("type") String type) {
		@SuppressWarnings("unchecked")
		Map<String, Object> params = request.getParameterMap();
		try {
			Integer result = singleViewService.getRelatedSitesRowCount(type, params);
			return result;
		} catch (IllegalArgumentException e) {
			throw new ViewNotFoundException(type, e);
		}
	}

	/**
	 * Produces the columns for the sitefile table.
	 *
	 * @return PojoCollection<String> containing all column names from sitefile
	 * as strings.
	 */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("relatedsites/{type}/columns")
	public PojoCollection<String> getSitefileColumnNames() {
		try {
			PojoCollection<String> result = new PojoCollection<String>();
			result
					.setCollection(singleViewService.getColumnNames(NwisViews.sitefile.toString()));
			return result;
		} catch (IllegalArgumentException e) {
			throw new ViewNotFoundException(NwisViews.sitefile.toString(), e);
		}
	}

	/**
	 * Pass through, for XML format. See {@link #getPaged}.
	 *
	 * @param request
	 * @param type
	 * @param pageSize
	 * @param row
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("{type}/xml/paged")
	public ResultRowCollection getPagedXml(@Context final HttpServletRequest request,
			@PathParam("type") String type,
			@QueryParam(DataViewService.PAGESIZE_WEB_PARAM_NAME) Integer pageSize,
			@QueryParam(DataViewService.ROW_WEB_PARAM_NAME) Integer row) {
		return getPaged(request, type, pageSize, row);
	}

	/**
	 * Pass through, for JSON format. See {@link #getPaged}.
	 *
	 * @param request
	 * @param type
	 * @param pageSize
	 * @param row
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{type}/json/paged")
	public ResultRowCollection getPagedJSON(@Context final HttpServletRequest request,
			@PathParam("type") String type,
			@QueryParam(DataViewService.PAGESIZE_WEB_PARAM_NAME) Integer pageSize,
			@QueryParam(DataViewService.ROW_WEB_PARAM_NAME) Integer row) {
		return getPaged(request, type, pageSize, row);
	}

	/**
	 * Pass through, for RDB format. See {@link #getPaged}.
	 *
	 * @param request
	 * @param type
	 * @param pageSize
	 * @param row
	 * @return
	 */
	@GET
	@Produces(NwisOutputFormat.TEXT_RDB)
	@Path("{type}/rdb/paged")
	public ResultRowCollection getPagedRDB(@Context final HttpServletRequest request,
			@PathParam("type") String type,
			@QueryParam(DataViewService.PAGESIZE_WEB_PARAM_NAME) Integer pageSize,
			@QueryParam(DataViewService.ROW_WEB_PARAM_NAME) Integer row) {
		return getPaged(request, type, pageSize, row);
	}

	/**
	 * Returns results from a single view (the type) based on the parameters in
	 * the request. Provides a page of the results based on pageSie and row
	 * parameters. Those parameters will default to DEFAULT_PAGE_SIZE and
	 * DEFAULT_STARTING_ROW.
	 *
	 * View type must be valid.
	 *
	 * @param request the service request which contains parameters
	 * @param type the service type
	 * @param pageSize defaults if null
	 * @param row defaults if null
	 * @return ResultRowCollection wrapper object
	 */
	public ResultRowCollection getPaged(@Context final HttpServletRequest request, String type,
			Integer pageSize, Integer row) {
		@SuppressWarnings("unchecked")
		Map<String, Object> params = request.getParameterMap();

		// defaults
		if (pageSize == null) {
			pageSize = DEFAULT_PAGE_SIZE;
		}

		if (pageSize > MAX_PAGE_SIZE) {
			pageSize = MAX_PAGE_SIZE;
		}

		if (row == null) {
			row = DEFAULT_STARTING_ROW;
		}

		try {
			List<ResultRow> list = singleViewService.getData(type, params, pageSize, row);

			ResultRowCollection result = new ResultRowCollection();
			result.setTotalCount(getTotalCount(request, type));
			result.setList(list);
			result.setStartingRow(row);
			result.setPageSize(list.size());
			return result;
		} catch (IllegalArgumentException e) {
			throw new ViewNotFoundException(type, e);
		}
	}

	/**
	 * XML format pass through. See {@link #getPagedRelatedSites}.
	 *
	 * @param request
	 * @param type
	 * @param pageSize
	 * @param row
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("relatedsites/{type}/xml/paged")
	public ResultRowCollection getPagedRelatedSitesXml(
			@Context final HttpServletRequest request, @PathParam("type") String type,
			@QueryParam(DataViewService.PAGESIZE_WEB_PARAM_NAME) Integer pageSize,
			@QueryParam(DataViewService.ROW_WEB_PARAM_NAME) Integer row) {
		return getPagedRelatedSites(request, type, pageSize, row);
	}

	/**
	 * JSON format pass through. See {@link #getPagedRelatedSites}.
	 *
	 * @param request
	 * @param type
	 * @param pageSize
	 * @param row
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("relatedsites/{type}/json/paged")
	public ResultRowCollection getPagedRelatedSitesRDB(
			@Context final HttpServletRequest request, @PathParam("type") String type,
			@QueryParam(DataViewService.PAGESIZE_WEB_PARAM_NAME) Integer pageSize,
			@QueryParam(DataViewService.ROW_WEB_PARAM_NAME) Integer row) {
		return getPagedRelatedSites(request, type, pageSize, row);
	}

	/**
	 * RDB format pass through. See {@link #getPagedRelatedSites}.
	 *
	 * @param request
	 * @param type
	 * @param pageSize
	 * @param row
	 * @return
	 */
	@GET
	@Produces(NwisOutputFormat.TEXT_RDB)
	@Path("relatedsites/{type}/rdb/paged")
	public ResultRowCollection getPagedRelatedSitesJSON(
			@Context final HttpServletRequest request, @PathParam("type") String type,
			@QueryParam(DataViewService.PAGESIZE_WEB_PARAM_NAME) Integer pageSize,
			@QueryParam(DataViewService.ROW_WEB_PARAM_NAME) Integer row) {
		return getPagedRelatedSites(request, type, pageSize, row);
	}

	/**
	 * Returns results from the sitefile which are related to the given type
	 * records. Type records are based on the parameters in the request.
	 * Provides a page of the results based on pageSie and row parameters. Those
	 * parameters will default to DEFAULT_PAGE_SIZE and DEFAULT_STARTING_ROW.
	 *
	 * View type must be valid.
	 *
	 * @param request the service request which contains parameters
	 * @param type the service type
	 * @param pageSize defaults if null
	 * @param row defaults if null
	 * @return ResultRowCollection wrapper object
	 */
	public ResultRowCollection getPagedRelatedSites(HttpServletRequest request, String type,
			Integer pageSize, Integer row) {
		@SuppressWarnings("unchecked")
		Map<String, Object> params = request.getParameterMap();

		// defaults
		if (pageSize == null) {
			pageSize = DEFAULT_PAGE_SIZE;
		}

		if (pageSize > MAX_PAGE_SIZE) {
			pageSize = MAX_PAGE_SIZE;
		}

		if (row == null) {
			row = DEFAULT_STARTING_ROW;
		}

		try {
			List<ResultRow> list = singleViewService.getRelatedSites(type, params, pageSize,
					row);

			ResultRowCollection result = new ResultRowCollection();
			result.setTotalCount(getRelatedSitesTotalCount(request, type));
			result.setList(list);
			result.setStartingRow(row);
			result.setPageSize(list.size());
			return result;
		} catch (IllegalArgumentException e) {
			throw new ViewNotFoundException(type, e);
		}
	}

	/**
	 * Pass through, for XML format. See {@link #streamData}.
	 *
	 * @param request
	 * @param type
	 * @return
	 */
	@GET
	@Path("{type}/xml")
	@Produces(MediaType.APPLICATION_XML)
	public StreamingOutput getXml(
			@Context final HttpServletRequest request,
			@PathParam("type") String type
	) {
		return streamData(request, type, new XmlStreamFormat());
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
	@Path("{type}/json")
	public StreamingOutput getJSON(
			@Context final HttpServletRequest request,
			@PathParam("type") String type
	) {
		return streamData(request, type, new JsonStreamFormat());
	}

	/**
	 * Pass through, for RDB format. See {@link #streamData}.
	 *
	 * @param request
	 * @param type
	 * @param pageSize
	 * @param row
	 * @return
	 */
	@POST
	@Produces(NwisOutputFormat.TEXT_RDB)
	@Path("{type}/rdb")
	public StreamingOutput postRDB(
			@Context final HttpServletRequest request,
			@PathParam("type") String type
	) {
		return streamData(request, type, new RdbStreamFormat());
	}

	/**
	 * Pass through, for RDB format. See {@link #streamData}.
	 *
	 * @param request
	 * @param type
	 * @param pageSize
	 * @param row
	 * @return
	 */
	@GET
	@Produces(NwisOutputFormat.TEXT_RDB)
	@Path("{type}/rdb")
	public StreamingOutput getRDB(
			@Context final HttpServletRequest request,
			@PathParam("type") String type
	) {
		return streamData(request, type, new RdbStreamFormat());
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
			final String type,
			final INwisStreamFormat format
	) {
		@SuppressWarnings("unchecked")
		final Map<String, Object> params = request.getParameterMap();

		return new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				singleViewService.streamData(type, params, output, format);
			}
		};
	}
}
