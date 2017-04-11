package gov.usgs.wma.nwql.spikelot.api.service;

import gov.usgs.wma.nwql.spikelot.api.format.INwqlStreamFormat;

import java.io.OutputStream;

public interface IDataViewService {

	/**
	 * Returns table records, serializes results using INwqlStreamFormat into an output stream.
	 *
	 * @param output the output stream to stream results into
	 * @param format a formatter for serialize the data into the stream
	 */
	public void streamData(OutputStream output,
			INwqlStreamFormat format);
	
	/**
	 * Gets the total row count for the given parameters against the view.
	 * 
	 * @return integer count
	 */
	public Integer getRowCount();
}
