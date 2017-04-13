package gov.usgs.wma.nwql.spikelot.format;

import com.google.gson.Gson;

import gov.usgs.wma.nwql.spikelot.model.ResultRow;

/**
 *
 * Produces format: {"records": [ {<recordJson>}, ..]
 * }
 */
public class JsonStreamFormat {

	//used to help conversion to json
	Gson g = new Gson();

	public String writeHeader(Integer total) {
		String header = "{";

		if (total != null) {
			header += "\"totalCount\": \"" + total + "\",";
		}

		header += "\"records\":[";
		return header;
	}

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

	public String writeFooter() {
		return "]}";
	}

	public String writeResultRowHeaders(ResultRow row) {
		return "";
	}

	public String writeDataTypeRow(ResultRow row) {
		return "";
	}

	public String writeRowSeparator() {
		return ",";
	}
}
