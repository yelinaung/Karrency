package com.yelinaung.karrency.app.async;

import com.yelinaung.karrency.app.model.Currency;
import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by Ye Lin Aung on 14/11/23.
 */
public interface CurrencyService {
  @GET("/latest")
  void getLatestCurrencies(Callback<Currency> currency);
}
