package gov.usgs.wma.nwql.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.*;
import org.codehaus.plexus.util.StringUtils;

import gov.usgs.wma.nwql.exception.UninitializedFactoryException;

public class SessionFactory {
	private static final Logger LOG = LoggerFactory.getLogger(SessionFactory.class);

	private static final String MYBATIS_CONFIG_FILE = "mybatis/mybatis-config.xml";

	protected static final Map<String, String> env = System.getenv();

	protected static String getProperty(String key) {
		String result = !StringUtils.isEmpty(env.get(key)) ? env.get(key) : env.get(key.toUpperCase());

		if (result == null) {
			result = System.getProperty(key);
		}

		LOG.debug(String.format("Loaded environment or system property %s", key));

		return result;
}
	
	/**
	 * Singleton factory pattern for SQL session creation.
	 */
	public static final SqlSessionFactory SQL_SESSION_FACTORY;

	static {
		InputStream inputStream = null;
		try {
			inputStream = Resources.getResourceAsStream(MYBATIS_CONFIG_FILE);
		} catch (IOException e) {
			LOG.error("Error encountered while setting up SQL Session", e);
		}

		if (inputStream == null) {
			SQL_SESSION_FACTORY = null;
		} else {
			SQL_SESSION_FACTORY = new SqlSessionFactoryBuilder().build(inputStream);
		}
	}

	public SqlSessionFactory getSessionFactory() {
		if (SQL_SESSION_FACTORY == null) {
			throw new UninitializedFactoryException(
					"Mybatis configuration failed to load at startup.");
		}
		return SQL_SESSION_FACTORY;
	}
}
