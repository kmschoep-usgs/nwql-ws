package gov.usgs.wma.nwql.spikelot.service;

import gov.usgs.wma.nwql.spikelot.dao.SingleViewDao;
import gov.usgs.wma.nwql.spikelot.format.JsonStreamFormat;

import java.io.OutputStream;

/**
 * Service layer providing access to single tables/views.
 *
 * @author kmschoep
 *
 *
 */
public class DataViewService {

	/**
	 * Use this pattern in case of future dependency injection
	 */
	private SingleViewDao singleViewDao = new SingleViewDao();

	public void setSingleViewDao(SingleViewDao singleViewDao) {
		this.singleViewDao = singleViewDao;
	}

	/**
	 * {@inheritDoc}
	 */
	public void streamData(OutputStream output,
			JsonStreamFormat format) {

		singleViewDao.streamResults(output, format);
	}

	/**
	 * Gets the total row count for the table.
	 * @return
	 */
	public Integer getRowCount() {
		return singleViewDao.getRowCount();
	}
}
