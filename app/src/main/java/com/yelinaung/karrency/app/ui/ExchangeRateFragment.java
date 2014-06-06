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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.yelinaung.karrency.app.R;
import com.yelinaung.karrency.app.model.Exchange;
import com.yelinaung.karrency.app.util.SharePrefUtils;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("ConstantConditions")
public class ExchangeRateFragment extends BaseFragment {

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

  private Context mContext;
  private MenuItem menuItem;
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
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    mContext = getActivity().getApplicationContext();

    // Inflate the layout for this fragment
    rootView = inflater.inflate(R.layout.fragment_exchange_rate, container, false);
    assert rootView != null;

    // Inject the views here
    ButterKnife.inject(this, rootView);

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
        new GetData().execute();
        SharePrefUtils.getInstance(mContext).noMoreFirstTime();
      } else {
        Toast.makeText(mContext, R.string.no_connection, Toast.LENGTH_SHORT).show();
        USD.setText("-");
        SGD.setText("-");
        EURO.setText("-");
        MYR.setText("-");
        GBP.setText("-");
        THB.setText("-");
      }
    } else {
      USD.setText(SharePrefUtils.getInstance(mContext).getUSD());
      SGD.setText(SharePrefUtils.getInstance(mContext).getSGD());
      EURO.setText(SharePrefUtils.getInstance(mContext).getEUR());
      MYR.setText(SharePrefUtils.getInstance(mContext).getMYR());
      GBP.setText(SharePrefUtils.getInstance(mContext).getGBP());
      THB.setText(SharePrefUtils.getInstance(mContext).getTHB());
    }
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    // Inflate the menu; this adds items to the action bar if it is present.
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.home, menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    menuItem = item;
    switch (item.getItemId()) {
      case R.id.action_about:
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
        new AlertDialog.Builder(rootView.getRootView().getContext()).setTitle(R.string.about)
            .setMessage(new SpannableStringBuilder().append(
                Html.fromHtml(getString(R.string.about_body, versionName))))
            .show();
        return true;
      case R.id.action_sync:
        ConnManager manager = new ConnManager(mContext);
        if (manager.isConnected()) {
          // FIXME
          //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
          //  menuItem.setActionView(R.layout.pg);
          //  menuItem.expandActionView();
          //}
          new GetData().execute();
        } else {
          Toast.makeText(mContext, R.string.no_connection, Toast.LENGTH_SHORT).show();
        }
    }
    return super.onOptionsItemSelected(item);
  }

  private void showPg(ProgressBar... pg) {
    for (ProgressBar mPg : pg) {
      mPg.setVisibility(View.VISIBLE);
    }
  }

  private void hideTv(TextView... tv) {
    for (TextView mTv : tv) {
      mTv.setVisibility(View.GONE);
    }
  }

  private void hidePg(ProgressBar... pg) {
    for (ProgressBar mPg : pg) {
      mPg.setVisibility(View.GONE);
    }
  }

  private void showTv(TextView... tv) {
    for (TextView mTv : tv) {
      mTv.setVisibility(View.VISIBLE);
    }
  }

  private class GetData extends AsyncTask<Void, Void, Exchange> {
    Exchange ex = new Exchange();
    private String nowTime =
        new SimpleDateFormat("yyyy/LLL/dd - hh:mm a").format(Calendar.getInstance().getTime());

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

      // FIXME This is raising NPE when the app is launched for the first time
      //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      //  menuItem.collapseActionView();
      //  menuItem.setActionView(null);
      //}

      hidePg(usdProgress, sgdProgress, euroProgress, gbpProgress, myrProgress, thbProgress);
      showTv(USD, SGD, EURO, MYR, GBP, THB);

      if (ex != null) {
        USD.setText(ex.usd);
        SGD.setText(ex.sgd);
        EURO.setText(ex.eur);
        MYR.setText(ex.myr);
        GBP.setText(ex.gbp);
        THB.setText(ex.thb);

        SharePrefUtils.getInstance(mContext)
            .saveCurrencies(nowTime, ex.usd, ex.sgd, ex.eur, ex.myr, ex.gbp, ex.thb);
      } else {
        Toast.makeText(mContext, R.string.no_connection, Toast.LENGTH_SHORT).show();
      }
    }

    Exchange parse(String result) throws JSONException {
      Exchange ex = new Exchange();
      ex.info = new JSONObject(result).getString("info");
      ex.description = new JSONObject(result).getString("description");
      //ex.timestamp = new JSONObject(result).getInt("timestamp");
      ex.usd = new JSONObject(new JSONObject(result).getString("rates")).getString("USD");
      ex.sgd = new JSONObject(new JSONObject(result).getString("rates")).getString("SGD");
      ex.eur = new JSONObject(new JSONObject(result).getString("rates")).getString("EUR");
      ex.myr = new JSONObject(new JSONObject(result).getString("rates")).getString("MYR");
      ex.gbp = new JSONObject(new JSONObject(result).getString("rates")).getString("GBP");
      ex.thb = new JSONObject(new JSONObject(result).getString("rates")).getString("THB");
      return ex;
    }
  }

  public class ConnManager {
    private Context mContext;

    public ConnManager(Context context) {
      this.mContext = context;
    }

    public boolean isConnected() {
      ConnectivityManager connectivity =
          (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

      if (connectivity != null) {
        NetworkInfo[] info = connectivity.getAllNetworkInfo();
        if (info != null) {
          for (NetworkInfo anInfo : info) {
            if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
              return true;
            }
          }
        }
      }
      return false;
    }
  }
}
