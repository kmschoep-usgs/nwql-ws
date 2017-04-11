package gov.usgs.wma.nwql.spikelot.dao;

import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.*;
import org.codehaus.plexus.util.StringUtils;

public abstract class BaseSessionFactory {

	private static final Logger LOG = LoggerFactory.getLogger(BaseSessionFactory.class);

	protected static final Map<String, String> env = System.getenv();

	public abstract SqlSessionFactory getSessionFactory();

	protected static String getProperty(String key) {
		String result = !StringUtils.isEmpty(env.get(key)) ? env.get(key) : env.get(key.toUpperCase());

		if (result == null) {
			result = System.getProperty(key);
		}

		LOG.debug(String.format("Loaded environment or system property %s", key));

		return result;
	}
}
