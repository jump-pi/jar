package com.jumppi.frwk.json;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jumppi.frwk.jump.RequestContext;
import com.jumppi.frwk.log.Log;
import com.jumppi.frwk.util.Util;

public class JSON {

	public JsonElement obj = null;

	public static JSON getInstanceObject() {
		JSON res = new JSON();
		res.obj = new JsonObject();
		return res;
	}

	public static JSON getInstanceArray() {
		JSON res = new JSON();
		res.obj = new JsonArray();
		return res;
	}

	public static JSON getInstance() {
		JSON res = new JSON();
		return res;
	}

	public static JSON parse(byte[] jsonBytes) {
		return parse(new String(jsonBytes));
	}
	
	public static JSON parse(String jsonString) {
		JSON res = new JSON();
		res.obj = new JsonParser().parse(jsonString);
		return res;
	}

	public static <T> T parse(String jsonString, Class<T> cl) {
		return Util.getGsonInstance().fromJson(jsonString, cl);
	}

	public static <T> T fromJson(JSON json, Class<T> cl) {
		return Util.getGsonInstance().fromJson(json.obj, cl);
	}
	
	
	public static JSON parse(Object obj) {
		String json = parseToString(obj);
		return parse(json);
	}

	public static String parseToString(Object obj) {
		return Util.getGsonInstance().toJson(obj);
	}

	public <T> T parse(Class<T> cl) {
		return Util.getGsonInstance().fromJson(obj, cl);
	}

	public JSON get(String name) {
		JSON res = getInstance();
		if (obj != null) {
			if (obj.isJsonObject()) {
				res.obj = ((JsonObject) obj).get(name);
			}
		}
		return res;
	}

	public JSON get(int i) {
		JSON res = getInstance();
		if (obj != null) {
			if (obj.isJsonArray()) {
				try {
					res.obj = ((JsonArray) obj).get(i);
				} catch (Exception e) {
					res.obj = null;
				}
			}
		}
		return res;
	}

	public int getInt() {
		int res = 0;
		if (obj != null) {
			res = obj.getAsInt();
		}
		return res;
	}

	public double getDouble() {
		double res = 0;
		if (obj != null) {
			res = obj.getAsDouble();
		}
		return res;
	}

	public String getString() {
		String res = "";
		if (obj != null) {
			res = Util.nvl(obj.getAsString());
		}
		return res;
	}

	public String getStringDecodeUTF8() {
		String res = "";
		if (obj != null) {
			res = Util.urlDecodeUTF8(Util.nvl(obj.getAsString()));
		}
		return res;
	}
	
	
	public boolean getBoolean(boolean defaultValue) {
		boolean res = defaultValue;
		if (obj != null) {
			res = obj.getAsBoolean();
		}
		return res;
	}

	
	public boolean getBoolean() {
		return getBoolean(false);
	}
	
	
	public java.util.Date getDate() {
		java.util.Date res = new java.util.Date();
		String s = null;
		if (obj != null) {
			s = obj.getAsString();
			res = Util.parseDate(s);
		}
		return res;

	}
	
	
	public void add(String name, Object o) {
		if (obj == null) {
			obj = new JsonObject();
		}
		JsonObject aux = (JsonObject) obj;
		JsonElement elem = Util.getGsonInstance().toJsonTree(o);
		aux.add(name, elem);
	}

	public JSON add(String name, JSON jobj) {
		if (obj == null) {
			obj = new JsonParser().parse("{}");
		}
		((JsonObject) obj).add(name, jobj.obj);
		return jobj;
	}

	/**
	 * For adding elements to array
	 */
	public void add(Object o) {
		if (obj == null) {
			obj = new JsonObject();
		}
		JsonArray aux = (JsonArray) obj;
		JsonElement elem = Util.getGsonInstance().toJsonTree(o);
		aux.add(elem);
	}

	/**
	 * For adding elements to array
	 */
	public JSON add(JSON jobj) {
		if (obj == null) {
			obj = new JsonParser().parse("[]");
		}
		((JsonArray) obj).add(jobj.obj);
		return jobj;
	}


	public JSON set(int i, Object o) {
		if (obj == null) {
			obj = new JsonObject();
		}
		JsonArray aux = (JsonArray) obj;
		JsonElement elem = Util.getGsonInstance().toJsonTree(o);
		aux.set(i, elem);
		return this;
	}

	public JSON set(int i, JSON jobj) {
		if (obj == null) {
			obj = new JsonParser().parse("[]");
		}
		((JsonArray) obj).set(i, jobj.obj);
		return this;
	}
	
	
	public void remove(String name) {
		if (obj != null) {
			try {
				((JsonObject) obj).remove(name);
			} catch (Exception e) {
			}
		}
	}

	
	public void remove(int i) {
		if (obj != null) {
			try {
				((JsonArray) obj).remove(i);
			} catch (Exception e) {
			}
		}
	}
	
	public void set(String name, Object obj) {
		remove(name);
		add(name, obj);
	}

	public JSON set(String name, JSON jobj) {
		remove(name);
		add(name, jobj.obj);
		return jobj;
	}

	public int size() {
		int res = 0;
		if (obj.isJsonArray()) {
			res = ((JsonArray) obj).size();
		} else if (obj.isJsonObject()) {
			res = 1;
		} else {
			res = 0;
		}
		return res;
	}

	public static JSON toJson(Object obj) {
		JSON res = getInstance();
		Gson g = Util.getGsonInstance();
		String json = g.toJson(obj);
		return JSON.parse(json);
	}

	public static String toJsonString(Object obj) {
		String res;
		if (obj != null) {
			Gson g = Util.getGsonInstance();
			if (obj instanceof JSON) {
				res = ((JSON) obj).obj.toString();
			} else {
				res = obj.toString();
			}
		} else {
			res = "";
		}
		return res;
	}

	public static String toJsonStringUTF8(Object obj) {
		String res = "";
		try {
			String stJson;
			if (obj != null) {
				Gson g = Util.getGsonInstance();
				if (obj instanceof JSON) {
					stJson = ((JSON) obj).obj.toString();
				} else {
					stJson = obj.toString();
				}
			} else {
				stJson = "";
			}
//			res = stJson;
			
			byte bytesJson[] = stJson.getBytes();
			res = new String(bytesJson, "UTF-8");			
			
		} catch (Exception e) {
//			e.printStackTrace();
			Log.error(e.getMessage(), e);
		}
		return res;
	}

	public String toJsonString() {
		return toJsonString(this);
	}

	public String toJsonStringUTF8() {
		return toJsonStringUTF8(this);
	}
	
	public String toString() {
		String res = "";
		if (obj != null) {
			res = obj.toString();
		} 
		return res;
	}
	
	public Object getObj() {
		return obj;
	}

	public boolean isEmpty() {
		return obj == null || obj.isJsonNull();
	}

	/**
	 * http://stackoverflow.com/questions/2779251/how-can-i-convert-json-to-a-hashmap-using-gson
	 */
	public static HashMap<String, Object> toHashMaps(String stJson) {
		JSON json = parse(stJson);
		JsonElement je = json.obj;
	    HashMap<String, Object> map = new HashMap<String, Object>();
		
		if (je.isJsonObject()) {
			Set<Map.Entry<String, JsonElement>> set = ((JsonObject) je).entrySet();
		    Iterator<Map.Entry<String, JsonElement>> iterator = set.iterator();
		    while (iterator.hasNext()) {
		        Map.Entry<String, JsonElement> entry = iterator.next();
		        String key = entry.getKey();
		        JsonElement value = entry.getValue();
		        if (!value.isJsonPrimitive()) {
		        	if (value.isJsonArray()) {
			        	List lst = new ArrayList();
			        	for (JsonElement jelem : (JsonArray) value) {
			        		lst.add(jelem);	
			        	}
			        	map.put(key, lst);
		        	} else {
			            map.put(key, parse(value.toString()));
		        	}
		        } else {
		            map.put(key, value.getAsString());
		        }
		    }			
		}

		if (je.isJsonArray()) {
			// pending
		}
		
		return map;
	}

	public boolean equals(JSON json) {
		return obj.equals(json.obj);
	}
	
	public static StringBuilder loadJsonFile(String fileName) {
		StringBuilder res = new StringBuilder();
	    try {
			FileInputStream  f = new FileInputStream(fileName);
			byte buf[] = new byte[2048]; // buffer de 2Kb
			int n;
			while ((n = f.read(buf)) != -1) {
				res.append(new String(buf, 0, n));
			}
		} catch (Exception e) {
//			e.printStackTrace();
			Log.error(e.getMessage(), e);
		} 		
	    return res;
	}

	public static StringBuilder loadJsonFromUrl(String url) {
		StringBuilder res = new StringBuilder();
		try {
			res = Util.pingHttpGetSb(url, "", "");
		} catch (Exception e) {
//			e.printStackTrace();
			Log.error(e.getMessage(), e);
		}
	    return res;
	}
	
	
	public static void saveJSON(String fileName, JSON json) {
	    try {
			FileOutputStream  f = new FileOutputStream(fileName);
			DataOutputStream d = new DataOutputStream(f);
			d.writeBytes(json.toJsonString());
			d.close();
		} catch (IOException e) {
//			e.printStackTrace();
			Log.error(e.getMessage(), e);
		}
	}
	
	public static void test() {
		JSON json = JSON.getInstance();
		JSON json2 = null;
		JSON json3 = null;
		JSON json4 = null;

		json2 = JSON.parse("{x:1; s='abc'}");
		json3 = JSON.parse("[1, 2, 3]");
		json4 = JSON.parse("{s='abc'; x:1}");

		System.out.println("-------------");
		System.out.println(json2.equals(json4));
		
		json.add("p", json2);
		/*
		 * System.out.println(json); json.set("p", json3);
		 * System.out.println(json); JSON json4 = JSON.parse(json.toString());
		 * System.out.println(json4); String res; res = "" +
		 * json4.get("p").get(1).getInt(); System.out.println(res);
		 * System.out.println(json4.get("p").size());
		 */
		System.out.println("End");
	}

	
	public static JSON getSample(RequestContext ctx, String fileSample) {
		JSON res = JSON.getInstanceObject();
		try {
			String path = ctx.getServletContext().getRealPath("/samples/");
			FileInputStream  f = new FileInputStream(path + "/" + fileSample);
			StringBuilder sb = new StringBuilder();
			int c;
			while ((c = f.read()) != -1) {
				sb.append((char) c);
			}
			f.close();
			String stJson = sb.toString();
			res = JSON.parse(stJson);
		} catch (Exception e) {
//			e.printStackTrace();
			Log.error(e.getMessage(), e);
		}		
		return res;
	}
	
	public static void main(String[] args) throws Exception {
		BasicConfigurator.configure();
		test();
	}
}
