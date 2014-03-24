/*
 * Copyright (c) 2014. Ye Lin Aung
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.yelinaung.myancur.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class Home extends ActionBarActivity {

  public static final String BASE_URL = "http://forex.cbm.gov.mm/api";

  @InjectView(R.id.usd) TextView USD;
  @InjectView(R.id.sgd) TextView SGD;
  @InjectView(R.id.euro) TextView EURO;
  @InjectView(R.id.myr) TextView MYR;
  @InjectView(R.id.gbp) TextView GBP;
  @InjectView(R.id.thb) TextView THB;

  @InjectView(R.id.usd_progress) ProgressBar usdProgress;
  @InjectView(R.id.sgd_progress) ProgressBar sgdProgress;
  @InjectView(R.id.euro_progress) ProgressBar euroProgress;
  @InjectView(R.id.myr_progress) ProgressBar myrProgress;
  @InjectView(R.id.gbp_progress) ProgressBar gbpProgress;
  @InjectView(R.id.thb_progress) ProgressBar thbProgress;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);
    ButterKnife.inject(this);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.home, menu);
    return true;
  }

  @Override protected void onResume() {
    super.onResume();
    if (SharePref.getInstance(Home.this).isFirstTime()) {
      new GetData().execute();
      SharePref.getInstance(Home.this).noMoreFirstTime();
    } else {
      USD.setText(SharePref.getInstance(Home.this).getUSD());
      SGD.setText(SharePref.getInstance(Home.this).getSGD());
      EURO.setText(SharePref.getInstance(Home.this).getEURO());
      MYR.setText(SharePref.getInstance(Home.this).getMYR());
      GBP.setText(SharePref.getInstance(Home.this).getGBP());
      THB.setText(SharePref.getInstance(Home.this).getTHB());
    }
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_about) {
      PackageManager pm = getPackageManager();
      String packageName = getPackageName();
      String versionName;
      try {
        assert pm != null;
        PackageInfo info = pm.getPackageInfo(packageName, 0);
        versionName = info.versionName;
      } catch (PackageManager.NameNotFoundException e) {
        versionName = "";
      }

      new AlertDialog.Builder(Home.this).setTitle(R.string.about)
          .setMessage(new SpannableStringBuilder().append(
              Html.fromHtml(getString(R.string.about_body, versionName))))
          .show();
      return true;
    } else if (id == R.id.action_sync) {
      new GetData().execute();
    }
    return super.onOptionsItemSelected(item);
  }

  public static class SharePref {
    private static SharePref pref;
    protected SharedPreferences mSharePreferences;
    protected SharedPreferences.Editor mEditor;

    public SharePref(Context context) {
      mSharePreferences = context.getSharedPreferences("CPref", 0);
      mEditor = mSharePreferences.edit();
    }

    public static SharePref getInstance(Context context) {
      if (pref == null) {
        pref = new SharePref(context);
      }
      return pref;
    }

    void saveCurrencies(String usd, String sgd, String euro, String myr, String gbp, String thb) {
      mEditor.putString("USD", usd)
          .putString("SGD", sgd)
          .putString("EURO", euro)
          .putString("MYR", myr)
          .putString("GBP", gbp)
          .putString("THB", thb)
          .commit();
    }

    String getUSD() {
      return mSharePreferences.getString("USD", "900");
    }

    String getSGD() {
      return mSharePreferences.getString("SGD", "700");
    }

    String getEURO() {
      return mSharePreferences.getString("EURO", "900");
    }

    String getMYR() {
      return mSharePreferences.getString("MYR", "290");
    }

    String getGBP() {
      return mSharePreferences.getString("GBP", "1500");
    }

    String getTHB() {
      return mSharePreferences.getString("THB", "30");
    }

    boolean isFirstTime() {
      return mSharePreferences.getBoolean("firstTime", true);
    }

    void noMoreFirstTime() {
      mEditor.putBoolean("firstTime", false).commit();
    }
  }

  private class GetData extends AsyncTask<Void, Void, Exchange> {

    Exchange ex;

    @Override protected void onPreExecute() {
      super.onPreExecute();
      showPg(usdProgress, sgdProgress, euroProgress, gbpProgress, myrProgress, thbProgress);
      hideTv(USD, SGD, EURO, MYR, GBP, THB);
    }

    @Override protected Exchange doInBackground(Void... urls) {
      HttpClient defaultHttpClient = new DefaultHttpClient();
      HttpGet httpGet = new HttpGet(BASE_URL + "/latest");
      try {
        HttpResponse response = defaultHttpClient.execute(httpGet);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
          ex = parse(EntityUtils.toString(response.getEntity()));
        }
      } catch (ClientProtocolException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (JSONException e) {
        e.printStackTrace();
      }
      return ex;
    }

    @Override protected void onPostExecute(Exchange ex) {
      super.onPostExecute(ex);

      hidePg(usdProgress, sgdProgress, euroProgress, gbpProgress, myrProgress, thbProgress);
      showTv(USD, SGD, EURO, MYR, GBP, THB);

      USD.setText(ex.getUsd());
      SGD.setText(ex.getSgd());
      EURO.setText(ex.getEuro());
      MYR.setText(ex.getMyr());
      GBP.setText(ex.getGbp());
      THB.setText(ex.getThb());

      SharePref.getInstance(Home.this)
          .saveCurrencies(ex.getUsd(), ex.getSgd(), ex.getEuro(), ex.getMyr(), ex.getGbp(),
              ex.getThb());
    }

    Exchange parse(String result) throws JSONException {
      Exchange ex = new Exchange();
      ex.info = new JSONObject(result).getString("info");
      ex.description = new JSONObject(result).getString("description");
      ex.timestamp = new JSONObject(result).getInt("timestamp");
      ex.usd = new JSONObject(new JSONObject(result).getString("rates")).getString("USD");
      ex.sgd = new JSONObject(new JSONObject(result).getString("rates")).getString("SGD");
      ex.euro = new JSONObject(new JSONObject(result).getString("rates")).getString("EUR");
      ex.myr = new JSONObject(new JSONObject(result).getString("rates")).getString("MYR");
      ex.gbp = new JSONObject(new JSONObject(result).getString("rates")).getString("GBP");
      ex.thb = new JSONObject(new JSONObject(result).getString("rates")).getString("THB");
      return ex;
    }
  }

  public class Exchange {
    int timestamp;
    String usd, sgd, euro, myr, gbp, thb, description, info;

    public String getEuro() {
      return euro;
    }

    public String getMyr() {
      return myr;
    }

    public String getGbp() {
      return gbp;
    }

    public String getThb() {
      return thb;
    }

    public String getUsd() {
      return usd;
    }

    public String getSgd() {
      return sgd;
    }
  }

  void showPg(ProgressBar... pg) {
    for (ProgressBar mPg : pg) {
      mPg.setVisibility(View.VISIBLE);
    }
  }

  void hideTv(TextView... tv) {
    for(TextView mTv : tv) {
      mTv.setVisibility(View.GONE);
    }
  }

  void hidePg(ProgressBar... pg) {
    for (ProgressBar mPg : pg) {
      mPg.setVisibility(View.GONE);
    }
  }

  void showTv(TextView... tv) {
    for(TextView mTv : tv) {
      mTv.setVisibility(View.VISIBLE);
    }
  }
}
