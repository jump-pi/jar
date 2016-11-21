package com.jumppi.frwk.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.weborganic.furi.URICoder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jumppi.frwk.js.JS;
import com.jumppi.frwk.json.JSON;
import com.jumppi.frwk.json.JsonDateDeserializer;
import com.jumppi.frwk.jump.CtlUriRoute;
import com.jumppi.frwk.jump.RequestContext;
import com.jumppi.frwk.log.Log;
import com.jumppi.frwk.sim.Ucsim;
import com.jumppi.frwk.sim.UcsimDic;
import com.jumppi.frwk.sql.Bag;
import com.jumppi.frwk.sql.DB;


public class Util {

	protected static int PIVOT_2000_YEAR = 80;
	protected static Gson gson = null;

	// Ini thread services
    protected static ThreadLocal<HashMap<String, Object>> threadScope;
    protected static HashMap<String, Object> threadHashMap;

    static {
    	threadScope = new ThreadLocal<HashMap<String, Object>>();
    	threadHashMap = new HashMap<String, Object>();
    }
	
    public static void setThreadProperty(String key, String value) {
    	HashMap<String, Object> htProps = getThreadProperties();
    	htProps.put(key, value);
    }

    public static String getThreadProperty(String key) {
    	HashMap<String, Object> htProps = getThreadProperties();
    	Object value = htProps.get(key);
    	value = value == null ? "" : value;
    	return (String) value;
    }
    
    public static void setThreadProperties(HashMap<String, Object> htProps) {
    	threadScope.set(htProps);
    }
    
    public static HashMap<String, Object> getThreadProperties() {
    	HashMap<String, Object> htProps = threadScope.get();
    	if (htProps == null) {
        	threadScope.set(threadHashMap);
        	htProps = threadHashMap;
    	}
    	return htProps;
    }
    
	// End thread services
    
	public static Field getField(Class c, String fieldName) {
		Field res = null;
		Class objOrSuper = c;
		do {
			try {
				res = objOrSuper.getDeclaredField(fieldName);
				res.setAccessible(true);
				break;
			} catch (Exception e) {
				objOrSuper = objOrSuper.getSuperclass();
			}
		} while (objOrSuper != null);

		return res;
	}

	public static Field[] getInheritedFields(Class<?> type) {
		List<Field> fields = new ArrayList<Field>();
		for (Class<?> c = type; c != null; c = c.getSuperclass()) {
			fields.addAll(Arrays.asList(c.getDeclaredFields()));
		}
		int n = fields.size();
		Field[] res = new Field[n];
		for (int i = 0; i < n; i++) {
			res[i] = fields.get(i);
		}
		return res;
	}


	public static void setValueOfAttributes(List vObj, String name, Object value) {
		for (Object obj : vObj) {
			Util.setValueOfAttribute(obj, "idCon", value);
		}
	}
	
	public static void setValueOfAttribute(Object obj, String name, Object value) {
		setValueOfAttribute(obj, name, value, false);
	}

	public static void setValueOfAttribute(Object obj, String name,
			Object value, boolean showInDates) {
		Class classObj = null;
		Class classAttr = null;
		Class classValue = null;
		Field f = null;
		Object val = null;

		try {
			classObj = obj.getClass();
			f = getField(classObj, name);
			if (f == null) {
				throw new SignalException("Field " + name + " not found in " + classObj.getName());
			}
			classAttr = (Class) f.getGenericType();
			classValue = value.getClass();

			// Conversiones para adaptar el destino

			if (classAttr.isAssignableFrom(Integer.class)
					|| classAttr.isAssignableFrom(Integer.TYPE)
					|| classAttr.isAssignableFrom(Long.class)
					|| classAttr.isAssignableFrom(Long.TYPE)) {

				val = toInt(value);

			} else if (classAttr.isAssignableFrom(Double.class)
					|| classAttr.isAssignableFrom(Double.TYPE)
					|| classAttr.isAssignableFrom(Float.class)
					|| classAttr.isAssignableFrom(Float.TYPE)) {

				val = toDouble(value);

			} else if (classAttr.isAssignableFrom(java.util.Date.class)) {

				val = toDate(value);

			} else if (classAttr.isAssignableFrom(String.class)) {

				val = toString(value, showInDates);

			} else {

				val = value;

			}

			invokeSetter(obj, name, val);
		} catch (SignalException e) {
			throw e;
		} catch (Exception e0) {
			try {
				f.set(obj, val);
			} catch (Exception e) {
				throw new SignalException(e.getMessage() + "."
						+ e.getMessage() + "Util.setValueOfAttribute(" + obj + "," + name + ", "
						+ value + "," + showInDates + ")");
			}
		}
	}

	public static <T> T getValueOfAttribute(Object obj, String attr) {
		T res = null;
		try {
			res = (T) invokeGetter(obj, attr);
		} catch (Exception e0) {
			try {
				Class<T> objOrSuper = (Class<T>) obj.getClass();
				Field camp = Util.getField(objOrSuper, attr);
				res = (T) camp.get(obj);
			} catch (Exception e) {
				throw new SignalException(e.getMessage() + "."
						+ e.getMessage() + "Util.getValueOfAttribute(" + obj + "," + attr
						+ ")");
			}
		}
		return res;
	}

	public static HashMap getAttributesNameValueHash(Object obj) {
		HashMap res = new HashMap();
		Field[] camps = null;
		Class objOrSuper = obj.getClass();
		String attrName = "";
		Object attrVal = "";
		int n;
		do {
			try {
				camps = objOrSuper.getDeclaredFields();
				n = camps.length;
				for (int i = 0; i < n; i++) {
					camps[i].setAccessible(true);
					attrName = camps[i].getName();
					attrVal = camps[i].get(obj);
					res.put(attrName, attrVal);
				}
				objOrSuper = objOrSuper.getSuperclass();
			} catch (Exception e) {
				objOrSuper = objOrSuper.getSuperclass();
			}
		} while (objOrSuper != null);
		return res;
	}

	public static Object invokeNoParams(Object obj, String nomMet) {
		Object resp = null;
		try {
			Method metodo = obj.getClass().getMethod(nomMet, new Class[0]);
			Object[] params = new Object[0];
			resp = metodo.invoke(obj, params);
		} catch (Exception e) {
			throw new SignalException(e);
		}
		return resp;
	}

	public static Object invokeSetter(Object obj, String attr, Object valor) {
		Object resp = null;
		try {
			String nomMet = "";
			nomMet = "set" + firstUppercase(attr);
			Class classeValor = null;
			if (valor == null) {
				classeValor = Object.class;
			} else {
				classeValor = valor.getClass();
			}

			Class classeVal = null;

			if (classeValor.isAssignableFrom(Integer.class)
					|| classeValor.isAssignableFrom(Integer.TYPE)) {
				classeVal = Integer.TYPE;
			} else if (classeValor.isAssignableFrom(Long.class)
					|| classeValor.isAssignableFrom(Long.TYPE)) {
				classeVal = Long.TYPE;
			} else if (classeValor.isAssignableFrom(Double.class)
					|| classeValor.isAssignableFrom(Double.TYPE)) {
				classeVal = Double.TYPE;
			} else if (classeValor.isAssignableFrom(Float.class)
					|| classeValor.isAssignableFrom(Float.TYPE)) {
				classeVal = Float.TYPE;
			} else if (classeValor.isAssignableFrom(Boolean.class)
					|| classeValor.isAssignableFrom(Boolean.TYPE)) {
				classeVal = Boolean.TYPE;
			} else {
				classeVal = classeValor;
			}

			Method metodo = obj.getClass().getMethod(nomMet, classeVal);
			Object[] params = new Object[1];
			params[0] = valor;
			resp = metodo.invoke(obj, params);
		} catch (Exception e) {
			throw new SignalException(e);
		}
		return resp;
	}

	public static Object invokeGetter(Object obj, String attr) {
		Object resp = null;
		String nomMet = "";
		nomMet = "get" + firstUppercase(attr);
		resp = invokeNoParams(obj, nomMet);
		return resp;
	}

	public static Object invokeBooleanGetter(Object obj, String attr) {
		Object resp = null;
		String nomMet = "";
		nomMet = "is" + firstUppercase(attr);
		resp = invokeNoParams(obj, nomMet);
		return resp;
	}

	public static String firstUppercase(String s) {
		String primLetra = s.substring(0, 1);
		return primLetra.toUpperCase() + s.substring(1);
	}

	public static String firstLowercase(String s) {
		String primLetra = s.substring(0, 1);
		return primLetra.toLowerCase() + s.substring(1);
	}

	public static String firstUppercaseRestLowercase(String s) {
		String primLetra = s.substring(0, 1);
		return primLetra.toUpperCase() + s.substring(1).toLowerCase();
	}

	public static String firstUppercaseRestLowercaseBlanks(String s) {
		String primLetra = s.substring(0, 1);
		return primLetra.toUpperCase()
				+ s.substring(1).toLowerCase().replace('_', ' ');
	}

	public static String firstUppercaseHungarian(String s) {
		String result = "";
		StringTokenizer st = new StringTokenizer(s, "_");
		while (st.hasMoreTokens()) {
			result += firstUppercaseRestLowercase(st.nextToken());
		}
		return result;
	}

	public static String firstLowercaseHungarian(String s) {
		String result = "";
		StringTokenizer st = new StringTokenizer(s, "_");
		int k = 0;
		while (st.hasMoreTokens()) {
			if (k++ == 0) {
				result += st.nextToken().toLowerCase();
			} else {
				result += firstUppercaseHungarian(st.nextToken());
			}
		}
		if (nvl(s).startsWith("_")) {
			result = "_" + result;
		}
		return result;
	}

	public static String firstUppercaseBlanks(String s0) {
		String s = firstUppercase(s0);
		int n = s.length();
		StringBuffer sb = new StringBuffer();
		sb.append(s.charAt(0));
		for (int i = 1; i < n; i++) {
			char c = s.charAt(i);
			if (Character.isUpperCase(c)) {
				sb.append(' ');
			}
			sb.append(Character.toLowerCase(c));
		}
		return sb.toString();
	}

	public static String firstUppercaseUnderscore(String s0) {
		String s = Util.nvl(s0);
		int n = s.length();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < n; i++) {
			char c = s.charAt(i);
			if (Character.isUpperCase(c)) {
				sb.append('_');
			}
			sb.append(Character.toUpperCase(c));
		}
		return sb.toString();
	}

	public static String nvl(Object valor) {
		return nvl(valor, "");
	}

	public static String nvl(Object valor, String valorSiNull) {
		return valor == null ? valorSiNull : valor.toString().trim();
	}

	public static int nvlInt(Integer valor) {
		return nvlInt(valor, 0);
	}

	public static int nvlInt(Integer valor, int valorSiNull) {
		return valor == null ? valorSiNull : valor;
	}

	public static String getTimestamp() {
		java.util.Date d = new java.util.Date(); // system date
		return getTimestamp(d);
	}

	public static String getTimestampMilis() {
		return "" + System.currentTimeMillis();
	}

	public static String getTimestamp(java.util.Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
		return sdf.format(d);
	}

	public static String getTimestampCompact() {
		java.util.Date d = new java.util.Date(); // system date
		return getTimestampCompact(d);
	}

	public static String getTimestampCompact(java.util.Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		return sdf.format(d);
	}

	public static String getDatestampCompact() {
		java.util.Date d = new java.util.Date(); 
		return getDatestampCompact(d);
	}

	public static String getDatestampCompact(java.util.Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(d);
	}

	public static String strFillR(String s, int i, String fill) {
		if (s == null) {
			s = "";
		}
		StringBuilder retorn = new StringBuilder(s);
		int longString = s.length();
		int dif = i - longString;
		for (i = 0; i < dif; i++) {
			retorn.append(fill);
		}
		return retorn.toString();
	}

	public static String strFill(String s, int i, String fill) {
		if (s == null) {
			s = "";
		}
		StringBuilder retorn = new StringBuilder();
		int longString = s.length();
		int dif = i - longString;
		for (i = 0; i < dif; i++) {
			retorn.append(fill);
		}
		retorn.append(s);
		return retorn.toString();
	}

	/**
	 * leftPad
	 */
	public static String strZero(String s, int i) {
		return strFill(s, i, "0");
	}

	public static String getStackTrace(Throwable t) {
		StringBuffer sb = new StringBuffer();
		int k = 0;
		String msg;
		while (t != null) {
			StackTraceElement elements[] = t.getStackTrace();
			if (k > 0) {
				sb.append("\nCaused by:");
			}
			msg = t.getMessage();
			if (msg == null) {
				msg = t.toString();
			}
			sb.append("\n" + t + "\n");
			for (int i = 0, n = elements.length; i < n; i++) {
				sb.append("  at " + elements[i].getClassName() + "."
						+ elements[i].getMethodName() + " ("
						+ elements[i].getFileName() + ":"
						+ elements[i].getLineNumber() + ")\n");
			}
			t = t.getCause();
			k++;
		}
		return sb.toString();
	}

	public static String getStackTrace() {
		String res = "";
		StackTraceElement[] st = null;
		try {
			throw new SignalException("dummy");
		} catch (Exception e) {
			st = e.getStackTrace();
		}

		StringBuffer sb = new StringBuffer();
		sb.append("[" + getTimestamp() + "]:\n");
		int n = st.length;
		for (int i = 1; i < n; i++) {
			sb.append(st[i].toString() + "\n");
		}
		res = sb.toString();
		return res;
	}

	public static int parseInt(String n) {
		return parseInt(n, 0);
	}

	public static int parseInt(String n, int defaultValue) {
		int res = 0;
		try {
			res = Integer.parseInt(n);
		} catch (NumberFormatException e) {
			res = defaultValue;
		}
		return res;
	}

	public static double parseDouble(String n) {
		return parseDouble(n, 0);
	}

	public static double parseDouble(String n, double defaultValue) {
		double res = 0;
		try {
			res = Double.parseDouble(fixQuotesDoubleInNumerics(n));
		} catch (NumberFormatException e) {
			res = defaultValue;
		}
		return res;
	}

	public static String fixQuotesDoubleInNumerics(String x) {
		String s = x;
		if (onePoint(x) && x.indexOf(',') < 0) { // Only one dot and no commas =>
			// Dot ignored
			s = x.replace(".", "");
		} else if (oneComa(x) && x.indexOf('.') < 0) { // One comma and not dot
			// => comma to dot
			s = x.replace(',', '.');
		} else if (onePoint(x) && oneComa(x)) {// One dot and one comma:
			int posp = x.indexOf('.');
			int posc = x.indexOf(',');
			if (posp < posc) { // First dot and after comma (latin style)
				String s0 = x.replace(".", "");
				s = s0.replace(',', '.');
			} else {
				// First comma and after dots (anglo style)
				s = x.replace(",", "");
			}
		} else if (moreThanOnePoint(x)) { // more than one point: latin
			String s0 = x.replaceAll(".", "");
			s = s0.replace(',', '.');
		} else if (moreThanOneComa(x)) { // more than one comma: anglo
			s = x.replaceAll(",", "");
		}

		return s;
	}

	public static boolean onePoint(String s) {
		boolean onepoint = true;
		int pos = s.indexOf('.');
		if (pos >= 0) {
			int pos2 = s.indexOf('.', pos + 1);
			if (pos2 >= 0) {
				onepoint = false;
			}
		} else {
			onepoint = false;
		}
		return onepoint;
	}

	public static boolean moreThanOnePoint(String s) {
		boolean mtonepoint = true;
		int pos = s.indexOf('.');
		if (pos >= 0) {
			int pos2 = s.indexOf('.', pos + 1);
			if (pos2 < 0) {
				mtonepoint = false;
			}
		} else {
			mtonepoint = false;
		}
		return mtonepoint;
	}

	public static boolean oneComa(String s) {
		boolean onecoma = true;
		int pos = s.indexOf(',');
		if (pos >= 0) {
			int pos2 = s.indexOf(',', pos + 1);
			if (pos2 >= 0) {
				onecoma = false;
			}
		} else {
			onecoma = false;
		}
		return onecoma;
	}

	public static boolean moreThanOneComa(String s) {
		boolean monecoma = true;
		int pos = s.indexOf(',');
		if (pos >= 0) {
			int pos2 = s.indexOf(',', pos + 1);
			if (pos2 < 0)
				monecoma = false;
		} else {
			monecoma = false;
		}
		return monecoma;
	}

	public static String formatDouble2ANSI(double x) {
		return formatDouble2ANSI("" + x);
	}

	public static String formatDouble2ANSI(String x) {
		String s = fixQuotesDoubleInNumerics(x);
		return s;
	}

	public static String formatDouble(double x) {
		return formatDouble(x, "#,##0.##");
	}
        
        public static String formatFloat(float f) {
            DecimalFormat df = new DecimalFormat("#0.00");
            return df.format(f);
        }
        
        public static float parseToFloat(String s) {
            DecimalFormat df = new DecimalFormat("#0.00");
            try {
                return df.parse(s).floatValue();
            } catch (ParseException ex) {
//                ex.printStackTrace();
    			Log.error(ex.getMessage(), ex);
                return 0f;
            }
        }

	public static String formatDouble(double x, String formatPattern) {
		Locale loc = new Locale("es", "ESP");
		NumberFormat nf = NumberFormat.getInstance(loc);
		if (nf instanceof DecimalFormat) {
			((DecimalFormat) nf).applyPattern(formatPattern);
		}
		return nf.format(x);
	}

	public static String escapeQuotes(String s) {
		String s2 = s.replaceAll("'", "''");
		return s2;
	}

	public static String formatDate(java.util.Date date) {
		String res = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			res = sdf.format(date);
		} catch (Exception e) {
		}
		return res;
	}

	public static String formatDateShort(java.util.Date date) {
		String res = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
			res = sdf.format(date);
		} catch (Exception e) {
		}
		return res;
	}

	public static String formatDateTimeISO8601(java.util.Date date) {
		String res = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			res = sdf.format(date);
		} catch (Exception e) {
//			e.printStackTrace();
			Log.error(e.getMessage(), e);
		}
		return res;
	}

	public static String formatDateISO8601(java.util.Date date) {
		String res = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			res = sdf.format(date);
		} catch (Exception e) {
		}
		return res;
	}

	public static String formatDateTime(java.util.Date date) {
		String res = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			res = sdf.format(date);
		} catch (Exception e) {
		}
		return res;
	}

	public static String formatDate2ANSI(java.util.Date date) {
		String res = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			res = sdf.format(date);
		} catch (Exception e) {
		}
		return res;
	}

	public static String formatDateTime2ANSI(java.util.Date date) {
		String res = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			res = sdf.format(date);
		} catch (Exception e) {
		}
		return res;
	}

	public static String formatDateTime2ANSI(String s) {
		try {
			SimpleDateFormat sd = new SimpleDateFormat();
			java.util.Date d = null;
			long t = 0;

			try {
				sd.applyPattern("dd/MM/yyyy HH:mm:ss");
				d = sd.parse(s);
			} catch (Exception ex0) {
				try {
					sd.applyPattern("dd/MM/yyyy HH:mm:ss.SSS");
					d = sd.parse(s);
				} catch (Exception e) {
					sd.applyPattern("dd-MM-yyyy");
					d = sd.parse(s);
				}
			}

			Calendar cal = Calendar.getInstance();
			cal.setTime(d);

			int y = cal.get(Calendar.YEAR);
			if (y >= 0 && y <= 100) {
				if (y < PIVOT_2000_YEAR) {
					cal.set(Calendar.YEAR, y + 2000);
				}
				if (y >= PIVOT_2000_YEAR) {
					cal.set(Calendar.YEAR, y + 1900);
				}
			}

			t = cal.getTime().getTime();
			java.sql.Timestamp ts = new java.sql.Timestamp(t);
			SimpleDateFormat sdf2 = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss.SSS");
			String res = sdf2.format(ts);
			return res;
		} catch (Exception ex1) {
			throw new SignalException(
					"Format error in Util.formatDateTime2ANSI(\"" + s
							+ "\")", ex1);
		}
	}

	public static String formatDate2ANSI(String s) {
		try {
			SimpleDateFormat sd = new SimpleDateFormat();
			java.util.Date d = null;
			long t = 0;

			try {
				sd.applyPattern("dd/MM/yyyy");
				d = sd.parse(s);
			} catch (Exception ex0) {
				sd.applyPattern("dd-MM-yyyy");
				d = sd.parse(s);
			}

			Calendar cal = Calendar.getInstance();
			cal.setTime(d);

			int y = cal.get(Calendar.YEAR);
			if (y >= 0 && y <= 100) {
				if (y < PIVOT_2000_YEAR) {
					cal.set(Calendar.YEAR, y + 2000);
				}
				if (y >= PIVOT_2000_YEAR) {
					cal.set(Calendar.YEAR, y + 1900);
				}
			}

			t = cal.getTime().getTime();
			java.sql.Timestamp ts = new java.sql.Timestamp(t);
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
			return sdf2.format(ts);
		} catch (Exception ex1) {
			throw new SignalException(
					"Format error in Util.formatDate2ANSI(\"" + s + "\")",
					ex1);
		}
	}

	public static java.sql.Date parseDate(String s) {
		java.sql.Date res = null;
		try {
			String t = formatDateTime2ANSI(s);
			java.sql.Timestamp ts = java.sql.Timestamp.valueOf(t);
			res = new java.sql.Date(ts.getTime());
		} catch (Exception e) {
			try {
				String d = formatDate2ANSI(s);
				res = java.sql.Date.valueOf(d);
			} catch (Exception e1) {
			}
		}
		return res;
	}

	public static java.sql.Date parseDateANSI(String s) {
		java.sql.Date res = null;
		try {
			java.sql.Timestamp ts = java.sql.Timestamp.valueOf(s);
			res = new java.sql.Date(ts.getTime());
		} catch (Exception e) {
			try {
				res = java.sql.Date.valueOf(s);
			} catch (Exception e1) {
			}
		}
		return res;
	}

	public static java.util.Date parseDateNumber (String date) {
		java.util.Date res = null;
		SimpleDateFormat sd = new SimpleDateFormat();
		try {
			sd.applyPattern("yyyyMMdd");
			res = sd.parse(date);
		} catch (Exception e) {
		}
		return res;
	}
	
	public static void copyFile(String in, String out) {
		copyFile(new File(in), new File(out));
	}
	
	public static void copyFile(File in, File out) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(in);
			fos = new FileOutputStream(out);
			byte[] buf = new byte[1024];
			int i = 0;
			while ((i = fis.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}
		} catch (Exception e) {
			throw new SignalException(e);
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (Exception e) {
//				e.printStackTrace();
				Log.error(e.getMessage(), e);
			}
		}
	}

	public static int oid2id(String s) {
		return oid2id(s, "");
	}

	public static int oid2id(String s, String prefix) {
		int res = 0;
		try {
			res = parseInt(s.substring(prefix.length()));
		} catch (Exception e) {
		}
		return res;
	}

	public static String sp(int n) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < n; i++) {
			sb.append(' ');
		}
		return sb.toString();
	}

	public static int getPosInStringArray(String[] v, String val) {
		int res = -1;
		boolean found = false;

		int i = 0;
		int n = v.length;

		while (!found && i < n) {
			if (v[i].equals(val)) {
				found = true;
			} else {
				i++;
			}
		}
		if (found) {
			res = i;
		}
		return res;
	}

	public static List getAllFields(Class c) {
		ArrayList vRes = new ArrayList();
		Field[] camps = null;
		Class objOrSuper = c;
		int n;
		do {
			try {
				camps = objOrSuper.getDeclaredFields();
				n = camps.length;
				for (int i = 0; i < n; i++) {
					camps[i].setAccessible(true);
					vRes.add(camps[i]);
				}
				objOrSuper = objOrSuper.getSuperclass();
			} catch (Exception e) {
				objOrSuper = objOrSuper.getSuperclass();
			}
		} while (objOrSuper != null);

		return vRes;
	}

	public static String[] getNombresAtributos(Object obj) {
		return getNombresAtributos(obj.getClass());
	}

	public static String[] getNombresAtributos(Class c) {
		String[] res = null;
		List<Field> vFields = getAllFields(c);

		int n = vFields.size();
		res = new String[n];
		for (int i = 0; i < n; i++) {
			res[i] = (String) vFields.get(i).getName();
		}
		return res;
	}

	public static Object[] getValuesOfAttributes(Object obj) {
		Object[] res = null;
		Class c = obj.getClass();
		List<Field> vFields = getAllFields(c);

		int n = vFields.size();
		res = new String[n];
		for (int i = 0; i < n; i++) {
			try {
				res[i] = vFields.get(i).get(obj);
			} catch (Exception e) {
			}
		}
		return res;
	}

	public static String getAttributeType(Object obj, String attr) {
		String res = "";
		try {
			Class c = obj.getClass();
			res = Util.getField(c, attr).getType().getName();
		} catch (Exception e) {
		}
		return res;
	}

	public static String[] getAttributesTypes(Object obj) {
		return getAttributesTypes(obj.getClass());
	}

	public static String[] getAttributesTypes(Class c) {
		String[] res = null;
		List<Field> vFields = getAllFields(c);

		int n = vFields.size();
		res = new String[n];
		for (int i = 0; i < n; i++) {
			res[i] = (String) vFields.get(i).getType().getName();
		}
		return res;
	}

	public static String getMessage(Throwable e) {
		String msg;
		msg = e.getLocalizedMessage();
		if (Util.nvl(msg).equals("") || Util.nvl(msg).equals("null")) {
			msg = e.getMessage();
			if (Util.nvl(msg).equals("") || Util.nvl(msg).equals("null")) {
				msg = e.toString();
			}
		}
		return msg;
	}

	public static boolean isBlankOrNull(String s) {
		if (Util.nvl(s).equals("") || Util.nvl(s).equals("null")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isNumeric(String str) {  
		  try {  
			  double d = Double.parseDouble(str);  
		  } catch(NumberFormatException nfe) {  
			  return false;  
		  }  
		  return true;  
	}	
	
	public static <T> T transfer2TO(Object entityOrig, Class<T> c) {
		return transfer2TO(entityOrig, c, false);
	}

	public static <T> T transfer2TO(Class<T> c, Object entityOrig) {
		return transfer2TO(entityOrig, c, false);
	}

	public static <T> T transfer2TO(Object entityOrig, Class<T> c,
			boolean showTimeInDates) {
		T res = null;
		try {
			res = c.newInstance();
			transfer2TO(entityOrig, res, showTimeInDates, null);
		} catch (Exception e) {
//			e.printStackTrace();
			Log.error(e.getMessage(), e);
		}
		return res;
	}

	public static void transfer2TO(Object objOrig, Object objDest) {
		transfer2TO(objOrig, objDest, false, null);
	}
	
	public static void transfer2TO(Object objOrig, Object objDest, HashMap map ) {
		transfer2TO(objOrig, objDest, false, map);
	}

	public static void transfer2TO(Object objOrig, Object objDest,
			boolean showTimeInDates, HashMap map) {
		String[] attrs = Util.getNombresAtributos(objDest);
		Object valor = null;
		int n = attrs.length;
		String attr;
		String mappedAttr;
		for (int i = 0; i < n; i++) {
			try {
				attr = attrs[i];
				if (map != null) {
					mappedAttr = (String) map.get(attr);
					if (mappedAttr == null) {
						mappedAttr = attr;
					}
				} else {
					mappedAttr = attr;
				}
				valor = getValueOfAttribute(objOrig, mappedAttr);
				setValueOfAttribute(objDest, attr, valor, showTimeInDates);
			} catch (Exception e) {
			}
		}
	}

	public static Collection transfer2TOs(Class dtoClass, Collection vObj) {
		return transfer2TOs(dtoClass, vObj, false, null);
	}
	
	public static Collection transfer2TOs(Class dtoClass, Collection vObj, HashMap map) {
		return transfer2TOs(dtoClass, vObj, false, map);
	}
	
	public static Collection transfer2TOs(Class dtoClass, Collection vObj,
			boolean showTimeInDates) {
		return transfer2TOs(dtoClass, vObj, showTimeInDates, null);		
	}
	
	public static Collection transfer2TOs(Class dtoClass, Collection vObj,
			boolean showTimeInDates, HashMap map) {
		((Bag) vObj).setDtoClass(dtoClass);
		((Bag) vObj).setShowTimeInDates(showTimeInDates);
		((Bag) vObj).setMapFields(map);
		return vObj;
	}

	public static <T> T transfer2Entity(Object toOrig, Class<T> c) {
		T res = null;
		try {
			res = c.newInstance();
			transfer2Entity(toOrig, res);
		} catch (Exception e) {
//			e.printStackTrace();
			Log.error(e.getMessage(), e);
		}
		return res;
	}

	public static void transfer2Entity(Object toOrig, Object objDest) {
		String[] attrs = Util.getNombresAtributos(objDest);
		Object valor = null;
		String tipoOrig = "";
		String tipoDest = "";
		int n = attrs.length;
		String attr;
		for (int i = 0; i < n; i++) {
			attr = attrs[i];
			tipoOrig = Util.getAttributeType(toOrig, attr);
			valor = Util.getValueOfAttribute(toOrig, attr);
			tipoDest = Util.getAttributeType(objDest, attr);
			if (tipoOrig.equals("java.lang.String")
					&& tipoDest.equals("java.util.Date")) {
				java.util.Date dValor = Util.parseDate((String) valor);
				Util.setValueOfAttribute(objDest, attr, dValor);
			} else if (tipoOrig.equals("java.lang.String")
					&& tipoDest.equals("boolean")) {
				boolean bValor;
				String sValor = Util.nvl(valor).toUpperCase();
				if (sValor.equals("TRUE") || sValor.equals("1")
						|| sValor.equals("Y") || sValor.equals("T")
						|| sValor.equals("V") || sValor.equals("S")
						|| sValor.equals("ON")) {
					bValor = true;
				} else {
					bValor = false;
				}
				Util.setValueOfAttribute(objDest, attr, bValor);
			} else if (tipoOrig.equals("java.lang.String")
					&& tipoDest.equals("int")) {
				int iValor;
				String sValor = Util.nvl(valor);
				iValor = Util.parseInt(sValor);
				Util.setValueOfAttribute(objDest, attr, iValor);
			} else if (tipoOrig.equals("java.lang.String")
					&& tipoDest.equals("double")) {
				double dValor;
				String sValor = Util.nvl(valor);
				dValor = Util.parseDouble(sValor);
				Util.setValueOfAttribute(objDest, attr, dValor);
			} else {
				Util.setValueOfAttribute(objDest, attr, valor);
			}
		}
	}

	public static List transferToList(Class dtoClass, Collection vObj) {
		return transferToList(dtoClass, vObj, 0, 9999, false, null);
	}

	public static List transferToList(Class dtoClass, Collection vObj, HashMap map) {
		return transferToList(dtoClass, vObj, 0, 9999, false, map);
	}

	public static List transferToList(Class dtoClass, Collection vObj,
			boolean showTimeInDates) {
		return transferToList(dtoClass, vObj, 0, 9999, showTimeInDates, null);
	}

	public static List transferToList(Collection vObj) {
		return transferToList(null, vObj, 0, 9999, false, null);
	}

	public static List transferToList(Collection vObj,
			boolean showTimeInDates) {
		return transferToList(null, vObj, 0, 9999, showTimeInDates, null);
	}

	public static List transferToList(Class dtoClass, Collection vObj,
			int from, int off) {
		return transferToList(dtoClass, vObj, from, off, false, null);
	}
	
	public static List transferToList(Class dtoClass, Collection vObj,
			int from, int off, HashMap map) {
		return transferToList(dtoClass, vObj, from, off, false, map);
	}

	public static List transferToList(Class dtoClass, Collection vObj,
			int from, int off, boolean showTimeInDates, HashMap map) {
		List vDtos = new ArrayList();

		Iterator it = vObj.iterator();
		int i = 0;
		while (i < from && it.hasNext()) { // rs.absolute(from);
			i++;
		}

		Object obj = null;
		Object dto = null;
		int j = 0;
		while (i < (from + off) && it.hasNext()) {
			i++;
			try {
				obj = it.next();
				if (dtoClass == null) {
					dtoClass = obj.getClass();
				}
				dto = dtoClass.newInstance();
				transfer2TO(obj, dto, showTimeInDates, map);
				vDtos.add(dto);
			} catch (Exception e) {
			}
			j++;
		}
		return vDtos;
	}

	public static boolean toBoolean(Object obj) {
		boolean res = false;
		if (obj instanceof Boolean) {
			res = (Boolean) obj;
		}
		return res;
	}

	public static int toInt(Object obj) {
		int res = 0;
		if (obj instanceof Float) {
			res = (int) (((Float) obj).doubleValue());
		} else if (obj instanceof Double) {
			res = (int) (((Double) obj).doubleValue());
		} else if (obj instanceof BigInteger) {
			res = (int) (((BigInteger)obj).intValue());
		} else if (obj instanceof BigDecimal) {
			res = (int) (((BigDecimal) obj).doubleValue());
		} else if (obj instanceof Integer) {
			res = ((Integer) obj).intValue();
		} else if (obj instanceof Long) {
			res = ((Long) obj).intValue();
		} else if (obj instanceof String) {
			res = (int) parseDouble(obj.toString());
		}
		return res;
	}

	public static double toDouble(Object obj) {
		double res = 0;
		if (obj instanceof Float) {
			res = (((Float) obj).doubleValue());
		} else if (obj instanceof Double) {
			res = (((Double) obj).doubleValue());
		} else if (obj instanceof BigDecimal) {
			res = (((BigDecimal) obj).doubleValue());
		} else if (obj instanceof Integer) {
			res = ((Integer) obj).intValue();
		} else if (obj instanceof Long) {
			res = ((Long) obj).intValue();
		} else if (obj instanceof String) {
			res = parseDouble(obj.toString());
		}
		return res;
	}

	public static String toString(Object obj) {
		return toString(obj, false);
	}

	public static String toString(Object obj, boolean showTimeInDates) {
		String res = "";
		try {
			java.util.Date obj2 = null;

			if (obj instanceof java.util.Date
					|| obj instanceof java.sql.Timestamp
					|| obj instanceof java.sql.Date) {
				obj2 = toDate(obj);
				if (showTimeInDates) {
					res = formatDateTime((java.util.Date) obj2);
				} else {
					res = formatDate((java.util.Date) obj2);
				}
			} else if (obj instanceof Double || obj instanceof Float) {
				res = formatDouble((Double) obj);
			} else if (obj instanceof BigDecimal) {
				res = formatDouble(((BigDecimal) obj).doubleValue());
			} else {
				res = obj.toString();
			}
		} catch (Exception e) {
		}
		return Util.nvl(res);
	}

	public static java.util.Date toDate(Object obj) {
		java.util.Date res = null;

		if (obj instanceof java.util.Date) {
			res = (java.util.Date) obj;
		} else if (obj instanceof java.sql.Timestamp
				|| obj instanceof java.sql.Date) {
			res = (java.util.Date) obj;
		} else {
			res = parseDate(obj.toString());
		}
		return res;
	}

	public static String tailExtension(String nomFitxer) {
		String res = "";

		try {
			int posPunt = nomFitxer.lastIndexOf(".");
			return nomFitxer.substring(0, posPunt);
		} catch (Exception e) {
		}

		return res;
	}

	public static String getExtension(String nomFitxer) {
		String res = "";

		try {
			int posPunt = nomFitxer.lastIndexOf(".");
			return nomFitxer.substring(posPunt + 1);
		} catch (Exception e) {
		}

		return res;
	}

	public static Gson getGsonInstance() {
		if (gson == null) {
			gson = new GsonBuilder()
					// .serializeNulls()
					.setDateFormat("yyyy-MM-dd HH:mm:ss")
					.registerTypeAdapter(java.util.Date.class,
							new JsonDateDeserializer())
					.setPrettyPrinting()
					.create();
		}
		return gson;
	}
	
	public static class LovElem {
		String value;
		String text;
	}

	public static List getLov(Collection vTOs, String fieldCod, String fieldVal) {
		return getLov(vTOs, fieldCod, fieldVal, false);
	}

	public static List getLov(Collection vTOs, String fieldCod,
			String fieldVal, boolean firstBlank) {
		List resp = new ArrayList();
		LovElem love = null;
		if (firstBlank) {
			love = new LovElem();
			love.value = "";
			love.text = "";
			resp.add(love);
		}
		for (Object to : vTOs) {
			love = new LovElem();
			love.value = getValueOfAttribute(to, fieldCod);
			love.text = getValueOfAttribute(to, fieldVal);
			resp.add(love);
		}
		return resp;
	}

	public static java.util.Date dateOffset(java.util.Date data, int dies) {
		Calendar cal0 = Calendar.getInstance();
		cal0.setTime(data);
		Calendar cal = (Calendar) cal0.clone();
		cal.add(Calendar.DATE, dies);
		return cal.getTime();
	}

	/*****************************************************************************/

	public static String dumpArray(Object[] arr) {
		StringBuffer sb = new StringBuffer();

		if (arr != null) {
			sb.append("\n");
			int n = arr.length;
			for (int i = 0; i < n; i++) {
				if (arr[i] != null)
					sb.append("|" + arr[i].toString() + "|\n");
				else
					sb.append("|null|\n");
			}
		} else
			Log.debug("Oooooops in Util.dumpArray(...) !!!! received array is null");

		return sb.toString();
	}

	public static String dumpHashMap(Map ht) {
		StringBuffer sb = new StringBuffer();
		if (ht != null) {
			Iterator keys = ht.keySet().iterator();
			sb.append("\n");
			while (keys.hasNext()) {
				Object clave = keys.next();
				Object elem = ht.get(clave);
				sb.append("|").append(clave).append("| - |").append(elem)
						.append("|\n");
			}
		} else {
			Log.debug("Oooooops in Util.dumpHashMap(...) !!!! received hashMap is null");
		}
		
		return sb.toString();
	}

	public static String dumpListTexts(List list) {
		StringBuffer sb = new StringBuffer();
		if (list != null) {
			int n = list.size();
			for (int i = 0; i < n; i++) {
				sb.append(list.get(i).toString() + "\n");
			}
		} else
			Log.debug("Oooooops in Util.dumpList(...) !!!! received list is null");

		return sb.toString();
	}

	public static String dumpList(List list) {
		return dumpCollection(list);
	}

	public static String dumpCollection(Collection vObj) {
		StringBuffer sb = new StringBuffer();
		if (vObj != null) {
			int n = vObj.size();
			for (Object obj : vObj) {
				sb.append(dumpObject(obj));
				// sb.append("----------------------------------------");
			}
		} else
			Log.debug("Oooooops in Util.dumpList(...) !!!! received list is null");

		return sb.toString();
	}

	public static String dumpObject(Object obj) {
		StringBuffer sb = new StringBuffer();
		if (obj != null) {
			Class clase = obj.getClass();
			Method[] metodos = clase.getMethods();
			String nomClas = clase.getName();

			int n = metodos.length;
			sb.append("<pre>");
			sb.append("\n");
			for (int i = 0; i < n; i++) {
				String nomMet = metodos[i].getName();
				if (nomMet.startsWith("get") || nomMet.startsWith("is")) {
					Class[] tiposParams = null;
					Object objRes;
					try {
						tiposParams = new Class[0];
						Method metodo = clase.getMethod(nomMet, tiposParams);
						nomMet = metodo.getName();
						Object[] params = new Object[0];
						objRes = metodo.invoke(obj, params);
						String stProp = "";
						if (objRes != null) {
							stProp = objRes.toString();
						}
						String nomProp = firstLowercase(nomMet.substring(3));
						sb.append(nomProp + " = |" + stProp + "|\n");
					} catch (Exception e) {
						Log.debug(e + " Captured at Util.dumpObject");
						Log.debug(" invoked: " + nomMet);
					}
				}
			}

			if (obj instanceof String[]) {
				sb.append("Array: ");
				sb.append(dumpArray((String[]) obj));
			}
		} else
			Log.debug("Oooooops en Util.dumpObject(...) !!!! received obj is null");

		sb.append("</pre>\n\n");
		return sb.toString();
	}

	public static String pingHttpPost(String url) {
		return pingHttpPost(url, "");
	}
	
	public static String pingHttpPost(String url, String data) {
		return pingHttpPost(url, data, "");
	}

	public static String pingHttpPost(String url, String data, String identityKey) {
		return pingHttpPostSb(url, data, identityKey).toString();
	}
	
	/**
	 * "p=" + URLEncoder.encode("abcde 123") + "&q=" +
	 * URLEncoder.encode("xyzt");
	 */
	public static StringBuilder pingHttpPostSb(String url, String data, String identityKey) {
		StringBuilder sb = new StringBuilder();
		try {
			URL urlObj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setRequestProperty("Content-Length", "" + data.length());
			con.setUseCaches(false);
			con.setDoInput(true);
			con.setDoOutput(true);
			if (!Util.nvl(identityKey).equals("")) {
				con.setRequestProperty("IDENTITY_KEY", identityKey);
			}

			DataOutputStream dout = new DataOutputStream(con.getOutputStream());
			dout.writeBytes(data);
			dout.close();

			InputStream in = con.getInputStream();

			int c;
			while ((c = in.read()) != -1) {
				sb.append((char) c);
			}

		} catch (Exception e) {
//			e.printStackTrace();
			Log.error(e.getMessage(), e);
		}

		return sb;
	}

	
	public static String pingHttpGet(String url) {
		return pingHttpGetSb(url, "", "").toString();
	}
	
	public static String pingHttpGet(String url, String dataQueryString) {
		return pingHttpGetSb(url, dataQueryString, "").toString();
	}
	
	public static String pingHttpGet(String url, String dataQueryString, String identityKey) {
		return pingHttpGetSb(url, dataQueryString, identityKey).toString();
	}

	
	public static StringBuilder pingHttpGetSb(String url, String dataQueryString, String identityKey) {
		StringBuilder sb = new StringBuilder();

		try {
			
			if (!Util.nvl(dataQueryString).equals("")) {
				url += "?" + dataQueryString;
			}
			
			URL urlObj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
			conn.setUseCaches(false);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			if (!Util.nvl(identityKey).equals("")) {
				conn.setRequestProperty("IDENTITY_KEY", identityKey);
			}

			InputStream in = conn.getInputStream();
			int c;
			while ((c = in.read()) != -1) {
				sb.append((char) c);
			}

		} catch (Exception e) {
//			e.printStackTrace();
			Log.error(e.getMessage(), e);
		}

		return sb;
	}
	
	
	public static List getIpAddresses() {
		ArrayList res = new ArrayList();
		String ip;
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface
					.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface iface = interfaces.nextElement();
				// filters out 127.0.0.1 and inactive interfaces
				if (iface.isLoopback() || !iface.isUp()) {
					continue;
				}
				Enumeration<InetAddress> addresses = iface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress addr = addresses.nextElement();
					ip = addr.getHostAddress();
					res.add("" + ip);
				}
			}
		} catch (Exception e) {
			throw new SignalException(e);
		}

		return res;
	}

	public static boolean haveIp(String ip) {
		List ips = getIpAddresses();
		int pos = ips.indexOf(ip);
		return pos >= 0;
	}


	public static String encryptData(String user, String pass, String keyAESb64) {
		byte[] keyAES = Base64.decode(keyAESb64);
		return encryptData(user, pass, keyAES);
	}

	public static String encryptData(String user, String hashPass, byte[] keyAES) {
		String s = "";
		try {
			String userPass = user + ":" + hashPass;
			// HASH de usuari i contrasenya
			byte hash[] = MessageDigest.getInstance("SHA-1").digest(
					userPass.getBytes());
			hash = printByteArray(hash).getBytes();
			// LoggerApp.i("AuthWebService", "HASH: " + new String(hash));
			// Crear el Cipher per xifrar el HASH amb AES
			Cipher crypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
			// Creacio parametres necessais per mode CBC clau i IV (Inicial
			// Vector)
			SecretKeySpec key = new SecretKeySpec(keyAES, "AES");
			String iv = "\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0"; // per defecte en
															// AES de PHP
			IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
			crypt.init(Cipher.ENCRYPT_MODE, key, ivspec);
			// Encriptem el HASH amb AES
			byte[] hashWithPadding = hash;
			byte[] crypted = crypt.doFinal(hashWithPadding);
			// Encriptem el AES en BASE64
			// byte[] output = Base64.encode(crypted, Base64.DEFAULT);
			// s = new String(output);
			s = Base64.encode(crypted);
			// LoggerApp.v("AuthWebService", "Data Sended: " + s);
		} catch (Exception ex) {
			// LoggerApp.v("ExceptionRuntime", ex.toString());
			throw new SignalException(99, "{msg:'" + ex.toString() + "'}");
		}
		return s;
	}

	
	public static String printByteArray(byte[] bs) {
		String s = "";
		if (bs != null) {
			for (int i = 0; i < bs.length; ++i) {
				int b = ((int) bs[i]) & 0xff;
				if (b > 0x0f) {
					s += Integer.toHexString(b);
				} else {
					s += "0" + Integer.toHexString(b);
				}
			}
		}
		return s;
	}

	
	public static byte[] decryptData(String dataToDecryptB64, String key) {
		byte[] dataToDecrypt = Base64.decode(dataToDecryptB64);
		return decryptData(dataToDecrypt, key);
	}

	public static byte[] decryptData(String dataToDecryptB64) {
		String key = "\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0";
		byte[] decrypted = null;
		try {
			// byte[] decryptB64 = Base64.decode(keyToDecrypt, Base64.DEFAULT);
			byte[] decryptB64 = Base64.decode(dataToDecryptB64);
			// Clau IV per defecte en el AES de PHP
			String iv = "\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0";
			IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
			// Clau secreta per defecte en el AES de PHP
			// String s = "\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0";
			String s = key;
			SecretKeySpec keyspec = new SecretKeySpec(s.getBytes(), "AES");
			Cipher cipher;
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
			decrypted = cipher.doFinal(decryptB64);
		} catch (Exception ex) {
			// LoggerApp.v("Debugging", ex.toString());
//			ex.printStackTrace();
			Log.error(ex.getMessage(), ex);
			throw new SignalException(99, "{msg:'" + ex.toString() + "'}");
		}
		return decrypted;

	}

	
	public static String sha1(String data) {
		String res = "";
		try {
			byte hash[] = MessageDigest.getInstance("SHA-1").digest(
					data.getBytes());
			res = printByteArray(hash);
		} catch (Exception e) {
			throw new SignalException(99, "{msg:" + e.toString() + "}");
		}
		return res;
	}


	public static byte[] decryptData(byte[] dataToDecrypt, String key) {
		byte[] decrypted = null;
		try {
			// byte[] decryptB64 = Base64.decode(keyToDecrypt, Base64.DEFAULT);
			byte[] decryptB64 = Base64.decode(dataToDecrypt);
			// Clau IV per defecte en el AES de PHP
			String iv = "\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0";
			IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
			// Clau secreta per defecte en el AES de PHP
			// String s = "\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0";
			String s = key;
			SecretKeySpec keyspec = new SecretKeySpec(s.getBytes(), "AES");
			Cipher cipher;
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
			decrypted = cipher.doFinal(decryptB64);
		} catch (Exception ex) {
			// LoggerApp.v("Debugging", ex.toString());
//			ex.printStackTrace();
			Log.error(ex.getMessage(), ex);
			throw new SignalException(99, "{msg:'" + ex.toString() + "'}");
		}
		return decrypted;
	}


	/**
	 * Ex.: msgEmail = fillTemplate("cognom", cognom, msgEmail);
	 */
	public static String fillTemplate(String camp, String valorCamp,
			String template) {
		String res = "";
		// return template.replaceAll("\\$\\{" + camp + "\\}", valorCamp);
		res = template.replace((CharSequence) camp, (CharSequence) valorCamp);
		// res = template.replaceAll(camp, valorCamp);
		return res;
	}

	/*
	 * KEY RECEIVED: pyXpYj/afzLR2GccIiA1KjnP7jMRo6Jg2bIDhw4BHwU= KEY DECRYPTED:
	 * 10886d4651959211 length: 128bits HASH:
	 * 8f0d7c8a340bbe0d18f65e1df11bde325abed193 Data:
	 * cLewmKZWXb7fu6QANX+dg5BQrk/bGSf/pQ3B8qq7WXnoG3nvhE+H+XI9QqUW1C5l
	 */
	public void decryptTest() {
		byte[] clau = Util
				.decryptData("pyXpYj/afzLR2GccIiA1KjnP7jMRo6Jg2bIDhw4BHwU=");
		String stDec = new String(clau);
		System.out.println(stDec);
		String hash = encryptData("demoeso", Util.sha1("demoeso"), clau);
		System.out.println(hash);
	}


	public void haveIpTest() {
		// String codiPhp = getPHPClass("marmite.Articulo", "Articulo");
		// System.out.println(codiPhp);

		// String res = pinchaPost("http://localhost:8080/vvaula/_ucr",
		// "_tok=1234&_op=marmite.model.ShopCtl.confirmarComandes");
		// System.out.println(res);
		// System.out.println("Fi");

		List ips = getIpAddresses();
		// System.out.println(Util.dumpListTexts(ips));
		System.out.println(Util.haveIp("172.25.1.9"));
		System.out.println("Fi");
	}

	public static List parseList(String list, String separator) {
		List res = new ArrayList();
		StringTokenizer st = new StringTokenizer(list, separator);
		String tok = "";
		while (st.hasMoreTokens()) {
			tok = st.nextToken();
			res.add(tok);
		}
		return res;
	}

	public static String toUTF8(String valor) {
		String res = "";
		try {
			res = new String(valor.getBytes("UTF-8"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			res = valor;
		}
		return res;
	}

	
	public static String urlDecodeUTF8 (String text) {
		String resp = "";
		try {
			resp = URLDecoder.decode(text, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		return resp;
	}
	
	public static String toCamelCase(String str) {
		return str.substring(0, 1).toLowerCase() + str.substring(1);
	}

	public static JsonObject parseJson(String jsonString) {
		JsonObject res = new JsonParser().parse(jsonString).getAsJsonObject();
		return res;
	}

	public static Throwable getRootCause(Throwable th) {
		if (th instanceof SQLException) {
			SQLException sql = (SQLException) th;
			if (sql.getNextException() != null) {
				return getRootCause(sql.getNextException());
			}
		} else if (th.getCause() != null) {
			return getRootCause(th.getCause());
		}
		return th;
	}

	public static void rethrow (Exception e) {
		if (e instanceof RuntimeException) {
			throw (RuntimeException) e;
		} else if (e instanceof SignalException) {
			throw (SignalException) e;
		} else {
			throw new SignalException (99, e.toString());
		}
	}
	
	
	/**
	 * @param ctl
	 * @param method
	 * @param ctx
	 * @return
	 * @throws SignalException
	 */
    public static Object invokeCallMethod(String ctl, String method, RequestContext ctx) throws SignalException { 
        String lastClass = ctl;
        try {
            Class ucClass = Class.forName(ctl);
            Object ucObject = ucClass.newInstance();
            Method met;
            Object resp;
            met = ucClass.getMethod(method, RequestContext.class);
            resp = met.invoke(ucObject, ctx);
            return resp;
        }
        catch(ClassNotFoundException cnf) {
            throw new SignalException(99, "Class " + lastClass + " does not exist.");
        }
        catch (NoSuchMethodException nsme) {
            throw new SignalException(99, "Method " + ctl + "::" + method + "(RequestContext.class) does not exist.");
        }
        catch (IllegalArgumentException iae) {
            throw new SignalException(99, "Incorrect parametres for " + ctl + "::" + method + "(RequestContext.class)");
        }
        catch (InvocationTargetException ite) {
        	Throwable tex = ite.getTargetException();
            if (tex instanceof SignalException) throw (SignalException) tex;
            else throw new SignalException(99, "Error invoking " + ctl + "::" + method, tex);
        }
        catch(Exception ex) {
            if (ex instanceof SignalException) throw (SignalException) ex;
            else throw new SignalException(99, "Error invoking " + ctl + "::" + method + ": " + Util.getRootCause(ex));
        }
    }
    

    public static Object invokeMethodRest(String uri, RequestContext ctx) throws SignalException { 
    	Object resp = null;
		String pkg = "";
		String met = "";
    	
        try {
    		String encodedUri = Util.getUriPathDecoded(uri);
    		String verb = ctx.getVerb();
    		String tplVerb = CtlUriRoute.getUriTemplate(encodedUri , verb);
    		if (tplVerb.equals("")) {
    			throw new BadUriRequestException("Template not found for |" + encodedUri + "| " + verb);
    		}
    		String jmet = CtlUriRoute.getJavaMethod(tplVerb);
    		pkg = CtlUriRoute.getPackage(jmet);
    		met = CtlUriRoute.getMethod(jmet);
    		String tpl = tplVerb.substring(0, tplVerb.indexOf("|"));
    		Map uriParams = CtlUriRoute.getParamsMap(encodedUri, tpl);
    		ctx.setUriParams(uriParams);
        	resp = Util.invokeCallMethod(pkg, met, ctx);                    
            return resp;
        } catch(Exception ex) {
            if (ex instanceof BadUriRequestException) { 
            	throw (BadUriRequestException) ex;
            } else if (ex instanceof SignalException) { 
            	throw (SignalException) ex;
            } else {
            	throw new SignalException(99, "Error invoking uri |" + uri + " (" + ex.toString() + ")", ex);
            }
        }
    }    
    

	public static Object invokeMethodRestSim(String uri, RequestContext ctx) throws SignalException { 
        Object resp = null;
		String tplVerb = "";
        try {
    		String encodedUri = Util.getUriPathDecoded(uri);
    		String verb = ctx.getVerb();
    		tplVerb = CtlUriRoute.getUriTemplateSim(encodedUri, verb);
    		if (tplVerb.equals("")) {
    			throw new SignalException(99, "Template not found for url |" + uri + "|");
    		}
    		
    		String tpl = tplVerb.substring(0, tplVerb.indexOf("|"));
    		Map uriParams = CtlUriRoute.getParamsMap(encodedUri, tpl);
    		ctx.setUriParams(uriParams);

    		UcsimDic ucd = UcsimDic.getInstance(DB.getDbUcsimName());
    		Ucsim uc = ucd.findByUriTemplate(tpl, verb);
        	
    		String jsCode = uc.getJsCode();
        	JS.init();
			JS.eval("importPackage(Packages.com.jumppi.frwk.sim);");

			byte[] bodyInput = ctx.getBodyInput();
			JSON params = JSON.parse(bodyInput);
			JSON jsonUrlParams = JSON.toJson(uriParams);

			String _uri = jsonUrlParams == null || jsonUrlParams.obj == null ? "{}" : jsonUrlParams.toJsonString();
			String _in = params == null || params.obj == null || params.obj instanceof JsonNull ? "{}" : params.toJsonString();
			
			JS.eval("var _uri = " + _uri + ";");
			JS.eval("var _out = {};");
			JS.eval("var _in = " + _in + ";");
			JS.eval("var _tok = '" + ctx.getToken() + "';");
			
			JS.eval("function _setPool(poolName, obj){" + 
					"	Pool.set(poolName, JSON.stringify(obj, null, 2));" + 
					"}");

			JS.eval("function _getPool(poolName){" + 
					"	var data = Pool.get(poolName);" + 
					"	return JSON.parse(data);" + 
					"}");

			JS.eval("function _erasePool(poolName){" + 
					"	Pool.set(poolName, '');" + 
					"}");

			JS.eval("function _clonePool(poolOrigName, poolDestName){" + 
					"	_setPool(poolDestName, _getPool(poolOrigName));" +
					"}");
			
			JS.eval(jsCode);
			resp = JS.eval("JSON.stringify(_out);");
            return resp;
        }
        catch(Exception ex) {
            if (ex instanceof BadUriRequestException) { 
            	throw (BadUriRequestException) ex;
            } else if (ex instanceof SignalException) { 
            	throw (SignalException) ex;
            } else {
            	throw new SignalException(99, "Error invoking " + tplVerb + " (" + ex.toString() + ")");
            }	
        }
    }
	
	public static String relPath(String absPath) {
		String res = "";
		int p1 = absPath.lastIndexOf("/");
		int p2 = absPath.lastIndexOf("\\");
		int pos = p1 > p2 ? p1 : p2;
		res = absPath.substring(pos + 1);
		return res;
	}

	public static String path(String absPath) {
		String res = "";
		int p1 = absPath.lastIndexOf("/");
		int p2 = absPath.lastIndexOf("\\");
		int pos = p1 > p2 ? p1 : p2;
		try {
			if (pos < 0) {
				res = "";
			} else {
				res = absPath.substring(0, pos);
			}
		} catch (Exception e) {
			Log.error(absPath + ", pos=" + pos, e);
//			e.printStackTrace();
		}
		return res;
	}
    
	public static String executeBatSyncDeprecated(String[] cmd) {
		String res = "";
		try {
			Runtime r = Runtime.getRuntime();
			Process p = r.exec(cmd);
			InputStream in = p.getInputStream();
			StringBuilder sb = new StringBuilder();
		    int c;
		    while ((c = in.read()) != -1) {
		    	sb.append((char) c);
//		    	Log.debug(sb.toString());
		    }
			in.close();
			res = sb.toString();
//			Log.debug(res);
			p.waitFor();
		} catch (Exception e) {
//			e.printStackTrace();
			Log.error(e.getMessage(), e);
		}
		return res;
	}


	public static String executeBatSync(String[] cmd) {
		return executeBatSync(cmd, null);
	}
	
	public static String executeBatSync(String[] cmd, File fOutBuffers) {
		String res = "";
		try {
			List<String> vCommand = new ArrayList(); 
			if (cmd != null) {
				int n = cmd.length;
				for (int i = 0; i < n; i++) {
					if (cmd[i] != null) {
						vCommand.add(cmd[i]);
					}
				}
			}
			
			ProcessBuilder processBuilder = new ProcessBuilder(vCommand);			
			if (fOutBuffers != null) {
			    processBuilder.redirectErrorStream(true);
			    processBuilder.redirectOutput(fOutBuffers);			
			}
			Process process = processBuilder.start();
			StringBuilder sb = new StringBuilder();
			BufferedReader reader =
			new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			process.waitFor();			
			res = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage(), e);
		}
		return res;
	}


	public static String executeBatAsync(String[] cmd) {
		return executeBatAsync(cmd, null);
	}
	
	public static String executeBatAsync(String[] cmd, File fOutBuffers) {
		String res = "";
		try {
			List<String> vCommand = new ArrayList(); 
			if (cmd != null) {
				int n = cmd.length;
				for (int i = 0; i < n; i++) {
					if (cmd[i] != null) {
						vCommand.add(cmd[i]);
					}
				}
			}
			
			ProcessBuilder processBuilder = new ProcessBuilder(vCommand);			
			if (fOutBuffers != null) {
			    processBuilder.redirectErrorStream(true);
			    processBuilder.redirectOutput(fOutBuffers);			
			}
			Process process = processBuilder.start();
			StringBuilder sb = new StringBuilder();
			BufferedReader reader =
			new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
//			process.waitFor();			
			res = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage(), e);
		}
		return res;
	}
	
	
	
	/**
	 * Path parameters must use URL safe characters.
	 */
	public static String getUriPathDecoded(String uri) {
		String res = "";
		
		int pos = uri.indexOf("?");
		String path;
		String queryString;
		if (pos >= 0) {
			path = uri.substring(0, pos); 
			queryString = uri.substring(pos + 1);
		} else {
			path = uri;
			queryString = "";
		}
		res = URICoder.encode(path, '/');
		if (pos >= 0) {
			res = res + "?" + queryString;
		}
		return res;
	}

	/**
	 *  {"errors":[{"code":10,"message":"Login error"}]}
	 */
	public static JSON getError(int cod, String msg) {
		JSON res = JSON.getInstanceObject();
		res.add("status", "KO");
		JSON vErrs = JSON.getInstanceArray();
		JSON err = JSON.getInstanceObject();
		err.add("code", cod);
		err.add("message", msg);
		vErrs.add(err);
		res.add("errors", vErrs);
		return res;
	}
	
	public static JSON getError(String cod, String msg) {
		return getError(Util.parseInt(cod), msg);
	}

	public static void saveToFile(String txt, String absFileName) {
		saveToFile(txt, absFileName, false);
	}

	public static void saveToFile(String txt, String absFileName, boolean append) {
		try {
			FileOutputStream fXmlIn = new FileOutputStream(absFileName);
			PrintStream pXmlIn = new PrintStream(fXmlIn);
			pXmlIn.println(txt);
			pXmlIn.close();
		} catch (Exception e) {
			Log.error(e);
		}
	}

	public static String readFromFile(String absFileName) {
		String res = "";
	    try {
			FileInputStream  f = new FileInputStream(absFileName);
			int c;
			StringBuilder sb = new StringBuilder();
			while ((c = f.read()) != -1) {
				sb.append((char) c);
			}		
			res = sb.toString();		
			f.close();
		} catch (Exception e) {
			Log.error(e);
		}
		return res;
	}
	
}


