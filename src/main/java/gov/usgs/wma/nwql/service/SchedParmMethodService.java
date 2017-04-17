package gov.usgs.wma.nwql.service;

import gov.usgs.wma.nwql.dao.SchedParmMethodDao;
import gov.usgs.wma.nwql.format.JsonStreamFormat;

import java.io.OutputStream;

/**
 * Service layer providing access to single tables/views.
 *
 * @author kmschoep
 *
 *
 */
public class SchedParmMethodService {

	/**
	 * Use this pattern in case of future dependency injection
	 */
	private SchedParmMethodDao schedParmMethodDao = new SchedParmMethodDao();

	public void setSingleViewDao(SchedParmMethodDao schedParmMethodDao) {
		this.schedParmMethodDao = schedParmMethodDao;
	}

	/**
	 * {@inheritDoc}
	 */
	public void streamData(OutputStream output,
			JsonStreamFormat format) {

		schedParmMethodDao.streamResults(output, format);
	}

	/**
	 * Gets the total row count for the table.
	 * @return
	 */
	public Integer getRowCount() {
		return schedParmMethodDao.getRowCount();
	}
}
