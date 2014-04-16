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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.google.analytics.tracking.android.EasyTracker;

/**
 * Created by Ye Lin Aung on 14/04/14.
 */
public class BaseFragment extends Fragment {
  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override public void onResume() {
    super.onResume();
  }

  @Override
  public void onStart() {
    super.onStart();
    EasyTracker.getInstance(getActivity()).activityStart(getActivity());  // Add this method.
  }
}
