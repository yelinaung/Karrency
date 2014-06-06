package com.yelinaung.karrency.app.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import com.yelinaung.karrency.app.R;
import com.yelinaung.karrency.app.ui.HomeActivity;
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

      int appWidgetId = appWidgetIds[i];

      RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.exchange_rate_widget);

      SharePrefUtils sharePrefUtils = SharePrefUtils.getInstance(context);
      int idx = sharePrefUtils.getSavedValue();

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

      ExchangeRateWidget.updateAppWidget(context, appWidgetManager, appWidgetId);

      appWidgetManager.updateAppWidget(appWidgetId, views);

      updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
    }
  }

  @Override
  public void onEnabled(Context context) {
    // Enter relevant functionality for when the first widget is created
  }

  @Override
  public void onDisabled(Context context) {
    // Enter relevant functionality for when the last widget is disabled
  }

  static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

    // Construct the RemoteViews object
    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.exchange_rate_widget);

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views);
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    super.onReceive(context, intent);

    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.exchange_rate_widget);
    intent = new Intent(context, HomeActivity.class);
    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
    views.setOnClickPendingIntent(R.id.widget_configure, pendingIntent);
  }
}


