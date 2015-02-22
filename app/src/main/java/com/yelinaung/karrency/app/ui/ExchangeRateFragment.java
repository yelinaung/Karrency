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
import com.yelinaung.karrency.app.R;
import com.yelinaung.karrency.app.async.CurrencyService;
import com.yelinaung.karrency.app.model.Currency;
import com.yelinaung.karrency.app.util.ConnManager;
import com.yelinaung.karrency.app.util.SharePrefUtils;
import java.util.Date;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

@SuppressWarnings("ConstantConditions")
public class ExchangeRateFragment extends BaseFragment {

  public static final String BASE_URL = "http://forex.cbm.gov.mm/api";

  @InjectView(R.id.exchange_rate_swipe_refresh) SwipeRefreshLayout exchangeSRL;
  @InjectView(R.id.currencies_wrapper) LinearLayout currenciesWrapper;

  private OkHttpClient okHttpClient = new OkHttpClient();
  private Context mContext;
  private View rootView;

  public ExchangeRateFragment() {
    // Required empty public constructor
  }

  public static ExchangeRateFragment newInstance() {
    return new ExchangeRateFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(final LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    mContext = getActivity().getApplicationContext();
    rootView = inflater.inflate(R.layout.fragment_exchange_rate, container, false);
    assert rootView != null;
    ButterKnife.inject(this, rootView);

    exchangeSRL.setColorSchemeColors(R.color.theme_primary);

    exchangeSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        RestAdapter restAdapter = new RestAdapter.Builder().setClient(new OkClient(okHttpClient))
            .setEndpoint(BASE_URL)
            .setLogLevel(RestAdapter.LogLevel.BASIC)
            .build();

        CurrencyService currencyService = restAdapter.create(CurrencyService.class);
        currencyService.getLatestCurrencies(new Callback<Currency>() {
          @Override public void success(Currency currency, Response response) {
            Date time = new Date((long) (Integer.valueOf(currency.getTimestamp()) * 1000));
            for (int i = 0; i < currency.getRates().getTotal(); i++) {

              final LinearLayout baseLayout =
                  (LinearLayout) inflater.inflate(R.layout.currency_row, null, false);
              TextView currencyName = (TextView) baseLayout.findViewById(R.id.currency_name);
              TextView currencyValue = (TextView) baseLayout.findViewById(R.id.currency_value);
              TextView currencyLongName =
                  (TextView) baseLayout.findViewById(R.id.currency_long_name);

              currencyName.setText(currency.getRates().getAllCurrenciesNames().get(i));
              currencyValue.setText(currency.getRates().getAll().get(i));
              currencyLongName.setText(currency.getRates().getAllCurrenciesLongNames().get(i));

              currenciesWrapper.addView(baseLayout);
            }

            exchangeSRL.setRefreshing(false);
          }

          @Override public void failure(RetrofitError error) {

          }
        });
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

    ConnManager manager = new ConnManager(mContext);
    if (SharePrefUtils.getInstance(mContext).isFirstTime()) {
      if (manager.isConnected()) {
        SharePrefUtils.getInstance(mContext).noMoreFirstTime();
      } else {
      }
    } else {
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
      case R.id.action_sync:
        ConnManager manager = new ConnManager(mContext);
        if (manager.isConnected()) {
          // FIXME
          //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
          //  menuItem.setActionView(R.layout.pg);
          //  menuItem.expandActionView();
          //}
        } else {
          Toast.makeText(mContext, R.string.no_connection, Toast.LENGTH_SHORT).show();
        }
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
}
