package com.jumppi.frwk.sql;

import java.util.*;
import java.lang.reflect.Method;
import java.sql.*;

import com.jumppi.frwk.log.Log;
import com.jumppi.frwk.util.SignalException;
import com.jumppi.frwk.util.Util;


public class Bag implements Collection {
	protected Statement stmt;
	protected ResultSet rs;
	protected boolean hasNext = true;
	protected Class[] entityClasses;
	protected Class dtoClass = null;
	protected String discrim = null;
	protected String[] valsDiscrim = null;
	protected boolean showTimeInDates = false;
	protected Class dbClass;
	protected HashMap map = null;
	
	public Bag (Class bdClase) {
		this.dbClass = bdClase;
	}
	
	public void setEntityClass(Class entityClass) {
		this.entityClasses = new Class[1];
		this.entityClasses[0] = entityClass;
	}
	
	public void setEntityClasses(Class[] entityClasses) {
		this.entityClasses = entityClasses;
	}

	public void setDtoClass(Class dtoClass) {
		this.dtoClass = dtoClass;
	}
	
	public void setDiscrim(String discrim) {
		this.discrim = discrim;
	}
	
	public void setValuesDiscriminator(String[] valsDiscrim) {
		this.valsDiscrim = valsDiscrim;
	}
	

	public void setShowTimeInDates(boolean showTimeInDates){
		this.showTimeInDates = showTimeInDates;
	}
	
	public void setRs(ResultSet rs) {
		try {
			this.rs = rs;
			stmt = rs.getStatement();
		} catch (SQLException e) {
			throw new SignalException(e);
		}
	}

	public void setMapFields(HashMap map) {
		this.map = map;
	}
	
	public void reload(){
		
	}
	
	
	public int size() {
		return -1;
	}

	public boolean isEmpty() {
		return false;
	}

	public boolean contains(Object o) {
		return false;
	}

	public Iterator iterator() {
		return new Iterator(){
			public boolean hasNext() {
				try {
					hasNext = rs.next();
					if (!hasNext){
						clear();
					}
					return hasNext;
				} catch (Exception e) {
					throw new SignalException(e);
				}
			}

			public Object next() {
				Object res = null;
				Method met = null;
				Object[] params = null;
				try {
					Class[] parTypes;
					if (Util.nvl(discrim).equals("")){
						parTypes = new Class[2];
						parTypes[0] = Class.class;
						parTypes[1] = ResultSet.class;
						met = dbClass.getMethod("getDTO", parTypes);
						params = new Object[2];
						params[0] = entityClasses[0];
						params[1] = rs;
						res = met.invoke(null, params);
						
//						res = BD.getDTO(entityClasses[0], rs);
					}else{
						parTypes = new Class[2];
						parTypes[0] = Class[].class;
						parTypes[1] = String.class;
						parTypes[2] = String[].class;
						parTypes[3] = ResultSet.class;
						met = dbClass.getMethod("getDTO", parTypes);
						params = new Object[2];
						params[0] = entityClasses;
						params[1] = discrim;
						params[2] = valsDiscrim;
						params[3] = rs;
						res = met.invoke(null, params);
						
//						res = BD.getDTO(entityClasses, discrim, valsDiscrim, rs);
					}
					if (dtoClass != null){
						res = Util.transfer2TO(res, dtoClass, showTimeInDates);
					}
				} catch (Exception e) {
//					e.printStackTrace();
					Log.error(e.getMessage(), e);
				}
				return res;
			}

			public void remove() {
			}
		};
	}

	public Object[] toArray() {
		return null;
	}

	public Object[] toArray(Object[] a) {
		return null;
	}

	public boolean add(Object e) {
		return false;
	}

	public boolean remove(Object o) {
		return false;
	}

	public boolean containsAll(Collection c) {
		return false;
	}

	public boolean addAll(Collection c) {
		return false;
	}

	public boolean removeAll(Collection c) {
		return false;
	}

	public boolean retainAll(Collection c) {
		return false;
	}
	

	public void clear() {
		try {
			rs.close();
		} catch (Exception e) {
		}
		try {
			stmt.close();
		} catch (Exception e) {
		}
	}
	
}
