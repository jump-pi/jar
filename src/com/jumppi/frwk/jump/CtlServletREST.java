package com.jumppi.frwk.jump;

import com.jumppi.frwk.json.JSON;
import com.jumppi.frwk.log.Log;
import com.jumppi.frwk.sql.IDBConfig;
import com.jumppi.frwk.util.BadUriRequestException;
import com.jumppi.frwk.util.SignalException;
import com.jumppi.frwk.util.Util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * 
 *  String uri = request.getScheme() + "://" + request.getServerName() + 
 *               ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() ) +
 *               request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
 * 
 * Based on https://code.google.com/p/wo-furi
 * 
 * Error handling:
 * https://dev.twitter.com/overview/api/response-codes
 * 
 */
public class CtlServletREST extends HttpServlet {

    protected ServletContext ctx = null;
    protected String dbConfigClass;

    public void init(ServletConfig config) throws ServletException {
            super.init(config);
            ctx = config.getServletContext();
            String relPath = ctx.getRealPath(".");
            Util.setThreadProperty("realPath", relPath);
            dbConfigClass = getServletContext().getInitParameter("dbconfig-class");
    }
    
    /**
      http://localhost:8080/sk/sv
     */
    public void service(HttpServletRequest request,
                    HttpServletResponse response) throws ServletException, IOException {
        
        String lang = "es";
        String resp = "";
        
        try {
            byte[] bodyInput = new byte[0];
            
            // Oriol dixit
            if (request.getMethod().equals("OPTIONS")) {
                response.setHeader("Access-Control-Allow-Methods", "POST, PUT, DELETE, GET, OPTIONS");
                sendResponse(HttpServletResponse.SC_OK, "", response);
                return;
            } else if (!request.getMethod().equals("GET")) {
                bodyInput = getBodyInputFromRequestPost(request);
            }
            
            String uri = request.getRequestURI();
            String uriNoSlash = uri.substring(1);
            int posIni = uriNoSlash.indexOf("/");
            String uriRel = uriNoSlash.substring(posIni + 1);
            posIni = uriRel.indexOf("/");
            String servletName = uriRel.substring(0, posIni);
            String uriCanonica = uriRel.substring(posIni + 1);
            String queryString = Util.nvl(request.getQueryString());
            String uriRest = uriCanonica + (!queryString.equals("") ? "?" + queryString : "");
            String verb = request.getMethod();

            String tokenHeader = request.getHeader("Authorization");
            String token;
            
            // In a heavy loaded systems it is advised the use of fast key/value databases such as Redis to manage user tokens
            
            if (!Util.nvl(tokenHeader).equals("")) {
            	int posToken = tokenHeader.indexOf("Bearer");    // Oauth2 Client Credentials  (RFC 6749)
            	token = Util.nvl(tokenHeader.substring(posToken + 7));
            } else {
                tokenHeader = request.getHeader("IDENTITY_KEY"); // Sentilo like API ID (http://www.sentilo.io/xwiki/bin/view/APIDocs/Security)
                token = Util.nvl(tokenHeader);
            }
            
            RequestContext rctx = new RequestContext();
            rctx.setToken(token);
            rctx.setHttpServletRequest(request);
            rctx.setHttpServletResponse(response);
            rctx.setServletContext(getServletContext());
            
            Object respObj;

            rctx.setUriRest(uriRest);
            rctx.setBodyInput(bodyInput);
			respObj = invokeMethod(uriRest, rctx);
            
            if (respObj == null) {
            	resp = "";
            } else if (respObj instanceof JSON) { 
            	resp = ((JSON)respObj).toJsonString();
                sendResponse(HttpServletResponse.SC_OK, resp, response);
            } else if (respObj instanceof String){
            	resp = (String) respObj;
//            	sendResponse(HttpServletResponse.SC_OK, resp, "text/plain", response);
            	sendResponse(HttpServletResponse.SC_OK, resp, response);
            } else if (respObj instanceof byte[]) {
            	OutputStream out = response.getOutputStream();
            	byte[] aResp = (byte[]) respObj;
            	out.write(aResp, 0, aResp.length);
            }
            
        }
        catch(BadUriRequestException ue) {
			Log.error(ue.getMessage(), ue);
            String errorJSON = "{\"errors\":[{\"message\":\"" + Util.nvl(ue.getJsonTranslatedMessage(lang)).replace("\"", "\\\"") + "\", \"code\":99}]}";
            sendResponse(HttpServletResponse.SC_BAD_REQUEST, errorJSON, response);
        }
        catch(InvalidApiKeyException iae) {
//        	se.printStackTrace();
			Log.warn(iae.getMessage());
            String errorJSON = "{\"errors\":[{\"message\":\"" + Util.nvl(iae.getJsonTranslatedMessage(lang)).replace("\"", "\\\"") + "\", \"code\":" + iae.getCode() + "}]}";
            sendResponse(HttpServletResponse.SC_OK, errorJSON, response);
        }
        catch(SignalException se) {
//        	se.printStackTrace();
			Log.error(se.getMessage(), se);
            String errorJSON = "{\"errors\":[{\"message\":\"" + Util.nvl(se.getJsonTranslatedMessage(lang)).replace("\"", "\\\"") + "\", \"code\":" + se.getCode() + "}]}";
            sendResponse(HttpServletResponse.SC_OK, errorJSON, response);
        }
        catch(Exception e) {
			Log.error(e.getMessage(), e);
            String errorJSON = "{\"errors\":[{\"message\":\"" + Util.nvl(e.getMessage()).replace("\"", "\\\"") + "\", \"code\":99}]}";
            sendResponse(HttpServletResponse.SC_OK, errorJSON, response);
        }
        finally {
            try {
            	IDBConfig dbc = (IDBConfig) Class.forName(dbConfigClass).newInstance();
            	dbc.closeResourcesEndOfThread();
            } catch (Exception ex) {
//                ex.printStackTrace();
    			Log.error(ex.getMessage(), ex);
            }
        }
    }
    

    protected byte[] getBodyInputFromRequestPost(HttpServletRequest request) throws SignalException {
    	byte[] res = new byte[0];
        try {
            byte buf[] = new byte[2048]; // buffer de 2Kb
            int n;
            InputStream in = request.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            while ((n = in.read(buf)) != -1) {
            	buffer.write(buf, 0, n);
            }         	
            res = buffer.toByteArray();
        } 
        catch (Exception e) { 
//       	e.printStackTrace();
			Log.error(e.getMessage(), e);
        }
        return res;
    }
    
    
    protected void sendResponse(int code, String resp, HttpServletResponse response) {
    	sendResponse(code, resp, "application/json", response);
    }
    
    
    protected void sendResponse(int code, String resp, String contentType, HttpServletResponse response) {
        try {
            response.setStatus(code);
            response.setCharacterEncoding("UTF-8");            
            response.setContentType(contentType);
            response.getWriter().write(resp);
            response.getWriter().flush();
            response.getWriter().close();
        } catch (IOException ex) {
//            ex.printStackTrace();
			Log.error(ex.getMessage(), ex);
        }
    }


    protected  Object invokeMethod(String uri, RequestContext ctx) throws SignalException { 
    	return Util.invokeMethodRest(uri, ctx);
    }    
    
}


