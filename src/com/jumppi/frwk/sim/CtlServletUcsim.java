package com.jumppi.frwk.sim;

import java.util.Map;

import com.google.gson.JsonNull;
import com.jumppi.frwk.js.JS;
import com.jumppi.frwk.json.JSON;
import com.jumppi.frwk.jump.CtlServletREST;
import com.jumppi.frwk.jump.CtlUriRoute;
import com.jumppi.frwk.jump.RequestContext;
import com.jumppi.frwk.sql.DB;
import com.jumppi.frwk.util.BadUriRequestException;
import com.jumppi.frwk.util.SignalException;
import com.jumppi.frwk.util.Util;


/**
Reserved words for js_code:

_in
_out
_uri
_tok
_setPool(...)
_getPool(...)
_erasePool(...)
_clonePool(...)

 */
public class CtlServletUcsim extends CtlServletREST {

	protected Object invokeMethod(String uri, RequestContext ctx) throws SignalException { 
		return Util.invokeMethodRestSim(uri, ctx);
    }
	
}

