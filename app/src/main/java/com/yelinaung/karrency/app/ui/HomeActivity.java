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

package com.yelinaung.karrency.app.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.analytics.tracking.android.EasyTracker;
import com.squareup.okhttp.OkHttpClient;
import com.yelinaung.karrency.app.BuildConfig;
import com.yelinaung.karrency.app.R;
import com.yelinaung.karrency.app.async.CurrencyService;
import com.yelinaung.karrency.app.model.Currency;
import com.yelinaung.karrency.app.util.ConnManager;
import com.yelinaung.karrency.app.util.SharePrefUtils;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class HomeActivity extends ActionBarActivity {

  @InjectView(R.id.toolbar) Toolbar mToolbar;
  @InjectView(R.id.exchange_rate_swipe_refresh) SwipeRefreshLayout exchangeSRL;
  @InjectView(R.id.currencies_wrapper) LinearLayout currenciesWrapper;

  public static final String BASE_URL = "http://forex.cbm.gov.mm/api";

  private OkHttpClient okHttpClient = new OkHttpClient();
  // private Realm realm;
  private LayoutInflater inflater;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //Fabric.with(this, new Crashlytics());

    setContentView(R.layout.activity_home);
    ButterKnife.inject(this);
    this.setSupportActionBar(mToolbar);

    exchangeSRL.setColorSchemeColors(R.color.theme_primary);

    exchangeSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        syncCurrencies();
      }
    });

    inflater = this.getLayoutInflater();
  }

  @Override protected void onResume() {
    if (SharePrefUtils.getInstance(HomeActivity.this).isFirstTime()) {
      syncCurrencies();
      SharePrefUtils.getInstance(HomeActivity.this).noMoreFirstTime();
    } else {
      syncCurrencies();
    }
    super.onResume();
  }

  @Override public void onStart() {
    super.onStart();
    EasyTracker.getInstance(this).activityStart(this);  // Add this method.
  }

  @Override public void onStop() {
    super.onStop();
    EasyTracker.getInstance(this).activityStop(this);  // Add this method.
  }

  private void syncCurrencies() {
    if (new ConnManager(HomeActivity.this).isConnected()) {
      RestAdapter restAdapter;
      if (BuildConfig.DEBUG) {
        restAdapter = new RestAdapter.Builder().setClient(new OkClient(okHttpClient))
            .setEndpoint(BASE_URL)
            .setLogLevel(RestAdapter.LogLevel.BASIC)
            .build();
      } else {
        restAdapter = new RestAdapter.Builder().setClient(new OkClient(okHttpClient))
            .setEndpoint(BASE_URL)
            .build();
      }

      CurrencyService currencyService = restAdapter.create(CurrencyService.class);
      currencyService.getLatestCurrencies(new Callback<Currency>() {
        @Override public void success(Currency currency, Response response) {
          //Date time = new Date((long) (Integer.valueOf(currency.getTimestamp()) * 1000));
          for (int i = 0; i < currency.getRates().getTotal(); i++) {

            //realm.beginTransaction();
            //Currency c = new Currency();
            //c.setRates(currency.getRates());
            //realm.commitTransaction();

            final LinearLayout baseLayout =
                (LinearLayout) inflater.inflate(R.layout.currency_row, null, false);
            TextView currencyName = (TextView) baseLayout.findViewById(R.id.currency_name);
            TextView currencyValue = (TextView) baseLayout.findViewById(R.id.currency_value);
            TextView currencyLongName = (TextView) baseLayout.findViewById(R.id.currency_long_name);

            currencyName.setText(currency.getRates().getAllCurrenciesNames().get(i));
            currencyValue.setText(currency.getRates().getAll().get(i));
            currencyLongName.setText(currency.getRates().getAllCurrenciesLongNames().get(i));

            currenciesWrapper.addView(baseLayout);
          }

          exchangeSRL.setRefreshing(false);
        }

        @Override public void failure(RetrofitError error) {
          error.printStackTrace();
        }
      });
    } else {
      Toast.makeText(HomeActivity.this, getString(R.string.no_connection), Toast.LENGTH_SHORT)
          .show();
    }
  }
}