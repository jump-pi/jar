package com.jumppi.frwk.sql;

import com.jumppi.frwk.util.Util;

public class DBDescriptor {

    public String protocol;
    public String subprotocol;
	public String server;
    public String port;
    public String dbName;
    public String localDbName;
	public String params;
    public String jdbcDriver;
    public String username;
    public String password;

    public static DBDescriptor getInstance() {
    	return new DBDescriptor();
    }
    
    public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

    public String getSubprotocol() {
		return subprotocol;
	}

	public void setSubprotocol(String subprotocol) {
		this.subprotocol = subprotocol;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

    public String getLocalDbName() {
		return localDbName;
	}

	public void setLocalDbName(String localDbName) {
		this.localDbName = localDbName;
	}
	
	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getJdbcDriver() {
		return jdbcDriver;
	}

	public void setJdbcDriver(String jdbcDriver) {
		this.jdbcDriver = jdbcDriver;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	
    public String buildUrl() {
        String res = "";
//        res = protocol + "://" + server + ":" + port + "/" + dbName + "?" + params;
        res = protocol + optSubprotocol(subprotocol) + optServer(server) + optDbName(dbName) + optPort(port) + optParams(params) + optLocalDbName(localDbName);
        return res;
    }

	public String buildUrl(String nameDb) {
        String res = "";
//        res = protocol + "://" + server + ":" + port + "/" + name + "?" + params;
        res = protocol + optSubprotocol(subprotocol) + optServer(server) + optDbName(nameDb) + optPort(port) + optParams(params);
        return res;
    }

    public String optSubprotocol(String subprotocol) {
    	String res = "";
    	if (Util.nvl(subprotocol).equals("")) {
    		res = "";
    	} else {
    		res = ":" + subprotocol;
    	}
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

    
    public String optDbName(String name) {
    	String res = "";
    	if (Util.nvl(name).equals("")) {
    		res = "";
    	} else {
    		res = "/" + name;
    	}
    	return res;
    }

    public String optLocalDbName(String name) {
    	String res = "";
    	if (Util.nvl(name).equals("")) {
    		res = "";
    	} else {
    		res = ":" + name;
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
        res.subprotocol = subprotocol;
        res.server = server;
        res.port = port;
        res.localDbName = localDbName;
        res.dbName = dbName;
        res.params = params;
        res.jdbcDriver = jdbcDriver;
        res.username = username;
        res.password = password;                    
        return res;
    }
}
