package gov.usgs.wma.nwql.service;

import gov.usgs.wma.nwql.dao.SpikeLotDao;
import gov.usgs.wma.nwql.format.JsonStreamFormat;

import java.io.OutputStream;

/**
 * Service layer providing access to single tables/views.
 *
 * @author kmschoep
 *
 *
 */
public class SpikeLotService {

	/**
	 * Use this pattern in case of future dependency injection
	 */
	private SpikeLotDao spikeLotDao = new SpikeLotDao();

	public void setSingleViewDao(SpikeLotDao spikeLotDao) {
		this.spikeLotDao = spikeLotDao;
	}

	/**
	 * {@inheritDoc}
	 */
	public void streamData(OutputStream output,
			JsonStreamFormat format) {

		spikeLotDao.streamResults(output, format);
	}

	/**
	 * Gets the total row count for the table.
	 * @return
	 */
	public Integer getRowCount() {
		return spikeLotDao.getRowCount();
	}
}
