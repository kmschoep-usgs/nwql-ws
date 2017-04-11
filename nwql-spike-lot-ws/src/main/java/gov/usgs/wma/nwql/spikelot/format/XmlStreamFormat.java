package gov.usgs.nwis.reporting.format;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

import gov.usgs.nwis.reporting.api.format.INwisStreamFormat;
import gov.usgs.nwis.reporting.model.Column;
import gov.usgs.nwis.reporting.model.ParameterDescription;
import gov.usgs.nwis.reporting.model.ResultRow;

/**
 * Produces format: <?xml version="1.0" encoding="UTF-8" ?> <records>
 * <totalCount>number</totalCount> <pageSize>number</pageSize>
 * <startingRow>number</startingRow>
 * <record> <record></record> . . <record></record> </record> </records>
 *
 */
public class XmlStreamFormat implements INwisStreamFormat {

	// TODO hardcoded list is brittle, consider moving this out
	public static final String[] TABLES_CONTAINING_PII = new String[]{"site_owner",
		"site_contact", "gw_otid", "addr", "party", "site_owner_cur"};

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see gov.usgs.nwis.reporting.api.format.INwisStreamFormat #writeHeader
	 */
	@Override
	public String writeHeader(Integer total, Integer pageSize, Integer startingRow) {
		String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + "<collection>";

		if (total != null) {
			header += "<totalCount>" + total + "</totalCount>";
		}

		if (pageSize != null) {
			header += "<pageSize>" + pageSize + "</pageSize>";
		}

		if (startingRow != null) {
			header += "<startingRow>" + startingRow + "</startingRow>";
		}

		header += "<records>";
		return header;

		/*        return writeHeader(new LinkedHashMap<String, ParameterDescription>(),
		 new ArrayList<Column>());  */
	}

	/**
	 *
	 */
	@Override
	public String writeHeader(LinkedHashMap<String, ParameterDescription> queryInfo,
			List<Column> columnInfo) {
//        return writeHeader(null, null, null);
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + "<collection>");

		appendDisclaimer(sb);
		appendProprietaryDataWarning(sb, queryInfo, columnInfo);
		appendPrivacyWarning(sb, queryInfo, columnInfo);

		sb.append("<records>");

		return sb.toString();
	}

	/**
	 * Check query parameters for potential download of proprietary data
	 *
	 * @param sb The string builder to append to
	 */
	protected void appendProprietaryDataWarning(StringBuilder sb,
			LinkedHashMap<String, ParameterDescription> queryInfo, List<Column> columnInfo) {
		boolean containsProprietaryData = false;

		for (Column col : columnInfo) {
			if (col.getTableName() != null) {
				if (col.getTableName().toLowerCase().contains("sitefile")
						&& (!queryInfo.containsKey("sitefile.site_web_cd") || queryInfo
						.get("sitefile.site_web_cd").getValue().contains("P"))) {
					containsProprietaryData = true;
					break;
				} else {
					if (col.getTableName().toLowerCase().contains("gw_lev")
							&& (!queryInfo.containsKey("groundwater.lev_web_cd") || queryInfo
							.get("groundwater.lev_web_cd").getValue().contains("P"))) {
						containsProprietaryData = true;
						break;
					} else {
						if (col.getTableName().toLowerCase().contains("gw_netw")
								&& (!queryInfo.containsKey("legacynetworks.netw_web_cd") || queryInfo
								.get("legacynetworks.netw_web_cd").getValue().contains("P"))) {
							containsProprietaryData = true;
							break;
						} else {
							if (col.getTableName().toLowerCase().contains("gw_msvl")
									&& (!queryInfo.containsKey("misc.msvl_web_cd") || queryInfo
									.get("misc.msvl_web_cd").getValue().contains("P"))) {
								containsProprietaryData = true;
								break;
							} else {
								if (col.getTableName().toLowerCase().contains("gw_otid")
										&& (!queryInfo.containsKey("otid.otid_web_cd") || queryInfo
										.get("otid.otid_web_cd").getValue()
										.contains("P"))) {
									containsProprietaryData = true;
									break;
								} else {
									if ((col.getTableName().toLowerCase().contains("qw_sample") || col
											.getTableName().toLowerCase().contains("qw_result"))
											&& (!queryInfo.containsKey("waterquality.anl_stat_cd") || queryInfo
											.get("waterquality.anl_stat_cd").getValue().contains("P"))) {
										containsProprietaryData = true;
										break;
									} else {
										if ((col.getTableName().toLowerCase()
												.contains("qw_sample") || col.getTableName()
												.toLowerCase().contains("qw_result"))
												&& (!queryInfo.containsKey("waterquality.dqi_cd") || (queryInfo
												.get("waterquality.dqi_cd").getValue().contains("P")
												|| queryInfo.get("waterquality.dqi_cd").getValue()
												.contains("O") || queryInfo.get("waterquality.dqi_cd")
												.getValue().contains("X")))) {
											containsProprietaryData = true;
											break;
										}
									}
								}
							}
						}
					}
				}
			}
		}

		if (containsProprietaryData) {
			sb.append("<proprietaryData>");
            sb.append("*** WARNING *** ");
            sb.append("The data you are retrieving from this computer system may contain ");
            sb.append("Proprietary or Restricted data about sites, organizations, or individuals. ");
            sb.append("This data may not be released outside the USGS or shared with internal ");
            sb.append("organizational units without a legitimate need for the data. ");
            sb.append("Violations of this restriction can have severe consequences.");
            sb.append("</proprietaryData>");
		}
	}

	/**
	 * Check query parameters for potential download of Privacy Act data
	 *
	 * @param sb The string builder to append to
	 */
	protected void appendPrivacyWarning(StringBuilder sb,
			LinkedHashMap<String, ParameterDescription> queryInfo, List<Column> columnInfo) {
		boolean containsPrivacyData = false;

		for (Column col : columnInfo) {
			for (String piiTable : TABLES_CONTAINING_PII) {
				if (col.getTableName() != null
						&& col.getTableName().toLowerCase().contains(piiTable)) {
					containsPrivacyData = true;
					break;
				}
			}
			if (containsPrivacyData) {
				break;
			}
		}

		if (containsPrivacyData) {
			sb.append("<piiData>");
			sb.append("*** WARNING *** ");
			sb.append("Before you download Department of the Interior (DOI) data to a computer or any ");
			sb.append("other device capable of storing electronic data you must comply with DOI standards ");
			sb.append("for data encryption and system security. You must also understand and agree to ");
			sb.append("comply with DOI requirements for deleting the data. Contact your Bureau Chief ");
			sb.append("Information Security Officer for specifications regarding standards and requirements. ");
			sb.append("Failure to comply may result in criminal, civil, and/or disciplinary action.");
			sb.append(" ");
			sb.append("You are receiving this warning because the output may contain information about ");
			sb.append("individuals that are protected by the Privacy Act of 1974.");
			sb.append(" ");
			sb.append("http://www.usdoj.gov/opcl/privacyact1974.htm");
			sb.append(" ");
			sb.append("Data saved to a file must be manually tracked and deleted after 90 days as described in:");
			sb.append(" ");
			sb.append("http://nwis.usgs.gov/communications/PII_Data.html")
					.append("</piiData>");
		}
	}

	/**
	 * Append the disclaimer to a string builder
	 *
	 * @param sb The string builder to append to
	 */
	protected void appendDisclaimer(StringBuilder sb) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

		sb.append("<disclaimer>");
		sb.append("File created on ");
		sb.append(simpleDateFormat.format(Calendar.getInstance().getTime())).append(" ")
				.append(" ");
		sb.append("U.S. Geological Survey (USGS) ");
		sb.append("NWIS Data Portal and Reports").append(" ");
		sb.append("This file contains selected data for stations in the National Water ");
		sb.append("Information System (NWIS).").append(" ");
		sb.append("The data you have secured from the USGS NWIS database may include ");
		sb.append("data that have not received Director's approval and as such are ");
		sb.append("provisional and subject to revision.  The data are released on the ");
		sb.append("condition that neither the USGS nor the United States Government may ");
		sb.append("be held liable for any damages resulting from its authorized or ");
		sb.append("unauthorized use.").append("</disclaimer>");
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see gov.usgs.nwis.reporting.api.format.INwisStreamFormat #writeHeader
	 */
	@Override
	public String writeResultRow(ResultRow row) {
		String rowString = "<record>";
		for (String col : row.keySet()) {
			rowString += "<" + col + ">" + escapeSpecialXml(row.get(col)) + "</" + col + ">";
		}
		rowString += "</record>";
		return rowString;
	}

	private String escapeSpecialXml(Object input) {
		if (input == null) {
			return "";
		}
		String output = input.toString();
		output = output.replaceAll("[&]", "&amp;");
		output = output.replaceAll("[\"]", "&quot;");
		output = output.replaceAll("[']", "&apos;");
		output = output.replaceAll("[<]", "&lt;");
		output = output.replaceAll("[>]", "&gt;");
		return output;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see gov.usgs.nwis.reporting.api.format.INwisStreamFormat #writeFooter
	 */
	@Override
	public String writeFooter() {
		return "</records></collection>";
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see gov.usgs.nwis.reporting.api.format.INwisStreamFormat #writeResultRowHeaders
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
	 * @see gov.usgs.nwis.reporting.api.format.INwisStreamFormat #writeRowSeparater
	 */
	@Override
	public String writeRowSeparator() {
		return "";
	}
}
