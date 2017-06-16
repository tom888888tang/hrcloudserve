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
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.QueryHints;
import org.json.JSONObject;

import odata.service.login.util.JWTFactory;
import odata.service.user.data.Storage;
import odata.service.wechat.web.Util;
import persistence.User;


public class Wechat_Login extends HttpServlet{
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
		String json = IOUtils.toString(req.getInputStream()); 
		JSONObject jsonObject = new JSONObject(json);
		String code = jsonObject.getString("code");
		PrintWriter out = resp.getWriter();
		//out.write(code + "/%%/");
		JSONObject js = new JSONObject(Util.get_wechat_token(code));
		//out.write(js.toString() + "/%%/");
		JSONObject wechat_user = new JSONObject(Util.get_wechat_userinfo(js.getString("openid"),Util.get_wechat_token()));
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
		emf = DSUtill.getDS(JWTFactory.getJWT_CUSTOMER(token));
		//EntityManagerFactory factory = Persistence.createEntityManagerFactory("cloudhr_server");
		EntityManager em = emf.createEntityManager();		
		EntityTransaction newTx = em.getTransaction();
	    newTx.begin();
	    String query = "select u from User u where u.wechat_id = '" + js.getString("openid") + "'" ; 
        List<User> result = em.createQuery(query).setHint(QueryHints.REFRESH, HintValues.TRUE).getResultList();
        newTx.commit();
        if(result.size() == 1){
        	User user = result.get(0);
        	//String ip = ip = req.getRemoteAddr();
//            if (req.getHeader("x-forwarded-for") == null) {  
            	//ip = req.getRemoteAddr();  
//            }else{  
//                ip = req.getHeader("x-forwarded-for");  
//            } 
            String jwt = JWTFactory.getJWT(user);
            Gson gson = new Gson();
        	resp.setStatus(200);
        	//PrintWriter out = resp.getWriter();
        	//out.write(user_json);
        	String user_json = gson.toJson(result);
        	HttpSession session = req.getSession(true);
            session.removeAttribute(JWTFactory.session_name);
            session.setAttribute(JWTFactory.session_name, jwt);
            JSONObject ret = new JSONObject();
            wechat_user.put("openid","");
            ret.append("access_token", jwt);
            ret.append("wechat_user", wechat_user);
            user.setWechat_img(wechat_user.getString("headimgurl"));
            user.setWechat_nickname(wechat_user.getString("nickname"));
            newTx.begin();
            em.merge(user);
            newTx.commit();
            em.close();
        	//out.write(user.getUser_id());
            out.write(ret.toString());  
        }else{          	
        	resp.setStatus(500);
        	//out = resp.getWriter();
            out.write("Login Failed");         	
        }
	
	
	}
	

}	
	


