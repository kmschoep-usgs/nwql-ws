package gov.usgs.wma.nwql.spikelot.dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.*;


/**
 * Base class for all DAO objects. Right now being used as the mybatis loader
 * and SQL Session factory provider. 
 *
 * @author kmschoep
 *
 */
public class BaseDao {

	public static final String MYBATIS_CORE_PACKAGE = "gov.usgs.wma.nwql.spikelot.mybatis";
	
	private static final Logger LOG = LoggerFactory.getLogger(BaseDao.class);

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
}
