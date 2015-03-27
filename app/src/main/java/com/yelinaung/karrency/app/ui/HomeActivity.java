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

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.analytics.tracking.android.EasyTracker;
import com.yelinaung.karrency.app.R;
import com.yelinaung.karrency.app.ui.widget.SlidingTabLayout;

public class HomeActivity extends ActionBarActivity {

  @InjectView(R.id.pager) ViewPager mPager;
  @InjectView(R.id.toolbar) Toolbar mToolbar;
  @InjectView(R.id.header) View mHeaderView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //Fabric.with(this, new Crashlytics());

    setContentView(R.layout.activity_home);

    ButterKnife.inject(this);

    this.setSupportActionBar(mToolbar);

    SlidingTabAdapter slidingTabAdapter =
        new SlidingTabAdapter(getSupportFragmentManager(), HomeActivity.this);

    SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
    slidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
    slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.indicator_color));
    slidingTabLayout.setDistributeEvenly(true);

    mPager.setAdapter(slidingTabAdapter);
    slidingTabLayout.setViewPager(mPager);
  }

  @Override public void onStart() {
    super.onStart();
    EasyTracker.getInstance(this).activityStart(this);  // Add this method.
  }

  @Override public void onStop() {
    super.onStop();
    EasyTracker.getInstance(this).activityStop(this);  // Add this method.
  }

  public class SlidingTabAdapter extends FragmentStatePagerAdapter {

    private Context mContext;

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
}