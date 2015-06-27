
package com.jumppi.frwk.sim.wiz;

import java.util.*;
import java.sql.*;

import com.jumppi.frwk.*;
import com.jumppi.frwk.sql.*;
import com.jumppi.frwk.sim.*;
import com.jumppi.frwk.util.SignalException;


/**
@@ Params gene XML:
 
<entity>
  <class-name>Ucsim</class-name>
  <class-name-plural>Ucsims</class-name-plural>
  <table-name>ucsim</table-name>
 
  <path-output-wiz>src_wiz</path-output-wiz>
  <path-output-nowiz>src_nowiz</path-output-nowiz>
 
  <driver>com.mysql.jdbc.Driver</driver>
  <url-db>jdbc:mysql://127.0.0.1/appl</url-db>
  <username>root</username>
  <password></password>
  <package>appl.model</package>
  <read-only>false</read-only>
  <logic-deletion>false</logic-deletion>
  <db-class-name>DB</db-class-name>
  <ts-crea>14-abr-2015 11:47:42</ts-crea>
</entity>
 
*/
public class UcsimDicWiz
{
	  protected String idCon;
	
	
	public static final String queryFindAll = 
     " SELECT id_ucsim, description, uri_template, http_method, js_code " +
     " FROM ucsim ";
	
	
	public static final String queryFindById = 
     " SELECT id_ucsim, description, uri_template, http_method, js_code " +
     " FROM ucsim " +
     " WHERE id_ucsim = ? ";
	
    protected UcsimDicWiz(String idCon) 
    {
      this.idCon = idCon;
    }
	
	public static final String queryCreateRecord = 
      "INSERT INTO ucsim(" + 
                       " description, " + 
                       " uri_template, " + 
                       " http_method, " + 
                       " js_code) " + 
                       " VALUES (?, ?, ?, ?)";	
	
	public static final String queryUpdateRecord = 	
      "UPDATE ucsim SET " + 
                       " description = ?, " + 
                       " uri_template = ?, " + 
                       " http_method = ?, " + 
                       " js_code = ?  " + 
                       " WHERE id_ucsim = ?";
	
	
	public static final String queryDeleteRecord = 	
     " DELETE FROM ucsim WHERE id_ucsim = ? ";
	
	
	public static final String queryDeleteRecordLogically = 	
     " UPDATE ucsim SET deleted = 1, timedeleted = CURRENT_TIMESTAMP() WHERE id_ucsim = ? ";
	
	
  
  public Ucsim createUcsim()
  {
    return createUcsim(true);
  }
  
  public Ucsim createUcsim(boolean persisted)
  {
    UcsimDic vObj = UcsimDic.getInstance(idCon);
    Ucsim obj = new Ucsim();
    if (persisted)
      vObj.save(obj);
    return obj;
  }
  
  
  public int createRecord(Ucsim obj)
  {
    int lastId = 0;
  
    try
	{
		DB db = DB.getInstance(idCon);
		List params = new ArrayList();
		
		params.add(obj.getDescription());
		params.add(obj.getUriTemplate());
		params.add(obj.getHttpMethod());
		params.add(obj.getJsCode());
		lastId = db.executeInsertSQL(queryCreateRecord, params);
		obj.setIdUcsim(lastId);
	}
	catch (Exception e)
	{
		throw new SignalException(e);
	}
    
    return lastId;
  }
  
  
  public int updateRecord(Ucsim obj)
  {
	int res = 0;
	
    try
	{
		
		List params = new ArrayList();
		
		params.add(obj.getDescription());
		params.add(obj.getUriTemplate());
		params.add(obj.getHttpMethod());
		params.add(obj.getJsCode());
		
		params.add(obj.getIdUcsim()); 
		
		DB db = DB.getInstance(idCon);
		res = db.executeSQL(queryUpdateRecord, params);
	}
	catch (Exception e)
	{
		throw new SignalException(e);
	}
    
    return res;
  }
  
  public int deleteRecord(Ucsim obj)
  {
    int idInt = obj.getIdInt();
    return deleteRecord(idInt);
  }
  
  
  public int remove(Ucsim obj)
  {
    return deleteRecord(obj);
  }
  
  
  public int removeById(String id)
  {
    UcsimDic objd = UcsimDic.getInstance(idCon);
    Ucsim obj = objd.findById(id);
    if (obj != null)
      return deleteRecord(obj);
    else
      return 0;
  }
  
  
  public int deleteRecord(int id)
  {
	int res = 0;
	
	try
	{
		List params = new ArrayList();
		params.add(id);
		DB db = DB.getInstance(idCon);
		res = db.executeSQL(queryDeleteRecord, params);
	}
	catch (Exception e)
	{
		throw new SignalException(e);
	}
	
    return res;
  }
  
  
  public int deleteRecordLogically(Ucsim obj)
  {
    int idInt = obj.getIdInt();
    return deleteRecordLogically(idInt);
  }
  
  
  public int deleteRecordLogically(int id)
  {
		int res = 0;
		
		try
		{
			List params = new ArrayList();
			params.add(id);
		DB db = DB.getInstance(idCon);
			res = db.executeSQL(queryDeleteRecordLogically, params);
		}
		catch (Exception e)
		{
			throw new SignalException(e);
		}
		
	    return res;
  }
  
  
  public int save(Ucsim dto)
  {
    int res;
    if (dto.getIdInt() == 0)
	     res = createRecord(dto);
    else
      res = updateRecord(dto);
    return res;
  }
  
  
  public int saveAndFlush(Ucsim dto)
  {
    return save(dto);
  }
  
    public boolean existsId(int id) {
        int cont = 0;
        try {
            DB db = DB.getInstance(idCon);
            String pquery = "SELECT COUNT(id_ucsim) FROM ucsim WHERE id_ucsim = ?";
            PreparedStatement st = db.prepareStatement(pquery);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            rs.next();
            cont = rs.getInt(1);
        }
        catch(Exception ex) {
            throw new SignalException("Error en existsId");
        }
        return cont > 0;
    }
  public int count(){
  	DB db = DB.getInstance(idCon);
  	int res = db.getCount(queryFindAll);
  	return res;
  }
  public int countByFilter(QueryFilter qf){
  	DB db = DB.getInstance(idCon);
  	String query = DB.buildFilteredQuery(queryFindAll, qf);
  	int res = db.getCount(query);
		return res;
  }
  
  public Collection findAll(){
	return findAll(Ucsim.class);
  }
  
  public Collection findAll(Class dtoClass){
	return findByFilter(dtoClass, null);
  }
  
  public Collection findByFilter(QueryFilter qf)
  {
		return findByFilter(Ucsim.class, qf);
  }
  
  public Collection findByFilter(Class dtoClass, QueryFilter qf)
  {
	Collection vObj = null;
	 
	DB db = DB.getInstance(idCon);
	String queryf = "";
	if (qf == null){
		queryf = queryFindAll;
	}else{
		queryf = DB.buildFilteredAndOrderedQuery(queryFindAll, qf);
	}
	vObj = db.getEntities(dtoClass, queryf);
	  
	return vObj;
  }
  
  public Ucsim findById(int id){
    return findById(new Integer(id));
  }
  
  
  public Ucsim findById(Object id)
  {
	 Ucsim obj = null;
	 
	   try
	   {
		DB db = DB.getInstance(idCon);
		PreparedStatement pstmt = db.prepareStatement(queryFindById);
		pstmt.setObject(1, id);
		ResultSet rs = pstmt.executeQuery();
		rs.next();
		obj = (Ucsim) db.getDTO(Ucsim.class, rs);
		rs.close();
		pstmt.close();
	 }
	 catch (Exception e)
	 {
		throw new SignalException(e);
	 }
	  
	 return obj;
  }
  
  
  public Ucsim findById(Object id, Class[] classes, String discrim, String[] discrims)
  {
	 Ucsim obj = null;
	 
	   try
	   {
		DB db = DB.getInstance(idCon);
		PreparedStatement pstmt = db.prepareStatement(queryFindById);
		pstmt.setObject(1, id);
		ResultSet rs = pstmt.executeQuery();
		rs.next();
		obj = (Ucsim) db.getDTO(classes, discrim, discrims, rs);
		rs.close();
		pstmt.close();
	 }
	 catch (Exception e)
	 {
		throw new SignalException(e);
	 }
	  
	 return obj;
  }
  
 public void reseqUcsims(int step){
		DB db = DB.getInstance(idCon);
 	try {
 		Statement stmt = db.createUpdatableStatement();
 		String query = "SELECT ORD FROM (" + queryFindAll + ") T ORDER BY ORD";
 		ResultSet rs = stmt.executeQuery(query);
 		int i = 0;
 		while (rs.next()){
				rs.updateInt("ord", i);
				rs.updateRow();
				i += step;
 		}
 		rs.close();
 		stmt.close();
 	} catch (SQLException e) {
 		throw new SignalException(e);
 	}
 }

  public void closeConnection()
  {
	   DB.close(idCon);
  }

  public static String getTableName()
  {
    return "ucsim";
  }

  public static String getFieldId()
  {
    return "id_ucsim";
  }

}

