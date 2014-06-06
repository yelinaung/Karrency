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

package com.yelinaung.karrency.app.ui.widget;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RemoteViews;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.yelinaung.karrency.app.R;
import com.yelinaung.karrency.app.ui.HomeActivity;
import com.yelinaung.karrency.app.util.SharePrefUtils;

/**
 * Created by Ye Lin Aung on 14/04/28.
 */
public class ConfigureCurrency extends Activity {

  int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

  SharePrefUtils sharePrefUtils;

  @InjectView(R.id.ok_btn) Button okayButton;
  @InjectView(R.id.currency_radio_group) RadioGroup currencyRadioGroup;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setResult(RESULT_CANCELED);
    setContentView(R.layout.choose_currency);
    ButterKnife.inject(this);
    sharePrefUtils = SharePrefUtils.getInstance(this);

    Intent intent = getIntent();
    Bundle extras = intent.getExtras();
    if (extras != null) {
      mAppWidgetId =
          extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    okayButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        final Context context = ConfigureCurrency.this;
        int id = currencyRadioGroup.getCheckedRadioButtonId();
        View radioButton = currencyRadioGroup.findViewById(id);
        int idx = currencyRadioGroup.indexOfChild(radioButton);

        sharePrefUtils.saveSelectedValue(idx);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        RemoteViews views =
            new RemoteViews(context.getPackageName(), R.layout.exchange_rate_widget);

        if (idx == 0) {
          views.setTextViewText(R.id.label_currency, "1 USD");
          views.setTextViewText(R.id.currency_value, sharePrefUtils.getUSD() + " MMK");
        } else if (idx == 1) {
          views.setTextViewText(R.id.label_currency, "1 SGD");
          views.setTextViewText(R.id.currency_value, sharePrefUtils.getSGD() + " MMK");
        } else if (idx == 2) {
          views.setTextViewText(R.id.label_currency, "1 EUR");
          views.setTextViewText(R.id.currency_value, sharePrefUtils.getEUR() + " MMK");
        } else if (idx == 3) {
          views.setTextViewText(R.id.label_currency, "1 MYR");
          views.setTextViewText(R.id.currency_value, sharePrefUtils.getMYR() + " MMK");
        } else if (idx == 4) {
          views.setTextViewText(R.id.label_currency, "1 GBP");
          views.setTextViewText(R.id.currency_value, sharePrefUtils.getGBP() + " MMK");
        } else if (idx == 5) {
          views.setTextViewText(R.id.label_currency, "1 THB");
          views.setTextViewText(R.id.currency_value, sharePrefUtils.getTHB() + " MMK");
        }

        Intent intent = new Intent(context, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_configure, pendingIntent);

        ExchangeRateWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

        appWidgetManager.updateAppWidget(mAppWidgetId, views);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
      }
    });

    // If they gave us an intent without the widget id, just bail.
    if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
      finish();
    }
  }

  private static void setText(RemoteViews views, String label, String value) {
    views.setTextViewText(R.id.label_currency, label);
    views.setTextViewText(R.id.currency_value, value + " Kyats");
  }
}
