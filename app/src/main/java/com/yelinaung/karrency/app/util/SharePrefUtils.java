/*
 * Copyright 2014 Ye Lin Aung
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yelinaung.karrency.app.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Ye Lin Aung on 14/04/14.
 */
public class SharePrefUtils {
  private static SharePrefUtils pref;
  protected SharedPreferences mSharePreferences;
  protected SharedPreferences.Editor mEditor;

  public SharePrefUtils(Context context) {
    mSharePreferences = context.getSharedPreferences("CPref", 0);
    mEditor = mSharePreferences.edit();
  }

  public static SharePrefUtils getInstance(Context context) {
    if (pref == null) {
      pref = new SharePrefUtils(context);
    }
    return pref;
  }

  public void saveCurrencies(String time, String usd, String sgd, String euro, String myr, String gbp,
      String thb) {
    mEditor.putString("time", time)
        .putString("USD", usd)
        .putString("SGD", sgd)
        .putString("EURO", euro)
        .putString("MYR", myr)
        .putString("GBP", gbp)
        .putString("THB", thb)
        .commit();
  }

  public String getTime() {
    return mSharePreferences.getString("time", "11:59 PM - 05 May 2014");
  }

  public String getUSD() {
    return mSharePreferences.getString("USD", "900");
  }

  public String getSGD() {
    return mSharePreferences.getString("SGD", "700");
  }

  public String getEUR() {
    return mSharePreferences.getString("EURO", "900");
  }

  public String getMYR() {
    return mSharePreferences.getString("MYR", "290");
  }

  public String getGBP() {
    return mSharePreferences.getString("GBP", "1500");
  }

  public String getTHB() {
    return mSharePreferences.getString("THB", "30");
  }

  public boolean isFirstTime() {
    return mSharePreferences.getBoolean("firstTime", true);
  }

  public void noMoreFirstTime() {
    mEditor.putBoolean("firstTime", false).commit();
  }

  public void saveSelectedValue(int value) {
    mEditor.putInt("selectedValue", value).commit();
  }

  public int getSavedValue() {
    return mSharePreferences.getInt("selectedValue", 0);
  }
}
