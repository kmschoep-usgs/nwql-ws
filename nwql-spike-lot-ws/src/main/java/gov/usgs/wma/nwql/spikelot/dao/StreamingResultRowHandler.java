package gov.usgs.wma.nwql.spikelot.dao;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.slf4j.*;

import gov.usgs.wma.nwql.spikelot.api.format.INwqlStreamFormat;
import gov.usgs.wma.nwql.spikelot.model.ResultRow;

/**
 * Used by mybatis, simple row to ResultRow conversion (this is just a LinkMap
 * of column-value pairs)
 *
 * @author kmschoep
 *
 */
public class StreamingResultRowHandler implements ResultHandler {

	private static final Logger LOG = LoggerFactory.getLogger(StreamingResultRowHandler.class);

	private OutputStream output;

	private boolean handleResultCalled = false;

	public void setOutput(OutputStream output) {
		this.output = output;
	}

	INwqlStreamFormat format;

	public void setFormat(INwqlStreamFormat format) {
		this.format = format;
	}

	@Override
	public void handleResult(ResultContext resultContext) {
		if (!handleResultCalled) {
			handleResultCalled = true;
			LOG.debug("Streaming started: " + System.currentTimeMillis());
		}

		ResultRow record = (ResultRow) resultContext.getResultObject();
		try {
			if (resultContext.getResultCount() > 1) {
				output.write(format.writeRowSeparator().getBytes());
			}
			output.write(format.writeResultRow(record).getBytes());
		} catch (IOException e) {
			LOG.error("Error processing result #"
					+ resultContext.getResultCount(), e);
			// we wan to stop execution, so throw runtime
			throw new RuntimeException("Error processing result #"
					+ resultContext.getResultCount(), e);
		}
	}
}
