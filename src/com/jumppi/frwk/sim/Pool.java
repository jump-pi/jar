package com.jumppi.frwk.sim;

import com.jumppi.frwk.sql.DB;

public class Pool {
	
	public static void set(String poolName, String data) {
		Object res = null;
		UcsimDic ucsd = UcsimDic.getInstance(DB.getDbUcsimName());
		Ucsim ucs = ucsd.findByUriTemplate(poolName, "POOL");
		ucs.setJsCode(data);
		ucsd.save(ucs);
	}	
	
	public static String get(String poolName) {
		String res = null;
		UcsimDic ucsd = UcsimDic.getInstance(DB.getDbUcsimName());
		Ucsim ucs = ucsd.findByUriTemplate(poolName, "POOL");
		String data = ucs.getJsCode();
		res = data;
		return res;
	}
	
}

