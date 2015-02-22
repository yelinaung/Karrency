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
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.github.ksoichiro.android.observablescrollview.CacheFragmentStatePagerAdapter;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.google.analytics.tracking.android.EasyTracker;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.yelinaung.karrency.app.R;
import com.yelinaung.karrency.app.ui.widget.SlidingTabLayout;

public class HomeActivity extends ActionBarActivity implements ObservableScrollViewCallbacks {

  @InjectView(R.id.pager) ViewPager mPager;
  @InjectView(R.id.toolbar) Toolbar mToolbar;
  @InjectView(R.id.header) View mHeaderView;

  private int mBaseTranslationY;
  private SlidingTabAdapter slidingTabAdapter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //Fabric.with(this, new Crashlytics());

    setContentView(R.layout.activity_home);

    ButterKnife.inject(this);

    this.setSupportActionBar(mToolbar);

    final int tabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);
    //findViewById(R.id.pager_wrapper).setPadding(0, getActionBarSize() + tabHeight, 0, 0);

    slidingTabAdapter = new SlidingTabAdapter(getSupportFragmentManager(), HomeActivity.this);

    SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
    slidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
    slidingTabLayout.setSelectedIndicatorColors(Color.WHITE);
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

  @Override public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
    if (dragging) {
      int toolbarHeight = mToolbar.getHeight();
      float currentHeaderTranslationY = ViewHelper.getTranslationY(mHeaderView);
      if (firstScroll) {
        if (-toolbarHeight < currentHeaderTranslationY) {
          mBaseTranslationY = scrollY;
        }
      }
      float headerTranslationY =
          ScrollUtils.getFloat(-(scrollY - mBaseTranslationY), -toolbarHeight, 0);
      ViewPropertyAnimator.animate(mHeaderView).cancel();
      ViewHelper.setTranslationY(mHeaderView, headerTranslationY);
    }
  }

  @Override public void onDownMotionEvent() {

  }

  @Override public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    mBaseTranslationY = 0;

    Fragment fragment = getCurrentFragment();
    if (fragment == null) {
      return;
    }
    View view = fragment.getView();
    if (view == null) {
      return;
    }

    int toolbarHeight = mToolbar.getHeight();
    final ObservableScrollView scrollView = (ObservableScrollView) view.findViewById(R.id.scroll);
    if (scrollView == null) {
      return;
    }
    int scrollY = scrollView.getCurrentScrollY();
    if (scrollState == ScrollState.DOWN) {
      showToolbar();
    } else if (scrollState == ScrollState.UP) {
      if (toolbarHeight <= scrollY) {
        hideToolbar();
      } else {
        showToolbar();
      }
    } else {
      // Even if onScrollChanged occurs without scrollY changing, toolbar should be adjusted
      if (toolbarIsShown() || toolbarIsHidden()) {
        // Toolbar is completely moved, so just keep its state
        // and propagate it to other pages
        propagateToolbarState(toolbarIsShown());
      } else {
        // Toolbar is moving but doesn't know which to move:
        // you can change this to hideToolbar()
        showToolbar();
      }
    }
  }

  private void showToolbar() {
    float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
    if (headerTranslationY != 0) {
      ViewPropertyAnimator.animate(mHeaderView).cancel();
      ViewPropertyAnimator.animate(mHeaderView).translationY(0).setDuration(200).start();
    }
    propagateToolbarState(true);
  }

  private void hideToolbar() {
    float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
    int toolbarHeight = mToolbar.getHeight();
    if (headerTranslationY != -toolbarHeight) {
      ViewPropertyAnimator.animate(mHeaderView).cancel();
      ViewPropertyAnimator.animate(mHeaderView)
          .translationY(-toolbarHeight)
          .setDuration(200)
          .start();
    }
    propagateToolbarState(false);
  }

  private boolean toolbarIsShown() {
    return ViewHelper.getTranslationY(mHeaderView) == 0;
  }

  private boolean toolbarIsHidden() {
    return ViewHelper.getTranslationY(mHeaderView) == -mToolbar.getHeight();
  }

  private void propagateToolbarState(boolean isShown) {
    int toolbarHeight = mToolbar.getHeight();

    // Set scrollY for the fragments that are not created yet
    slidingTabAdapter.setScrollY(isShown ? 0 : toolbarHeight);

    // Set scrollY for the active fragments
    for (int i = 0; i < slidingTabAdapter.getCount(); i++) {
      // Skip current item
      if (i == mPager.getCurrentItem()) {
        continue;
      }

      // Skip destroyed or not created item
      Fragment f = slidingTabAdapter.getItemAt(i);
      if (f == null) {
        continue;
      }

      ObservableScrollView scrollView =
          (ObservableScrollView) f.getView().findViewById(R.id.scroll);
      if (isShown) {
        // Scroll up
        if (0 < scrollView.getCurrentScrollY()) {
          scrollView.scrollTo(0, 0);
        }
      } else {
        // Scroll down (to hide padding)
        if (scrollView.getCurrentScrollY() < toolbarHeight) {
          scrollView.scrollTo(0, toolbarHeight);
        }
      }
    }
  }

  protected int getActionBarSize() {
    TypedValue typedValue = new TypedValue();
    int[] textSizeAttr = new int[] { R.attr.actionBarSize };
    int indexOfAttrTextSize = 0;
    TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
    int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
    a.recycle();
    return actionBarSize;
  }

  private Fragment getCurrentFragment() {
    return slidingTabAdapter.getItemAt(mPager.getCurrentItem());
  }

  public class SlidingTabAdapter extends CacheFragmentStatePagerAdapter {

    private Context mContext;
    private int mScrollY;

    public SlidingTabAdapter(FragmentManager fm, Context context) {
      super(fm);
      this.mContext = context;
    }

    public void setScrollY(int scrollY) {
      mScrollY = scrollY;
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

    @Override protected Fragment createItem(int i) {
      Fragment f = new ExchangeRateFragment();
      if (0 <= mScrollY) {
        Bundle args = new Bundle();
        args.putInt(ExchangeRateFragment.ARG_SCROLL_Y, mScrollY);
        f.setArguments(args);
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