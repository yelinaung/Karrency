package com.yelinaung.karrency.app.model;

/**
 * Created by Ye Lin Aung on 14/11/23.
 */
public class Currency {
  private String info;
  private String description;
  private String timestamp;
  private Rates rates;

  public String getInfo() {
    return info;
  }

  public void setInfo(String info) {
    this.info = info;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public Rates getRates() {
    return rates;
  }

  public void setRates(Rates rates) {
    this.rates = rates;
  }
}
