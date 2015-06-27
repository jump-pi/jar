package com.jumppi.frwk.log;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *  History:  http://blog.frankel.ch/thoughts-on-java-logging-and-slf4j		
 *            http://www.slf4j.org/download.html
 */
public class Log {
	   protected static final Logger logger = LoggerFactory.getLogger("jump-pi");
	   
	   public static Logger getLogger() {
		   return logger;
	   }
	   
	   public static void debug (String message) {
		   logger.debug(message);
	   }
	   
	   public static void error (String message) {
		   logger.error(message);
	   }

	   public static void error (String msg, Throwable e) {
		   logger.error(msg, e);
	   }
	   
	   public static void error (Throwable e) {
		   logger.error(e.getMessage(), e);
	   }
	   
	   public static void info (String message) {
		   logger.info(message);
	   }
	   
	   public static void trace (String message) {
		   logger.trace(message);
	   }
	   
	   public static void warn (String message) {
		   logger.warn(message);
	   }
	   

	   /**
	    * Test
	    */
	   public static void main(String[] args) {
		   BasicConfigurator.configure();
		   
		   Log.info("Info message test");
		   Log.debug("Debug message test");
		   Log.error("Error message test");
	   }
}

