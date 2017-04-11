package gov.usgs.wma.nwql.spikelot.webservice;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import gov.usgs.wma.nwql.spikelot.webservice.providers.ResulRowCollectionProvider;

import java.util.HashSet;
import java.util.Set;

/**
 * Primary entry point to our NWIS Reporting webservices. (Standard Jersey
 * JAX-RS application).
 *
 * Wire up all configuration classes. Includes webservice, format, and error
 * handling provides.
 *
 * @author thongsav-usgs
 */
@ApplicationPath("/service")
// NOTE: this matches web.xml
public class NwqlServicesEntryPoint extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		final Set<Class<?>> classes = new HashSet<Class<?>>();

		// webservices
		classes.add(RootWebservice.class);
		classes.add(SearchWebservice.class);
		classes.add(ReportInformationWebservice.class);
		classes.add(ReferenceValuesWebservice.class);
		classes.add(DataViewWebservice.class);
		classes.add(ReportWebservice.class);

		// format providers
		classes.add(ResulRowCollectionProvider.class);
		classes.add(ResultRowCollectionListProvider.class);

		// error handling providers
		classes.add(UnauthorizedExceptionMapper.class);
		classes.add(ViewNotFoundExceptionMapper.class);
		classes.add(LookupNotFoundExceptionMapper.class);
		classes.add(ReportTypeNotFoundExceptionMapper.class);
		classes.add(InvalidParameterExceptionMapper.class);
		classes.add(NotAllowedExceptionMapper.class);
		classes.add(NotFoundExceptionMapper.class);
		classes.add(TooManyColumnsExceptionMapper.class);
		classes.add(PersistenceExceptionMapper.class);
		classes.add(UninitializedFactoryExceptionMapper.class);
		classes.add(UnknownExceptionMapper.class);

		
		// Security components
		if(!AuthClientSingleton.isInitialized()) {
			AuthClientSingleton.initAuthClient(CachingAuthClient.class);
		}
		classes.add(NwisAuthTokenService.class);
		classes.add(NwisTokenBasedSecurityFilter.class);
		classes.add(NotAuthorizedExceptionMapper.class);
		
		return classes;
	}
}
