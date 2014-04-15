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
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.yelinaung.karrency.app.util.SharePrefUtils;
import com.yelinaung.myancur.app.R;

public class CalculatorFragment extends BaseFragment {

  @InjectView(R.id.spinner_currencies) Spinner mCurrencies;
  @InjectView(R.id.edittext_amount) EditText mEditText;
  @InjectView(R.id.textview_result) TextView mResult;
  @InjectView(R.id.calculate) Button mCalculate;

  private SharePrefUtils sharePref;

  public static CalculatorFragment newInstance() {
    return new CalculatorFragment();
  }

  public CalculatorFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    Context mContext = getActivity().getApplicationContext();
    sharePref = SharePrefUtils.getInstance(mContext);

    View rootView = inflater.inflate(R.layout.fragment_calculator, container, false);
    assert rootView != null;

    ButterKnife.inject(this, rootView);

    // TODO Probably, can iterate through SharePref and XML String array
    //String[] currencies = {
    //    "USD - " + sharePref.getUSD() + " MMK",
    //    "SGD - " + sharePref.getSGD() + " MMK",
    //    "EUR - " + sharePref.getEUR() + " MMK",
    //    "MYR - " + sharePref.getMYR() + " MMK",
    //    "GBP - " + sharePref.getGBP() + " MMK",
    //    "THB - " + sharePref.getTHB() + " MMK"
    //};

    String[] currencies = getResources().getStringArray(R.array.currencies);

    final mSpinnerAdapter mSpinnerAdapter =
        new mSpinnerAdapter(mContext, R.layout.actionbar_spinner, currencies);
    mSpinnerAdapter.setDropDownViewResource(R.layout.actionbar_spinner_dropdown);

    // FIXME NothingSelectedSpinnerAdapter is always returning null
    //mCurrencies.setAdapter(
    //    new NothingSelectedSpinnerAdapter(mSpinnerAdapter, R.layout.spinner_row_nothing_selected,
    //        mContext)
    //);

    mCurrencies.setAdapter(mSpinnerAdapter);

    mCurrencies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override public void onItemSelected(AdapterView<?> parent, View view, int position,
          long id) {
        mCalculate.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            if (mEditText.getText().toString().isEmpty()) {
              mEditText.setError(getString(R.string.enter_amount));
            } else {
              if (mCurrencies.getSelectedItem().toString().equalsIgnoreCase("USD")) {
                mResult.setText(
                    ((Math.round(Float.parseFloat(sharePref.getUSD()))) * (Integer.parseInt(
                        mEditText.getText().toString()))) + " MMK"
                );
              } else if (mCurrencies.getSelectedItem().toString().equalsIgnoreCase("SGD")) {
                mResult.setText(
                    ((Math.round(Float.parseFloat(sharePref.getSGD()))) * (Integer.parseInt(
                        mEditText.getText().toString()))) + " MMK"
                );
              } else if (mCurrencies.getSelectedItem().toString().equalsIgnoreCase("EUR")) {
                mResult.setText(
                    ((Math.round(Float.parseFloat(sharePref.getEUR()))) * (Integer.parseInt(
                        mEditText.getText().toString()))) + " MMK"
                );
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
