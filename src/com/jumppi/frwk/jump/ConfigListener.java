package com.jumppi.frwk.jump;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


//import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

import com.jumppi.frwk.log.Log;
import com.jumppi.frwk.sql.DB;
import com.jumppi.frwk.sql.IDBConfig;

public class ConfigListener implements ServletContextListener {

	public static String serverUrl = "";
	
	@Override
	public void contextInitialized(ServletContextEvent ctxEvt) {
		try {
			ServletContext ctx = ctxEvt.getServletContext();
			String path = ctx.getRealPath("/WEB-INF");
//			BasicConfigurator.configure();
			
			// http://geekswithblogs.net/rgupta/archive/2009/03/03/tips-on-using-log4net-rollingfileappender.aspx
			
			PropertyConfigurator.configure(path + "/classes/log4j.properties");
			
			String dbConfig = ctx.getInitParameter("dbconfig-class");
			DB.setDBConfig((IDBConfig) Class.forName(dbConfig).newInstance());

			String uriRoute = ctx.getInitParameter("uriroute-class");
			CtlUriRoute.setUriRoute((IUriRoute) Class.forName(uriRoute).newInstance());

			String dbUcsimDbName = ctx.getInitParameter("dbucsim-db-name");
			DB.setDbUcsimName(dbUcsimDbName);
			String dbUcsimDbPort = ctx.getInitParameter("dbucsim-db-port");
			DB.setDbUcsimName(dbUcsimDbPort);
			String dbUcsimUsername = ctx.getInitParameter("dbucsim-username");
			DB.setDbUcsimUserame(dbUcsimUsername);
			String dbUcsimPassword = ctx.getInitParameter("dbucsim-password");
			DB.setDbUcsimPassword(dbUcsimPassword);
			
		} catch (Exception e) {
//			e.printStackTrace();
			Log.error(e.getMessage(), e);
		}
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent ctxEvt) {
	}
}
