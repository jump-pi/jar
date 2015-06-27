package com.jumppi.test;

import org.apache.log4j.BasicConfigurator;

import com.jumppi.frwk.json.JSON;
import com.jumppi.frwk.util.Util;

public class CallTest {

	/**
	 *     Modo simulació:
	 *     http://localhost:8080/jump/sv?{_ctl:'appl.test.CallTest',_met:'hola',_in:{x:123,y:9876,s:'Hola Coca-Cola'}}	   
	 */

	public static void main(String[] args) throws Exception {
		
		BasicConfigurator.configure();
		JSON in = JSON.getInstanceObject();

		in.add("code", 9876);
		in.add("name", "abc");
		in.add("date", "2015-04-10");
		
		String resp = Util.pingHttpPost("http://localhost:8080/sv/jump-pi_ddd/sv/op", JSON.toJsonString(in));
		
		System.out.println(resp);
		System.out.println("Fi");
	}
}



