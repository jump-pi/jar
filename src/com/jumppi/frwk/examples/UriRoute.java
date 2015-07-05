package com.jumppi.frwk.examples;

import java.util.*;

import org.apache.log4j.BasicConfigurator;

import com.jumppi.frwk.jump.CtlUriRoute;
import com.jumppi.frwk.jump.IUriRoute;
import com.jumppi.frwk.util.Util;

public class UriRoute implements IUriRoute {
	public void setup(Map<String, String> uriMap) {
		uriMap.put("op/{id}|GET", "appl.rest.Services::opGet");
		uriMap.put("op|POST", "appl.rest.Services::opPost");
		uriMap.put("op/{id}|POST", "appl.rest.Services::opPostWithPathParams");
		uriMap.put("op2/{id}?x={x}|GET", "appl.rest.Services::op2");
		uriMap.put("op/{id}/subop|GET", "appl.rest.Services::subop");
	}
	
}


