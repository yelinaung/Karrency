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

package com.yelinaung.karrency.app.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.okhttp.OkHttpClient;
import com.yelinaung.karrency.app.BuildConfig;
import com.yelinaung.karrency.app.R;
import com.yelinaung.karrency.app.async.CurrencyService;
import com.yelinaung.karrency.app.model.Currency;
import com.yelinaung.karrency.app.util.ConnManager;
import com.yelinaung.karrency.app.util.SharePrefUtils;
import io.realm.Realm;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class ExchangeRateFragment extends BaseFragment {

  public static final String BASE_URL = "http://forex.cbm.gov.mm/api";

  @InjectView(R.id.exchange_rate_swipe_refresh) SwipeRefreshLayout exchangeSRL;
  @InjectView(R.id.currencies_wrapper) LinearLayout currenciesWrapper;

  private Context mContext;
  private View rootView;
  private LayoutInflater inflater;
  private OkHttpClient okHttpClient;
  private Realm realm;

  public ExchangeRateFragment() {
    // Required empty public constructor
  }

  public static ExchangeRateFragment newInstance() {
    return new ExchangeRateFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    okHttpClient = new OkHttpClient();
  }

  @Override
  public View onCreateView(final LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    this.inflater = inflater;

    mContext = getActivity().getApplicationContext();
    rootView = inflater.inflate(R.layout.fragment_exchange_rate, container, false);
    assert rootView != null;
    ButterKnife.inject(this, rootView);

    //realm = Realm.getInstance(mContext);

    exchangeSRL.setColorSchemeColors(R.color.theme_primary);

    exchangeSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        syncCurrencies();
      }
    });

    return rootView;
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
  }

  @Override public void onDetach() {
    super.onDetach();
  }

  @Override public void onResume() {
    super.onResume();

    if (SharePrefUtils.getInstance(mContext).isFirstTime()) {
      syncCurrencies();
      SharePrefUtils.getInstance(mContext).noMoreFirstTime();
    } else {
      syncCurrencies();
    }
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    // Inflate the menu; this adds items to the action bar if it is present.
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.home, menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_about:
        showAbout();
    }
    return super.onOptionsItemSelected(item);
  }

  private void showAbout() {
    PackageManager pm = mContext.getPackageManager();
    String packageName = mContext.getPackageName();
    String versionName;
    try {
      assert pm != null;
      PackageInfo info = pm.getPackageInfo(packageName, 0);
      versionName = info.versionName;
    } catch (PackageManager.NameNotFoundException e) {
      versionName = "";
    }
    AlertDialog.Builder b =
        new AlertDialog.Builder(rootView.getRootView().getContext()).setTitle(R.string.about)
            .setMessage(new SpannableStringBuilder().append(
                Html.fromHtml(getString(R.string.about_body, versionName))));
    b.create().show();
  }

  private void syncCurrencies() {
    if (new ConnManager(mContext).isConnected()) {

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
      Toast.makeText(mContext, getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
    }
  }
}
