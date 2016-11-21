package com.jumppi.frwk.sql;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

import com.jumppi.frwk.json.JSON;
import com.jumppi.frwk.log.Log;
import com.jumppi.frwk.util.SignalException;
import com.jumppi.frwk.util.Util;

/**
 * http://www.ibm.com/developerworks/java/library/j-threads3.html (IBM ThreadLocal)
 * http://www.theserverside.com/news/1365244/Why-Prepared-Statements-are-important-and-how-to-use-them-properly (PreparedStatements)
 * http://josearrarte.com/blog/tag/aop (Spring)
 */
public class DB {

    protected static Hashtable<String, DBDescriptor> gDbMap = null;
    protected static ThreadLocal<HashMap<String, Connection>> tConMap;
    protected static boolean monitorDB = false;
    protected String idCon = "";
    protected static IDBConfig dbConfig = null;
    protected static String dbUcsimName = "";
	protected static String dbUcsimUserame = "";
    protected static String dbUcsimPassword = "";
    
    static {
        tConMap = new ThreadLocal<HashMap<String, Connection>>();
    }

    public static String getDbUcsimName() {
		return dbUcsimName;
	}

	public static void setDbUcsimName(String dbUcsimName) {
		DB.dbUcsimName = dbUcsimName;
	}

	public static String getDbUcsimUserame() {
		return dbUcsimUserame;
	}
	
	public static void setDbUcsimUserame(String dbUcsimUserame) {
		DB.dbUcsimUserame = dbUcsimUserame;
	}

	public static String getDbUcsimPassword() {
		return dbUcsimPassword;
	}

	public static void setDbUcsimPassword(String dbUcsimPassword) {
		DB.dbUcsimPassword = dbUcsimPassword;
	}
    
    public static void setDBConfig(IDBConfig config) {
    	dbConfig = config;
    }
    
    protected DB(String idCon) {
        // Protected para evitar new DB() externos
        this.idCon = idCon;
    }

    public static void addDescriptor(String idCon, DBDescriptor dbd) {
    	if (gDbMap == null) {
            gDbMap = new Hashtable<String, DBDescriptor>();
    	}        
    	gDbMap.put(idCon, dbd);
    }

    
    public static DB getInstance(DBDescriptor dbd) {
    	addDescriptor("_defaultCon", dbd);
    	return getInstance("_defaultCon");
    }
    
    
    public static DB getInstance(String idCon) {
    	return getInstance(idCon, dbConfig);
    }
    
    
    public static DB getInstance(String idCon, IDBConfig dbConfig) {
    	
    	if (gDbMap == null) {
            gDbMap = new Hashtable<String, DBDescriptor>();
            if (dbConfig != null) {
                dbConfig.setup(gDbMap, DB.getDbUcsimName(), DB.getDbUcsimUserame(), DB.getDbUcsimPassword());
            }
    	}        
    	
        DBDescriptor dbd = gDbMap.get(idCon);
        if (dbd == null) {
            throw new SignalException(99, "idCon " + idCon + " not known");
        }

        DB db = null;
        db = new DB(idCon);

        if (tConMap.get() == null) {
            tConMap.set(new HashMap<String, Connection>());
        }

        Connection con = null;
        if (tConMap.get().get(idCon) == null) {
            con = getNewConnection(dbd.username, dbd.password, dbd.jdbcDriver, dbd.buildUrl());
            tConMap.get().put(idCon, con);
        }
        return db;
    }
    

    /*protected static HashMap<String, Connection> getTConMap() {
     if(tConMap.get() == null) {
     	tConMap.set(new HashMap<String, Connection>());
     }
     return tConMap.get();
     }*/
    public Connection getConnection() {
        Connection con = tConMap.get().get(idCon);

        if (monitorDB) {
            //Hashtable<String, DbDescriptor> gDbMap = tBdMap.get();
            String user = gDbMap.get(idCon).username;
            String pass = gDbMap.get(idCon).password;
            String driv = gDbMap.get(idCon).jdbcDriver;
            String urldb = gDbMap.get(idCon).buildUrl();

            Thread t = Thread.currentThread();
            Log.debug("#USE  (DB): t.hash = " + t.hashCode()
                    + ", con.hash = " + con.hashCode() + " " + con.toString()
                    + "user = |" + user + "|, pass = |******|, driver = |" + driv + "|, url = |" + urldb + "|");
        }

        return con;
    }

    public void set(PreparedStatement pstmt, int pos, String val) {
        try {
            pstmt.setString(pos, val);
        } catch (SQLException e) {
            throw new SignalException(e);
        }
    }

    public static Connection getNewConnection(String username, String password,
            String driver, String url) {
        Connection con = null;
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, username, password);

            if (monitorDB) {
                String user = username;
                String pass = password;
                String driv = driver;
                String urldb = url;

                Thread t = Thread.currentThread();
                Log.debug("#OPEN: t.hash = " + t.hashCode()
                        + ", con.hash = " + con.hashCode() + " "
                        + con.toString() + " " + Util.getTimestamp()
                        + "user = |" + user + "|, pass = |******|"
                        + ", driver = |" + driv + "|, url = |" + urldb + "|");

            }
        } catch (Exception e) {
            throw new SignalException(
                    "Ooops en DB.getNewConnection(). driver = |" + driver
                    + "|, url = |" + url + "| username = |******|, pass = |******|", e);
        }
        return con;
    }

    public static void closeAll() {
        HashMap<String, Connection> tConHash = tConMap.get();
        if (tConHash != null) {
            Set<String> keys = tConHash.keySet();
            Set<String> aux = new HashSet<String>();
            for(String s: keys) {
                aux.add(s);
            }
            for (String key : aux) {
                try {
                    close(key);
                } catch (Exception e) {
//                    e.printStackTrace();
        			Log.error(e.getMessage(), e);
                }
            }
            //tConMap.remove();
        }
    }

    public static void close(String idCon) {
        Thread t = Thread.currentThread();        
        DB db = DB.getInstance(idCon);
        if (db != null) {
            Connection con = db.getConnection();

            if (con != null) {
                if (monitorDB) {
                    //Hashtable<String, DbDescriptor> gDbMap = tBdMap.get();
                    String user = gDbMap.get(idCon).username;
                    String pass = gDbMap.get(idCon).password;
                    String driv = gDbMap.get(idCon).jdbcDriver;
                    String urldb = gDbMap.get(idCon).buildUrl();

                    Log.debug("#CLOSE(BD): t.hash = " + t.hashCode()
                            + ", con.hash = " + con.hashCode() + " "
                            + con.toString() + "user = |" + user
                            + "|, pass = |******|, driver = |" + driv
                            + "|, url = |" + urldb + "|");
                }
                try {
                    con.close();
                } catch (Exception ex) {
                }
            }
            db.tConMap.get().remove(idCon);
        }

    }

    public void close(PreparedStatement pstmt) {
        try {
            pstmt.close();
        } catch (SQLException e) {
            throw new SignalException(e);
        }
    }

    public void beginTrans() {
        try {
            Connection con = getConnection();
            con.setAutoCommit(false);
        } catch (Exception e) {
            throw new SignalException("Error in DB.beginTrans()", e);
        }
    }

    public void commitTrans() {
        try {
            Connection con = getConnection();
            con.commit();
            con.setAutoCommit(true);

        } catch (Exception e) {
            throw new SignalException("Error in DB.commitTrans()", e);
        }
    }

    public void rollbackTrans() {
        try {
            Connection con = getConnection();
            con.rollback();
        } catch (Exception e) {
            throw new SignalException("Error in DB.rollbackTrans()", e);
        }
    }

    public Statement createStatement() throws SQLException {
        Connection con = getConnection();
        Statement stmt = con.createStatement();
        return stmt;
    }

    public Statement createUpdatableStatement() throws SQLException {
        Connection con = getConnection();
        Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        return stmt;
    }

    public PreparedStatement prepareStatement(String pquery) {
        Connection con = getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(pquery);
        } catch (Exception e) {
            throw new SignalException(e);
        }
        return pstmt;
    }

    public PreparedStatement prepareStatement(String pquery,
            int autoGeneratedKeys) {
        Connection con = getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(pquery, autoGeneratedKeys);
        } catch (Exception e) {
            throw new SignalException(e);
        }
        return pstmt;
    }

    public CallableStatement prepareCall(String nameStored) {
        Connection con = getConnection();
        CallableStatement cstmt = null;
        try {
            cstmt = con.prepareCall(nameStored);
        } catch (Exception e) {
            throw new SignalException(e);
        }
        return cstmt;
    }

    public int executeSQL(String query) {
        int res = 0;
        Connection con = getConnection();
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(query);
        } catch (Exception e) {
            throw new SignalException(e + " |" + query + "|", e);
        } finally {
            try {
                stmt.close();
            } catch (Exception ex) {
            }
        }
        return res;
    }

    public int executeSQL(String pquery, List params) {
        int res = 0;
        PreparedStatement pstmt = null;
        try {
            pstmt = prepareStatement(pquery);
            int np = params.size();
            Object param = null;
            for (int k = 0; k < np; k++) {
            	param = params.get(k);
                pstmt.setObject(k + 1, param);
            }
            res = pstmt.executeUpdate();
        } catch (Exception e) {
            throw new SignalException(e + " |" + pquery + "|", e);
        } finally {
            try {
                pstmt.close();
            } catch (Exception ex) {
            }
        }
        return res;
    }

    public int executeInsertSQL(String query) {
        int res = 0;
        Connection con = getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
            rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                res = rs.getInt(1);
            }

        } catch (Exception ex) {
//            ex.printStackTrace();
			Log.error(ex.getMessage(), ex);
            throw new SignalException(ex);
        } finally {
            try {
                stmt.close();
            } catch (Exception ex) {
            }
        }
        return res;
    }

    public int executeInsertSQL(String pquery, List params) {
        int res = 0;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = prepareStatement(pquery,
                    PreparedStatement.RETURN_GENERATED_KEYS);
            int np = params.size();
            Object param = null;
            for (int k = 0; k < np; k++) {
            	param = params.get(k);
                pstmt.setObject(k + 1, param);
            }

            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();

            if (rs.next()) {
                res = rs.getInt(1);
            }

        } catch (Exception ex) {
//            ex.printStackTrace();
			Log.error(ex.getMessage(), ex);
            throw new SignalException(ex);
        } finally {
            try {
                pstmt.close();
            } catch (Exception ex) {
            }
        }
        return res;
    }

    public int getCount(String query) {
        int count = 0;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            query = "SELECT COUNT(*) FROM (" + tailOrderBy(query) + ") T";
            stmt = createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            count = rs.getInt(1);
        } catch (Exception e) {
            throw new SignalException("Error: " + e.getMessage() + " |"
                    + query + "|", e);
        } finally {
            try {
                rs.close();
            } catch (Exception ex) {
            }
            try {
                stmt.close();
            } catch (Exception ex) {
            }
        }
        return count;
    }

    public int getCount(String pquery, List params) {
        int count = 0;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String pqueryCount = "";
        try {
            pqueryCount = "SELECT COUNT(*) FROM (" + tailOrderBy(pquery)
                    + ") T";
            pstmt = prepareStatement(pqueryCount);
            int np = params.size();
            for (int k = 0; k < np; k++) {
                pstmt.setObject(k + 1, params.get(k));
            }
            rs = pstmt.executeQuery();
            rs.next();
            count = rs.getInt(1);
        } catch (Exception e) {
            throw new SignalException("Error: " + e.getMessage() + " |"
                    + pqueryCount + "|", e);
        } finally {
            try {
                rs.close();
            } catch (Exception ex) {
            }
            try {
                pstmt.close();
            } catch (Exception ex) {
            }
        }
        return count;
    }

    public int getCountNoAlias(String pquery, List params) {
        int count = 0;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String pqueryCount = "";
        try {
            pqueryCount = "SELECT COUNT(*) FROM " + tailOrderBy(pquery);
            pstmt = prepareStatement(pqueryCount);
            int np = params.size();
            for (int k = 0; k < np; k++) {
                pstmt.setObject(k + 1, params.get(k));
            }
            if (monitorDB) {
            	Log.debug(Util.nvl(pquery) + " " + dumpParams(params)
                        + " [DB.getCount()]");
            }
            rs = pstmt.executeQuery();
            rs.next();
            count = rs.getInt(1);
        } catch (Exception e) {
            throw new SignalException("Error: " + e.getMessage() + " |"
                    + pqueryCount + "|", e);
        } finally {
            try {
                rs.close();
            } catch (Exception ex) {
            }
            try {
                pstmt.close();
            } catch (Exception ex) {
            }
        }
        return count;
    }

    public int getFieldValueInt(String query) {
        String stRes = getFieldValue(query);
        return Util.parseInt(stRes);
    }

    public int getFieldValueInt(String query, List params) {
        String stRes = getFieldValue(query, params);
        return Util.parseInt(stRes);
    }

    public String getFieldValue(String query) {
        String res = "";
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = createStatement();
            rs = stmt.executeQuery(query);

            if (rs.next()) {
                res = rs.getString(1);
            }
        } catch (Exception e) {
//            e.printStackTrace();
			Log.error(e.getMessage(), e);
            throw new SignalException(e);
        } finally {
            try {
                rs.close();
            } catch (Exception ex) {
            }
            try {
                stmt.close();
            } catch (Exception ex) {
            }
        }
        return res;
    }

    public String getFieldValue(String pquery, List params) {
        String res = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = prepareStatement(pquery);
            int np = params.size();
            for (int k = 0; k < np; k++) {
                pstmt.setObject(k + 1, params.get(k));
            }
            rs = pstmt.executeQuery();

            if (rs.next()) {
                res = rs.getString(1);
            }
        } catch (Exception e) {
//            e.printStackTrace();
			Log.error(e.getMessage(), e);
            throw new SignalException(e);
        } finally {
            try {
                rs.close();
            } catch (Exception ex) {
            }
            try {
                pstmt.close();
            } catch (Exception ex) {
            }
        }
        return res;
    }

    public Object getValue(String columnName, String query) {
        Object result = null;
        Statement stmt = null;
        ResultSet rs = null;

        // String query = DB.noDel(query0);
        try {
            stmt = createStatement();
            rs = stmt.executeQuery(query);
            result = getValue(columnName, rs);
            rs.close();
            stmt.close();
        } catch (SQLException e1) {
            throw new SignalException(e1);
        }

        return result;
    }

    public Object getValue(String columnName, String pquery, List params) {
        Object result = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        // String query = DB.noDel(query0);
        try {
            pstmt = prepareStatement(pquery);
            rs = pstmt.executeQuery();
            result = getValue(columnName, rs);
            rs.close();
            pstmt.close();
        } catch (SQLException e1) {
            throw new SignalException(e1);
        }

        return result;
    }

    public Object getValue(int numCol, String query) {
        Object result = null;
        Statement stmt = null;
        ResultSet rs = null;

        // String query = DB.noDel(query0);
        try {
            stmt = createStatement();
            rs = stmt.executeQuery(query);
            result = getValue(numCol, rs);
            rs.close();
            stmt.close();
        } catch (SQLException e1) {
            throw new SignalException(e1);
        }

        return result;
    }

    public Object getValue(int numCol, String pquery, List params) {
        Object result = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        // String query = DB.noDel(query0);
        try {
            pstmt = prepareStatement(pquery);
            rs = pstmt.executeQuery();
            result = getValue(numCol, rs);
            rs.close();
            pstmt.close();
        } catch (SQLException e1) {
            throw new SignalException(e1);
        }

        return result;
    }

    public Object getValue(String columnName, ResultSet rs) {
        Object result = null;
        try {
            Object valor = null;

            if (rs.next()) {
                try {
                    valor = rs.getObject(columnName);
                } catch (Exception e) {
                    e.getMessage();
                }
            }

            result = valor;
        } catch (Exception e2) {
            throw new SignalException(e2);
        }

        return result;
    }

    public Object getValue(int columnNum, ResultSet rs) {
        Object result = null;
        try {
            Object valor = null;

            if (rs.next()) {
                try {
                    valor = rs.getObject(columnNum);
                } catch (Exception e) {
                    e.getMessage();
                }
            }

            result = valor;
        } catch (Exception e2) {
            throw new SignalException(e2);
        }

        return result;
    }

    // ////////////////////////////////////////////////////////////////////////////
    // INICI getDTOs i getDTO (i addicionalment getEntities)
    // ////////////////////////////////////////////////////////////////////////////
    public List getDTOs(Class dtoClass, String query) {
        return getDTOs(dtoClass, query, 0, 9999);
    }

    public List getDTOs(Class dtoClass, String query, int from, int offset) {
        List vDtos = new ArrayList();

        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = createStatement();
            rs = stmt.executeQuery(query);

            if (from <= 0) {
                from = 0;
            }
            if (offset <= 0) {
                offset = 9999;
            }

            int i = 0;
            while (i < from && rs.next()) { // rs.absolute(from);
                i++;
            }

            int j = 0;
            while (i < (from + offset) && rs.next()) {
                i++;
                Object dto = getDTO(dtoClass, rs);
                vDtos.add(dto);
                j++;
            }
        } catch (Exception e) {
            throw new SignalException(e + " |" + query + "|", e);
        } finally {
            try {
                rs.close();
            } catch (Exception ex) {
            }
            try {
                stmt.close();
            } catch (Exception ex) {
            }
        }
        return vDtos;
    }

    public List getDTOsNoLimit(Class dtoClass, String pquery) {
        return getDTOs(dtoClass, pquery, 0, Integer.MAX_VALUE);
    }
    
    public List getDTOsNoLimit(Class dtoClass, String pquery, List params) {
        return getDTOs(dtoClass, pquery, params, 0, Integer.MAX_VALUE);
    }
    
    public List getDTOs(Class dtoClass, String pquery, List params) {
        return getDTOs(dtoClass, pquery, params, 0, 9999);
    }

    public List getDTOs(Class dtoClass, String pquery, List params, int from,
            int offset) {
        List vRes = null;
        Class[] vDtoClass = new Class[1];
        vDtoClass[0] = dtoClass;
        vRes = getDTOs(vDtoClass, "", null, pquery, params, from, offset);
        return vRes;
    }

    public List getDTOs(Class[] vDtoClass, String discrim, String[] vDiscrims,
            String pquery, List params) {
        List vRes = null;
        vRes = getDTOs(vDtoClass, discrim, vDiscrims, pquery, params, 0, 9999);
        return vRes;
    }

    public List getDTOs(Class[] vDtoClass, String discrim, String[] vDiscrims,
            String pquery, List params, int from, int offset) {
        List vDtos = new ArrayList();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int np;
        try {
            pstmt = prepareStatement(pquery);

            if (params != null) {
                np = params.size();
                for (int k = 0; k < np; k++) {
                    pstmt.setObject(k + 1, params.get(k));
                }
            }
            if (monitorDB) {
            	Log.debug(Util.nvl(pquery) + " " + dumpParams(params)
                        + " [DB.getDTOs()]");
            }
            rs = pstmt.executeQuery();
            int i = 0;
            while (i < from && rs.next()) // rs.absolute(from);
            {
                i++;
            }

            int j = 0;
            while (i < (from + offset) && rs.next()) {
                i++;
                Object dto = getDTO(vDtoClass, discrim, vDiscrims, rs);                
                vDtos.add(dto);
                j++;
            }
        } catch (Exception e) {
            throw new SignalException(e + " |" + pquery + "|", e);
        } finally {
            try {
                rs.close();
            } catch (Exception ex) {
            }
            try {
                pstmt.close();
            } catch (Exception ex) {
            }
        }
        return vDtos;
    }
    
    public <T> T getDTO(Class<T> dtoClass, String query) {
        T result = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = createStatement();
            if (monitorDB) {
            	Log.debug(Util.nvl(query) + " [DB.getDTO()]");
            }
            rs = stmt.executeQuery(query);
            rs.next();
            result = getDTO(dtoClass, rs);
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new SignalException(e + " |" + query + "|", e);
        } finally {
            try {
                rs.close();
            } catch (Exception ex) {
            }
            try {
                stmt.close();
            } catch (Exception ex) {
            }
        }
        return result;
    }

    public Object getDTO(Class[] vDtoClass, String discrim, String[] vDiscrims,
            String pquery) {
        return getDTO(vDtoClass, discrim, vDiscrims, pquery, null);
    }

    public <T> T getDTO(Class<T>[] vDtoClass, String discrim,
            String[] vDiscrims, String pquery, List params) {
        T result = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int np;
        try {
            pstmt = prepareStatement(pquery);
            if (params != null) {
                np = params.size();
                for (int k = 0; k < np; k++) {
                    pstmt.setObject(k + 1, params.get(k));
                }
            }
            if (monitorDB) {
            	Log.debug(Util.nvl(pquery) + " " + dumpParams(params)
                        + " [DB.getDTO()]");
            }
            rs = pstmt.executeQuery();
            if (rs.next()) {
                result = getDTO(vDtoClass, discrim, vDiscrims, rs);
            } else {
                result = (T) vDtoClass[0].newInstance();
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            throw new SignalException(e + " |" + pquery + "|", e);
        } finally {
            try {
                rs.close();
            } catch (Exception ex) {
            }
            try {
                pstmt.close();
            } catch (Exception ex) {
            }
        }
        return result;
    }

    public <T> T getDTO(Class<T> dtoClass, String pquery, List params) {
        T result = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int np;
        try {
            pstmt = prepareStatement(pquery);
            if (params != null) {
                np = params.size();
                for (int k = 0; k < np; k++) {
                    pstmt.setObject(k + 1, params.get(k));
                }
            }
            if (monitorDB) {
            	Log.debug(Util.nvl(pquery) + " " + dumpParams(params)
                        + " [DB.getDTO()]");
            }
            rs = pstmt.executeQuery();
            rs.next();
            result = getDTO(dtoClass, rs);
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            throw new SignalException(e + " |" + pquery + "|", e);
        } finally {
            try {
                rs.close();
            } catch (Exception ex) {
            }
            try {
                pstmt.close();
            } catch (Exception ex) {
            }
        }
        return result;
    }

    public static <T> T getDTO(Class<T> dtoClass, ResultSet rs) {
        T res = null;
        Class<T>[] vDtoClass = new Class[1];

        vDtoClass[0] = dtoClass;
        res = getDTO(vDtoClass, "", null, rs);
        return res;
    }

    public static <T> T getDTO(Class<T>[] dtoClass, String discrim,
            String[] valsDiscrim, ResultSet rs) {
        T result = null;
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int numCols = rsmd.getColumnCount();
            T dto = null;
            if (valsDiscrim == null) {
                dto = (T) dtoClass[0].newInstance();
            } else {
                String valDiscrim = rs.getString(discrim);
                int pd = Util.getPosInStringArray(valsDiscrim, valDiscrim);
                if (pd < 0) {
                    throw new SignalException("Discriminator " + valDiscrim
                            + " unknown");
                }
                if (dtoClass.length < pd + 1) {
                    throw new SignalException(
                            "Check classes array size (1st param for getDTOx).");
                }
                dto = (T) dtoClass[pd].newInstance();
            }

            for (int k = 1; k <= numCols; k++) {
                try {
                    // String attr = rsmd.getColumnLabel(k).toLowerCase();
                    String attr = rsmd.getColumnLabel(k);
                    String colType = rsmd.getColumnTypeName(k);
                    Object valor = null;
                    if (DB.getDriverName(rs).startsWith("Oracle")
                            && colType.equals("DATE")) {
                        java.sql.Timestamp ts = rs.getTimestamp(k);
                        valor = new java.util.Date(ts.getTime());
                    } else {
                        valor = rs.getObject(k);
                    }
                    String attrHung = Util.firstLowercaseHungarian(attr);
                    if (dto != null) {
                    	Util.setValueOfAttribute(dto, attrHung, valor);
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            }
            result = dto;
        } catch (Exception e) {
            throw new SignalException(e);
        }
        return result;
    }
    
    public JSON getJSON(String query) {
        JSON result = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = createStatement();
            if (monitorDB) {
            	Log.debug(Util.nvl(query) + " [DB.getJSON()]");
            }
            rs = stmt.executeQuery(query);
            rs.next();
            result = getJSON(rs);
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new SignalException(e + " |" + query + "|", e);
        } finally {
            try {
                rs.close();
            } catch (Exception ex) {
            }
            try {
                stmt.close();
            } catch (Exception ex) {
            }
        }
        return result;
    }
    
    public JSON getJSON(String pquery, List params) {
        JSON result = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int np;
        try {
            pstmt = prepareStatement(pquery);
            if (params != null) {
                np = params.size();
                for (int k = 0; k < np; k++) {
                    pstmt.setObject(k + 1, params.get(k));
                }
            }
            if (monitorDB) {
            	Log.debug(Util.nvl(pquery) + " " + dumpParams(params)
                        + " [DB.getJSON()]");
            }
            rs = pstmt.executeQuery();
            rs.next();
            result = getJSON(rs);
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            throw new SignalException(e + " |" + pquery + "|", e);
        } finally {
            try {
                rs.close();
            } catch (Exception ex) {
            }
            try {
                pstmt.close();
            } catch (Exception ex) {
            }
        }
        return result;
    }
    
    public static JSON getJSON(ResultSet rs) {
        JSON result = null;
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int numCols = rsmd.getColumnCount();
            JSON dto = JSON.getInstanceObject();
            for (int k = 1; k <= numCols; k++) {
                try {
                    // String attr = rsmd.getColumnLabel(k).toLowerCase();
                    String attr = rsmd.getColumnLabel(k);
                    String colType = rsmd.getColumnTypeName(k);
                    Object valor = null;
                    if (DB.getDriverName(rs).startsWith("Oracle")
                            && colType.equals("DATE")) {
                        java.sql.Timestamp ts = rs.getTimestamp(k);
                        valor = new java.util.Date(ts.getTime());
                    } else {
                        valor = rs.getObject(k);
                    }
                    if(valor instanceof java.util.Date) {
                        valor = Util.formatDateTime2ANSI((java.util.Date) valor);
                    }
                    String attrHung = Util.firstLowercaseHungarian(attr);
                    dto.add(attrHung, valor);
                } catch (Exception e) {
                    e.getMessage();
                }
            }
            result = dto;
        } catch (Exception e) {
            throw new SignalException(e);
        }
        return result;
    }
    
    public JSON getJSONs(String query) {
        JSON vDtos = JSON.getInstanceArray();

        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                JSON dto = getJSON(rs);
                vDtos.add(dto);
            }
        } catch (Exception e) {
            throw new SignalException(e + " |" + query + "|", e);
        } finally {
            try {
                rs.close();
            } catch (Exception ex) {
            }
            try {
                stmt.close();
            } catch (Exception ex) {
            }
        }
        return vDtos;
    }
    
    public JSON getJSONs(String pquery, List params) {
        JSON vDtos = JSON.getInstanceArray();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int np;
        try {
            pstmt = prepareStatement(pquery);

            if (params != null) {
                np = params.size();
                for (int k = 0; k < np; k++) {
                    pstmt.setObject(k + 1, params.get(k));
                }
            }
            if (monitorDB) {
            	Log.debug(Util.nvl(pquery) + " " + dumpParams(params)
                        + " [DB.getDTOs()]");
            }
            rs = pstmt.executeQuery();

            while (rs.next()) {
                JSON dto = getJSON(rs);                
                vDtos.add(dto);
            }
        } catch (Exception e) {
            throw new SignalException(e + " |" + pquery + "|", e);
        } finally {
            try {
                rs.close();
            } catch (Exception ex) {
            }
            try {
                pstmt.close();
            } catch (Exception ex) {
            }
        }
        return vDtos;
    }

    // ////////////////////////////////////////////////////////////////////////////
    // FI getDTO - Inici getEntities
    // ////////////////////////////////////////////////////////////////////////////
    public Collection getEntities(Class entityClass, String query) {
        Collection res = null;
        Statement stmt = null;
        ResultSet rs = null;
        Bag vObj = null;
        Class[] entityClasses = new Class[1];
        entityClasses[0] = entityClass;
        try {
            stmt = createStatement();
            if (monitorDB) {
            	Log.debug(Util.nvl(query) + " [DB.getEntities()]");
            }
            rs = stmt.executeQuery(query);
            vObj = new Bag(this.getClass());
            vObj.setEntityClasses(entityClasses);
            vObj.setRs(rs);
            res = vObj;
        } catch (Exception e) {
            throw new SignalException(e + " |" + query + "|", e);
        }
        return res;
    }

    public Collection getEntities(Class entityClass, String pquery, List params) {
        Class[] vEntityClasses = new Class[1];
        vEntityClasses[0] = entityClass;
        return getEntities(vEntityClasses, null, null, pquery, params);
    }

    public Collection getEntities(Class[] vEntityClasses, String discrim,
            String[] vDiscrims, String pquery, List params) {
        Collection res = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Bag vObj = null;
        int numParam;
        Object valParam;
        try {
            pstmt = prepareStatement(pquery);
            int np = params.size();
            for (int k = 0; k < np; k++) {
                numParam = k + 1;
                valParam = params.get(k);
                pstmt.setObject(numParam, valParam);
            }
            if (monitorDB) {
            	Log.debug(Util.nvl(pquery) + " " + dumpParams(params)
                        + " [DB.getEntities()]");
            }
            rs = pstmt.executeQuery();
            vObj = new Bag(this.getClass());
            vObj.setEntityClasses(vEntityClasses);
            vObj.setDiscrim(discrim);
            vObj.setValuesDiscriminator(vDiscrims);
            vObj.setRs(rs);
            res = vObj;
        } catch (Exception e) {
            throw new SignalException(e + " |" + pquery + "|", e);
        }
        return res;
    }

    // ////////////////////////////////////////////////////////////////////////////
    // FI getDTO
    // ////////////////////////////////////////////////////////////////////////////
    public static String tailOrderBy(String query) {
        int pos = query.toUpperCase().indexOf(" ORDER BY ");
        int posParentesi = query.indexOf(")", pos + 1);
        String result = query;
        if (pos > 0) {
            if (posParentesi > 0) {
                result = query.substring(0, pos) + " "
                        + query.substring(posParentesi);
            } else {
                result = query.substring(0, pos);
            }
        }
        return result;
    }

    public static String nvl(Object s) {
        return nvl(s, "");
    }

    public static String nvl(Object s, String defaultValue) {
        return s == null ? defaultValue : s.toString().trim();
    }

    public static String getDriverName(ResultSet rs) {
        try {
            return rs.getStatement().getConnection().getMetaData()
                    .getDriverName();
        } catch (SQLException e) {
            throw new SignalException(e);
        }
    }

    public String getDriverName() {
        try {
            return getConnection().getMetaData().getDriverName();
        } catch (SQLException e) {
            throw new SignalException(e);
        }
    }

    public String getSubProtocol() {
        String res = "";
        try {
            String jdbcUrl = getConnection().getMetaData().getURL();
            int pos1 = jdbcUrl.indexOf(':');
            int pos2 = jdbcUrl.indexOf(':', pos1 + 1);
            String subProt = jdbcUrl.substring(pos1 + 1, pos2);
            res = subProt;
        } catch (SQLException e) {
            throw new SignalException(e);
        }
        return res;
    }

    public static String buildFilteredAndOrderedQuery(String query,
            QueryFilter qf) {

        if (qf == null) {
            return query;
        }

        String res = "";
        String queryBaseNvl = Util.nvl(query);
        String searchFieldNvl = Util.firstUppercaseUnderscore(qf
                .getSearchField());
        String searchStringNvl = Util.nvl(qf.getSearchString());
        String searchOperNvl = Util.nvl(qf.getSearchOper());
        String ordFieldNvl = Util.firstUppercaseUnderscore(qf.getOrd());
        String ascDescNvl = Util.nvl(qf.getAscDesc());
        String joker = Util.nvl(qf.getJoker());
        String quote = Util.nvl(qf.getQuote());
        String where = "";

        if (!searchFieldNvl.equals("")) {
            if (searchOperNvl.equals("eq")) {
                where += " where " + searchFieldNvl + " = " + quote
                        + searchStringNvl + quote;
            } else if (searchOperNvl.equals("cn")) {
                where += " where " + searchFieldNvl + " like " + quote + joker
                        + searchStringNvl + joker + quote;
            }
        }

        if (!ordFieldNvl.equals("")) {

            if (queryBaseNvl.trim().toLowerCase().startsWith("select")) {
                res = "select * from (" + queryBaseNvl + ") T " + where
                        + " order by " + ordFieldNvl;
            } else {
                res = "select * from " + queryBaseNvl + " " + where
                        + " order by " + ordFieldNvl;
            }

            if (ascDescNvl.toLowerCase().equals("desc")) {
                res += " desc";
            }
        } else {

            if (queryBaseNvl.trim().toLowerCase().startsWith("select")) {
                res = "select * from (" + queryBaseNvl + ") T " + where;
            } else {
                res = "select * from " + queryBaseNvl + " " + where;
            }
        }
        return res;
    }

    public static String buildFilteredQuery(String query, QueryFilter qf) {

        if (qf == null) {
            return query;
        }

        String res = "";
        String queryBaseNvl = Util.nvl(query);
        String searchFieldNvl = Util.firstUppercaseUnderscore(qf
                .getSearchField());
        String searchStringNvl = Util.nvl(qf.getSearchString());
        String searchOperNvl = Util.nvl(qf.getSearchOper());
        String joker = Util.nvl(qf.getJoker());
        String quote = Util.nvl(qf.getQuote());
        String where = "";

        if (!searchFieldNvl.equals("")) {
            if (searchOperNvl.equals("eq")) {
                where += " where " + searchFieldNvl + " = " + quote
                        + searchStringNvl + quote;
            } else if (searchOperNvl.equals("cn")) {
                where += " where " + searchFieldNvl + " like " + quote + joker
                        + searchStringNvl + joker + quote;
            }
        }

        if (queryBaseNvl.trim().toLowerCase().startsWith("select")) {
            res = "select * from (" + queryBaseNvl + ") T " + where;
        } else {
            res = "select * from " + queryBaseNvl + " " + where;
        }

        return res;
    }

    public static String buildOrderedQuery(String query, QueryFilter qf) {

        if (qf == null) {
            return query;
        }

        String res = "";
        String queryBaseNvl = Util.nvl(query);
        String ordFieldNvl = Util.firstUppercaseUnderscore(qf.getOrd());
        String ascDescNvl = Util.nvl(qf.getAscDesc());

        if (!ordFieldNvl.equals("")) {

            if (queryBaseNvl.trim().toLowerCase().startsWith("select")) {
                res = "select * from (" + queryBaseNvl + ") T  order by "
                        + ordFieldNvl;
            } else {
                res = "select * from " + queryBaseNvl + " order by "
                        + ordFieldNvl;
            }

            if (ascDescNvl.toLowerCase().equals("desc")) {
                res += " desc";
            }
        } else {

            if (queryBaseNvl.trim().toLowerCase().startsWith("select")) {
                res = "select * from (" + queryBaseNvl + ") T ";
            } else {
                res = "select * from " + queryBaseNvl;
            }
        }
        return res;
    }

    public boolean executeStored(String nameStored) {
        boolean res = false;
        CallableStatement cstmt = null;
        try {
            cstmt = prepareCall(nameStored);
            if (monitorDB) {
            	Log.debug(Util.nvl(nameStored)
                        + " [DB.ejecutarSrored()]");
            }
            res = cstmt.execute();
        } catch (Exception ex) {
//            ex.printStackTrace();
			Log.error(ex.getMessage(), ex);
        } finally {
            try {
                cstmt.close();
            } catch (Exception ex) {
            }
        }
        return res;
    }

    public String dumpParams(List params) {
        StringBuilder sb = new StringBuilder();

        int n = params.size();
        for (int i = 0; i < n; i++) {
            if (i < n - 1) {
                sb.append("|" + params.get(i) + "|, ");
            } else {
                sb.append("|" + params.get(i) + "|");
            }
        }
        return sb.toString();
    }
    
    public DBDescriptor getDbDescriptor() {
        return gDbMap.get(idCon);
    }

    public static DBDescriptor getDbDescriptor(String idCon) {
        return gDbMap.get(idCon);
    }
    
    public void close() {
        DB.close(idCon);
    }
    
    public static String getIdConFrom(String idCon, String dbName) {
        if(!gDbMap.contains(dbName)) {
            DBDescriptor copy = gDbMap.get(idCon);
            copy.dbName = dbName;
            gDbMap.put(dbName, copy);
        }
        return dbName;
    }
    
    public static String getIdConFrom(Object dtoOrEntity) {
    	String res = "";
    	try {
			String fullName = dtoOrEntity.getClass().getName();
			String fullNameDic = fullName + "Dic";
			Class c = Class.forName(fullNameDic);
			Field fld = Util.getField(c, "_idCon");
			String idCon = fld.get(dtoOrEntity).toString();
			res = Util.nvl(idCon);
		} catch (Exception e) {
		}
        return res;
    }

}

