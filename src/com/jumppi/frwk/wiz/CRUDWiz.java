package com.jumppi.frwk.wiz;

import java.io.*;
import java.util.*;
import java.sql.*;
import java.awt.event.*;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/*
 <entity>
 <class>Article</class>
 <classPlural>Articles</classPlural>
 <table>SCHEME.ARTICLES</table>
 <sequencer>SCHEME.SEQ_ARTICLES</sequencer>
  
 <path-out-wiz>src</path-out-wiz>
 <path-out-nowiz>src_nowiz</path-out-nowiz>

 <driver>oracle.jdbc.driver.OracleDriver</driver>
 <url-db>jdbc:oracle:thin:@127.0.0.1:1521:XE</url-db>
 <username>system</username>
 <password>oracle</password>
 <package>appl.model</package>
 <read-only>false</read-only>
 <logic-deletion>false<logic-deletion>
 <ts-crea>17-mar-2015 12:42:53</ts-crea>
 </entity>
 
 */
public class CRUDWiz extends JFrame {
	// VARCHAR(1)=S/N instead of boolean

    protected final String initialOutputPath = "src_wiz";
    protected final String initialOutputPathNoWiz = "src_nowiz";
    protected final String initialColumnId = "0";
    protected final String initialTextAreaValue
            = "<entity>\n"
            + "     <class-name>Ucsim</class-name>\n"
            + "     <class-name-plural>Ucsims</class-name-plural>\n"
            + "     <table-name>ucsim</table-name>\n"
            + //			"     <sequencer>SIHOD.SEQ_ARTICULOS</sequencer>\n" +
            "     \n"
            + "     <path-output-wiz>src_wiz</path-output-wiz>\n"
            + "     <path-output-nowiz>src_nowiz</path-output-nowiz>\n"
            + "     \n"
            + //			"     <driver>oracle.jdbc.driver.OracleDriver</driver>\n" +
            //			"     <url-db>jdbc:oracle:thin:@127.0.0.1:1521:XE</url-db>\n" +
            "     <driver>com.mysql.jdbc.Driver</driver>\n"
            + "     <url-db>jdbc:mysql://127.0.0.1/sk</url-db>\n"
            + "     <username>root</username>\n"
            + "     <password></password>\n"
            + "     <package>appl.model</package>\n"
            + "     <read-only>false</read-only>\n"
            + "     <logic-deletion>false</logic-deletion>\n"
            + "     <db-class-name>DB</db-class-name>\n"
            + "     <col-id>1</col-id>\n"
            + "</entity>"
            + "\n";


    protected String import1 = "";
    protected String import2 = "";
    protected String import3 = "";
    protected String import4 = "";
    protected String import5 = "";

    protected JLabel label1 = new JLabel(); // Títle (CRUDWiz)
    protected JButton button1 = new JButton(); // Ok 
    private JButton btnHelp = null;
    private JScrollPane jScrollPane = null;
    private JTextArea jTextArea = null;

    protected String packageName;
    protected String className;
    protected String classNamePlural;
    protected String sequencer;
    protected String driver;
    protected String dataOrigin;
    protected String username;
    protected String password;
    protected String outputPathClassWiz;
    protected String outputPathClassNoWiz;
    protected String tableName;
    protected String header;
    protected String classUC;
    protected String classCtl;
    protected String db = "?";
    protected String dbClassName;
    protected int colId;
    protected boolean generatedKeys;   //  generatedKeys == !read-only
    protected boolean logicDeletion;

    protected Vector tableFields = new Vector();
    protected Vector tableFieldsTypes = new Vector();

    private DocumentBuilderFactory factory = null;
    private DocumentBuilder builder = null;
    private Document doc = null;

    public String getDbClassName() {
        return dbClassName;
    }

    public void setDbClassName(String dbClassName) {
        this.dbClassName = dbClassName;
    }

    public void setGeneratedKeys(boolean value) {
        generatedKeys = value;
    }

    public boolean isGeneratedKeys() {
        return generatedKeys;
    }

    public void setLogicDeletion(boolean value) {
        logicDeletion = value;
    }

    public boolean isLogicDeletion() {
        return logicDeletion;
    }

    public void setPackageName(String value) {
        packageName = value;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getPackageInitial() {
        String res = "";
        String paq = getPackageName();
        res = paq.substring(0, paq.indexOf("."));
        return res;
    }

    public void setSequencer(String value) {
        sequencer = value;
    }

    public String getSequencer() {
        return sequencer;
    }

    public String getPackageToPath() {
        return packageName.replace('.', '/');
    }

    public void setClassName(String value) {
        className = value;
    }

    public String getClassName() {
        return className;
    }

    public void setClassNamePlural(String value) {
        classNamePlural = value;
    }

    public String getClassNamePlural() {
        return classNamePlural;
    }

    public void setTableName(String value) {
        tableName = value;
    }

    public String getTableName() {
        return tableName;
    }

    public void setImport1(String value) {
        import1 = value;
    }

    public void setImport2(String value) {
        import2 = value;
    }

    public void setImport3(String value) {
        import3 = value;
    }

    public void setImport4(String value) {
        import4 = value;
    }

    public void setImport5(String value) {
        import5 = value;
    }

    public void setDataOrigin(String value) {
        dataOrigin = value;
    }

    public String getDataOrigin() {
        return dataOrigin;
    }

    public void setDriver(String value) {
        driver = value;
    }

    public String getDriver() {
        return driver;
    }

    public void setUsername(String value) {
        username = value;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String value) {
        password = value;
    }

    public String getPassword() {
        return password;
    }

    public void setOutputPathClassWiz(String value) {
    	outputPathClassWiz = value;
    }

    public String getOutputPathClassWiz() {
        return outputPathClassWiz;
    }

    public void setOutputPathClassNoWiz(String value) {
        outputPathClassNoWiz = value;
    }

    public String getOutputPathClassNoWiz() {
        return outputPathClassNoWiz;
    }

    public void setColId(int value) {
        colId = value;
    }

    public int getColId() {
        return colId;
    }

    public String getClassUC() {
        return classUC;
    }

    public String getClassCtl() {
        return classCtl;
    }

    public void setClassUC(String value) {
        this.classUC = value;
    }

    public void setClassCtl(String value) {
        this.classCtl = value;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    protected void loadTableFields() {
        try {
            tableFields.clear();
            tableFieldsTypes.clear();
            Class.forName(driver);
            Connection con = DriverManager.getConnection(dataOrigin, username, password);
            Statement stmt = con.createStatement();
            
            ResultSet rs;
            if (getSubprotocol().equals("mysql")) {
                rs = stmt.executeQuery("SELECT * FROM `" + getTableName() + "` WHERE 1=0");
            } else {
                rs = stmt.executeQuery("SELECT * FROM " + getTableName() + " WHERE 1=0");
            }
            
            ResultSetMetaData rsmd = rs.getMetaData();
            int numCols = rsmd.getColumnCount();
            for (int i = 0; i < numCols; i++) {
                String fieldName = rsmd.getColumnName(i + 1);
                String sqlType = rsmd.getColumnTypeName(i + 1);
                int scale = rsmd.getScale(i + 1);
                String javaType = sqlType2JavaType(sqlType, scale);
                int dispSize = rsmd.getColumnDisplaySize(i + 1);

                tableFields.addElement(fieldName);
                tableFieldsTypes.addElement(javaType);

            }
            rs.close();
            stmt.close();
            con.close();
        } catch (Exception e) {
            printException(e);
        }
    }

    public String sqlType2JavaType(String sqlType, int scale) {
        sqlType = sqlType.toUpperCase();
        String sqlTypeMapped = "";

        if (sqlType.equals("VARCHAR")) {
            sqlTypeMapped = "String";
        } else if (sqlType.equals("VARCHAR2")) {
            sqlTypeMapped = "String";
        } else if (sqlType.equals("NVARCHAR")) {
            sqlTypeMapped = "String";
        } else if (sqlType.equals("CHAR")) {
            sqlTypeMapped = "String";
        } else if (sqlType.equals("NCHAR")) {
            sqlTypeMapped = "String";
        } else if (sqlType.equals("TEXT")) {
            sqlTypeMapped = "String";
        } else if (sqlType.equals("LONGTEXT")) {
            sqlTypeMapped = "String";
        } else if (sqlType.equals("MEDIUMTEXT")) {
            sqlTypeMapped = "String";
        } else if (sqlType.equals("TINYTEXT")) {
            sqlTypeMapped = "String";
        } else if (sqlType.equals("SMALLINT")) {
            sqlTypeMapped = "short";
        } else if (sqlType.equals("INT")) {
            sqlTypeMapped = "int";
        } else if (sqlType.equals("INT UNSIGNED")) {
            sqlTypeMapped = "int";
        } else if (sqlType.equals("BIGINT")) {
            sqlTypeMapped = "int";
        } else if (sqlType.equals("MEDIUMINT")) {
            sqlTypeMapped = "int";
        } else if (sqlType.equals("TINYINT")) {
            sqlTypeMapped = "boolean";
        } else if (sqlType.equals("INTEGER UNSIGNED")) {
            sqlTypeMapped = "int";
        } else if (sqlType.equals("INTEGER")) {
            sqlTypeMapped = "int";
        } else if (sqlType.equals("LONG")) {
            sqlTypeMapped = "int";
        } else if (sqlType.equals("DATE")) {
            sqlTypeMapped = "java.util.Date";
        } else if (sqlType.equals("DATETIME")) {
            sqlTypeMapped = "java.util.Date";
        } else if (sqlType.equals("TIMESTAMP")) {
            sqlTypeMapped = "java.util.Date";
        } else if (sqlType.equals("LONGBINARY")) {
            sqlTypeMapped = "Object";
        } else if (sqlType.equals("LONGCHAR")) {
            sqlTypeMapped = "String";
        } else if (sqlType.equals("REAL")) {
            sqlTypeMapped = "double";
        } else if (sqlType.equals("NUMBER")) {
            if (getSubprotocol().equals("oracle")) {
                if (scale > 0) {
                    sqlTypeMapped = "double";
                } else {
                    sqlTypeMapped = "int";
                }
            } else {
                sqlTypeMapped = "int";
            }
        } else if (sqlType.equals("DECIMAL")) {
            sqlTypeMapped = "double";
        } else if (sqlType.equals("DOUBLE")) {
            sqlTypeMapped = "double";
        } else if (sqlType.equals("FLOAT")) {
            sqlTypeMapped = "double";
        } else if (sqlType.equals("COUNTER")) {
            sqlTypeMapped = "int";
        } else if (sqlType.equals("BIT")) {
            sqlTypeMapped = "boolean";
        } else if (sqlType.equals("OTHER")) {
            sqlTypeMapped = "Object";
        } else if (sqlType.equals("INT IDENTITY")) {
            sqlTypeMapped = "int";
        } else if (sqlType.equals("BIGINT UNSIGNED")) {
            sqlTypeMapped = "int";
        } else {
            System.out.println("Type " + sqlType + " unknown. Object type used");
            sqlTypeMapped = "Object";
        }

        return sqlTypeMapped;
    }

    protected boolean existField(String fieldName) {
        return tableFields.indexOf(nvl(fieldName).toLowerCase()) >= 0
                || tableFields.indexOf(nvl(fieldName).toUpperCase()) >= 0;
    }

    protected String getField(int i) {
        return (String) tableFields.elementAt(i);
    }

    protected String getFieldFirstUppercaseHungarian(int i) {
        String s = getField(i);
        return firstUpperCaseHungarian(s);
    }

    protected String getFieldFirstLowercaseHungarian(int i) {
        String s = getField(i);
        return firstLowercaseHungarian(s);
    }

    protected String getFieldType(int i) {
        return (String) tableFieldsTypes.elementAt(i);
    }

    protected String getFieldTypeFirstUppercaseHungarian(int i) {
        String res = "";
        String res0 = (String) tableFieldsTypes.elementAt(i);

        if (res0.startsWith("java.sql.")) {
            res = res0.substring(9);
        }

        if (res0.startsWith("java.util.")) {
            res = res0.substring(10);
        }

        return firstUpperCaseHungarian(res);
    }

    protected int getFieldsCountTable() {
        return tableFields.size();
    }

    public String getFieldsNamesRaw() {
        String fieldsNamesRaw = "" + getField(0);
        int n = getFieldsCountTable();
        for (int i = 1; i < n; i++) {
            fieldsNamesRaw += ", " + getField(i);
        }
        return fieldsNamesRaw;
    }

    protected static String firstUpperCaseHungarian(String s) {
        String result = "";
        String s2 = s.replace('.', '_');
        StringTokenizer st = new StringTokenizer(s2, "_");
        while (st.hasMoreTokens()) {
            result += firstUppercaseRestLowercase(st.nextToken());
        }

        if (s2.startsWith("_")) {
            result = "_" + result;
        }

        return result;
    }

    protected static String firstUppercase(String s) {
        String firstLetter = s.substring(0, 1);
        return firstLetter.toUpperCase() + s.substring(1);
    }

    protected static String firstUppercaseRestLowercase(String s) {
        String firstLetter = s.substring(0, 1);
        return firstLetter.toUpperCase() + s.substring(1).toLowerCase();
    }

    protected static String firstLowercaseHungarian(String s) {
        String result = "";
        String s2 = s.replace('.', '_');
        StringTokenizer st = new StringTokenizer(s2, "_");
        int k = 0;
        while (st.hasMoreTokens()) {
            if (k++ == 0) {
                result += st.nextToken().toLowerCase();
            } else {
                result += firstUpperCaseHungarian(st.nextToken());
            }
        }

        if (s2.startsWith("_")) {
            result = "_" + result;
        }

        return result;
    }

    public static String firstUppercaseRestLowercaseBlanks(String s) {
        String firstLetter = s.substring(0, 1);
        return firstLetter.toUpperCase() + s.substring(1).toLowerCase().replace('_', ' ');
    }

    public void generateSourceDictionaryNoWiz(PrintStream p) {
        p.println("");
        p.println("package " + getPackageName() + ";");
        p.println("");
        p.println("import " + getPackageName() + ".wiz.*;");
        p.println("import com.jumppi.frwk.sql.DB;");
        p.println("");
        p.println("public class " + getClassName() + "Dic extends " + getClassName() + "DicWiz {");
        p.println("");

        p.println("  protected " + getClassName() + "Dic() {");
        p.println("    super();");
        p.println("  }");
        p.println("");
        
        p.println("  public static " + getClassName() + "Dic getInstance() {");
        p.println("    return new " + getClassName() + "Dic();");
        p.println("  }");
        p.println(" ");
        
        p.println("  /**");
        p.println("  *@deprecated");
        p.println("  */");
        p.println("  protected " + getClassName() + "Dic(String idCon) {");
        p.println("    super(idCon);");
        p.println("  }");
        p.println("");

        p.println("  /**");
        p.println("  *@deprecated");
        p.println("  */");
        p.println("  public static " + getClassName() + "Dic getInstance(String idCon) {");
        p.println("    return new " + getClassName() + "Dic(idCon);");
        p.println("  }");
        p.println(" ");
        
        p.println("}");
        p.println("");
    }

    public void generateSourceDictionaryWiz(PrintStream p) {
        int n = getFieldsCountTable();

        p.println("");
        p.println("package " + getPackageName() + ".wiz;");
        p.println("");
        p.println("import java.util.*;");
        p.println("import java.sql.*;");
        p.println("import com.jumppi.frwk.util.*;");
        p.println("import com.jumppi.frwk.sql.*;");
        p.println("import " + getPackageName() + ".*;");

        if (!import1.equals("")) {
            p.println("import " + import1 + ";");
        }
        if (!import2.equals("")) {
            p.println("import " + import2 + ";");
        }
        if (!import3.equals("")) {
            p.println("import " + import3 + ";");
        }
        if (!import4.equals("")) {
            p.println("import " + import4 + ";");
        }
        if (!import5.equals("")) {
            p.println("import " + import5 + ";");
        }

        p.println("");
        p.println("");
        p.println("/**");
        p.println("@@ Params gene XML:");
        p.println(" ");
        p.println("<entity>");
        p.println("  <class-name>" + getClassName() + "</class-name>");
        p.println("  <class-name-plural>" + getClassNamePlural() + "</class-name-plural>");
        p.println("  <table-name>" + getTableName() + "</table-name>");
        if (db.equals("Oracle")) {
            p.println("  <sequencer>" + getSequencer() + "</sequencer>");
        }
        p.println(" ");
        p.println("  <path-output-wiz>" + outputPathClassWiz + "</path-output-wiz>");
        p.println("  <path-output-nowiz>src_nowiz</path-output-nowiz>");
        p.println(" ");
        p.println("  <driver>" + driver + "</driver>");
        p.println("  <url-db>" + dataOrigin + "</url-db>");
        p.println("  <username>" + username + "</username>");
        p.println("  <password>" + password + "</password>");
        p.println("  <package>" + getPackageName() + "</package>");
        p.println("  <read-only>" + (!isGeneratedKeys() ? "true" : "false") + "</read-only>");
        p.println("  <logic-deletion>" + (isLogicDeletion() ? "true" : "false") + "</logic-deletion>");
//		p.println("  <header>" + getHeader() + "</header>");
        p.println("  <db-class-name>" + getDbClassName() + "</db-class-name>");
        p.println("  <ts-crea>" + new java.util.Date().toLocaleString() + "</ts-crea>");
        p.println("</entity>");
        p.println(" ");
        p.println("*/");

        p.println("public class " + getClassName() + "DicWiz {");
        p.println("	 protected static String _idCon = \"\";");
        p.println("	");
        p.println("	");

        if (isLogicDeletion()) {
            p.println("	public static final String queryFindAll = ");
            p.println("     \" SELECT " + getFieldsNamesRaw() + " \" +");
            p.println("     \" FROM " + getTableName() + " WHERE deleted = 0 \";");
            p.println("	");
            p.println("	");

            p.println("	public static final String queryFindById = ");
            p.println("     \" SELECT " + getFieldsNamesRaw() + " \" +");
            p.println("     \" FROM " + getTableName() + " \" +");
            p.println("     \" WHERE " + getField(colId) + " = ? AND deleted = 0 \";");
            p.println("	");
        } else {
            p.println("	public static final String queryFindAll = ");
            p.println("     \" SELECT " + getFieldsNamesRaw() + " \" +");
            p.println("     \" FROM " + getTableName() + " \";");
            p.println("	");
            p.println("	");

            p.println("	public static final String queryFindById = ");
            p.println("     \" SELECT " + getFieldsNamesRaw() + " \" +");
            p.println("     \" FROM " + getTableName() + " \" +");
            p.println("     \" WHERE " + getField(colId) + " = ? \";");
            p.println("	");
        }

        p.println("    /**");
        p.println("    *@deprecated");
        p.println("    */");
        p.println("    protected " + getClassName() + "DicWiz(String idCon) {");
        p.println("      _idCon = idCon;");
        p.println("    }");
        p.println("	");

        p.println("    protected " + getClassName() + "DicWiz() {");
        p.println("    }");
        p.println("	");
        
        p.println("    public static void setIdCon(String value) {");
        p.println("      _idCon = value;");
        p.println("    }");
        p.println("	");
        
        p.println("    public static String getIdCon() {");
        p.println("      return _idCon;");
        p.println("    }");
        p.println("	");
        
        if (isGeneratedKeys()) {
            p.println("	public static final String queryCreateRecord = ");
            p.println("      \"INSERT INTO " + getTableName() + "(\" + ");

            for (int i = 0; i < n - 1; i++) {
                if (i == colId) {
                    if (db.equals("Oracle")) {
                        p.println("                       \" " + getField(i) + ", \" + ");
                    }
                } else {
                    p.println("                       \" " + getField(i) + ", \" + ");
                }
            }
            p.println("                       \" " + getField(n - 1) + ") \" + ");
            p.print("                       \" VALUES (");
            for (int i = 0; i < n - 1; i++) {
                if (i == colId) {
                    if (db.equals("Oracle")) {
                        p.print("?, ");
                    }
                } else {
                    p.print("?, ");
                }
            }

            p.print("?)\";");

            p.println("	");
            p.println("	");

            p.println("	public static final String queryUpdateRecord = 	");
            p.println("      \"UPDATE " + getTableName() + " SET \" + ");

            for (int i = 0; i < n; i++) {
                if (i != colId) {
                    if (i < n - 1) {
                        p.println("                       \" " + getField(i) + " = ?, \" + ");
                    } else {
                        p.println("                       \" " + getField(i) + " = ?  \" + ");
                    }
                }
            }

            if (isLogicDeletion()) {
                p.println("                       \" WHERE " + getField(colId) + " = ? AND deleted = 0  \";");
            } else {
                p.println("                       \" WHERE " + getField(colId) + " = ?\";");
            }

            p.println("	");
            p.println("	");
            p.println("	public static final String queryDeleteRecord = 	");

            if (isLogicDeletion()) {
                p.println("     \" DELETE FROM " + getTableName() + " WHERE " + getField(colId) + " = ? AND deleted = 0 \";");
            } else {
                p.println("     \" DELETE FROM " + getTableName() + " WHERE " + getField(colId) + " = ? \";");
            }

            p.println("	");
            p.println("	");
            p.println("	public static final String queryDeleteRecordLogically = 	");

            if (isLogicDeletion()) {
                p.println("     \" UPDATE " + getTableName() + " SET deleted = 1, timedeleted = CURRENT_TIMESTAMP() WHERE " + getField(colId) + " = ? AND deleted = 0 \";");
            } else {
                p.println("     \" UPDATE " + getTableName() + " SET deleted = 1, timedeleted = CURRENT_TIMESTAMP() WHERE " + getField(colId) + " = ? \";");
            }

            p.println("	");
            p.println("	");
        }

        if (isGeneratedKeys()) {

            p.println("  ");
            p.println("  public " + getClassName() + " create" + getClassName() + "() {");
            p.println("    return create" + getClassName() + "(true);");
            p.println("  }");
            p.println("  ");
            p.println("  public " + getClassName() + " create" + getClassName() + "(boolean persisted) {");
            p.println("    " + getClassName() + "Dic vObj = " + getClassName() + "Dic.getInstance(_idCon);");
            p.println("    " + getClassName() + " obj = new " + getClassName() + "();");
            p.println("    if (persisted)");
            p.println("      vObj.save(obj);");
            p.println("    return obj;");
            p.println("  }");
            p.println("  ");

			// CREATE RECORD
            p.println("  ");
            p.println("  public int createRecord(" + getClassName() + " obj) {");
            p.println("    int lastId = 0;");
            p.println("  ");
            p.println("    try");
            p.println("	{");

            p.println("		" + getDbClassName() + " db = " + getDbClassName() + ".getInstance(_idCon);");

            p.println("		List params = new ArrayList();");

            if (db.equals("Oracle")) {
                p.println("		String queryNewId = \"select " + getSequencer() + ".NEXTVAL from dual\";");
                p.println("		Object newId = db.getField(queryNewId);");
            }

            p.println("		");

            int j = 1;
            String fieldType = "";
            for (int i = 0; i < n; i++) {
                if (colId == i) {
                    if (db.equals("Oracle")) {
                        p.println(" 		params.add(newId);");
                        j++;
                    }
                } else {
                    fieldType = getFieldType(i);
                    if (fieldType.equals("java.util.Date")) {
                        p.println("		if(obj.get" + getFieldFirstUppercaseHungarian(i) + "() != null) {");
                        p.println("			params.add("
                                + "new java.sql.Timestamp(obj.get" + getFieldFirstUppercaseHungarian(i) + "().getTime()));");
                        p.println("		}else{");
                        p.println("			params.add(null);");
                        p.println("		}");
                    } else if (fieldType.equals("boolean")) {
                        p.println("		params.add(obj.is"
                                + getFieldFirstUppercaseHungarian(i) + "());");
                    } else {
                        p.println("		params.add(obj.get"
                                + getFieldFirstUppercaseHungarian(i) + "());");
                    }
                    j++;
                }
            }

            p.println("		lastId = db.executeInsertSQL(queryCreateRecord, params);");
            if (db.equals("Oracle")) {
                p.println("		lastId = Util.parseInt(\"\" + newId);");
            }
            p.println("		obj.set" + getFieldFirstUppercaseHungarian(colId) + "(lastId);");
            p.println("	}");
            p.println("	catch (Exception e)");
            p.println("	{");
            p.println("		throw new SignalException(e);");
            p.println("	}");
            p.println("    ");
            p.println("    return lastId;");
            p.println("  }");
            p.println("  ");

			// UPDATE RECORD
            p.println("  ");
            p.println("  public int updateRecord(" + getClassName() + " obj) {");
            p.println("	int res = 0;");
            p.println("	");
            p.println("    try");
            p.println("	{");
            p.println("		");
            p.println("		List params = new ArrayList();");
            p.println("		");

            j = 1;
            for (int i = 0; i < n; i++) {
                if (colId != i) {
                    fieldType = getFieldType(i);
                    if (fieldType.equals("java.util.Date")) {
                        p.println("		if(obj.get" + getFieldFirstUppercaseHungarian(i) + "() != null) {");
                        p.println("			params.add(new java.sql.Timestamp(obj.get"
                                + getFieldFirstUppercaseHungarian(i) + "().getTime()));");
                        p.println("		}else{");
                        p.println("			params.add(null);");
                        p.println("		}");
                    } else if (fieldType.equals("boolean")) {
                        p.println("		params.add(obj.is"
                                + getFieldFirstUppercaseHungarian(i) + "());");
                    } else {
                        p.println("		params.add(obj.get"
                                + getFieldFirstUppercaseHungarian(i) + "());");
                    }
                    j++;
                }
            }

            p.println("		");
            p.println("		params.add(obj.get" + getFieldFirstUppercaseHungarian(colId)
                    + "()); ");
            p.println("		");
            p.println("		" + getDbClassName() + " db = " + getDbClassName() + ".getInstance(_idCon);");
            p.println("		res = db.executeSQL(queryUpdateRecord, params);");
            p.println("	}");
            p.println("	catch (Exception e)");
            p.println("	{");
            p.println("		throw new SignalException(e);");
            p.println("	}");
            p.println("    ");
            p.println("    return res;");
            p.println("  }");
            p.println("  ");

			// DELETE RECORD
            p.println("  public int deleteRecord(" + getClassName() + " obj) {");
            p.println("    int idInt = obj.getIdInt();");
            p.println("    return deleteRecord(idInt);");
            p.println("  }");
            p.println("  ");

            p.println("  ");
            p.println("  public int remove(" + getClassName() + " obj) {");
            p.println("    return deleteRecord(obj);");
            p.println("  }");
            p.println("  ");

            p.println("  ");
            p.println("  public int removeById(String id) {");
            p.println("    " + getClassName() + "Dic objd = " + getClassName() + "Dic.getInstance(_idCon);");
            p.println("    " + getClassName() + " obj = objd.findById(id);");
            p.println("    if (obj != null)");
            p.println("      return deleteRecord(obj);");
            p.println("    else");
            p.println("      return 0;");
            p.println("  }");
            p.println("  ");

            p.println("  ");
            p.println("  public int deleteRecord(int id) {");
            p.println("	int res = 0;");
            p.println("	");
            p.println("	try");
            p.println("	{");
            p.println("		List params = new ArrayList();");
            p.println("		params.add(id);");
            p.println("		" + getDbClassName() + " db = " + getDbClassName() + ".getInstance(_idCon);");
            p.println("		res = db.executeSQL(queryDeleteRecord, params);");
            p.println("	}");
            p.println("	catch (Exception e)");
            p.println("	{");
            p.println("		throw new SignalException(e);");
            p.println("	}");
            p.println("	");
            p.println("    return res;");
            p.println("  }");
            p.println("  ");

			// DELETE RECORD LOGICALLY
            p.println("  ");
            p.println("  public int deleteRecordLogically(" + getClassName() + " obj) {");
            p.println("    int idInt = obj.getIdInt();");
            p.println("    return deleteRecordLogically(idInt);");
            p.println("  }");
            p.println("  ");

            p.println("  ");
            p.println("  public int deleteRecordLogically(int id) {");
            p.println("		int res = 0;");
            p.println("		");
            p.println("		try");
            p.println("		{");
            p.println("			List params = new ArrayList();");
            p.println("			params.add(id);");
            p.println("		" + getDbClassName() + " db = " + getDbClassName() + ".getInstance(_idCon);");
            p.println("			res = db.executeSQL(queryDeleteRecordLogically, params);");
            p.println("		}");
            p.println("		catch (Exception e)");
            p.println("		{");
            p.println("			throw new SignalException(e);");
            p.println("		}");
            p.println("		");
            p.println("	    return res;");
            p.println("  }");
            p.println("  ");

			// save
            p.println("  ");
            p.println("  public int save(" + getClassName() + " dto) {");
            p.println("    int res;");
            p.println("    if (dto.getIdInt() == 0)");
            p.println("	     res = createRecord(dto);");
            p.println("    else");
            p.println("      res = updateRecord(dto);");
            p.println("    return res;");
            p.println("  }");
            p.println("  ");

            p.println("  ");
            p.println("  public int saveAndFlush(" + getClassName() + " dto) {");
            p.println("    return save(dto);");
            p.println("  }");
            p.println("  ");

        }
          
        p.println("    public boolean existsId(int id) {");
        p.println("        int cont = 0;");
        p.println("        try {");
        p.println("            " + getDbClassName() + " db = " + getDbClassName() + ".getInstance(_idCon);");
        p.println("            String pquery = \"SELECT COUNT(" + getField(colId) +") FROM " + getTableName() + " WHERE " + getField(colId) + " = ?\";");
        p.println("            PreparedStatement st = db.prepareStatement(pquery);");
        p.println("            st.setInt(1, id);");
        p.println("            ResultSet rs = st.executeQuery();");
        p.println("            rs.next();");
        p.println("            cont = rs.getInt(1);");
        p.println("        }");
        p.println("        catch(Exception ex) {");
        p.println("            throw new SignalException(\"Error en existsId\");");
        p.println("        }");
        p.println("        return cont > 0;");
        p.println("    }");

        p.println("  public int count(){");
        p.println("  	" + getDbClassName() + " db = " + getDbClassName() + ".getInstance(_idCon);");
        p.println("  	int res = db.getCount(queryFindAll);");
        p.println("  	return res;");
        p.println("  }");

        p.println("  public int countByFilter(QueryFilter qf){");
        p.println("  	" + getDbClassName() + " db = " + getDbClassName() + ".getInstance(_idCon);");
        p.println("  	String query = " + getDbClassName() + ".buildFilteredQuery(queryFindAll, qf);");
        p.println("  	int res = db.getCount(query);");
        p.println("		return res;");
        p.println("  }");
        p.println("  ");
        p.println("  public Collection findAll(){");
        p.println("	return findAll(" + getClassName() + ".class);");
        p.println("  }");
        p.println("  ");
        p.println("  public Collection findAll(Class dtoClass){");
        p.println("	return findByFilter(dtoClass, null);");
        p.println("  }");
        p.println("  ");
        p.println("  public Collection findByFilter(QueryFilter qf) {");
        p.println("		return findByFilter(" + getClassName() + ".class, qf);");
        p.println("  }");
        p.println("  ");
        p.println("  public Collection findByFilter(Class dtoClass, QueryFilter qf) {");
        p.println("	Collection vObj = null;");
        p.println("	 ");
        p.println("	" + getDbClassName() + " db = " + getDbClassName() + ".getInstance(_idCon);");

        p.println("	String queryf = \"\";");
        p.println("	if (qf == null){");
        p.println("		queryf = queryFindAll;");
        p.println("	}else{");
        p.println("		queryf = " + getDbClassName() + ".buildFilteredAndOrderedQuery(queryFindAll, qf);");
        p.println("	}");

        p.println("	vObj = db.getEntities(dtoClass, queryf);");
        p.println("	  ");
        p.println("	return vObj;");
        p.println("  }");
        p.println("  ");

        p.println("  public " + getClassName() + " findById(int id){");
        p.println("    return findById(new Integer(id));");
        p.println("  }");
        p.println("  ");

        p.println("  ");
        p.println("  public " + getClassName() + " findById(Object id) {");
        p.println("	 " + getClassName() + " obj = null;");
        p.println("	 ");
        p.println("	   try");
        p.println("	   {");
        p.println("		" + getDbClassName() + " db = " + getDbClassName() + ".getInstance(_idCon);");

        p.println("		PreparedStatement pstmt = db.prepareStatement(queryFindById);");
        p.println("		pstmt.setObject(1, id);");
        p.println("		ResultSet rs = pstmt.executeQuery();");
        p.println("		rs.next();");

        p.println("		obj = (" + getClassName() + ") db.getDTO(" + getClassName() + ".class, rs);");
        p.println("		rs.close();");
        p.println("		pstmt.close();");
        p.println("	 }");
        p.println("	 catch (Exception e)");
        p.println("	 {");
        p.println("		throw new SignalException(e);");
        p.println("	 }");
        p.println("	  ");
        p.println("	 return obj;");
        p.println("  }");
        p.println("  ");

        p.println("  ");
        p.println("  public " + getClassName() + " findById(Object id, Class[] classes, String discrim, String[] discrims) {");
        p.println("	 " + getClassName() + " obj = null;");
        p.println("	 ");
        p.println("	   try {");
        p.println("		" + getDbClassName() + " db = " + getDbClassName() + ".getInstance(_idCon);");

        p.println("		PreparedStatement pstmt = db.prepareStatement(queryFindById);");
        p.println("		pstmt.setObject(1, id);");
        p.println("		ResultSet rs = pstmt.executeQuery();");
        p.println("		rs.next();");

        p.println("		obj = (" + getClassName() + ") db.getDTO(classes, discrim, discrims, rs);");
        p.println("		rs.close();");
        p.println("		pstmt.close();");
        p.println("	 }");
        p.println("	 catch (Exception e)");
        p.println("	 {");
        p.println("		throw new SignalException(e);");
        p.println("	 }");
        p.println("	  ");
        p.println("	 return obj;");
        p.println("  }");
        p.println("  ");

        p.println(" public void reseq" + getClassNamePlural() + "(int step){");
        p.println("		" + getDbClassName() + " db = " + getDbClassName() + ".getInstance(_idCon);");
        p.println(" 	try {");
        p.println(" 		Statement stmt = db.createUpdatableStatement();");
        p.println(" 		String query = \"SELECT ORD FROM (\" + queryFindAll + \") T ORDER BY ORD\";");
        p.println(" 		ResultSet rs = stmt.executeQuery(query);");
        p.println(" 		int i = 0;");
        p.println(" 		while (rs.next()) {");
        p.println("				rs.updateInt(\"ord\", i);");
        p.println("				rs.updateRow();");
        p.println("				i += step;");
        p.println(" 		}");
        p.println(" 		rs.close();");
        p.println(" 		stmt.close();");
        p.println(" 	} catch (SQLException e) {");
        p.println(" 		throw new SignalException(e);");
        p.println(" 	}");
        p.println(" }");
        p.println("");

        p.println("  public void closeConnection() {");

        p.println("	   " + getDbClassName() + ".close(_idCon);");

        p.println("  }");
        p.println("");

        p.println("  public static String getTableName() {");
        p.println("    return \"" + getTableName() + "\";");
        p.println("  }");
        p.println("");
        p.println("  public static String getFieldId() {");
        p.println("    return \"" + getField(colId) + "\";");
        p.println("  }");
        p.println("");
        p.println("}");
        p.println("");
    }

    public void generateSourceDTONoWiz(PrintStream p) {
        p.println("");
        p.println("package " + getPackageName() + ";");
        p.println("");
        p.println("import " + getPackageName() + ".wiz.*;");
        p.println("");

        p.println("public class " + getClassName() + " extends " + getClassName() + "Wiz {");
        p.println("}");
        p.println("");
    }

    public void generateSourceDTOWiz(PrintStream p) {
        p.println("");
        p.println("package " + getPackageName() + ".wiz;");
        p.println("");

        if (!import1.equals("")) {
            p.println("import " + import1 + ";");
        }
        if (!import2.equals("")) {
            p.println("import " + import2 + ";");
        }
        if (!import3.equals("")) {
            p.println("import " + import3 + ";");
        }
        if (!import4.equals("")) {
            p.println("import " + import4 + ";");
        }
        if (!import5.equals("")) {
            p.println("import " + import5 + ";");
        }

        p.println("");

        p.println("public class " + getClassName() + "Wiz {");

        int n = getFieldsCountTable();
        for (int i = 0; i < n; i++) {
            if (getField(i).toUpperCase().equals("DEL")) {
//                p.println("  protected " + getFieldType(i) + " " + getFieldFirstLowercaseHungarian(i) + " = \"N\";");
                p.println("  protected " + getFieldType(i) + " " + getFieldFirstLowercaseHungarian(i) + " = 0;");
            } else {
                p.println("  protected " + getFieldType(i) + " " + getFieldFirstLowercaseHungarian(i) + ";");
            }
        }

        p.println("");
        p.println("");

        // getters
        p.println("///////////////////////////////////////////////////////////");
        p.println("//  getters ");
        p.println("///////////////////////////////////////////////////////////");
        p.println("");

        if (isGeneratedKeys()) {
            p.println("  public int getIdInt() {");
            String fieldFirstUppercaseHungarian = getFieldFirstUppercaseHungarian(colId);
            p.println("    return get" + fieldFirstUppercaseHungarian + "();");
            p.println("  }");

            p.println("");
            p.println("    public boolean exists() {");
            p.println("        return getIdInt() != 0;");
            p.println("    }");
            p.println("");
        }

        for (int i = 0; i < n; i++) {
            p.println("");
            if (getFieldType(i).equals("boolean")) {
                p.println("  public " + getFieldType(i) + " is" + getFieldFirstUppercaseHungarian(i) + "() {");
            } else {
                p.println("  public " + getFieldType(i) + " get" + getFieldFirstUppercaseHungarian(i) + "() {");
            }

            p.println("    return this." + getFieldFirstLowercaseHungarian(i) + ";");
            p.println("  }");
        }
        p.println("");

        // setters
        p.println("///////////////////////////////////////////////////////////");
        p.println("//  setters ");
        p.println("///////////////////////////////////////////////////////////");
        p.println("");

        if (isGeneratedKeys()) {
            // Setter IdInt
            p.println("  public void setIdInt(int value) {");
            String fieldFirstUppercaseHungarian = getFieldFirstUppercaseHungarian(colId);
            p.println("     set" + fieldFirstUppercaseHungarian + "(value);");
            p.println("  }");
        }

        for (int i = 0; i < n; i++) {
            p.println("");
            p.println("  public void set" + getFieldFirstUppercaseHungarian(i) + "(" + getFieldType(i) + " value) {");
            p.println("    this." + getFieldFirstLowercaseHungarian(i) + " = value;");
            p.println("  }");
        }
        p.println("}");
    }

    public void generateDictionary() throws IOException {
        setDataOrigin(dataOrigin);
        setClassName(className);
        setPackageName(packageName);
        loadTableFields();

        String classWizPath = getOutputPathClassWiz() + "/" + getPackageToPath() + "/";
        String classNoWizPath = getOutputPathClassNoWiz() + "/" + getPackageToPath() + "/";

        File fil = new File(classWizPath);
        fil.mkdirs();
        File fil2 = new File(classNoWizPath);
        fil2.mkdirs();
        File fil3 = new File(classWizPath + "wiz");
        fil3.mkdirs();

        FileOutputStream f = null;
        PrintStream p = null;
        String fabs = "";
        boolean dicWizErr = false;
        boolean dtoWizErr = false;

        try {
            fabs = classWizPath + "wiz/" + getClassName() + "DicWiz.java";

            f = new FileOutputStream(fabs);
            p = new PrintStream(f);
            System.out.println("Generation of " + fabs);
            generateSourceDictionaryWiz(p);
            p.close();
        } catch (Exception e) {
            dicWizErr = true;
            printException(e);
            p.close();
            File fp = new File(fabs);
            fp.delete();
        }

        try {
            fabs = classNoWizPath + getClassName() + "Dic.java";

            f = new FileOutputStream(fabs);
            p = new PrintStream(f);
            System.out.println("Generation of " + fabs);
            generateSourceDictionaryNoWiz(p);
            p.close();
        } catch (Exception e) {
            dicWizErr = true;
            printException(e);
            p.close();
            File fp = new File(fabs);
            fp.delete();
        }

        try {
            fabs = classWizPath + "wiz/" + getClassName() + "Wiz.java";

            f = new FileOutputStream(fabs);
            p = new PrintStream(f);
            System.out.println("Generation of " + fabs);
            generateSourceDTOWiz(p);
            p.close();
        } catch (Exception e) {
            dtoWizErr = true;
            printException(e);
            p.close();
            File fp = new File(fabs);
            fp.delete();
        }

        try {
            fabs = classNoWizPath + getClassName() + ".java";

            f = new FileOutputStream(fabs);
            p = new PrintStream(f);
            System.out.println("Generation of " + fabs);
            generateSourceDTONoWiz(p);
            p.close();
        } catch (Exception e) {
            dtoWizErr = true;
            printException(e);
            p.close();
            File fp = new File(fabs);
            fp.delete();
        }
    }

    public CRUDWiz() {
        try {
            factory = DocumentBuilderFactory.newInstance();
            //		    factory.setValidating(true);
            factory.setIgnoringElementContentWhitespace(true);
            builder = factory.newDocumentBuilder();
            jbInit();
            jTextArea.setText(initialTextAreaValue);
        } catch (Exception e) {
            printException(e);
        }
    }

    public static void printException(Exception e) {
		//		System.out.println(e.getMessage());
        //		System.out.println(e.toString());
        e.printStackTrace();
    }

    protected void jbInit() throws Exception {
        setTitle("CRUDWiz");
        this.setSize(new Dimension(649, 542));
        Container cp = this.getContentPane();
        cp.setLayout(null);
        cp.setBackground(Color.lightGray);
        this.setContentPane(cp);
        label1.setBounds(new Rectangle(18, 12, 414, 25));
        label1.setBackground(Color.lightGray);
        label1.setFont(new java.awt.Font("Dialog", 1, 24));
        label1.setForeground(Color.black);
        label1.setText("CRUDWiz (" + db + ")");

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                this_windowClosing(e);
            }
        });

        button1.setBounds(new Rectangle(284, 466, 74, 29));
//		button1.setFont(new java.awt.Font("Dialog", 1, 14));
        button1.setText("Ok");
        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                button1_actionPerformed(e);
            }
        });

        cp.add(button1);
        cp.add(label1);

        cp.add(getBtnHelp(), null);
        cp.add(getJScrollPane(), null);
        cp.add(button1, null);
    }

    public int getNumEntities() {
        NodeList nodElems = doc.getElementsByTagName("entity");
        int res = nodElems.getLength();
        return res;
    }

    public String getByTagName(int numEntity, String tag) {
        String res = "";
        try {
            Element root = doc.getDocumentElement();
            NodeList nodElems = null;
            Element elem = null;

            if (root.getTagName().equals("entity")) {
                elem = root;
            } else {
                nodElems = root.getElementsByTagName("entity");
                elem = (Element) nodElems.item(numEntity);
            }
            NodeList nodElems2 = elem.getElementsByTagName(tag);
            res = nodElems2.item(0).getTextContent().trim();
        } catch (Exception e) {
            //			printException(e);
        }
        return res;
    }

    public void button1_actionPerformed(ActionEvent e) {
        try {
            // Valores por defecto
            setOutputPathClassWiz(initialOutputPath);
            setOutputPathClassNoWiz(initialOutputPathNoWiz);
            setGeneratedKeys(generatedKeys);
            setLogicDeletion(logicDeletion);
            setPackageName("appl.model");
            setColId(Integer.parseInt(initialColumnId));

            String msg = jTextArea.getText();
            doc = builder.parse(new StringBufferInputStream(msg));
            int n = getNumEntities();
            String tagVal = "";
            String jdbcUrl = "";

            for (int i = 0; i < n; i++) {

                setDriver(getByTagName(i, "driver"));

                jdbcUrl = getByTagName(i, "url-db");
                if (jdbcUrl.startsWith("jdbc:oracle")) {
                    setDb("Oracle");
                } else if (jdbcUrl.startsWith("jdbc:mysql")) {
                    setDb("MySQL");
                } else if (jdbcUrl.startsWith("jdbc:sqlserver") || jdbcUrl.startsWith("jdbc:jtds")) {
                    setDb("SQL Server");
                } else {
                    setDb("ANSI SQL");
                }
                label1.setText("CRUDWiz (" + getDb() + ")");

                setDataOrigin(jdbcUrl);
                setUsername(getByTagName(i, "username"));
                setPassword(getByTagName(i, "password"));
                setClassName(getByTagName(i, "class-name"));
                setTableName(getByTagName(i, "table-name"));

                tagVal = getByTagName(i, "sequencer");
                if (!tagVal.equals("")) {
                    setSequencer(tagVal);
                }

                tagVal = getByTagName(i, "package");
                if (!tagVal.equals("")) {
                    setPackageName(tagVal);
                }

                tagVal = getByTagName(i, "db-class-name");
                if (tagVal.equals("")) {
                    setDbClassName("DB");
                } else {
                    setDbClassName(tagVal);
                }

                tagVal = getByTagName(i, "col-id");
                if (!tagVal.equals("")) {
                    setColId(Integer.parseInt(tagVal) - 1);
                }

                tagVal = getByTagName(i, "path-output-wiz");
                if (!tagVal.equals("")) {
                    setOutputPathClassWiz(tagVal);
                }

                tagVal = getByTagName(i, "path-output-nowiz");
                if (!tagVal.equals("")) {
                    setOutputPathClassNoWiz(tagVal);
                }

                boolean bRo = false;
                tagVal = getByTagName(i, "read-only").toLowerCase();
                if (!tagVal.equals("")) {
                    bRo = tagVal.equals("true") || tagVal.equals("si") || tagVal.equals("s");
                }
                setGeneratedKeys(!bRo);

                boolean bLd = false;
                tagVal = getByTagName(i, "logic-deletion").toLowerCase();
                if (!tagVal.equals("")) {
                    bLd = tagVal.equals("true") || tagVal.equals("yes") || tagVal.equals("y");
                }
                setLogicDeletion(bLd);

                tagVal = getByTagName(i, "header");
                if (!tagVal.equals("")) {
                    setHeader(tagVal);
                }

                tagVal = getByTagName(i, "class-name-plural");
                if (!tagVal.equals("")) {
                    setClassNamePlural(tagVal);
                    setClassCtl(tagVal + "DCtl");
                    setClassUC(tagVal + "PCtl");
                } else {
                    setClassNamePlural(firstUpperCaseHungarian(tailSchema(getTableName())));

                    tagVal = getByTagName(i, "class-ctl");
                    if (!tagVal.equals("")) {
                        setClassCtl(tagVal);
                    } else {
                        setClassCtl(getClassNamePlural() + "DCtl");
                    }

                    tagVal = getByTagName(i, "class-uc");
                    if (!tagVal.equals("")) {
                        setClassUC(tagVal);
                    }
                }
                generateDictionary();
            }

            // 			JOptionPane.showMessageDialog(this, "Ok");
        } catch (Exception ex) {
            System.out.println("Captured at CRUDWiz.actionPerformed()");
            printException(ex);
        }
        System.out.println(" ******* End ********");
        //		System.exit(0);
    }

    public void this_windowClosing(WindowEvent e) {
        System.exit(0);
    }

    public void center() {
        int screenWidth = getToolkit().getScreenSize().width;
        int screenHeight = getToolkit().getScreenSize().height;

        int windowWidth = getSize().width;
        int windowHeight = getSize().height;

        setLocation((screenWidth - windowWidth) / 2, (screenHeight - windowHeight) / 2);
    }

    public String nvl(Object value) {
        return value == null ? "" : value.toString().trim();
    }

    public String noCrLf(String value) {
        String s = value.replace('\n', ' ');
        String s2 = s.replace('\r', ' ');
        return s2;
    }

    public String tailSchema(String table) {
        String res = "";

        int pos = table.indexOf('.');
        if (pos >= 0) {
            res = table.substring(pos + 1);
        } else {
            res = table;
        }
        return res;
    }

    public String getSubprotocol() {
        int pos1 = dataOrigin.indexOf(':');
        int pos2 = dataOrigin.indexOf(':', pos1 + 1);
        String subProt = dataOrigin.substring(pos1 + 1, pos2);
        return subProt;
    }

    /**
     * This method initializes btnHelp
     *
     * @return java.awt.Button
     */
    private JButton getBtnHelp() {
        if (btnHelp == null) {
            btnHelp = new JButton();
            btnHelp.setBounds(new Rectangle(531, 7, 86, 27));
            btnHelp.setText("Readme");
            btnHelp.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String text = "Naming rules:\n"
                            + "\n"
                            + "- Class names must start uppercase and be in singular.\n"
                            + "- Tablenames must be all lowercase (MySQL style) or all uppercase (Oracle style)\n"
                            + "  word separed with _ (underscore) and in plural\n";
                    JOptionPane.showMessageDialog(null, text);
                }
            });
        }
        return btnHelp;
    }

    /**
     * This method initializes jScrollPane
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
            jScrollPane = new JScrollPane();
            jScrollPane.setBounds(new Rectangle(10, 50, 613, 406));
            jScrollPane.setViewportView(getJTextArea());
        }
        return jScrollPane;
    }

    /**
     * This method initializes jTextArea
     *
     * @return javax.swing.JTextArea
     */
    private JTextArea getJTextArea() {
        if (jTextArea == null) {
            jTextArea = new JTextArea();
        }
        return jTextArea;
    }

    
    /////////////////////////////////////////////////////////
    // main
    /////////////////////////////////////////////////////////
    public static void main(String[] args) throws Exception {
        CRUDWiz dc = new CRUDWiz();
        try {
//			dc.setSize(640, 500);
            dc.center();
            dc.setVisible(true);
        } catch (Exception e) {
            System.out.println(" ******* Exception " + e.getMessage() + " captured at main() ********");
            printException(e);
        }

        System.out.println("");
    }
} 

/*************************
 public Article findByCode(String value){ 
 	Client res = null; 
 	DB db = DB.getInstance(_idCon); 
 	String pquery = " select * from articles where code = ? ";
 	List params = new ArrayList(); 
 	params.add(value); 
 	res = db.getDTO(Article.class, pquery, params); 
 	return res; 
 }  
 
 public Collection<Article> findByDescriptionFragment(String value){
 	Collection vRes = null; 
 	DB db = DB.getInstance(_idCon); 
 	String pquery = " select * from articles where description like ? "; 
 	List params = new ArrayList();
 	params.add("%" + value + "%"); 
 	vRes = db.getEntities(Article.class, pquery, params); 
 	return vRes; 
 } 
 
 */
