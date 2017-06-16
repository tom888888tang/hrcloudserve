package odata.service.wechat.web;

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

import core.DSUtill;

import org.apache.cxf.helpers.IOUtils;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.json.JSONObject;

import odata.service.login.util.JWTFactory;
import odata.service.user.data.Storage;
import odata.service.wechat.web.Util;
import persistence.User;
import persistence.Wechat_AccToken;


public class Wechat_Update_Token extends HttpServlet{
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
	    req.setCharacterEncoding("UTF-8");
	    resp.setContentType("application/json; odata.metadata=minimal;charset=utf-8");
	    
	    //resp.setContentType("text/html;charset=utf-8");
		//String json = IOUtils.toString(req.getInputStream()); 
		//JSONObject jsonObject = new JSONObject(json);
		//String code = jsonObject.getString("code");
		PrintWriter out = resp.getWriter();
		//out.write(code + "/%%/");
		//JSONObject js = new JSONObject(Util.get_wechat_token(code));
		//out.write(js.toString() + "/%%/");
		//JSONObject wechat_user = new JSONObject(Util.get_wechat_userinfo(js.getString("openid"),js.getString("access_token")));
		//String user_id = getParameter("12");
		//String customer_id = jsonObject.getString("Customer_id");
		String token = (String)req.getHeader(JWTFactory.session_name);
//		Connection connection = null;
//        try {
//            InitialContext ctx = new InitialContext();
//            ds = (DataSource) ctx.lookup("java:comp/env/jdbc/DefaultDB");
//
//            Map properties = new HashMap();
//            properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, ds);
//            
//            emf = Persistence.createEntityManagerFactory("cloudhr_server",properties);
//            //emf = Persistence.createEntityManagerFactory("cloudhr_server");
//        } catch (NamingException e) {
//            e.printStackTrace();
//        }
		//EntityManagerFactory factory = Persistence.createEntityManagerFactory("cloudhr_server");
		emf = DSUtill.getDS(JWTFactory.getJWT_CUSTOMER(token));
		EntityManager em = emf.createEntityManager();		
		EntityTransaction newTx = em.getTransaction();
	    newTx.begin();
	    Wechat_AccToken wa = new Wechat_AccToken();
	    wa.setToken(Util.get_wechat_token_save());
	    wa.setToken_id("1");
	    em.merge(wa);
	    newTx.commit();
	    em.close();
	   
	
	
	}
	

}	
	


