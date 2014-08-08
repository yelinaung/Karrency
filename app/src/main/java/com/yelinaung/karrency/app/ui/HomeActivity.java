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

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.bugsnag.android.Bugsnag;
import com.crashlytics.android.Crashlytics;
import com.google.analytics.tracking.android.EasyTracker;
import com.yelinaung.karrency.app.R;
import com.yelinaung.karrency.app.ui.widget.SlidingTabLayout;
import com.yelinaung.karrency.app.util.SharePrefUtils;

public class HomeActivity extends FragmentActivity {

  @InjectView(R.id.pager) ViewPager mPager;
  @InjectView(R.id.last_sync_time) TextView lastSync;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Crashlytics.start(this);
    Bugsnag.register(this, "e8f35ea322d1f0fd3772434967d89c72");

    setContentView(R.layout.activity_home);

    ButterKnife.inject(this);

    final ActionBar mActionBar = getActionBar();
    assert mActionBar != null;

    SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
    int mPrimaryColor = getResources().getColor(R.color.accent_color);
    slidingTabLayout.setSelectedIndicatorColors(mPrimaryColor);
    slidingTabLayout.setDividerColors(mPrimaryColor);

    mPager.setAdapter(new SlidingTabAdapter(getSupportFragmentManager(), HomeActivity.this));

    firstTimeTask();
  }

  @Override
  public void onStart() {
    super.onStart();
    EasyTracker.getInstance(this).activityStart(this);  // Add this method.
  }

  @Override
  public void onStop() {
    super.onStop();
    EasyTracker.getInstance(this).activityStop(this);  // Add this method.
  }

  public class SlidingTabAdapter extends FragmentPagerAdapter {

    Context mContext;

    public SlidingTabAdapter(FragmentManager fm, Context context) {
      super(fm);
      this.mContext = context;
    }

    @Override public int getCount() {
      return 2;
    }

    @Override public Fragment getItem(int position) {
      Fragment f = null;
      switch (position) {
        case 0:
          f = ExchangeRateFragment.newInstance();
          break;
        case 1:
          f = CalculatorFragment.newInstance();
          break;
      }
      return f;
    }

    @Override public CharSequence getPageTitle(int position) {
      switch (position) {
        case 0:
          return mContext.getString(R.string.rate);
        case 1:
          return mContext.getString(R.string.calc);
      }
      return null;
    }
  }

  private void firstTimeTask() {
    if (SharePrefUtils.getInstance(getApplicationContext()).isFirstTime()) {
      lastSync.setVisibility(View.GONE);
    } else {
      String time = SharePrefUtils.getInstance(getApplicationContext()).getTime();
      SpannableStringBuilder lastSyncTime = new SpannableStringBuilder();
      lastSyncTime.append(Html.fromHtml(getString(R.string.sync_time, time)));
      lastSync.setText(lastSyncTime);
    }
  }
}