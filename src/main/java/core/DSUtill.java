package core;

import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DSUtill {
	private static Logger log = LoggerFactory.getLogger(DSUtill.class);
	private static String SERVICE_NAME = "cloudhr_server";

	public EntityManagerFactory getDS(String customerId) {
		String jndiName = getJndiName(customerId);
		try {
			InitialContext ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup(jndiName);

			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, ds);

			EntityManagerFactory emf = Persistence.createEntityManagerFactory(SERVICE_NAME, properties);

			return emf;
		} catch (NamingException e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	private String getJndiName(String customerId) {
		return "java:comp/env/jdbc/DefaultDB";
	}
}
