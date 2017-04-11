package gov.usgs.wma.nwql.spikelot.api.format;

import java.util.List;

import gov.usgs.wma.nwql.spikelot.model.Column;
import gov.usgs.wma.nwql.spikelot.model.ResultRow;

/**
 * The INwisStreamFormat provides a way for data to be serialized into various
 * formats.
 *
 * All formats consist of a.. header - data which appears at the start of a
 * dataset, such as metadata or opening tags result row header - optional header
 * row for CSV/Tab formats result rows - serialized ResultRows result row
 * separators - a way to delineate between rows (eg: commas for JSON, and
 * newline for CSV/TAB) footer - data which ends a data set (eg: closing tag)
 *
 * @author kmschoep
 *
 */
public interface INwqlStreamFormat {

	/**
	 * Header, will include opening tags and/or metadata. 
	 *
	 * @param total
	 * @return A string containing the header
	 */
	public String writeHeader(Integer total);

	/**
	 * Footer
	 *
	 * @return
	 */
	public String writeFooter();

	/**
	 * A way to delineate between rows (eg: commas for JSON, and newline for
	 * CSV/TAB)
	 *
	 * @return
	 */
	public String writeRowSeparator();

	/**
	 * How to seralize a given ResultRow
	 *
	 * @param row
	 * @return
	 */
	public String writeResultRow(ResultRow row);

	/**
	 * Optional way to insert a header
	 *
	 * @param row
	 * @return
	 */
	public String writeResultRowHeaders(ResultRow row, List<String> exclusionList);

	/**
	 * Optional way to insert a header
	 *
	 * @param row
	 * @return
	 */
	public String writeDataTypeRow(ResultRow row, List<String> exclusionList);
}
