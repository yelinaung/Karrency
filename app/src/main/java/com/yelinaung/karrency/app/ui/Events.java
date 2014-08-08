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

import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import rx.Observable;
import rx.subjects.PublishSubject;

public class Events {

    // no instances for helper class
    private Events() { }

    /*
     * Creates a subject that emits events for each click on view
     */
    public static Observable<Object> click(View view) {
        final PublishSubject<Object> subject = PublishSubject.create();
        view.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                subject.onNext(new Object());
            }
        });
        return subject;
    }

    /*
     * Creates a subject that emits events for item clicks of list views
     */
    public static Observable<Integer> itemClick(AbsListView view) {
        final PublishSubject<Integer> subject = PublishSubject.create();
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                subject.onNext(position);
            }
        });
        return subject;
    }
}
