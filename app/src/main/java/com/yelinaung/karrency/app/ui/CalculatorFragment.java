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
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.yelinaung.karrency.app.util.SharePrefUtils;
import com.yelinaung.myancur.app.R;

@SuppressWarnings("ConstantConditions")
public class CalculatorFragment extends BaseFragment {

  private static int ANIMATION_DURATION = 300;

  @InjectView(R.id.spinner_currencies) Spinner mCurrencies;
  @InjectView(R.id.edittext_amount) ClearableEditText mEditText;
  @InjectView(R.id.textview_result) TextView mResult;
  @InjectView(R.id.calculate) Button mCalculate;
  @InjectView(R.id.change) ImageButton mChange;
  @InjectView(R.id.label_mmk) TextView mMMK;

  boolean noSwap = true;
  private Context mContext;
  private SharePrefUtils sharePref;
  private View rootView;
  private int viewHeight;
  private RotateAnimation rotateClockwise;
  private RotateAnimation rotateAntiClockwise;

  public CalculatorFragment() {
    // Required empty public constructor
  }

  public static CalculatorFragment newInstance() {
    return new CalculatorFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    mContext = getActivity().getApplicationContext();
    sharePref = SharePrefUtils.getInstance(mContext);

    rootView = inflater.inflate(R.layout.fragment_calculator, container, false);
    assert rootView != null;

    ButterKnife.inject(this, rootView);

    mResult.setText(" - ");

    // TODO Probably, can iterate through SharePref and XML String array
    String[] currencies = {
        "USD - " + sharePref.getUSD() + " MMK", "SGD - " + sharePref.getSGD() + " MMK",
        "EUR - " + sharePref.getEUR() + " MMK", "MYR - " + sharePref.getMYR() + " MMK",
        "GBP - " + sharePref.getGBP() + " MMK", "THB - " + sharePref.getTHB() + " MMK"
    };

    //String[] currencies = getResources().getStringArray(R.array.currencies);

    final mSpinnerAdapter mSpinnerAdapter =
        new mSpinnerAdapter(mContext, R.layout.actionbar_spinner, currencies);
    mSpinnerAdapter.setDropDownViewResource(R.layout.actionbar_spinner_dropdown);

    // FIXME NothingSelectedSpinnerAdapter is always returning null
    //mCurrencies.setAdapter(
    //    new NothingSelectedSpinnerAdapter(mSpinnerAdapter, R.layout.spinner_row_nothing_selected,
    //        mContext)
    //);

    ViewTreeObserver viewTreeObserver = mCurrencies.getViewTreeObserver();
    if (viewTreeObserver.isAlive()) {
      viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
          mCurrencies.getViewTreeObserver().addOnGlobalLayoutListener(this);
          viewHeight = mCurrencies.getHeight();
          mCurrencies.getLayoutParams();
        }
      });
    }

    mCurrencies.setAdapter(mSpinnerAdapter);

    rotateClockwise =
        (RotateAnimation) AnimationUtils.loadAnimation(mContext, R.anim.rotate_clockwise);
    rotateClockwise.setDuration(ANIMATION_DURATION);

    rotateAntiClockwise =
        (RotateAnimation) AnimationUtils.loadAnimation(mContext, R.anim.rotate_anitclockwise);
    rotateAntiClockwise.setDuration(ANIMATION_DURATION);

    mChange.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (noSwap) {

          mChange.setAnimation(rotateClockwise);
          rotateClockwise.start();

          TranslateAnimation ta1 = new TranslateAnimation(0, 0, 0, viewHeight);
          ta1.setDuration(ANIMATION_DURATION);
          ta1.setFillAfter(true);
          mCurrencies.startAnimation(ta1);
          mCurrencies.bringToFront();

          TranslateAnimation ta2 = new TranslateAnimation(0, 0, 0, -viewHeight);
          ta2.setDuration(ANIMATION_DURATION);
          ta2.setFillAfter(true);
          mMMK.startAnimation(ta2);
          mMMK.bringToFront();

          noSwap = false;
        } else {
          mChange.setAnimation(rotateAntiClockwise);
          rotateAntiClockwise.start();

          TranslateAnimation ta1 = new TranslateAnimation(0, 0, viewHeight, 0);
          ta1.setDuration(ANIMATION_DURATION);
          ta1.setFillAfter(true);
          mCurrencies.startAnimation(ta1);
          mCurrencies.bringToFront();

          TranslateAnimation ta2 = new TranslateAnimation(0, 0, -viewHeight, 0);
          ta2.setDuration(ANIMATION_DURATION);
          ta2.setFillAfter(true);
          mMMK.startAnimation(ta2);
          mMMK.bringToFront();

          noSwap = true;
        }
      }
    });

    mCurrencies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override public void onItemSelected(AdapterView<?> parent, View view, int position,
          long id) {
        mCalculate.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            if (mEditText.getText().toString().isEmpty()) {
              mEditText.setError(getString(R.string.enter_amount));
            } else {
              if (mCurrencies.getSelectedItem()
                  .toString()
                  .equalsIgnoreCase("USD - " + sharePref.getUSD() + " MMK")) {
                mResult.setText(insertComma(
                    ((Math.round(Float.parseFloat(sharePref.getUSD().replace(",", ""))))
                        * (Long.parseLong(mEditText.getText().toString()))) + ""
                ));
              } else if (mCurrencies.getSelectedItem()
                  .toString()
                  .equalsIgnoreCase("SGD - " + sharePref.getSGD() + " MMK")) {
                mResult.setText(insertComma(
                    ((Math.round(Float.parseFloat(sharePref.getSGD()))) * (Integer.parseInt(
                        mEditText.getText().toString()))) + " "
                ));
              } else if (mCurrencies.getSelectedItem()
                  .toString()
                  .equalsIgnoreCase("EUR - " + sharePref.getEUR() + " MMK")) {
                // yah. there was a command so. Float.parseFloat can't parse
                int special = Math.round(Float.parseFloat(sharePref.getEUR().replace(",", "")));
                mResult.setText(
                    insertComma((special * (Long.parseLong(mEditText.getText().toString()))) + ""));
              } else if (mCurrencies.getSelectedItem()
                  .toString()
                  .equalsIgnoreCase("MYR - " + sharePref.getMYR() + " MMK")) {
                mResult.setText(insertComma(
                    ((Math.round(Float.parseFloat(sharePref.getMYR()))) * (Long.parseLong(
                        mEditText.getText().toString()))) + " "
                ));
              } else if (mCurrencies.getSelectedItem()
                  .toString()
                  .equalsIgnoreCase("GBP - " + sharePref.getGBP() + " MMK")) {
                mResult.setText(insertComma(
                    ((Math.round(Float.parseFloat(sharePref.getGBP().replace(",", ""))))
                        * (Long.parseLong(mEditText.getText().toString()))) + " "
                ));
              } else if (mCurrencies.getSelectedItem()
                  .toString()
                  .equalsIgnoreCase("THB - " + sharePref.getTHB() + " MMK")) {
                mResult.setText(insertComma(
                    ((Math.round(Float.parseFloat(sharePref.getTHB()))) * (Long.parseLong(
                        mEditText.getText().toString()))) + " "
                ));
              }
            }
          }
        });
      }

      @Override public void onNothingSelected(AdapterView<?> parent) {
      }
    });

    // Inflate the layout for this fragment
    return rootView;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
  }

  @Override
  public void onDetach() {
    super.onDetach();
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    // Inflate the menu; this adds items to the action bar if it is present.
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.calc, menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
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
    }
    return super.onOptionsItemSelected(item);
  }

  private String insertComma(String digits) {
    String result = digits;
    if (digits.length() <= 3) {
      return digits;
    } else {
      for (int i = 0; i < (digits.length() - 1) / 3; i++) {
        int commaPos = (digits.length() - 3) - (3 * i);
        result = result.substring(0, commaPos) + "," + result.substring(commaPos);
      }
      return result;
    }
  }

  public class mSpinnerAdapter extends ArrayAdapter<String> {
    public mSpinnerAdapter(Context context, int resource, String[] data) {
      super(context, resource, data);
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
      TextView v = (TextView) super.getView(position, convertView, parent);
      assert v != null;
      v.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
      return v;
    }

    @Override public View getDropDownView(int position, View convertView, ViewGroup parent) {
      TextView v = (TextView) super.getView(position, convertView, parent);
      assert v != null;
      v.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
      v.setHeight(100);
      v.setGravity(Gravity.CENTER_VERTICAL);
      return v;
    }

    @Override public int getPosition(String item) {
      return super.getPosition(item) - 1;
    }
  }
}
