package com.yelinaung.karrency.app.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Ye Lin Aung on 14/11/27.
 */
public class ConnManager {
  private Context mContext;

  public ConnManager(Context context) {
    this.mContext = context;
  }

  public boolean isConnected() {
    ConnectivityManager connectivityManager =
        (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
  }
}
