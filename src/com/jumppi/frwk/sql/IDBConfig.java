package com.jumppi.frwk.sql;

import java.util.Hashtable;

public interface IDBConfig {
	public void setup(Hashtable<String, DBDescriptor> gDbMap, String dbUcsimDbName, int dbUcsimDbPort, String dbUcsimUsername, String dbUcsimPassword);
	public void closeResourcesEndOfThread();
}


