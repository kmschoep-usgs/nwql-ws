package gov.usgs.wma.nwql.spikelot.webservice.providers;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

import gov.usgs.wma.nwql.spikelot.format.JsonStreamFormat;
import gov.usgs.wma.nwql.spikelot.format.XmlStreamFormat;
import gov.usgs.wma.nwql.spikelot.model.ResultRow;
import gov.usgs.wma.nwql.spikelot.model.ResultRowCollection;

/**
 * This is a MessageBodyWriter for jersey to write a ResultRowCollection(which
 * is a wrapper for ResultRowCollection) object into the following types:
 * MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
 * NwisOutputFormat.TEXT_RDB
 *
 * @author thongsav
 *
 *
 */
public class ResulRowCollectionProvider
		implements MessageBodyWriter<ResultRowCollection> {

	/**
	 * Determines if this provider should be used. Returns true for
	 * ResultRowCollection and MediaTypes of JSON, XML, or RDB.
	 *
	 * return boolean if this provider is to be used.
	 *
	 * @param type
	 * @param genericType
	 * @param annotations
	 * @param mediaType
	 * @return
	 */
	@Override
	public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
		try {
			return isSupportedMedia(mediaType) && ResultRowCollection.class.equals(type);
		} catch (Exception e) {
			// Don't log because this is a very common call and the reason for failure isn't currently important
			return false;
		}
	}

	/**
	 * Private helper function to determine if mediatype is one of our supported
	 * types (XML, JSON, RDB)
	 *
	 * @param mediaType
	 * @return
	 */
	private boolean isSupportedMedia(final MediaType mediaType) {
		return mediaType.toString().equals(MediaType.APPLICATION_JSON)
				|| mediaType.toString().equals(MediaType.APPLICATION_XML)
				|| mediaType.toString().equals(NwqlOutputFormat.XML_DOWNLOAD)
				|| mediaType.toString().equals(NwqlOutputFormat.JSON_DOWNLOAD);
	}

	/**
	 * API function. We never know the actual size.
	 *
	 * @param myObjectTypeInstance
	 * @param type
	 * @param genericType
	 * @param annotations
	 * @param mediaType
	 * @return
	 */
	@Override
	public long getSize(final ResultRowCollection myObjectTypeInstance, final Class<?> type, final Type genericType,
			final Annotation[] annotations, final MediaType mediaType) {
		// return the exact response length if you know it... -1 otherwise
		return -1;
	}

	/**
	 * Depending on the MediaType, start serializing our ResultRowCollection
	 * into RDB, XML, or JSON.
	 *
	 * @param myObjectTypeInstance
	 * @param type
	 * @param genericType
	 * @param annotations
	 * @param mediaType
	 * @param httpHeaders
	 * @param entityStream
	 * @throws java.io.IOException
	 */
	@Override
	public void writeTo(final ResultRowCollection myObjectTypeInstance,
			final Class<?> type, final Type genericType,
			final Annotation[] annotations,
			final MediaType mediaType,
			final MultivaluedMap<String, Object> httpHeaders,
			final OutputStream entityStream) throws IOException, WebApplicationException {
		switch (mediaType.toString()) {
			case MediaType.APPLICATION_JSON:
				writeJson(myObjectTypeInstance, type, genericType, annotations, mediaType, httpHeaders, entityStream);
				break;
			case MediaType.APPLICATION_XML:
				writeXml(myObjectTypeInstance, type, genericType, annotations, mediaType, httpHeaders, entityStream);
				break;
			default:
				throw new RuntimeException("Media type not supported by this writer");
		}

	}

	/**
	 * Private implementation for JSON format. Format: { "totalCount" :
	 * "recordCount", "pageSize" : "numberofrecords", "startingRow" : "rowNum",
	 * "records": [ {<recordJson>}, ..] }
	 *
	 * @param myObjectTypeInstance
	 * @param type
	 * @param genericType
	 * @param annotations
	 * @param mediaType
	 * @param httpHeaders
	 * @param entityStream
	 * @throws IOException
	 * @throws WebApplicationException
	 */
	private void writeJson(final ResultRowCollection myObjectTypeInstance,
			final Class<?> type, final Type genericType,
			final Annotation[] annotations,
			final MediaType mediaType,
			final MultivaluedMap<String, Object> httpHeaders,
			final OutputStream entityStream) throws IOException, WebApplicationException {
		JsonStreamFormat format = new JsonStreamFormat();
		String xmlHeader = format.writeHeader(myObjectTypeInstance.getListSize());
		entityStream.write(xmlHeader.getBytes());

		boolean firstRow = true;
		for (ResultRow row : myObjectTypeInstance.getList()) {
			if (firstRow) {
				firstRow = false;
			} else {
				entityStream.write(format.writeRowSeparator().getBytes());
			}
			entityStream.write(format.writeResultRow(row).getBytes());
		}
		entityStream.write(format.writeFooter().getBytes());
	}

	/**
	 * Private implementation for XML format. Format:
	 * <?xml version="1.0" encoding="UTF-8" ?>
	 * <records>
	 * <totalCount>number</totalCount>
	 * <pageSize>number</pageSize>
	 * <startingRow>number</startingRow>
	 * <record>all record elements</record>
	 * </records>
	 *
	 * @param myObjectTypeInstance
	 * @param type
	 * @param genericType
	 * @param annotations
	 * @param mediaType
	 * @param httpHeaders
	 * @param entityStream
	 * @throws IOException
	 * @throws WebApplicationException
	 */
	private void writeXml(final ResultRowCollection myObjectTypeInstance,
			final Class<?> type, final Type genericType,
			final Annotation[] annotations,
			final MediaType mediaType,
			final MultivaluedMap<String, Object> httpHeaders,
			final OutputStream entityStream) throws IOException, WebApplicationException {
		XmlStreamFormat format = new XmlStreamFormat();
		String xmlHeader = format.writeHeader(
				myObjectTypeInstance.getListSize(),
				myObjectTypeInstance.getPageSize(),
				myObjectTypeInstance.getStartingRow()
		);
		entityStream.write(xmlHeader.getBytes());
		for (ResultRow row : myObjectTypeInstance.getList()) {
			entityStream.write(format.writeResultRow(row).getBytes());
		}
		entityStream.write(format.writeFooter().getBytes());
	}

	/**
	 * Private implementation for RDB format. Format: # #Total Count: number
	 * #Page Size: number #Starting Row: number tab separated header row tab
	 * separated records rows
	 *
	 *
	 * @param myObjectTypeInstance
	 * @param type
	 * @param genericType
	 * @param annotations
	 * @param mediaType
	 * @param httpHeaders
	 * @param entityStream
	 * @throws IOException
	 * @throws WebApplicationException
	 */
	private void writeRdb(final ResultRowCollection myObjectTypeInstance,
			final Class<?> type, final Type genericType,
			final Annotation[] annotations,
			final MediaType mediaType,
			final MultivaluedMap<String, Object> httpHeaders,
			final OutputStream entityStream) throws IOException, WebApplicationException {

		RdbStreamFormat format = new RdbStreamFormat();

		String comments = format.writeHeader(
				myObjectTypeInstance.getListSize(),
				myObjectTypeInstance.getPageSize(),
				myObjectTypeInstance.getStartingRow()
		);
		entityStream.write(comments.getBytes());

		entityStream.write(format.writeResultRowHeaders(myObjectTypeInstance.getList().get(0), new ArrayList<String>()).getBytes());

		for (ResultRow row : myObjectTypeInstance.getList()) {
			entityStream.write(format.writeResultRow(row).getBytes());
			entityStream.write(format.writeRowSeparator().getBytes());
		}
	}
}
