package core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.sql.DataSource;

import odata.service.tool.StringUtil;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.QueryHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import persistence.Customer_Datasource;

public class DSUtill {
	private static Logger log = LoggerFactory.getLogger(DSUtill.class);
	private static String SERVICE_NAME = "cloudhr_server";
	
	public static Map<String, String> dbMap = new HashMap<String, String>();

	public static EntityManagerFactory getDS(String customerId) {
		String jndiName = getJndiName(customerId);
		try {
			InitialContext ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:comp/env/"+jndiName);

			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, ds);

			EntityManagerFactory emf = Persistence.createEntityManagerFactory(SERVICE_NAME, properties);

			return emf;
		} catch (NamingException e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private static String getJndiName(String customerId) {
		if(StringUtil.isEmpty(customerId)) {
			return "jdbc/DefaultDB";
		}
		
		if(!StringUtil.isEmpty(dbMap.get(customerId))) {
			return dbMap.get(customerId);
		}
		
		try {
			InitialContext ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/DefaultDB");

			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, ds);

			EntityManagerFactory emf = Persistence.createEntityManagerFactory(SERVICE_NAME, properties);
			
			EntityManager em = emf.createEntityManager();
			EntityTransaction newTx = em.getTransaction();
			newTx.begin();
			String query = "select t from Customer_Datasource t where t.customer_id = '" + customerId + "' and t.status = '1' ";
			List<Customer_Datasource> result = em.createQuery(query).setHint(QueryHints.REFRESH, HintValues.TRUE).getResultList();
			newTx.commit();
			
			if (result.size() == 1) {
				Customer_Datasource dataSource = result.get(0);
				String jndiName = dataSource.getJndi_name();
				
				dbMap.put(customerId, jndiName);
				return jndiName;
			}else{
				throw new Exception("未找到数据源配置，请检查！customerId:"+customerId);
			}
				

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return "jdbc/DefaultDB";
		}
		
	}
}
