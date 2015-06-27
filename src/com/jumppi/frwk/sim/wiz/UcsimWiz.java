
package com.jumppi.frwk.sim.wiz;


public class UcsimWiz
{
  protected int idUcsim;
  protected String description;
  protected String uriTemplate;
  protected String httpMethod;
  protected String jsCode;


///////////////////////////////////////////////////////////
//  getters 
///////////////////////////////////////////////////////////

  public int getIdInt() {
    return getIdUcsim();
  }

    public boolean exists() {
        return getIdInt() != 0;
    }


  public int getIdUcsim() {
    return this.idUcsim;
  }

  public String getDescription() {
    return this.description;
  }

  public String getUriTemplate() {
    return this.uriTemplate;
  }

  public String getHttpMethod() {
    return this.httpMethod;
  }

  public String getJsCode() {
    return this.jsCode;
  }

///////////////////////////////////////////////////////////
//  setters 
///////////////////////////////////////////////////////////

  public void setIdInt(int value) {
     setIdUcsim(value);
  }

  public void setIdUcsim(int value) {
    this.idUcsim = value;
  }

  public void setDescription(String value) {
    this.description = value;
  }

  public void setUriTemplate(String value) {
    this.uriTemplate = value;
  }

  public void setHttpMethod(String value) {
    this.httpMethod = value;
  }

  public void setJsCode(String value) {
    this.jsCode = value;
  }
}
