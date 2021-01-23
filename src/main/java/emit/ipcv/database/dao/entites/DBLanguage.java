/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emit.ipcv.database.dao.entites;

import java.util.Locale;

/**
 *
 * @author rinelfi
 */
public class DBLanguage {
  private int id;
  private String code, country, variant;
  
  public DBLanguage() {
    Locale local = Locale.getDefault();
    this.code = local.getLanguage();
    this.country = local.getCountry();
    this.variant = local.getVariant();
  }
  
  public DBLanguage(String code, String country, String variant) {
    this.code = code;
    this.country = country;
    this.variant = variant;
  }

  public int getId() {
    return id;
  }

  public DBLanguage setId(int id) {
    this.id = id;
    return this;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getCountry() {
    return country;
  }

  public void setPays(String pays) {
    this.country = pays;
  }

  public String getVariant() {
    return variant;
  }

  public void setVariant(String variant) {
    this.variant = variant;
  }
  
  @Override
  public String toString() {
    return this.code + "_" + this.getCountry() + (!getVariant().equals("") ? "(" + getVariant() + ")" : "");
  }
}
