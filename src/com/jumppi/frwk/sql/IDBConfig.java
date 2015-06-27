package com.jumppi.frwk.sql;

import java.util.Hashtable;

public interface IDBConfig {
	public void setup(Hashtable<String, DBDescriptor> gDbMap, String dbUcsimName, String dbUcsimUsername, String dbUcsimPassword);
	public void closeResourcesEndOfThread();
}


