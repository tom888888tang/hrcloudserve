package odata.service.login.web;

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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import com.google.gson.Gson;
import org.apache.cxf.helpers.IOUtils;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.json.JSONObject;

import odata.service.login.util.JWTFactory;
import odata.service.user.data.Storage;
import persistence.User;


public class ServerRefresh extends HttpServlet{
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
	    resp.setContentType("application/json; odata.metadata=minimal;charset=utf-8");
	    
    	PrintWriter out = resp.getWriter();
    	User user = new User();
    	JSONObject ret;
		ret = new JSONObject();
    	user.setUser_id(JWTFactory.getJWT_USER((String)req.getHeader(JWTFactory.session_name)));
		ret.append("access_token", JWTFactory.getJWT(user));
		ret.append("wechat_user", "");

	
	
	}
	
}	
	


