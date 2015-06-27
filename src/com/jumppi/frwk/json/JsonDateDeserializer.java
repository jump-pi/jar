package com.jumppi.frwk.json;

import java.lang.reflect.Type;
import java.util.*;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.jumppi.frwk.util.Util;

public class JsonDateDeserializer implements JsonDeserializer<Date> {

	  public java.util.Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
	      throws JsonParseException
	  {
		  java.util.Date res = null;
		  String d = json.getAsString();
		  res = Util.parseDateANSI(d);
		  return res;
	  }
}

