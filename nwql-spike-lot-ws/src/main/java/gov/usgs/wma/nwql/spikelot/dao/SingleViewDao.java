package gov.usgs.wma.nwql.spikelot.dao;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import gov.usgs.wma.nwql.spikelot.format.JsonStreamFormat;

/**
 * Data access object for accessing a single table/view in the database.
 *
 * @author kmschoep
 *
 *
 */
public class SingleViewDao {
	private static String ROWNUM_HEADER = "RN"; 
	public static final String MYBATIS_CORE_PACKAGE = "gov.usgs.wma.nwql.spikelot.mybatis";
	
	private SqlSessionFactory sessionFactory;

	public void setSessionFactory(SqlSessionFactory newFactory) {
		sessionFactory = newFactory;
	}

	public SqlSession getNewSession() {
		if (sessionFactory == null) {
			sessionFactory = (new SessionFactory()).getSessionFactory();
		}
		SqlSession session = sessionFactory.openSession(true);
		return session;
	}
		
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
			JsonStreamFormat format) {
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
