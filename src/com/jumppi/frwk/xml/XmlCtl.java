package com.jumppi.frwk.xml;


import java.io.*;
import java.net.*;

import javax.net.ssl.*;

import java.util.*;

import org.w3c.dom.Text;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.jumppi.frwk.util.SignalException;
import com.jumppi.frwk.util.Util;

import java.security.Security;

/**
  Command Line: java -Djavax.xml.parsers.SAXParserFactory=MyParserFactory MyClass
  In Code:      System.setProperty("javax.xml.parsers.SAXParserFactory", "foo.bar.MyParserFactory");
                System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "foo.bar.MyDocumentBuilder");
*/
public class XmlCtl {
  protected Document doc = null;
  protected String dtd;
  protected String newDTD = "";
  protected DocumentBuilderFactory factory;
  protected DocumentBuilder builder;


  public static XmlCtl getInstance() {
	  return new XmlCtl();
  }
  
  public XmlCtl() {
    try {
		factory = DocumentBuilderFactory.newInstance();
//      factory.setValidating(true);
		factory.setIgnoringElementContentWhitespace(true);
		builder = factory.newDocumentBuilder();
		doc = builder.newDocument();
	} catch (Exception e) {
		Util.rethrow(e);
	}
  }

  public Document getDocument() {
    return doc;
  }

/*  Proxy jump
  http://www.developer.com/java/other/article.php/1551421
*/
  public Document getDocument(String url) {
    try {
      doc = builder.parse(url);
      return (doc);
    } catch (Exception e) {
      throw new SignalException("Problems in XmlCtl.getDocument(" + url + "). |" + e + "|, cause = |" + Util.getRootCause(e) + "|");
    }
  }

  // Testing pending
  public Document getDocument__(InputStream in) {
    try {
      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      int c;
      while ((c = in.read()) != -1) {
        bout.write(c);
      }    
      return (getDocument(bout.toString()));
    } catch (Exception e) {
      throw new SignalException("Problems in XmlCtl.getDocument. |" + e + "|, cause = |" + e.getCause() + "|");
    }
  }

  
  public Document getDocument(InputStream in) {
    try
    {
      doc = builder.parse(in);
      return (doc);
    } catch (Exception e) {
      throw new SignalException("Problems in XmlCtl.getDocument. |" + e + "|, cause = |" + e.getCause() + "|");
    }
  }


  public Document getDocumentFromFile (String absFile) {
	Document res = null;  
	try {
		res = getDocument(new FileInputStream(absFile));
	} catch (Exception e) {
		Util.rethrow(e);
	}
	return res;
  }

  
  
  public Document getDocumentString(String s) {
    try {
      ByteArrayInputStream bi = new ByteArrayInputStream(s.getBytes());
      doc = getDocument(bi);
      return (doc);
    } catch (Exception e) {
      throw new SignalException("Problems in XmlCtl.getDocumentString. " + e);
    }
  }

  /**
   * Testing pending
   */
  public Document getDocument(String url, String otherDTD) {
    try {
      InputStream in = new URL(url).openStream();
      doc = builder.parse(in, otherDTD);
      return (doc);
    } catch (Exception e) {
      throw new SignalException("Problems in XmlCtl.getDocument. " + e);
    }
  }

  public String getXmlString() {
	 ByteArrayOutputStream ba = new ByteArrayOutputStream();
     try {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		 Transformer transformer = tFactory.newTransformer();
		 DOMSource source = new DOMSource(doc);
		 StreamResult result = new StreamResult(ba);
		 transformer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
		 transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		 transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		 transformer.transform(source, result);
	} catch (Exception e) {
		Util.rethrow(e);
	}
     return new String(ba.toByteArray());
  }

  public void visualizeDOM() {
    NodeList nl = doc.getElementsByTagName("*");
    System.out.println("\nAll levels:");
    for (int i = 0; i < nl.getLength(); i++)
    {
      Node nod = nl.item(i);
      try {
        System.out.println(nod.getNodeName() + ": " + nod.getFirstChild().getNodeValue());
      } catch(Exception e){
        System.out.println(nod.getNodeName() + ": ");
      }
    }
  }

  public void setNewDTD(String value) {
    newDTD = value;
  }


  public String getValueElement(Element elem) {
    return (elem.getFirstChild() == null) ? "" : elem.getFirstChild().getNodeValue();
  }

  public String getFirstValueTag(Element rootElem, String tag) {
    if (rootElem == null) return "";
    NodeList nl = rootElem.getElementsByTagName(tag);
    if (nl == null) return "";
    Node nod = nl.item(0);
    if (nod == null) return "";
    return (nod.getFirstChild() == null) ? "" : nod.getFirstChild().getNodeValue();
  }


  public Element getFirstElement(String tag, String value) {
    return getFirstElement(getElementRoot(), tag, value);
  }

  public Element getFirstElement(Element raiz, String tag, String value) {
    NodeList nl = raiz.getElementsByTagName(tag);
    int n = nl.getLength();

    for (int i = 0; i < n; i++)
    {
      Node nod = nl.item(i);
      String nodTag = nod.getNodeName();
      String nodvalue = (nod.getFirstChild() == null) ? "" : nod.getFirstChild().getNodeValue();

      if (nvl(tag).equals(nvl(nodTag)) && nvl(value).equals(nvl(nodvalue)))
      {
        return (Element) nod.getParentNode();
      }
    }
    return null;
  }

  public Element[] getElements(Element raiz, String tag, String value) {
    NodeList nl = raiz.getElementsByTagName(tag);
    int n = nl.getLength();
    Vector v = new Vector();

    for (int i = 0; i < n; i++)
    {
      Node nod = nl.item(i);
      String nodTag = nod.getNodeName();
      String nodvalue = (nod.getFirstChild() == null) ? "" : nod.getFirstChild().getNodeValue();

      if (nvl(tag).equals(nvl(nodTag)) && nvl(value).equals(nvl(nodvalue)))
      {
        v.addElement(nod.getParentNode());
      }
    }

    n = v.size();
    Element[] aElems = new Element[n];
    for (int i = 0; i < n; i++)
    {
      aElems[i] = (Element) v.elementAt(i);
    }
    return aElems;
  }


  public String getFirstValueTag(String tag) {
    NodeList nl = doc.getElementsByTagName(tag);
    Node nod = nl.item(0);
    if (nod != null)
      return (nod.getFirstChild() == null) ? "" : nod.getFirstChild().getNodeValue();
    else
      return "";
  }

  public String getFirstValueTag(String tag, String value, String tag2) {
    Element elem = getFirstElement(tag, value);
    String res = getFirstValueTag(elem, tag2);
/*    
    if (res == null)
      res = "";
    else if (res.toLowerCase().trim().equals("undefined"))
      res = "";
*/      
    return res;
  }

  public Element getFirstElement(Element raiz, String tag) {
    if (raiz == null) return null;
    NodeList nl = raiz.getElementsByTagName(tag);
    if (nl == null) return null;
    Node nod = nl.item(0);
    if (nod == null) return null;
    return (Element) nod;
  }

  public Element getFirstElement(String tag) {
    NodeList nl = doc.getElementsByTagName(tag);
    if (nl == null) return null;
    Node nod = nl.item(0);
    if (nod == null) return null;
    return (Element) nod;
  }

  public Element[] getElements(Element raiz, String tag) {
    NodeList nl = raiz.getElementsByTagName(tag);
    int n = nl.getLength();
    Element[] aElems = new Element[n];
    for (int i = 0; i < n; i++)
      aElems[i] = (Element) nl.item(i);

    return aElems;
  }

  public Element getElementRoot() {
    return doc.getDocumentElement();
  }

  public Element[] getElements() {
    return getElements("*");
  }
  
  public Element[] getElements(String tag) {
    NodeList nl = doc.getElementsByTagName(tag);
    int n = nl.getLength();
    Element[] aElems = new Element[n];
    for (int i = 0; i < n; i++)
      aElems[i] = (Element) nl.item(i);

    return aElems;
  }


  public Element[] getElementsSons() {
    return getElementsSons(getElementRoot());
  }


  /**
   * Sólo primer nivel
   */
  public Element[] getElementsSons(Element raiz) {
    NodeList nl = raiz.getElementsByTagName("*");
    int n = nl.getLength();
    Vector v = new Vector();
    for (int i = 0; i < n; i++)
    {
     Node parent = nl.item(i).getParentNode();
     Node nodoRaiz = (Node) raiz;
     if (parent.equals(raiz))
       v.addElement(nl.item(i));
    }

    n = v.size();
    Element[] aElems = new Element[n];
    for (int i = 0; i < n; i++)
    {
      aElems[i] = (Element) v.elementAt(i);
    }

    return aElems;
  }

  public String getLabel(Element elem) {
    return elem.getNodeName();
  }

  public void addAttribute(Element nod, String nameAttribute, String valueAttribute) {
    nod.setAttribute(nameAttribute, valueAttribute);
  }

  public String getAttribute(Element nod, String nameAttribute) {
    return nod.getAttribute(nameAttribute);
  }


  public String[] getValuesTag(Element raiz, String tag) {
    NodeList nl = raiz.getElementsByTagName(tag);
    int n = nl.getLength();
    String[] values = new String[n];

    for (int i = 0; i < n; i++)
    {
      Node nod = nl.item(i);
      values[i] = (nod.getFirstChild() == null) ? "" : nod.getFirstChild().getNodeValue();
    }
    return values;
  }

  public String[] getValuesTag(String tag) {
    NodeList nl = doc.getElementsByTagName(tag);
    int n = nl.getLength();
    String[] values = new String[n];

    for (int i = 0; i < n; i++) {
      Node nod = nl.item(i);
      values[i] = (nod.getFirstChild() == null) ? "" : nod.getFirstChild().getNodeValue();
    }
    return values;
  }

  /**
  * Accept * as tagParent wildcard
  */
  public String getValueTagBySibling(String tag, String tagParent, String tagSibling, String valueTagSibling) {
    String valueTag = "";
    NodeList nl = doc.getElementsByTagName(tagSibling);
    int n = nl.getLength();
    for (int i = 0; i < n; i++) {
      Node nod = nl.item(i);
      String value = nod.getFirstChild().getNodeValue();
      if (value.equals(valueTagSibling)) {
        Node np = nod.getParentNode();
        if (np.getNodeName().equals(tagParent)) {
          for(Node child = nod.getNextSibling();child != null; child = child.getNextSibling()) {
            if (child.getNodeName().equals(tag)) {
                valueTag = (child.getFirstChild() == null) ? "" : child.getFirstChild().getNodeValue();
            }
          }
        }
      }
    }
    return valueTag;
  }

  public String getValueTagBySibling(String tag, String tagSibling, String valueTagSibling) {
    String valueTag = "";
    NodeList nl = doc.getElementsByTagName(tagSibling);
    int n = nl.getLength();
    for (int i = 0; i < n; i++) {
      Node nod = nl.item(i);
      String value = nod.getFirstChild().getNodeValue();
      if (value.equals(valueTagSibling)) {
        for(Node child = nod.getNextSibling();child != null; child = child.getNextSibling()) {
          if (child.getNodeName().equals(tag))
              valueTag = (child.getFirstChild() == null) ? "" : child.getFirstChild().getNodeValue();
        }
      }
    }
    return valueTag;
  }

  public String[] getValueTagBySibling(String tag, String tagSibling) {
    NodeList nl = doc.getElementsByTagName(tagSibling);
    int n = nl.getLength();
    int j = 0;
    String[] valueTag = new String[n];
    for (int i = 0; i < n; i++)
    {
      Node nod = nl.item(i);
      String value = nod.getFirstChild().getNodeValue();
      for(Node child = nod.getNextSibling();child != null; child = child.getNextSibling()) {
        if (child.getNodeName().equals(tag)) {
          valueTag[j] = (nod.getFirstChild() == null) ? "" : nod.getFirstChild().getNodeValue();
          j++;
        }
      }
    }
    int i = 0;
    for (int ii = 0; valueTag[ii]!=null ; ii++) {
      i = ii;
    }
    String[] valuees = new String[i+1];
    for (int jj = 0; jj<=i ; jj++) {
      valuees[jj] = valueTag[jj];
    }
    return valuees;
  }

  public Element addTagRoot(String nameTag) {
	  return addTagRoot(nameTag, "", false, false);
  }
  
  public Element addTagRoot(String nameTag, String valueTag, boolean isCDATA, boolean sonText) {
	Element res = null;
	Element tag = (Element) doc.createElement (nameTag);

	Text text;
	if (isCDATA) {
	  text = doc.createCDATASection(valueTag);
	} else {
	  text = doc.createTextNode(valueTag);
	}
	if (sonText) {
	  tag.appendChild(text);
	}
	 
    res = (Element) doc.appendChild(tag);
	return tag;
  }

  public Element addTag(String tagParent, String nameTag, String valueTag) {
    return addTag(tagParent, nameTag, valueTag, false, true);
  }

  public Element addTag(String tagParent, String nameTag, String valueTag, boolean isCDATA) {
    return addTag(tagParent, nameTag, valueTag, isCDATA, true);
  }

  public Element addTag(String tagParent, String nameTag, String valueTag, boolean isCDATA, boolean sonText) {
	Element res = null;
    Document newDoc = builder.newDocument();
    Element tag = (Element) newDoc.createElement (nameTag);
    
	Text text;
    if (isCDATA) {
      text = newDoc.createCDATASection(valueTag);
    } else {
      text = newDoc.createTextNode(valueTag);
    }
    if (sonText) {
      tag.appendChild(text);
    }

    newDoc.appendChild(tag);
    Node nodeParent;

	NodeList nl = doc.getElementsByTagName(tagParent);
	int n = nl.getLength();
	nodeParent = nl.item(n-1);
	Node node = doc.importNode(newDoc.getDocumentElement().cloneNode(true), true);
	res = (Element) nodeParent.appendChild(node);

    return res;
  }


  public Element addTag(Element parentNode, String nameTag) {
	  return addTag(parentNode, nameTag, "", false, true);
  }
  
  public Element addTag(Element parentNode, String nameTag, String valueTag) {
	  return addTag(parentNode, nameTag, valueTag, false, true);
  }
  
  
  public Element addTag(Element parentNode, String nameTag, String valueTag, boolean isCDATA, boolean sonText) {
	    Element res = null;
	    try {
			Document newDoc = builder.newDocument();
			Element tag = (Element) newDoc.createElement (nameTag);
			
			Text text;
			if (isCDATA) {
			  text = newDoc.createCDATASection(valueTag);
			} else {
			  text = newDoc.createTextNode(valueTag);
			}  
			if (sonText) {
			  tag.appendChild(text);
			} 

			newDoc.appendChild(tag);

			Node node = doc.importNode(newDoc.getDocumentElement().cloneNode(true), true);
			res = (Element) parentNode.appendChild(node);
			
		} catch (Exception e) {
			Util.rethrow(e);
		}
		return res;
  }
  
  
  public void cloneTag(String nameTag, String tagDestination, boolean sons) {
    NodeList nl = doc.getElementsByTagName(nameTag);
    Node node = nl.item(0);

    NodeList nl2 = doc.getElementsByTagName(tagDestination);
    int n = nl2.getLength();
    for (int i = 0; i < n; i++)
    {
      Node nodeParent = nl2.item(i);
      nodeParent.appendChild(node.cloneNode(sons));
    }
  }

  public void deleteTag(String tagParent, String tag) {
    NodeList nl = doc.getElementsByTagName(tagParent);
    int n = nl.getLength();
    for (int i = 0; i < n; i++)
    {
      Node nod = nl.item(i);
      NodeList nl2 = nod.getChildNodes();
      for (int j = 0; j < nl2.getLength(); j++)
      {
        Node nodson = nl2.item(j);
        if (nodson.getNodeName().equals(tag))
        {
          nod.removeChild(nodson);
        }
      }
    }
  }

  public void deleteTextTag(String tagParent, String tag) {
    NodeList nl = doc.getElementsByTagName(tagParent);
    int n = nl.getLength();
    for (int i = 0; i < n; i++)
    {
      Node nod = nl.item(i);
      NodeList nl2 = nod.getChildNodes();
      for (int j = 0; j < nl2.getLength(); j++)
      {
        Node nodson = nl2.item(j);
        if (nodson.getNodeName().equals(tag))
        {
          if (nodson.getFirstChild()!=null)
            nodson.removeChild(nodson.getFirstChild());
        }
      }
    }
  }

  /**
   * Sirve para borrar la referencia a la DTD de un XML
   */
  public void generateFileOtherDTD(String ficheroSalida, String otraDTD) {
    try
    {
      TransformerFactory transFactory = TransformerFactory.newInstance();
      Transformer transformer = transFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
      transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, otraDTD);
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");

      DOMSource source = new DOMSource(doc);
      File newXML = new File(ficheroSalida);
      FileOutputStream os = new FileOutputStream(newXML);
      StreamResult result = new StreamResult(os);
      transformer.transform(source, result);
      os.close();
    }
    catch (Exception e) {
    	Util.rethrow(e);
    }
  }

  public void setDTD(String url) {
    dtd = url;
  }


  public String fixSlash(String cadOrig) {
    StringBuffer cadDesti = new StringBuffer(255);

    for (int i = 0; i < cadOrig.length(); i++)
    {
      if (cadOrig.charAt(i) == '\\')
      {
        cadDesti.append('/');
      }
      else
      {
        cadDesti.append(cadOrig.charAt(i));
      }
    }
    return cadDesti.toString();
  }

  public String nvl(String s) {
    return s == null ? "" : s.trim();
  }

  public void moveElement(Element elem, int posDestination) {
    NodeList nl = doc.getElementsByTagName(elem.getParentNode().getNodeName());
    int n = nl.getLength();
    Node nod = nl.item(0);

    Node nodeClon = elem.cloneNode(true);
    doc.removeChild((Node)elem);

    NodeList nl2 = doc.getElementsByTagName(nod.getNodeName());
    Node nod2 = nl2.item(posDestination);
    doc.insertBefore(nodeClon,nod2);
  }

  public void changeValueElement(Element elem, String value, boolean isCDATA) {
    Text text = null;
    if (isCDATA)
      text = doc.createCDATASection(value);
    else
      text = doc.createTextNode(value);
    if (elem.getFirstChild()!=null)
      elem.removeChild(elem.getFirstChild());
    elem.appendChild(text);
  }

  public InputStream post(String url) throws Exception
  {
	InputStream res = null;
    try {
		URL XMLURL = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) XMLURL.openConnection();
		connection.setDefaultUseCaches(false);
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestMethod("POST");

		PrintWriter postWriter = new PrintWriter(connection.getOutputStream());
		postWriter.print(getXmlString());
		postWriter.flush();
		postWriter.close();
		res = connection.getInputStream();
	} catch (Exception e) {
		Util.rethrow(e);
	}
    return res;
  }

  public String getString(InputStream is) {
	String res = "";
    try {
		InputStreamReader isr = new InputStreamReader( is );
		BufferedReader br = new BufferedReader( isr );
		String inputLine;
		StringBuffer sb = new StringBuffer();
		while((inputLine = br.readLine()) != null) {
		    sb.append(inputLine);
		}
		br.close();
		res = sb.toString();
	} catch (IOException e) {
		Util.rethrow(e);
	}
    return res;
  }

  public static String postRequest(String request, String URLserver) {
    String responseXML = null;
    try {
		URL url = new URL(URLserver);
		URLConnection connection = url.openConnection();
		HttpsURLConnection httpConn = (HttpsURLConnection) connection;

//    String requestFull = URLEncoder.encode(request);
		String requestFull = request;

		byte[] requestXML = requestFull.getBytes();

		// Set the appropriate HTTP parameters.
		int len = requestXML.length;

		httpConn.setRequestProperty( "Content-Length", "" + len);
//    httpConn.setRequestProperty("Content-Type", "text/xml; charset=ISO-8859-1");
		httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		httpConn.setRequestMethod( "POST" );
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);

		// Send the String that was read into postByte.
		OutputStream out = httpConn.getOutputStream();
		out.write(requestXML);
		out.close();

		// Read the response and write it to standard out.

		InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());

		BufferedReader br = new BufferedReader(isr);
		String temp;
		String tempResponse = "";

		//Create a string using response from web services
		while ((temp = br.readLine()) != null)
		    tempResponse = tempResponse + temp;

		responseXML = tempResponse;
		br.close();
		isr.close();
	} catch (Exception e) {
		Util.rethrow(e);
	}
    return responseXML;
  }


  // http://arcweb.esri.com/arcwebonline/getting_started/postxmlrest.htm
  public static void main(String[] args) throws Exception {
    // Local file: "file:///C:/Windows/Escritorio/pedido.xml";

    XmlCtl xc = XmlCtl.getInstance();
//    xc.getDocument("http://www.bit-net.org/java/pedido.xml");
//    xc.getDocument("C:\Archivos de programa\Apache Tomcat 4.0\webapps\teleaula\buzoncontenidos\actividad\244.xml");
//    xc.getDocument("file:///C:/Tomcat 4.1.18/webapps/info33/WEB-INF/jbuilder/pruebaRCA.xml");

//    String tag = "USUARI";
//    String value = xc.getPrimervalueTag(tag);
//    System.out.println("value de " + tag + " = |" + value + "|");
//    System.out.println("-------------------------------------------------");


    System.setProperty("java.protocol.handler.pkgs","com.sun.net.ssl.internal.www.protocol");
    Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
  factory.setValidating(true);
    factory.setIgnoringElementContentWhitespace(true);
    DocumentBuilder builder = factory.newDocumentBuilder();

    String origenXML = "https://portal.scs.es:7778/gcx/AppJava/GCX";
   // String origenXML = "http://localhost:8080";

    // Monitoriza conexión con un GET mínimo (muestra la fecha del server)
//    Document doc = builder.parse(origenXML);
//    NodeList nl = doc.getChildNodes();
//    for (int i = 0; i < nl.getLength(); i++)
//      System.out.println(nl.item(i).getNodeName() + " = " + nl.item(i).getFirstChild().getNodeValue());
/*
   String xixa = "xml=" +
    "<?xml version='1.0' encoding='ISO-8859-1'?>" + "\r\n" +
    "<GCX_PETICIO>" + "\r\n" +
    "  <APLICACIO>RCAXML</APLICACIO>" + "\r\n" +
    "  <VERSIO>RCAXML_V01</VERSIO>" + "\r\n" +
    "  <OPCIO>CONSULTA_CIP</OPCIO>" + "\r\n" +
    "  <USUARI>MPUIGVERT</USUARI>" + "\r\n" +
    "  <CLAU>JAZ</CLAU>" + "\r\n" +
    "  <MISSATGE><![CDATA[" + "\r\n" +
    "     <CONSULTA_CIP>" + "\r\n" +
    "       <COR>05</COR>" + "\r\n" +
    "       <CUP>02038</CUP>" + "\r\n" +
    "       <CIP>MAVA1670621002</CIP>" + "\r\n" +
    "       <DREF>22/09/2003</DREF>" + "\r\n" +
    "     </CONSULTA_CIP>" + "\r\n" +
    "  ]]></MISSATGE>" + "\r\n" +
    "</GCX_PETICIO>" + "\r\n";
*/
   String xixa = "xml=" +
    "<?xml version='1.0' encoding='ISO-8859-1'?>" + "\r\n" +
    "<GCX_PETICIO>" + "\r\n" +
    "  <APLICACIO>RCAXML</APLICACIO>" + "\r\n" +
    "  <VERSIO>RCAXML_V01</VERSIO>" + "\r\n" +
    "  <OPCIO>CONSULTA_CANDIDATS</OPCIO>" + "\r\n" +
    "  <USUARI>MPUIGVERT</USUARI>" + "\r\n" +
    "  <CLAU>JAZ</CLAU>" + "\r\n" +
    "  <MISSATGE><![CDATA[" + "\r\n" +
    "     <CONSULTA_CANDIDATS>" + "\r\n" +
    "       <DOCUMENT>" + "\r\n" +
    "         <DFC_TDI>1</DFC_TDI>" + "\r\n" +
    "         <DFC_NDID>37747601</DFC_NDID>" + "\r\n" +
    "       </DOCUMENT>" + "\r\n" +
    "     </CONSULTA_CANDIDATS>" + "\r\n" +
    "  ]]></MISSATGE>" + "\r\n" +
    "</GCX_PETICIO>";



  System.out.println(xixa);
 String respuesta = postRequest(xixa, origenXML);
  System.out.println(respuesta);
/*
  xc.getDocumentString(respuesta);
  String DFC_NTE_1 = xc.getPrimervalueTag("DFC_TFX");
  System.out.println("DFC_NTE_1 = " + DFC_NTE_1);

  String DFC_DCP = xc.getPrimervalueTag("DFC_DCP");
  System.out.println("DFC_DCP = " + DFC_DCP);

    xc.agregarTagRaiz("PETIPETI");
    xc.agregarTag("PETIPETI", "USUARI", "PETETE");
    xc.agregarTag("PETIPETI", "USUARI2", "PETETO", true);

    System.out.println(xc.getXML());

    InputStream respuesta = xc.post("http://xxx.xx.com/zzz");
    XmlCtl xcResp = new XmlCtl();
    System.out.println(xc.getString(respuesta));
    xcResp.getDocument(respuesta);
    System.out.println(xcResp.getXML());



          String xixa = "xml=" +
          "<?xml version='1.0' encoding='ISO-8859-1'?>" + "\r\n" +
          "<GCX_PETICIO>" + "\r\n" +
          "  <APLICACIO>RCAXML</APLICACIO>" + "\r\n" +
          "  <VERSIO>RCAXML_V01</VERSIO>" + "\r\n" +
          "  <OPCIO>CONSULTA_CIP</OPCIO>" + "\r\n" +
          "  <USUARI>GALO</USUARI>" + "\r\n" +
          "  <CLAU>GALO1</CLAU>" + "\r\n" +
          "  <MISSATGE><![CDATA[" + "\r\n" +
          "     <CONSULTA_CIP>" + "\r\n" +
          "       <COR>05</COR>" + "\r\n" +
          "       <CUP>02038</CUP>" + "\r\n" +
          "       <CIP>ESVI1710806004</CIP>" + "\r\n" +
          "       <DREF>22/09/2003</DREF>" + "\r\n" +
          "     </CONSULTA_CIP>" + "\r\n" +
          "  ]]></MISSATGE>" + "\r\n" +
          "</GCX_PETICIO>" + "\r\n";


          String xixa = "xml=" +
          "<?xml version='1.0' encoding='ISO-8859-1'?>" + "\r\n" +
          "<GCX_PETICIO>" + "\r\n" +
          "  <APLICACIO>RCAXML</APLICACIO>" + "\r\n" +
          "  <VERSIO>RCAXML_V01</VERSIO>" + "\r\n" +
          "  <OPCIO>CONSULTA_CANDIDATS</OPCIO>" + "\r\n" +
          "  <USUARI>GALO</USUARI>" + "\r\n" +
          "  <CLAU>GALO1</CLAU>" + "\r\n" +
          "  <MISSATGE><![CDATA[" + "\r\n" +
          "     <CONSULTA_CANDIDATS>" + "\r\n" +
          "       <PERSONALS>" + "\r\n" +
          "         <DFC_PCG>PASCUAL</DFC_PCG>" + "\r\n" +
          "         <DFC_SCG>ESBELLA</DFC_SCG>" + "\r\n" +
          "         <DFC_DNA>13/06/1939</DFC_DNA>" + "\r\n" +
          "         <DFC_SEXE>0</DFC_SEXE>" + "\r\n" +
          "       </PERSONALS>" + "\r\n" +
          "     </CONSULTA_CANDIDATS>" + "\r\n" +
          "  ]]></MISSATGE>" + "\r\n" +
          "</GCX_PETICIO>" + "\r\n";


 String xixa = "xml=<?xml version='1.0' encoding='ISO-8859-1'?>"+
"<GCX_PETICIO>"+
"  <APLICACIO>RCAXML</APLICACIO>"+
"  <VERSIO>RCAXML_V01</VERSIO>"+
"  <OPCIO>CONSULTA_CANDIDATS</OPCIO>"+
"  <USUARI>GALO</USUARI>"+
"  <CLAU>GALO1</CLAU>"+
"  <MISSATGE><![CDATA["+
"     <CONSULTA_CANDIDATS>"+
"       <PERSONALS>"+
"         <DFC_NPE>kk</DFC_NPE>"+
"         <DFC_PCG>LOPEZ</DFC_PCG>"+
"         <DFC_SCG>MARCO</DFC_SCG>"+
"         <DFC_DNA>06/07/1938</DFC_DNA>"+
"         <DFC_SEXE>0</DFC_SEXE>"+
"       </PERSONALS>"+
"     </CONSULTA_CANDIDATS>"+
"  ]]></MISSATGE>"+
"</GCX_PETICIO>";

           System.out.println(xixa);
           String respuesta = XmlCtl.postRequest(xixa, origenXML);
           System.out.println(respuesta);

*/
  }
}
