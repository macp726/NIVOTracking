package mg.nivo.tracking;

import javax.naming.NamingException;

import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

public class SpringTestUtils {
	public static PGSimpleDataSource installContext(SimpleNamingContextBuilder builder) throws IllegalStateException,
			NamingException {
		PGSimpleDataSource ds = new PGSimpleDataSource();
//		ds.setDatabaseName("itinerarydb");
//		ds.setServerName("190.26.198.130"); 
//		ds.setPortNumber(5436); 
		
		ds.setDatabaseName("edwintests");
		ds.setServerName("localhost"); 
		ds.setPortNumber(5432);
		
		ds.setUser("postgres");
		ds.setPassword("musica-2");
		builder.bind("java:comp/env/jdbc/trackingDS", ds);
		
		builder.bind("java:comp/env/myVar", "MyTestValue");
		
		return ds;
	}

}
