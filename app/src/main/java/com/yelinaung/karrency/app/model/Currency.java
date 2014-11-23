package com.yelinaung.karrency.app.model;

/**
 * Created by Ye Lin Aung on 14/11/23.
 */
public class Currency {
  private String info;
  private String description;
  private String timestamp;
  private Rates rates;

  /**
   * @return The info
   */
  public String getInfo() {
    return info;
  }

  /**
   * @param info The info
   */
  public void setInfo(String info) {
    this.info = info;
  }

  /**
   * @return The description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description The description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * @return The timestamp
   */
  public String getTimestamp() {
    return timestamp;
  }

  /**
   * @param timestamp The timestamp
   */
  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  /**
   * @return The rates
   */
  public Rates getRates() {
    return rates;
  }

  /**
   * @param rates The rates
   */
  public void setRates(Rates rates) {
    this.rates = rates;
  }
}
