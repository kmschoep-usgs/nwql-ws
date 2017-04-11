package gov.usgs.wma.nwql.spikelot.dao;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import gov.usgs.wma.nwql.spikelot.api.format.INwqlStreamFormat;

/**
 * Data access object for accessing a single table/view in the database.
 *
 * @author kmschoep
 *
 *
 */
public class SingleViewDao extends BaseDao {
	private static String ROWNUM_HEADER = "RN"; 

	/**
	 * Starts streaming filtered results
	 *
	 * @param params filter parameters
	 * @param output the output to stream results into
	 * @param format a formatter for configuring the data in steam
	 *
	 * @return
	 */
	public void streamResults(OutputStream output,
			INwqlStreamFormat format) {
		try (SqlSession session = getNewSession();
				OutputStream outputClosable = output;
				) {
			StreamingResultRowHandler streamingResultHandler = new StreamingResultRowHandler();
			streamingResultHandler.setOutput(outputClosable);
			streamingResultHandler.setFormat(format);
			outputClosable.write(format.writeHeader(null).getBytes());
			session.select(MYBATIS_CORE_PACKAGE + ".doSingleViewQuery",
					streamingResultHandler);
			outputClosable.write(format.writeFooter().getBytes());
		} catch (IOException e) {
			throw new RuntimeException("Error encountered while streaming results", e);
		} 
	}

	/**
	 * Returns the record count for a query
	 *
	 * @param params filter parameters
	 * @return
	 */
	public Integer getRowCount() {
		Integer result = null;
		try (SqlSession session = getNewSession()) {
			result = session.selectOne(MYBATIS_CORE_PACKAGE + ".doSingleViewQueryCount");
		} 
		return result;
	}

}
