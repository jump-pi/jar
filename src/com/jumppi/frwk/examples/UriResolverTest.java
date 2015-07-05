package com.jumppi.frwk.examples;

import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.weborganic.furi.URIPattern;
import org.weborganic.furi.URIResolveResult;
import org.weborganic.furi.URIResolver;

import com.jumppi.frwk.jump.CtlUriRoute;
import com.jumppi.frwk.util.Util;



// http://pageseeder.org/furi.html
// https://code.google.com/p/wo-furi
// https://code.google.com/p/uri-templates
// Restlet

public class UriResolverTest {

	public static void main (String[] args) {
		   BasicConfigurator.configure();
		   
		   String url = "/ddd/sv/op1/test/home?x=5";
		   String uriTpl = "/ddd/sv/op1/{ctl}/{op}?x={x}";
		
		  // Create a resolver instance 
		   URIResolver resolver = new URIResolver(url);
		 
		   // Resolve the URI for the specified pattern, the result holds all the matching info
		   URIResolveResult result = resolver.resolve(new URIPattern(uriTpl));
		   URIResolveResult.Status st = result.getStatus();
		   System.out.println("status " + st);
		   
		   System.out.println(result.get("ctl"));
		   System.out.println(st.name());
		   System.out.println(result.get("op"));
		   System.out.println(st.name());
		   System.out.println(result.names());

		   System.out.println("-------------------");
		   
		   Map params = CtlUriRoute.getParamsMap(url, uriTpl);
		   System.out.println(Util.dumpHashMap(params));
	}
}



