package com.jumppi.frwk.sql;

import com.jumppi.frwk.util.Util;

public class DBDescriptor {

    public String protocol;
    public String server;
    public String port;
    public String dbName;
    public String params;
    public String jdbcDriver;
    public String username;
    public String password;

    public String buildUrl() {
        String res = "";
//        res = protocol + "://" + server + ":" + port + "/" + dbName + "?" + params;
        res = protocol + optServer(server) + optName(dbName) + optParams(params);
        return res;
    }
    
    public String buildUrl(String name) {
        String res = "";
//        res = protocol + "://" + server + ":" + port + "/" + name + "?" + params;
        res = protocol + optServer(server) + optName(name) + optParams(params);
        return res;
    }

    public String optServer(String server) {
    	String res = "";
    	if (Util.nvl(server).equals("")) {
    		res = "";
    	} else {
    		res = "://" + server;
    	}
    	return res;
    }

    public String optPort(String port) {
    	String res = "";
    	if (Util.nvl(port).equals("")) {
    		res = "";
    	} else {
    		res = ":" + port;
    	}
    	return res;
    }

    
    public String optName(String name) {
    	String res = "";
    	if (Util.nvl(name).equals("")) {
    		res = "";
    	} else {
    		res = "/" + name;
    	}
    	return res;
    }

    public String optParams(String params) {
    	String res = "";
    	if (Util.nvl(params).equals("")) {
    		res = "";
    	} else {
    		res = "?" + params;
    	}
    	return res;
    }
    
    @Override
    public DBDescriptor clone() {
        DBDescriptor res = new DBDescriptor();
        res.protocol = protocol;
        res.server = server;
        res.port = port;
        res.dbName = dbName;
        res.params = params;
        res.jdbcDriver = jdbcDriver;
        res.username = username;
        res.password = password;                    
        return res;
    }
}
