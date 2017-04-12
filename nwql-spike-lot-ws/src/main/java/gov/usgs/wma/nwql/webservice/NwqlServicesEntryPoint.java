package gov.usgs.wma.nwql.webservice;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

/**
 * Primary entry point to our NWQL Spike Lot webservices. (Standard Jersey
 * JAX-RS application).
 *
 * Wire up all configuration classes. Includes webservice, format, and error
 * handling provides.
 *
 * @author kmschoep
 */
@ApplicationPath("/service")
// NOTE: this matches web.xml
public class NwqlServicesEntryPoint extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		final Set<Class<?>> classes = new HashSet<Class<?>>();

		// webservices
		classes.add(DataViewWebservice.class);

		return classes;
	}
}
