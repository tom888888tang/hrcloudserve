package odata.service.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import javax.ws.rs.core.HttpHeaders;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.cxf.helpers.IOUtils;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetIteratorRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientEntitySetIterator;
import org.apache.olingo.client.core.ODataClientFactory;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.json.JSONObject;

import odata.service.login.util.JWTFactory;
import odata.service.user.data.Storage;
import persistence.User;


public class LearningAPUtest extends HttpServlet{
    private DataSource ds;
    private EntityManagerFactory emf;
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(req, resp);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
    	/*PrintWriter out = resp.getWriter(); 
        String token = req.getHeader(JWTFactory.session_name);
        ODataClient client = ODataClientFactory.getClient();
		URI absoluteUri = client
				.newURIBuilder(
						"https://jiaxing-stage.lms.sapsf.cn/learning/odatav4/public/user/learningHistory/v1/")
				.appendEntitySetSegment("learninghistorys").filter("criteria/maxNumberToRetrieve eq 999999")
				.build();
		//System.out.println("URI = " + absoluteUri);
		
		ODataEntitySetIteratorRequest<ClientEntitySet, ClientEntity> request = client.getRetrieveRequestFactory()
				.getEntitySetIteratorRequest(absoluteUri);
		
		request.setAccept("application/json;odata.metadata=minimal");
		request.addCustomHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
		request.addCustomHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
		request.addCustomHeader("P3P", "CP=" + '"' + "OUR" + '"');
		request.addCustomHeader("Server", "undisclosed");
		ODataRetrieveResponse<ClientEntitySetIterator<ClientEntitySet, ClientEntity>> response = request.execute();
		ClientEntitySetIterator<ClientEntitySet, ClientEntity> iterator = response.getBody();
		while (iterator.hasNext()) {
			ClientEntity ce = iterator.next();
			out.write(ce.getProperties().toString());
		}
*/
		PrintWriter out = resp.getWriter();
		OkHttpClient client = new OkHttpClient();
		String token = req.getHeader(JWTFactory.session_name);
		MediaType JSON = MediaType.parse("application/json; charset=utf-8");
		Request request = new Request.Builder()
		  .url("https://jiaxing-stage.lms.sapsf.cn/learning/odatav4/public/user/learningHistory/v1/learninghistorys?%24filter=criteria%2FmaxNumberToRetrieve%20eq%2010")
		  .get()
		  .addHeader("authorization", "Bearer " + token)
		  .addHeader("content-type", "application/json")
		  .addHeader("cache-control", "no-cache").build();
		  //.addHeader("postman-token", "7f5dc8c0-90a8-5785-5e1c-4da54aa3c16a")
		  //.build(); 

		Response response = client.newCall(request).execute();
		//response.body()
		response.networkResponse().body();
		boolean ss = response.isSuccessful();
	    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
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
	    JsonObject obj = new JsonParser().parse(response.body().string()).getAsJsonObject();//.get("value").getAsJsonObject();
	    JsonArray ja = obj.getAsJsonArray("value");
	    for(JsonElement ja_obj : ja){
	    	JsonObject item = ja_obj.getAsJsonObject();
		    Set<Entry<String, JsonElement>> entrySet = item.entrySet();
		    for (Entry<String, JsonElement> entry : entrySet) {
		    	out.write(entry.getKey() + "/");
		    	out.write(entry.getValue() + "++");
		    }
	    }
	    //JsonObject obj = (JsonObject) new JsonParser().parse(response.body().string()).getAsJsonObject().get("value");
		//out.write(		response.body().string());
	}
	
}	
	


