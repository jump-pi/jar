package com.jumppi.frwk.jump;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.weborganic.furi.URIPattern;
import org.weborganic.furi.URIResolveResult;
import org.weborganic.furi.URIResolver;

import com.jumppi.frwk.log.Log;
import com.jumppi.frwk.sim.Ucsim;
import com.jumppi.frwk.sim.UcsimDic;
import com.jumppi.frwk.sql.DB;
import com.jumppi.frwk.util.SignalException;



/**
 * Basat en 
 * https://code.google.com/p/wo-furi
 * https://code.google.com/p/uri-templates
 * https://code.google.com/p/uri-templates/wiki/Implementations
 * 
*/
public class CtlUriRoute {

	
	protected static List vUriTemplates = null;
	protected static List vUriTemplatesSim = null;
	protected static Map<String, String> uriMap = null;
    protected static IUriRoute uriRoute;

	public static Map<String, String> getUriMap() {
		try {
			if (uriMap == null) {
				uriMap = new HashMap<String, String>();
				if (uriRoute == null) {
					throw new SignalException(99, "No uriroute-class defined (check web.xml)");
				} else {
					uriRoute.setup(uriMap);
				}
			}
		} catch (Exception e) {
//			e.printStackTrace();
			Log.error(e.getMessage(), e);
		}
		return uriMap;
	}
	
	public static void setUriRoute (IUriRoute route) {
		uriRoute = route;
	}
	
	protected static void loadArray() {
		Iterator itKeys = getUriMap().keySet().iterator();
		while (itKeys.hasNext()) {
			vUriTemplates.add(itKeys.next());
		}
	}

	public static String getUriTemplate(String escapedUrl, String verb) {
		String res = "";
		
		if (vUriTemplates == null) {
			vUriTemplates = new ArrayList();
			loadArray();
		}
		
		URIResolver resolver = new URIResolver(escapedUrl + "|" + verb);
		int resolvedCount = 0;
		for (Object otpl : vUriTemplates) {
			String tpl = (String) otpl;
			URIResolveResult result = resolver.resolve(new URIPattern(tpl));
			String status = result.getStatus().toString();
			if (status.equals("RESOLVED")) {
				res = tpl;
				resolvedCount++;			
			}
		}
		if (resolvedCount != 1) {
			throw new SignalException(99, "URI conflict");
		}
		return res;
	}
	
	public static String getJavaMethod(String uriTemplateVerb) {
		String res = "";
		Map<String, String> uriMap = getUriMap();
		res = (String) uriMap.get(uriTemplateVerb);
		return res;
	}

	public static String getPackage(String javaMethod) {
		String res = "";
		try {
			res = javaMethod.substring(0, javaMethod.indexOf("::"));
		} catch (Exception e) {
		}
		return res;
	}
	
	public static String getMethod(String javaMethod) {
		String res = "";
		try {
			res = javaMethod.substring(javaMethod.indexOf("::") + 2);
		} catch (Exception e) {
		}
		return res;
	}
	
	public static Map getParamsMap(String escapedUrl, String uriTemplate) {
		Map res = new HashMap();
		URIResolver resolver = new URIResolver(escapedUrl);
		URIResolveResult result = resolver.resolve(new URIPattern(uriTemplate));
		Set vsNames = result.names();
		Object[] aNames = vsNames.toArray();
		Object val;
		for (Object name : aNames) {
			val = result.get((String) name);
			res.put(name, val);
		}
		return res;
	}

	public static String getUriTemplateSim(String escapedUrl, String verb) {
		String res = "";
		
		if (vUriTemplatesSim == null) {
			vUriTemplatesSim = new ArrayList();
		}

		clearArraySim();
		loadArraySim();
		
		URIResolver resolver = new URIResolver(escapedUrl + "|" + verb);
		int resolvedCount = 0;
		for (Object otpl : vUriTemplatesSim) {
			String tpl = (String) otpl;
			URIResolveResult result = resolver.resolve(new URIPattern(tpl));
			String status = result.getStatus().toString();
//			Log.debug("|" + tpl + "| =>  |" + status + "|");
			if (status.equals("RESOLVED")) {
				res = tpl;
				resolvedCount++;
			}
		}
		if (resolvedCount != 1) {
			throw new SignalException(99, "URI conflict");
		}
		
		return res;
	}
    
	protected static void clearArraySim() {
		if (vUriTemplatesSim != null) {
			vUriTemplatesSim.clear();
		} 
	}
	
	protected static void loadArraySim() {
		UcsimDic ucd = UcsimDic.getInstance(DB.getDbUcsimName());
		Collection<Ucsim> vUcsims = ucd.findAll();
		for (Ucsim oUc : vUcsims) {
			Ucsim uc = oUc;
			vUriTemplatesSim.add(uc.getUriTemplate() + "|" + uc.getHttpMethod());
		}
	}	
}


