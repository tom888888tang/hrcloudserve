package odata.service.tool;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SuppressWarnings("rawtypes")
public class HttpUtil {
	
    private static int connTimeOut = 30000;
    private static int readTimeOut = 30000;
    
    private static Logger log = LoggerFactory.getLogger(HttpUtil.class);

    public static StringBuffer URLGet(String strUrl, Map parameterMap) throws IOException {
        String strTotalURL = "";
        strTotalURL = getTotalURL(strUrl, parameterMap);
        URL url = new URL(strTotalURL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setUseCaches(false);
        con.setConnectTimeout(connTimeOut);
        con.setReadTimeout(readTimeOut);
        HttpURLConnection.setFollowRedirects(true);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        return getStringBufferFormBufferedReader(in);
    }

    public static StringBuffer URLGet(String strUrl, String content) throws IOException {
        String strTotalURL = "";
        strTotalURL = getTotalURL(strUrl, content);
        URL url = new URL(strTotalURL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setUseCaches(false);
        con.setConnectTimeout(connTimeOut);
        con.setReadTimeout(readTimeOut);
        HttpURLConnection.setFollowRedirects(true);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        return getStringBufferFormBufferedReader(in);
    }

    public static StringBuffer URLGetJson(String strUrl, Map parameterMap) throws IOException {
    	String strTotalURL = "";
    	strTotalURL = getTotalURL(strUrl, parameterMap);
    	
    	log.info("发送get请求："+strTotalURL);
    	
        URL url = new URL(strTotalURL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setUseCaches(false);
        con.setRequestProperty("Accept", "application/json;charset=UTF-8");
        con.setConnectTimeout(connTimeOut);
        con.setReadTimeout(readTimeOut);
        HttpURLConnection.setFollowRedirects(true);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        return getStringBufferFormBufferedReader(in);
    }
    
    public static StringBuffer URLGetJson(String strUrl, Map parameterMap, int cTimeOut, int rTimeOut) throws IOException {
    	String strTotalURL = "";
    	strTotalURL = getTotalURL(strUrl, parameterMap);
    	
    	log.info("发送get请求："+strTotalURL);
    	
        URL url = new URL(strTotalURL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setUseCaches(false);
        con.setRequestProperty("Accept", "application/json;charset=UTF-8");
        con.setConnectTimeout(cTimeOut);
        con.setReadTimeout(rTimeOut);
        HttpURLConnection.setFollowRedirects(true);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        return getStringBufferFormBufferedReader(in);
    }
    
    public static StringBuffer URLPost(String strUrl, Map map) throws IOException {
    	//https证书问题 begin
    	HostnameVerifier hv = new HostnameVerifier() {
    		
			public boolean verify(String hostname, SSLSession session) {
				log.info("Warning: URL Host: " + hostname + " vs. "+ session.getPeerHost());
				return true;
			}
		};
        try {
			trustAllHttpsCertificates();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new IOException("回调失败！"+e.getMessage());
		}
		HttpsURLConnection.setDefaultHostnameVerifier(hv);
		//https证书问题 end
        String content = getContentURL(map);
        log.info("发送url请求参数：{}",content);
        URL url = new URL(strUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setAllowUserInteraction(false);
        con.setUseCaches(false);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        con.setConnectTimeout(connTimeOut);
        con.setReadTimeout(readTimeOut);
        BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
        bout.write(content);
        bout.flush();
        bout.close();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
        return getStringBufferFormBufferedReader(in);
    }
    
    private static void trustAllHttpsCertificates() throws Exception {
		TrustManager[] trustAllCerts = new TrustManager[1];
		TrustManager tm = new miTM();
		trustAllCerts[0] = tm;
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, null);
		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc
				.getSocketFactory());
	}
    
    static class miTM implements TrustManager,X509TrustManager {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}
		
		public boolean isServerTrusted(
				java.security.cert.X509Certificate[] certs) {
			return true;
		}
		
		public boolean isClientTrusted(
				java.security.cert.X509Certificate[] certs) {
			return true;
		}
		
		public void checkServerTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}
		
		public void checkClientTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}
	}
    
    public static StringBuffer URLPostGBK(String strUrl, Map map) throws IOException {
    	String content = getContentURL(map);
    	URL url = new URL(strUrl);
    	HttpURLConnection con = (HttpURLConnection) url.openConnection();
    	con.setDoInput(true);
    	con.setDoOutput(true);
    	con.setAllowUserInteraction(false);
    	con.setUseCaches(false);
    	con.setRequestMethod("POST");
    	con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=GBK");
    	con.setConnectTimeout(connTimeOut);
    	con.setReadTimeout(readTimeOut);
    	BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
    	bout.write(content);
    	bout.flush();
    	bout.close();
    	BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(),"GBK"));
    	return getStringBufferFormBufferedReader(in);
    }

    public static StringBuffer URLPost(String strUrl, String content) throws IOException {
    	//https证书问题 begin
    	HostnameVerifier hv = new HostnameVerifier() {
			
			public boolean verify(String hostname, SSLSession session) {
				log.info("Warning: URL Host: " + hostname + " vs. "+ session.getPeerHost());
				return true;
			}
        };
        try {
			trustAllHttpsCertificates();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new IOException("回调失败！"+e.getMessage());
		}
		HttpsURLConnection.setDefaultHostnameVerifier(hv);
		//https证书问题 end
        URL url = new URL(strUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setAllowUserInteraction(false);
        con.setUseCaches(false);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        con.setConnectTimeout(connTimeOut);
        con.setReadTimeout(readTimeOut);
        BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(),"UTF-8"));
        bout.write(content);
        bout.flush();
        bout.close();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
        return getStringBufferFormBufferedReader(in);
    }
    
    public static StringBuffer URLPostJson(String strUrl, String content) throws IOException {
    	//https证书问题 begin
    	HostnameVerifier hv = new HostnameVerifier() {
    		
			public boolean verify(String hostname, SSLSession session) {
				log.info("Warning: URL Host: " + hostname + " vs. "+ session.getPeerHost());
				return true;
			}
        };
        try {
			trustAllHttpsCertificates();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new IOException("回调失败！"+e.getMessage());
		}
		HttpsURLConnection.setDefaultHostnameVerifier(hv);
		//https证书问题 end
        URL url = new URL(strUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setAllowUserInteraction(false);
        con.setUseCaches(false);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        con.setConnectTimeout(connTimeOut);
        con.setReadTimeout(readTimeOut);
        BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(),"UTF-8"));
        bout.write(content);
        bout.flush();
        bout.close();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
        return getStringBufferFormBufferedReader(in);
    }
    

    private static StringBuffer getStringBufferFormBufferedReader(BufferedReader in)
            throws IOException {
        StringBuffer returnStringBuffer = new StringBuffer();
        char[] tmpbuf = new char[1024];
        int num = in.read(tmpbuf);
        while (num != -1) {
            returnStringBuffer.append(tmpbuf, 0, num);
            num = in.read(tmpbuf);
        }
        in.close();
        return returnStringBuffer;
    }

    public static String getTotalURL(String strUrl, Map parameterMap) {
        String content = getContentURL(parameterMap);
        return getTotalURL(strUrl, content);
    }

    public static String getTotalURL(String strUrl, String content) {
        String totalURL = strUrl;
        if (totalURL.indexOf("?") == -1)
            totalURL = totalURL + "?";
        else
            totalURL = totalURL + "&";

        totalURL = totalURL + content;
        return totalURL;
    }

    public static String getContentURL(Map parameterMap) {
        if ((parameterMap == null) || (parameterMap.keySet().size() == 0))
            return "";
        StringBuffer url = new StringBuffer();
        Set keys = parameterMap.keySet();
        for (Iterator i = keys.iterator(); i.hasNext();) {
            String key = String.valueOf(i.next());
            if (!(parameterMap.containsKey(key)))
                break;
            Object val = parameterMap.get(key);
            String str = (val != null) ? val.toString() : "";
            try {
                str = URLEncoder.encode(str, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            url.append(key).append("=").append(str).append("&");
        }
        String strURL = "";
        strURL = url.toString();
        if ('&' == strURL.charAt(strURL.length() - 1)) {
            strURL = strURL.substring(0, strURL.length() - 1);
        }
        return strURL;
    }
    
    /**
	 * 读取request流
	 * 
	 * @param req
	 * @return
	 */
	public static String readReqStr(HttpServletRequest request) {
		BufferedReader reader = null;
		StringBuilder sb = new StringBuilder();
		try {
			reader = new BufferedReader(new InputStreamReader(
					request.getInputStream(), "utf-8"));
			String line = null;

			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != reader) {
					reader.close();
				}
			} catch (IOException e) {

			}
		}
		return sb.toString();
	}
	
	/**
	 * 获取请求的真实Ip
	 * 
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (!StringUtil.isEmpty(ip) && ip.contains(",")) {
			String[] ips = ip.split(",");
			ip = ips[ips.length - 1];
		}
		return ip;
	}
}