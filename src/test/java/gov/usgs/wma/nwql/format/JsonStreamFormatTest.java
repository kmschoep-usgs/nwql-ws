package gov.usgs.wma.nwql.format;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import gov.usgs.wma.nwql.model.ResultRow;
import gov.usgs.wma.nwql.format.JsonStreamFormat;

/**
 * Test harness for JsonStreamFormat class. 
 * @author kmschoep
 */
public class JsonStreamFormatTest {
	/**
	 * The format that can be reused for all the tests
	 */
	protected JsonStreamFormat format = null;

	/**
	 * The name of the format, to be overridden by subclasses
	 */
	protected String formatName = null;
	
	/**
	 * No-argument constructor. Overrides format with JsonStreamFormat and
	 * formatName with "JSON"
	 */
	public JsonStreamFormatTest() {
		format = new JsonStreamFormat();
		formatName = "JSON";
	}

	/**
	 * Make sure things are escaped
	 */
	@Test
	public void writeResultRow() {
		ResultRow row = new ResultRow();
		row.put("ABC", "12\"3");
		row.put("JKL", "4\n56");
		row.put("EFG", "7\r89");
		String line = format.writeResultRow(row);

		assertTrue(line.contains("12\\\"3")); //escaped quote
		assertTrue(line.contains("4\\n56")); //escapted newline
		assertTrue(line.contains("7\\r89")); //escapted newline
	}
}
