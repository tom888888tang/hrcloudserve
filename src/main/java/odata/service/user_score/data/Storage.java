/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package odata.service.user_score.data;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import odata.service.user_score.service.EdmProvider;
import odata.service.util.Util;
import persistence.User_Score;
import persistence.User;
import persistence.UserBean;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriParameter;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.QueryHints;
import org.json.JSONObject;

public class Storage {
	@PersistenceContext
	private EntityManager em;
	private static final String PERSISTENCE_UNIT_NAME = "cloudhr_server";
	private static EntityManagerFactory factory;
	private List<Entity> userScoreList;
	@EJB
	// private UserBean userBean;
	private DataSource ds;
	private EntityManagerFactory emf;

	public Storage(String user_id, boolean is_total) {
		userScoreList = new ArrayList<Entity>();
		if (is_total == true) {
			initData_total(user_id);
		} else {
			initData(user_id);
		}

	}

	/* PUBLIC FACADE */

	public EntityCollection readEntitySetData(EdmEntitySet edmEntitySet) throws ODataApplicationException {

		// actually, this is only required if we have more than one Entity Sets
		if (edmEntitySet.getName().equals(EdmProvider.ES_NAME)) {
			return getUsers();
		}

		return null;
	}

	public Entity readEntityData(EdmEntitySet edmEntitySet, List<UriParameter> keyParams)
			throws ODataApplicationException {

		EdmEntityType edmEntityType = edmEntitySet.getEntityType();

		// actually, this is only required if we have more than one Entity Type
		if (edmEntityType.getName().equals(EdmProvider.ET_NAME)) {
			return getUser(edmEntityType, keyParams);
		}

		return null;
	}

	/* INTERNAL */

	private EntityCollection getUsers() {
		EntityCollection retEntitySet = new EntityCollection();

		for (Entity userEntity : this.userScoreList) {
			retEntitySet.getEntities().add(userEntity);
		}

		return retEntitySet;
	}

	private Entity getUser(EdmEntityType edmEntityType, List<UriParameter> keyParams) throws ODataApplicationException {

		// the list of entities at runtime
		EntityCollection entitySet = getUsers();

		/* generic approach to find the requested entity */
		Entity requestedEntity = Util.findEntity(edmEntityType, entitySet, keyParams);

		if (requestedEntity == null) {
			// this variable is null if our data doesn't contain an entity for
			// the requested key
			// Throw suitable exception
			throw new ODataApplicationException("Entity for requested key doesn't exist",
					HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ENGLISH);
		}

		return requestedEntity;
	}

	/* HELPER */

	public void initData(String user_id) {
		Connection connection = null;
		try {
			InitialContext ctx = new InitialContext();
			ds = (DataSource) ctx.lookup("java:comp/env/jdbc/DefaultDB");

			Map properties = new HashMap();
			properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, ds);
			emf = Persistence.createEntityManagerFactory("cloudhr_server", properties);
			// emf = Persistence.createEntityManagerFactory("cloudhr_server");
		} catch (NamingException e) {
			e.printStackTrace();
		}

		EntityManager em = emf.createEntityManager();
		// List<User> UserList = userBean.getAllUsers();
		String query = "select us from User_Score us where us.user_id = '" + user_id + "' ";
		List<User_Score> UserScoreList = em.createQuery(query).setHint(QueryHints.REFRESH, HintValues.TRUE).getResultList();
		// List<User> UserList = em.createNamedQuery("AllUsers",
		// User.class).getResultList();
		for (int i = 0; i < UserScoreList.size(); i++) {
			Entity et = new Entity();
			et.addProperty(new Property(null, "User_id", ValueType.PRIMITIVE, UserScoreList.get(i).getUser_id()))
					.addProperty(
							new Property(null, "Course_id", ValueType.PRIMITIVE, UserScoreList.get(i).getCourse_id()))
					.addProperty(new Property(null, "Course_version", ValueType.PRIMITIVE,
							UserScoreList.get(i).getCourse_version()))
					.addProperty(new Property(null, "Score", ValueType.PRIMITIVE, UserScoreList.get(i).getScore()))
					.addProperty(new Property(null, "Title", ValueType.PRIMITIVE, UserScoreList.get(i).getTitle()))
					.addProperty(new Property(null, "Course_type", ValueType.PRIMITIVE,
							UserScoreList.get(i).getCourse_type()));
			et.setId(createId("Users", UserScoreList.get(i).getUser_id()));
			et.setId(createId("Users", UserScoreList.get(i).getCourse_id()));
			et.setId(createId("Users", UserScoreList.get(i).getCourse_version()));

			userScoreList.add(et);
		}

		/*
		 * Entity et = new Entity(); et.addProperty(new Property(null,
		 * "User_id", ValueType.PRIMITIVE, '1' )) .addProperty(new
		 * Property(null, "Status", ValueType.PRIMITIVE, '1')) .addProperty(new
		 * Property(null, "Last_name", ValueType.PRIMITIVE, '1'))
		 * .addProperty(new Property(null, "First_name", ValueType.PRIMITIVE,
		 * '1')) .addProperty(new Property(null, "Mobile", ValueType.PRIMITIVE,
		 * '1')) .addProperty(new Property(null, "Wechat_id",
		 * ValueType.PRIMITIVE, '1')) .addProperty(new Property(null, "Sf_user",
		 * ValueType.PRIMITIVE, '1')) ; et.setId(createId("Users", 1));
		 * 
		 * userlist.add(et);
		 */

	}

	public void initData_total(String user_id) {
		//String acc_token = odata.service.wechat.web.Util.get_wechat_token();
		Connection connection = null;
		try {
			InitialContext ctx = new InitialContext();
			ds = (DataSource) ctx.lookup("java:comp/env/jdbc/DefaultDB");

			Map properties = new HashMap();
			properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, ds);
			emf = Persistence.createEntityManagerFactory("cloudhr_server", properties);
			// emf = Persistence.createEntityManagerFactory("cloudhr_server");
		} catch (NamingException e) {
			e.printStackTrace();
		}

		EntityManager em = emf.createEntityManager();
		// List<User> UserList = userBean.getAllUsers();
		String query = "select us from User_Score us ";
		List<User_Score> UserScoreList = em.createQuery(query).setHint(QueryHints.REFRESH, HintValues.TRUE).getResultList();
		// List<User> UserList = em.createNamedQuery("AllUsers",
		// User.class).getResultList();
		HashMap<String, Integer> score_total = new HashMap();
		for (int i = 0; i < UserScoreList.size(); i++) {

			if (score_total.get(UserScoreList.get(i).getUser_id()) == null) {
				score_total.put(UserScoreList.get(i).getUser_id(), Integer.parseInt(UserScoreList.get(i).getScore()));
			} else {
				score_total.put(UserScoreList.get(i).getUser_id(), Integer.parseInt(UserScoreList.get(i).getScore())
						+ score_total.get(UserScoreList.get(i).getUser_id()));
			}
		}
		//ValueComparator bvc =  new ValueComparator(score_total);  
        //TreeMap<String,Integer> sorted_map = new TreeMap<String,Integer>(bvc);  
        //sorted_map.putAll(score_total);
        score_total = (HashMap<String, Integer>) sortByValue(score_total);
		for (Map.Entry<String, Integer> entry : score_total.entrySet()) {
			query = "select us from User us where us.user_id = '" + entry.getKey() + "' ";
			List<User> User = em.createQuery(query).setHint(QueryHints.REFRESH, HintValues.TRUE).getResultList();
					//setHint(QueryHints.REFRESH, HintValues.TRUE);   

			Entity et = new Entity();
			et.addProperty(new Property(null, "User_id", ValueType.PRIMITIVE, entry.getKey()))
					.addProperty(new Property(null, "Score", ValueType.PRIMITIVE, entry.getValue()))
					.addProperty(new Property(null, "Manager", ValueType.PRIMITIVE, User.get(0).getManager()))
					.addProperty(new Property(null, "Position", ValueType.PRIMITIVE, User.get(0).getPosition()))
					.addProperty(new Property(null, "Department", ValueType.PRIMITIVE, User.get(0).getDepartment()))
			        .addProperty(new Property(null, "Region", ValueType.PRIMITIVE, User.get(0).getRegion()))
					;
			if (User.get(0).getWechat_id() == null ) {

			} else {
				if(User.get(0).getWechat_id().isEmpty()){
					
				}else{
				//JSONObject ret = new JSONObject(
						//odata.service.wechat.web.Util.get_wechat_userinfo(User.get(0).getWechat_id(),acc_token));
                
				et.addProperty(new Property(null, "Wechat_nickname", ValueType.PRIMITIVE, User.get(0).getWechat_nickname()))
						.addProperty(new Property(null, "Wechat_img", ValueType.PRIMITIVE, User.get(0).getWechat_img()));

				}
			}
			
			et.setId(createId("Users", entry.getKey()));
			userScoreList.add(et);

		}
		
		

	}

	/*
	 * Entity et = new Entity(); et.addProperty(new Property(null, "User_id",
	 * ValueType.PRIMITIVE, '1' )) .addProperty(new Property(null, "Status",
	 * ValueType.PRIMITIVE, '1')) .addProperty(new Property(null, "Last_name",
	 * ValueType.PRIMITIVE, '1')) .addProperty(new Property(null, "First_name",
	 * ValueType.PRIMITIVE, '1')) .addProperty(new Property(null, "Mobile",
	 * ValueType.PRIMITIVE, '1')) .addProperty(new Property(null, "Wechat_id",
	 * ValueType.PRIMITIVE, '1')) .addProperty(new Property(null, "Sf_user",
	 * ValueType.PRIMITIVE, '1')) ; et.setId(createId("Users", 1));
	 * 
	 * userlist.add(et);
	 */

	private URI createId(String entitySetName, Object id) {
		try {
			return new URI(entitySetName + "(" + String.valueOf(id) + ")");
		} catch (URISyntaxException e) {
			throw new ODataRuntimeException("Unable to create id for entity: " + entitySetName, e);
		}
	}
	
	class ValueComparator implements Comparator<String> {  
		  
	    Map<String, Integer> base;  
	    public ValueComparator(Map<String, Integer> base) {  
	        this.base = base;  
	    }  
	  
	    // Note: this comparator imposes orderings that are inconsistent with equals.      
	    public int compare(String a, String b) {  
	        if (base.get(a) >= base.get(b)) {  
	            return -1;  
	        } else {  
	            return 1;  
	        } // returning 0 would merge keys  
	    }  
	} 
	
    public  <K, V extends Comparable<? super V>> Map<K, V>   
    sortByValue( Map<K, V> map )  
{  
    List<Map.Entry<K, V>> list =  
        new LinkedList<Map.Entry<K, V>>( map.entrySet() );  
    Collections.sort( list, new Comparator<Map.Entry<K, V>>()  
    {  
        public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )  
        {  
            return (o2.getValue()).compareTo( o1.getValue() );  
        }  
    } );  

    Map<K, V> result = new LinkedHashMap<K, V>();  
    for (Map.Entry<K, V> entry : list)  
    {  
        result.put( entry.getKey(), entry.getValue() );  
    }  
    return result;  
}
}
