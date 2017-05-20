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
package odata.service.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetIteratorRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientEntitySetIterator;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriParameter;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.QueryHints;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import odata.service.login.util.JWTFactory;
import persistence.User;
import persistence.UserSR;
import persistence.UserBean;

import persistence.User_Score;

import org.apache.commons.io.IOUtils;

public class Util {
	public static String sf_learning_key = "Basic amlheGluZzpjNjdmODBlODJlMWFkOGIzZjc0OGU1ODQ2YWQ5ODQ1Mzc2ZGU5NjU0ODNjNjM5NTAzMDZiMTAwYjdlMDhkMzFi";
	public static String type_post = "POST";
	public static String type_get = "GET";
	@PersistenceContext
	private static EntityManager em;
	private static final String PERSISTENCE_UNIT_NAME = "cloudhr_server";
	private static EntityManagerFactory factory;
	private List<Entity> userlist;
	@EJB
	private UserBean userBean;
	private static DataSource ds;
	private static EntityManagerFactory emf;

	public static Entity findEntity(EdmEntityType edmEntityType, EntityCollection entitySet,
			List<UriParameter> keyParams) throws ODataApplicationException {

		List<Entity> entityList = entitySet.getEntities();

		// loop over all entities in order to find that one that matches all
		// keys in request
		// e.g. contacts(ContactID=1, CompanyID=1)
		for (Entity entity : entityList) {
			boolean foundEntity = entityMatchesAllKeys(edmEntityType, entity, keyParams);
			if (foundEntity) {
				return entity;
			}
		}

		return null;
	}

	public static boolean entityMatchesAllKeys(EdmEntityType edmEntityType, Entity entity, List<UriParameter> keyParams)
			throws ODataApplicationException {

		// loop over all keys
		for (final UriParameter key : keyParams) {
			// key
			String keyName = key.getName();
			String keyText = key.getText();

			// Edm: we need this info for the comparison below
			EdmProperty edmKeyProperty = (EdmProperty) edmEntityType.getProperty(keyName);
			Boolean isNullable = edmKeyProperty.isNullable();
			Integer maxLength = edmKeyProperty.getMaxLength();
			Integer precision = edmKeyProperty.getPrecision();
			Boolean isUnicode = edmKeyProperty.isUnicode();
			Integer scale = edmKeyProperty.getScale();
			// get the EdmType in order to compare
			EdmType edmType = edmKeyProperty.getType();
			EdmPrimitiveType edmPrimitiveType = (EdmPrimitiveType) edmType;

			// Runtime data: the value of the current entity
			// don't need to check for null, this is done in olingo library
			Object valueObject = entity.getProperty(keyName).getValue();

			// now need to compare the valueObject with the keyText String
			// this is done using the type.valueToString
			String valueAsString;
			try {
				valueAsString = edmPrimitiveType.valueToString(valueObject, isNullable, maxLength, precision, scale,
						isUnicode);
			} catch (EdmPrimitiveTypeException e) {
				throw new ODataApplicationException("Failed to retrieve String value",
						HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ENGLISH, e);
			}

			if (valueAsString == null) {
				return false;
			}

			boolean matches = valueAsString.equals(keyText);
			if (!matches) {
				// if any of the key properties is not found in the entity, we
				// don't need to search further
				return false;
			}
		}

		return true;
	}

	public static String getURLResponse(String urlString, String auth, String payload, String type) {
		// Logger logger = LoggerFactory.getLogger(this.getClass());
		String result = null;
		OutputStream output = null;
		BufferedReader br = null;
		InputStreamReader isr = null;
		InputStream is = null;

		try {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// int statusCode2 = conn.getResponseCode();
			// if (conn instanceof HttpsURLConnection) {
			// disableSSLVerification((HttpsURLConnection) conn);
			// }
			if (type == type_post) {
				conn.setRequestMethod(HttpMethod.POST);
				conn.setRequestProperty(HttpHeaders.CONTENT_TYPE, "application/json");
			} else if (type == type_get) {
				conn.setRequestMethod(HttpMethod.GET);
				conn.setDoOutput(false);
				// conn.setRequestProperty(HttpHeaders.ACCEPT,
				// "application/json");
				// conn.setRequestProperty(HttpHeaders.CONTENT_TYPE,
				// "application/json");
			}
			// conn.setRequestMethod(HttpMethod.POST);
			conn.setRequestProperty(HttpHeaders.AUTHORIZATION, auth);

			if (payload != null) {
				conn.setDoOutput(true);
				output = conn.getOutputStream();
				output.write(payload.getBytes(StandardCharsets.UTF_8));
				output.flush();
				// int statusCode3 = conn.getResponseCode();
			}

			int statusCode = conn.getResponseCode();
			System.out.println(conn.getResponseMessage());
			if (400 <= statusCode || statusCode >= 599) {
				// logger.error("get post response failed, connection response
				// code: " + statusCode + ", message: "
				// + conn.getResponseMessage());

				return null;
			}

			is = conn.getInputStream();
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			String inputLine;
			StringBuilder response = new StringBuilder();
			while ((inputLine = br.readLine()) != null) {
				response.append(inputLine);
			}
			result = response.toString();
		} catch (IOException e) {

			// logger.error("get post response failed, connection response code:
			// " + e.getMessage());
		} finally {
			IOUtils.closeQuietly(output);
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(isr);
			IOUtils.closeQuietly(is);
		}
		return result;
	}

	private static void disableSSLVerification(HttpsURLConnection connection) {
		connection.setHostnameVerifier(new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		});
	}

	public static String update_score() {
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
		String status = new String();
		EntityManager em = emf.createEntityManager();
		em.setFlushMode(FlushModeType.COMMIT);
		// List<User> UserList = userBean.getAllUsers();
		String query = "select u from User u ";
		
		List<User> UserList = em.createQuery(query).setHint(QueryHints.REFRESH, HintValues.TRUE).getResultList();
	    query = "select usr from UserSR usr";
		//where usr.group_id = '" + UserList.get(i).getLearning_score_group() + "' and usr.course_id = '" + User_Score.getCourse_id() + "'";
        List<UserSR> usr = em.createQuery(query).setHint(QueryHints.REFRESH, HintValues.TRUE).getResultList();
        //query = "select usr from User_Score_Rule usr ";
		//List<User_Score_Rule> usrList = em.createQuery(query).getResultList();
		String user_id;
		String sf_user_id;
		String sf_user_type;
		String user_group;
		for (int i = 0; i < UserList.size(); i++) {
			
			user_id = UserList.get(i).getUser_id();
			user_group = UserList.get(i).getLearning_score_group();
			sf_user_id = UserList.get(i).getSf_learning_user();
			sf_user_type = UserList.get(i).getSf_learning_user_type();
			
			em = emf.createEntityManager();
			em.setFlushMode(FlushModeType.COMMIT);
			EntityTransaction et = em.getTransaction();
			et.begin();
			query = "delete from User_Score us where us.user_id = "+"'" + user_id + "'" ;
			em.createQuery(query).executeUpdate();
			et.commit();
			
			// 需要抽取
			JSONObject param = new JSONObject();
			JSONObject scope = new JSONObject();
			JSONObject param1 = new JSONObject();
			param.put("grant_type", "client_credentials");
			scope.put("userId", sf_user_id);
			scope.put("companyId", "jiaxing");
			scope.put("userType", sf_user_type);
			scope.put("resourceType", "learning_public_api");
			param.put("scope", scope);

			String jsonString = getURLResponse("https://jiaxing-stage.lms.sapsf.cn/learning/oauth-api/rest/v1/token",
					sf_learning_key, param.toString(), type_post);
			JSONObject js = new JSONObject(jsonString); 
			// String jsonresult =
			// getURLResponse("https://jiaxing-stage.lms.sapsf.cn/learning/odatav4/public/user/learningHistory/v1/"
			// + "learninghistorys?$filter=criteria/fromDate eq
			// 1493440919","Bearer " + js.getString("access_token"), null,
			// type_get);
			
			OkHttpClient client = new OkHttpClient();
			//String token = req.getHeader(JWTFactory.session_name);
			MediaType JSON = MediaType.parse("application/json; charset=utf-8");
			Request request = new Request.Builder()
			  .url("https://jiaxing-stage.lms.sapsf.cn/learning/odatav4/public/user/learningHistory/v1/learninghistorys?%24filter=criteria%2FmaxNumberToRetrieve%20eq%209999")
			  .get()
			  .addHeader("authorization", "Bearer " + js.getString("access_token"))
			  .addHeader("content-type", "application/json")
			  .addHeader("cache-control", "no-cache").build();
			  //.addHeader("postman-token", "7f5dc8c0-90a8-5785-5e1c-4da54aa3c16a")
			  //.build(); 

			Response response = null;
			try {
				response = client.newCall(request).execute();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//response.body()
			response.networkResponse().body();
			//boolean ss = response.isSuccessful();
		    if (!response.isSuccessful())
				try {
					throw new IOException("Unexpected code " + response);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	/*		OkHttpClient client = new OkHttpClient();

			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, "{\r\n    \"grant_type\": \"client_credentials\",\r\n    \"scope\": {\r\n        \"userId\": \"Eddiy\",\r\n        \"companyId\": \"jiaxing\",\r\n        \"userType\": \"user\",\r\n        \"resourceType\": \"learning_public_api\"\r\n    }\r\n}");
			Request request = new Request.Builder()
			  .url("https://jiaxing-stage.lms.sapsf.cn/learning/oauth-api/rest/v1/token")
			  .post(body)
			  .addHeader("content-type", "application/json")
			  .addHeader("authorization", "Basic amlheGluZzpjNjdmODBlODJlMWFkOGIzZjc0OGU1ODQ2YWQ5ODQ1Mzc2ZGU5NjU0ODNjNjM5NTAzMDZiMTAwYjdlMDhkMzFi")
			  .addHeader("cache-control", "no-cache")
			  .addHeader("postman-token", "90dc8cfc-521f-feb4-f054-d028764b9ffb")
			  .build();*/
			//Response response = client.newCall(request).execute();
		    JsonObject obj = null;
			try {
				obj = new JsonParser().parse(response.body().string()).getAsJsonObject();
			} catch (JsonSyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}//.get("value").getAsJsonObject();
		    JsonArray ja = obj.getAsJsonArray("value");
		    em = emf.createEntityManager();
			em.setFlushMode(FlushModeType.COMMIT);
			et = em.getTransaction();
			et.begin();

		    for(JsonElement ja_obj : ja){
				User_Score User_Score = new User_Score();
		    	JsonObject item = ja_obj.getAsJsonObject();
			    Set<Entry<String, JsonElement>> entrySet = item.entrySet();
			    for (Entry<String, JsonElement> entry : entrySet) {
			    	if(entry.getKey().equals("componentID")){
						User_Score.setCourse_id(entry.getValue().getAsString());
			    	}else if(entry.getKey().equals("revisionNumber")){
						if (entry.getValue().isJsonNull()) {
							User_Score.setCourse_version("0");
						} else {
							User_Score.setCourse_version(entry.getValue().getAsString());
						}
			    	}else if(entry.getKey().equals("title")){
						User_Score.setTitle(entry.getValue().getAsString());
			    	}else if(entry.getKey().equals("componentTypeID")){
						User_Score.setCourse_type(entry.getValue().getAsString());
			    	}
			    }
			    //String u_score = "5";
			   // EntityManager em2 = emf.createEntityManager();
			    boolean find_score = false;
			    for (int i1 = 0; i1 < usr.size(); i1++) {  
			    	if(usr.get(i1).getCourse_id() == null || usr.get(i1).getCourse_id().isEmpty()){
			    		continue;
			    	}
			        if(usr.get(i1).getGroup_id().equals(UserList.get(i).getLearning_score_group() )&& usr.get(i1).getCourse_id().equals(User_Score.getCourse_id())){
			        	 User_Score.setScore(usr.get(i1).getScore());
			        	 find_score = true;
			        	 break;
			         }
			    }  
			    if(find_score == false){

				    for (int i1 = 0; i1 < usr.size(); i1++) {  
				    	if(usr.get(i1).getCourse_type() == null || usr.get(i1).getCourse_type().isEmpty()){
				    		continue;
				    	}
				         if( usr.get(i1).getGroup_id().equals( UserList.get(i).getLearning_score_group() )&& usr.get(i1).getCourse_type().equals(User_Score.getCourse_type())){
				        	 User_Score.setScore(usr.get(i1).getScore());
				        	 find_score = true;
				        	 break;
				         }
				    }  
			    }
			    
			    if(find_score == false){
			    	User_Score.setScore("0");
			    }
				
				//User_Score.setScore(u_score);
				User_Score.setUser_id(user_id);
				//System.out.println(User_Score.getTitle().toString());
				em.merge(User_Score);
		    }
			
		    try {
				//et.begin();
				et.commit();

			} catch (Exception e) {
				System.out.println(e.getMessage());
				et.rollback();
			}

		}
		em.close();
		return status;

	}

	public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {
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
		em.setFlushMode(FlushModeType.COMMIT);
		// List<User> UserList = userBean.getAllUsers();
		String query = "select u from User2 u ";
		
		List<User> UserList = em.createQuery(query).setHint(QueryHints.REFRESH, HintValues.TRUE).getResultList();
		

	}

	private static String prettyPrint(Map<String, Object> properties, int level) {
		StringBuilder b = new StringBuilder();
		Set<Entry<String, Object>> entries = properties.entrySet();

		for (Entry<String, Object> entry : entries) {
			intend(b, level);
			b.append(entry.getKey()).append(": ");
			Object value = entry.getValue();
			if (value instanceof Map) {
				value = prettyPrint((Map<String, Object>) value, level + 1);
			} else if (value instanceof Calendar) {
				Calendar cal = (Calendar) value;
				value = SimpleDateFormat.getInstance().format(cal.getTime());
			}
			b.append(value).append("\n");
		}
		// remove last line break
		b.deleteCharAt(b.length() - 1);
		return b.toString();
	}

	private static void intend(StringBuilder builder, int intendLevel) {
		for (int i = 0; i < intendLevel; i++) {
			builder.append("  ");
		}
	}

	private static void print(String content) {
		System.out.println(content);
	}
}
