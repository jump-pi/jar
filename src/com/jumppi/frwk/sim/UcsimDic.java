package com.jumppi.frwk.sim;

import java.util.ArrayList;
import java.util.List;

import com.jumppi.frwk.json.JSON;
import com.jumppi.frwk.sim.wiz.*;
import com.jumppi.frwk.sql.DB;
import com.jumppi.frwk.util.BadUriRequestException;
import com.jumppi.frwk.util.Util;


public class UcsimDic extends UcsimDicWiz {

	private static final Class<Ucsim> cl = Ucsim.class;
	private final DB bd;

	protected UcsimDic(String idCon) {
		super(idCon);
		bd = DB.getInstance(idCon);
	}

	public static UcsimDic getInstance(String idCon) {
		return new UcsimDic(idCon);
	}

	public Ucsim findByUriTemplate(String uriTemplate, String verb) {
		Ucsim res = null;
		try {
			DB bd = DB.getInstance(idCon);
			String pquery = " select * from ucsim where uri_template = ? and http_method = ? ";
			List params = new ArrayList();
			params.add(Util.nvl(uriTemplate));
			params.add(Util.nvl(verb).toUpperCase());
			res = bd.getDTO(Ucsim.class, pquery, params);
		} catch (Exception e) {
			throw new BadUriRequestException(e.toString());
		}
		return res;
	}

}




