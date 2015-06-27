package com.jumppi.frwk.js;

import java.io.IOException;
import java.io.Reader;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.tools.shell.Global;

import com.jumppi.frwk.log.Log;


public class JS {

	protected static Context context;
	protected static Global global;

	
	public static void init() {
		try {
			global = new Global();
			context = Context.enter();
			context.setOptimizationLevel(-1);
			context.initStandardObjects();
			global.init(context);
/*			
			FileReader envIn = new FileReader("env.rhino.1.2.js");
			FileReader jQueryIn = new FileReader("jquery-1.6.2.js");
			
			System.out.println("env pre");
			eval(envIn);
			System.out.println("env post");
			System.out.println("jQuery load pre");
			eval(jQueryIn);
			System.out.println("jQuery load post");
*/			
		} catch (Exception e) {
//			e.printStackTrace();
			Log.error(e.getMessage(), e);
		}
	}
	
	public static Object eval(String expr) {
		Object result = null;
		result = context.evaluateString(global, expr, "text_script", 1, null);
		return result;
	}

	public static Object eval(Reader in) {
		Object result = null;
		try {
			result = context.evaluateReader(global, in, "file_script", 1, null);
		} catch (IOException e) {
//			e.printStackTrace();
			Log.error(e.getMessage(), e);
		}
		return result;
	}

}

