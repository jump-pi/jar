package com.jumppi.frwk.examples;

import com.jumppi.frwk.json.JSON;
import com.jumppi.frwk.sql.DB;
import com.jumppi.frwk.sql.DBDescriptor;

public class StandAloneDBTest {

	public static void main(String[] args) throws Exception {
		DBDescriptor dbd = DBDescriptor.getInstance();
		dbd.setProtocol("jdbc:mysql");
		dbd.setJdbcDriver("com.mysql.jdbc.Driver");
		dbd.setServer("127.0.0.1");
		dbd.setDbName("appl");
		dbd.setUsername("root");
		DB.addDescriptor("mysql", dbd);
		DB db = DB.getInstance("mysql");

		JSON res = db.getJSON("select * from ucsim");
		
		db.close();
		System.out.println(res.toJsonString());
	}
}

