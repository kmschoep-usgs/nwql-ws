package gov.usgs.wma.nwql.spikelot.dao;

import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.*;

import gov.usgs.wma.nwql.spikelot.exception.UninitializedFactoryException;

public class SessionFactory extends BaseSessionFactory {
	private static final Logger LOG = LoggerFactory.getLogger(SessionFactory.class);

	private static final String MYBATIS_CONFIG_FILE = "mybatis/mybatis-config.xml";
	
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

	@Override
	public SqlSessionFactory getSessionFactory() {
		if (SQL_SESSION_FACTORY == null) {
			throw new UninitializedFactoryException(
					"Mybatis configuration failed to load at startup.");
		}
		return SQL_SESSION_FACTORY;
	}
}
