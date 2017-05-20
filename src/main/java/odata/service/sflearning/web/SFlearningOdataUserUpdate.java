package odata.service.sflearning.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.cxf.helpers.IOUtils;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.QueryHints;
import org.json.JSONObject;

import odata.service.login.util.JWTFactory;
import odata.service.user.data.Storage;
import odata.service.util.Util;
import persistence.User;


public class SFlearningOdataUserUpdate extends HttpServlet{
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

		// We need a signing key, so we'll create one just for this example. Usually
		// the key would be read from your application configuration instead.
//		String json = IOUtils.toString(req.getInputStream()); 
//		JSONObject jsonObject = new JSONObject(json);
//		String user_id = jsonObject.getString("");
//		//String user_id = getParameter("12");
//		String password = jsonObject.getString("Password");
		//String customer_id = jsonObject.getString("Customer_id");
		Connection connection = null;
        try {
            InitialContext ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("java:comp/env/jdbc/DefaultDB");

            Map properties = new HashMap();
            properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, ds);
            emf = Persistence.createEntityManagerFactory("cloudhr_server", properties);
            //emf = Persistence.createEntityManagerFactory("cloudhr_server");
        } catch (NamingException e) {
            e.printStackTrace();
        }
		//EntityManagerFactory factory = Persistence.createEntityManagerFactory("cloudhr_server");
		EntityManager em = emf.createEntityManager();		
		EntityTransaction newTx = em.getTransaction();
	    newTx.begin();
	    HttpSession session = req.getSession(true);
	    String query = "select u from User u "; 
        List<User> result = em.createQuery(query).setHint(QueryHints.REFRESH, HintValues.TRUE).getResultList();
        newTx.commit();
        newTx.begin();
        for(int i = 0 ; i < result.size(); i++){
        	User user = result.get(i);
        	String ip = ip = req.getRemoteAddr();
//            if (req.getHeader("x-forwarded-for") == null) {  
            	//ip = req.getRemoteAddr();  
//            }else{  
//                ip = req.getHeader("x-forwarded-for");  
//            } 
            //String jwt = JWTFactory.getJWT(user);
            //Gson gson = new Gson();
        	resp.setStatus(200);
        	//PrintWriter out = resp.getWriter();
        	//out.write(user_json);
        	//String user_json = gson.toJson(result);

    	    JSONObject param = new JSONObject();
    	    JSONObject scope = new JSONObject();

    	    param.put("grant_type", "client_credentials");
    	    scope.put("userId", "PLATEAU");
    	    scope.put("companyId", "jiaxing");
    	    scope.put("userType", "admin");
    	    scope.put("resourceType", "learning_public_api");
    	    param.put("scope", scope);
    	    //out.write(param);
    	    String jsonString = Util.getURLResponse("https://jiaxing-stage.lms.sapsf.cn/learning/oauth-api/rest/v1/token",Util.sf_learning_key, param.toString(), Util.type_post);
    	    JSONObject js = new JSONObject(jsonString);
    	    OkHttpClient client = new OkHttpClient();
			//String token = req.getHeader(JWTFactory.session_name);
			MediaType JSON = MediaType.parse("application/json; charset=utf-8");
			Request request = new Request.Builder()
			  .url("https://jiaxing-stage.lms.sapsf.cn/learning/odatav4/searchStudent/v1/Students?$filter=criteria/learnerID eq '" + user.getSf_learning_user() + "'")
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
		    User u = result.get(i);
		    JSONObject jst = new JSONObject(response.body().string());
		    JSONObject student = jst.getJSONArray("value").getJSONObject(0);
    		u.setDepartment(student.getString("orgID"));
        	u.setPosition(student.getString("jobPosID"));
        	u.setManager(student.getString("superField"));
        	u.setRegion(student.getString("regionID"));
        	em.merge(u);
            //out.write(jwt);  
        }
        newTx.commit();
        em.close();
	
	}
	
}	
	


