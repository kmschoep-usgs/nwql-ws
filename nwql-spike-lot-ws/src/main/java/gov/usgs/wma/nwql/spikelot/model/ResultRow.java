package gov.usgs.wma.nwql.spikelot.model;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * Generic extension to LinkedHashMap so we can intercept them in Mybatis and
 * jersey
 *
 * @author kmschoep
 *
 *
 */
public class ResultRow extends LinkedHashMap<String, Object> implements Serializable {

	/**
	 * suid.
	 */
	private static final long serialVersionUID = 4324144710762879713L;
}
