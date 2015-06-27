package com.jumppi.frwk.jump;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jumppi.frwk.json.JSON;

public class RequestContext {
	protected String token;
	protected HttpServletRequest httpServletRequest;
	protected HttpServletResponse httpServletResponse;
	protected ServletContext servletContext;
	protected byte[] bodyInput;
	protected String uriRest;
	protected Map<String, String> uriParams;

	
	public byte[] getBodyInput() {
		return bodyInput;
	}
	
	public JSON getBodyInputJson() {
		JSON res = JSON.parse(bodyInput);
		return res;
	}
	

	public void setBodyInput(byte[] bodyInput) {
		this.bodyInput = bodyInput;
	}

	public String getUriRest() {
		return uriRest;
	}

	public void setUriRest(String uri) {
		this.uriRest = uri;
	}

	public Map<String, String> getUriParams() {
		return uriParams;
	}

	public void setUriParams(Map<String, String> uriParams) {
		this.uriParams = uriParams;
	}

	public String getUriParam(String paramName) {
		return uriParams.get(paramName);
	}
	
	
	public static String getServerUrl(ServletRequest request) {
		String scheme = request.getScheme();
		String serverName = request.getServerName();
		int port = request.getServerPort();
		String serverUrl = scheme + "://" + serverName + ":" + port;
		return serverUrl;
	}

	public static String getJsondocUrl(ServletContext ctx, ServletRequest request) {
		String contextPath = ctx.getContextPath();
		String jsondocUrl = getServerUrl(request) + contextPath + "/jsondoc";
		return jsondocUrl;
	}
	
	public HttpServletRequest getHttpServletRequest() {
		return httpServletRequest;
	}

	public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
		this.httpServletRequest = httpServletRequest;
	}

	public HttpServletResponse getHttpServletResponse() {
		return httpServletResponse;
	}

	public void setHttpServletResponse(HttpServletResponse httpServletResponse) {
		this.httpServletResponse = httpServletResponse;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getVerb() {
		return httpServletRequest.getMethod();
	}
	
	public void setResponseContentType(String contentType) {
		httpServletResponse.setContentType(contentType);
	}
}
