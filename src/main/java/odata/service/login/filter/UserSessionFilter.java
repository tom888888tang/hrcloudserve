package odata.service.login.filter;



import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import odata.service.login.util.JWTFactory;
import odata.service.tool.StringUtil;

import persistence.User;

public class UserSessionFilter implements Filter {

	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		String token = request.getParameter(JWTFactory.session_name);
		
		if(!StringUtil.isEmpty(token) && !StringUtil.isEmpty(JWTFactory.getJWT_USER(token))){
			User user = new User();
	    	
	    	user.setUser_id(JWTFactory.getJWT_USER(token));
	    	String customerId = JWTFactory.getJWT_CUSTOMER(token);
	    	
	    	JWTFactory.getJWT(user, customerId);
		}
		
		chain.doFilter(request, response);
	}

	public void destroy() {
		// TODO Auto-generated method stub
		
	}



}
