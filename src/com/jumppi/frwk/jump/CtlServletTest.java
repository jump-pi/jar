package com.jumppi.frwk.jump;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jumppi.frwk.util.Util;


public class CtlServletTest extends HttpServlet {
    
	protected ServletContext ctx = null;
	
	public void init(ServletConfig config) throws ServletException {
	        super.init(config);
	        ctx = config.getServletContext();
            String relPath = ctx.getRealPath(".");
            Util.setThreadProperty("realPath", relPath);
	}
	
	protected void service(HttpServletRequest request,
	                HttpServletResponse response) throws ServletException, IOException {
		
		String token = "" + new java.util.Date().getTime();
		
		PrintWriter p = response.getWriter();
		p.println("[");
		p.println("  {idClassif: 1, descClassif: 'Producte', tok:'" + token + "'},");
		p.println("  {idClassif: 2, descClassif: 'Comanda'}");
		p.println("]");
	}
}

