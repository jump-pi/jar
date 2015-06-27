package com.jumppi.frwk.jsondoc;

/**
	http://jsondoc.org
  	https://jjordamontejano.wordpress.com/tag/jsondoc
	http://search.maven.org/#search|ga|1|jsondoc
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jodd.json.JsonSerializer;
import org.jsondoc.core.pojo.JSONDoc;
import org.jsondoc.core.scanner.DefaultJSONDocScanner;




public class JSONDocServlet extends HttpServlet {

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		List<String> vPckgs = new ArrayList();
		vPckgs.add("appl.rest");
		String contextName = getServletContext().getContextPath().substring(1);
		response.setContentType("text/plain");
		response.setHeader("Access-Control-Allow-Origin", "*");

		// getScheme(), getServerName(), getServerPort() and getContextPath() 	
		
		String scheme = request.getScheme();
		String serverName = request.getServerName();
		int port = request.getServerPort();
		String serverUrl = scheme + "://" + serverName + ":" + port;
		String serverJsonUrl = serverUrl + "/" + contextName + "/jsondoc";
		ServletContext ctx = getServletContext();
		ctx.setAttribute("jsondocurl", serverJsonUrl);
		
		JSONDoc jsd = new DefaultJSONDocScanner().getJSONDoc("1.02", serverUrl + "/" + contextName + "/sv/", vPckgs, true, JSONDoc.MethodDisplay.SUMMARY);

		JsonSerializer jsonSerializer = new JsonSerializer();
		out.print(jsonSerializer.deep(true).serialize(jsd));
	    
	}
}


