package gov.usgs.wma.nwql.spikelot.format;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gson.Gson;

import gov.usgs.nwis.reporting.api.format.INwisStreamFormat;
import gov.usgs.nwis.reporting.model.Column;
import gov.usgs.nwis.reporting.model.ParameterDescription;
import gov.usgs.nwis.reporting.model.ResultRow;

/**
 *
 * Produces format: { "totalCount" : "recordCount" :
 * "numberofrecords", "startingRow" : "rowNum", "records": [ {<recordJson>}, ..]
 * }
 */
public class JsonStreamFormat implements INwqlStreamFormat {

	// TODO hardcoded list is brittle, consider moving this out
	public static final String[] TABLES_CONTAINING_PII = new String[]{"site_owner",
		"site_contact", "gw_otid", "addr", "party", "site_owner_cur"};

	//used to help conversion to json
	Gson g = new Gson();

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see gov.usgs.wma.nwql.spikelot.api.format.INwqlStreamFormat #writeHeader
	 */
	@Override
	public String writeHeader(Integer total) {
		String header = "{";

		if (total != null) {
			header += "\"totalCount\": \"" + total + "\",";
		}

		header += "\"records\":[";
		return header;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see gov.usgs.wma.nwql.spikelot.api.format.INwqlStreamFormat #writeHeader
	 */
	@Override
	public String writeResultRow(ResultRow row) {
		String rowString = "{";

		boolean firstCol = true;
		for (String col : row.keySet()) {
			if (firstCol) {
				firstCol = false;
			} else {
				rowString += ",";
			}

			String cleanedValue = g.toJson(row.get(col).toString());

			String columnString = g.toJson(col) + ":" + cleanedValue;
			rowString += columnString;
		}
		rowString += "}";
		return rowString;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see gov.usgs.wma.nwql.spikelot.api.format.INwqlStreamFormat #writeFooter
	 */
	@Override
	public String writeFooter() {
		return "]}";
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see gov.usgs.wma.nwql.spikelot.api.format.INwqlStreamFormat #writeResultRowHeaders
	 */
	@Override
	public String writeResultRowHeaders(ResultRow row, List<String> exclusionList) {
		return "";
	}

	@Override
	public String writeDataTypeRow(ResultRow row, List<String> exclusionList) {
		return "";
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see gov.usgs.wma.nwql.spikelot.api.format.INwqlStreamFormat #writeRowSeparater
	 */
	@Override
	public String writeRowSeparator() {
		return ",";
	}
}
