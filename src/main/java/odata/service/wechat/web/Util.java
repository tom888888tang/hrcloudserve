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
package odata.service.wechat.web;

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
import javax.servlet.http.HttpServletRequest;
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
import org.json.JSONException;
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
import persistence.UserBean;
import persistence.User_Score;
import persistence.Wechat_AccToken;

import org.apache.commons.io.IOUtils;

public class Util {
	// tom: wxe2d99d242b3d089f
	// caoping: wxf3c616f02c3b8af7
	public static String appid = "wxe2d99d242b3d089f";
	// tom: c1727080e25ab65b8a2892fe29c443af
	// caoping: 12825c43eab4a6d173d577482015bb23
	public static String secret = "c1727080e25ab65b8a2892fe29c443af";

	private EntityManager em;
	private static final String PERSISTENCE_UNIT_NAME = "cloudhr_server";
	private static EntityManagerFactory factory;
	private List<Entity> userScoreList;
	private static DataSource ds;
	private static EntityManagerFactory emf;

	public static String get_wechat_token() {
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
		String query = "select token from Wechat_AccToken token ";
		List<Wechat_AccToken> AccToken = em.createQuery(query).setHint(QueryHints.REFRESH, HintValues.TRUE).getResultList();
		return AccToken.get(0).getToken();

	}

	public static String get_wechat_token_save() {
		OkHttpClient client = new OkHttpClient();

		Request request = new Request.Builder()
				.url("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appid + "&secret="
						+ secret)
				.get().addHeader("cache-control", "no-cache").build();

		Response response;
		try {
			response = client.newCall(request).execute();
			return (new JSONObject(response.body().string())).getString("access_token");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}

	}

	public static String get_wechat_userinfo(String openid) {
		OkHttpClient client = new OkHttpClient();
		String acc_token = get_wechat_token();
		MediaType mediaType = MediaType.parse("application/octet-stream");
		Request request = new Request.Builder().url("https://api.weixin.qq.com/cgi-bin/user/info?access_token="
				+ acc_token + "&openid=" + openid + "&lang=zh_CN").get().addHeader("cache-control", "no-cache").build();

		try {
			Response response = client.newCall(request).execute();
			return response.body().string();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}

	}

	public static String get_wechat_userinfo(String openid, String acc_token) {
		OkHttpClient client = new OkHttpClient();
		// String acc_token = get_wechat_token();
		MediaType mediaType = MediaType.parse("application/octet-stream");
		Request request = new Request.Builder().url("https://api.weixin.qq.com/cgi-bin/user/info?access_token="
				+ acc_token + "&openid=" + openid + "&lang=zh_CN").get().addHeader("cache-control", "no-cache").build();

		try {
			Response response = client.newCall(request).execute();
			return response.body().string();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}

	}

	public static String get_wechat_token(String code) {
		OkHttpClient client = new OkHttpClient();

		Request request = new Request.Builder().url("https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appid
				+ "&secret=" + secret + "&code=" + code + "&grant_type=authorization_code").get()
				.addHeader("cache-control", "no-cache").build();

		Response response;
		try {
			response = client.newCall(request).execute();
			// JSONObject jj = new JSONObject(response.body().string());
			// String access_token = jj.getString("access_token");
			return response.body().string();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}

	}

}
