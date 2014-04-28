package com.yelinaung.karrency.app.ui.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;
import com.yelinaung.karrency.app.R;
import com.yelinaung.karrency.app.util.SharePrefUtils;

/**
 * Implementation of App Widget functionality.
 */
public class ExchangeRateWidget extends AppWidgetProvider {

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    //There may be multiple widgets active, so update all of them
    final int N = appWidgetIds.length;
    for (int i = 0; i < N; i++) {
      updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
    }
    Log.i("onUpdate", "I cam called");
  }

  @Override
  public void onEnabled(Context context) {
    // Enter relevant functionality for when the first widget is created
    Log.i("onEnabled", "I cam called");
  }

  @Override
  public void onDisabled(Context context) {
    // Enter relevant functionality for when the last widget is disabled
    Log.i("onDisabled", "I cam called");
  }

  static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

    // Construct the RemoteViews object
    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.exchange_rate_widget);

    SharePrefUtils sharePrefUtils = SharePrefUtils.getInstance(context);
    int savedCurrency = sharePrefUtils.getSavedValue();

    Log.i("saved value", savedCurrency + "");

    switch (savedCurrency) {
      case 0:
        setText(views, "USD", sharePrefUtils.getUSD());
      case 1:
        setText(views, "SGD", sharePrefUtils.getSGD());
      case 2:
        setText(views, "EUR", sharePrefUtils.getEUR());
      case 3:
        setText(views, "MYR", sharePrefUtils.getMYR());
      case 4:
        setText(views, "GBP", sharePrefUtils.getGBP());
      case 5:
        setText(views, "THB", sharePrefUtils.getTHB());
      default:
        setText(views, "USD", sharePrefUtils.getUSD());
    }

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views);
  }

  private static void setText(RemoteViews views, String label, String value) {
    views.setTextViewText(R.id.label_currency, label);
    views.setTextViewText(R.id.currency_value, value + " Kyats");
  }
}


