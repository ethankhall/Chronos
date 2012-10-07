/*******************************************************************************
 * Copyright (c) 2011-2012 Ethan Hall
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 ******************************************************************************/

package com.kopysoft.chronos.activities.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ehdev.chronos.lib.Chronos;
import com.ehdev.chronos.lib.enums.Defines;
import com.ehdev.chronos.lib.enums.PayPeriodDuration;
import com.ehdev.chronos.lib.types.Job;
import com.kopysoft.chronos.R;

/**
 * Created with IntelliJ IDEA.
 * User: ethan
 * Date: 10/5/12
 * Time: 7:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class OvertimeFragment extends Fragment {

    private static String TAG = Defines.TAG + " - " + OvertimeFragment.class.getSimpleName();

    //used for disableing
    View startOfPayPeriod;
    View startOfDay;

    CompoundButton.OnCheckedChangeListener startOfDayListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean value) {
            if(!value){
                startOfDay.setVisibility(View.GONE);
            } else {
                startOfDay.setVisibility(View.VISIBLE);
            }
        }
   };

    AdapterView.OnItemSelectedListener ppLengthListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            PayPeriodDuration ppd = PayPeriodDuration.values()[position];
            switch (ppd){
                case FIRST_FIFTEENTH:
                case FULL_MONTH:
                    startOfPayPeriod.setVisibility(View.GONE);
                    break;
                default:
                    startOfPayPeriod.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            startOfPayPeriod.setVisibility(View.VISIBLE);
        }
    };

    private class LockButton implements CompoundButton.OnCheckedChangeListener{
        View locker;
        public LockButton(View viewToLock){
            locker = viewToLock;
        }
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean value) {
            locker.setEnabled(!value);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //create view
        View v = inflater.inflate(R.layout.preferences_job, container, false);
        Chronos chrono = new Chronos(getActivity());
        Job thisJob = chrono.getAllJobs().get(0);

        //we need to define things that are used globally
        startOfPayPeriod = (LinearLayout)v.findViewById(R.id.start_of_pp);
        startOfDay = (LinearLayout)v.findViewById(R.id.startOfDay);

        View payRateView = v.findViewById(R.id.payRate);
        ((TextView)payRateView.findViewById(R.id.name)).setText("Pay Rate");
        ((TextView)payRateView.findViewById(R.id.summary)).setText("How much do you normally make?");
        ((EditText)payRateView.findViewById(R.id.editText)).setText(Float.toString(thisJob.getPayRate()));

        //pay period length
        View payPeriodLength = v.findViewById(R.id.pp_length);
        ((TextView)payPeriodLength.findViewById(R.id.name)).setText("Pay Period Length");
        ((TextView)payPeriodLength.findViewById(R.id.summary)).setVisibility(View.GONE);
        String[] ppLengthArray = getResources().getStringArray(R.array.periodTimes);
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                ppLengthArray);
        ((Spinner)payPeriodLength.findViewById(R.id.spinnerValue)).setAdapter(spinnerArrayAdapter);
        ((Spinner)payPeriodLength.findViewById(R.id.spinnerValue)).setOnItemSelectedListener(ppLengthListener);
        ((Spinner)payPeriodLength.findViewById(R.id.spinnerValue)).setSelection(thisJob.getDuration().ordinal());

        //when does it start?
        //startOfPayPeriod = (LinearLayout)v.findViewById(R.id.start_of_pp); @ start of function
        ((TextView)startOfPayPeriod.findViewById(R.id.name)).setText("Start of pay period");
        ((TextView)startOfPayPeriod.findViewById(R.id.summary)).setText("When does your pay period start?\n" +
                "Lock keeps you from accidentally changing it");
        ((CheckBox)startOfPayPeriod.findViewById(R.id.lock)).setOnCheckedChangeListener(
                new LockButton((startOfPayPeriod.findViewById(R.id.date)))
        );
        ((CheckBox)startOfPayPeriod.findViewById(R.id.lock)).setChecked(true);
        if (android.os.Build.VERSION.SDK_INT >= 11) {//remove the calendar
            ((DatePicker)startOfPayPeriod.findViewById(R.id.date)).setCalendarViewShown(false);
        }
        ((DatePicker)startOfPayPeriod.findViewById(R.id.date)).init(
                thisJob.getStartOfPayPeriod().getYear(),
                thisJob.getStartOfPayPeriod().getMonthOfYear(),
                thisJob.getStartOfPayPeriod().getDayOfMonth(),
                null
        );

        View workOverMidnight = (LinearLayout)v.findViewById(R.id.workOverMidnight);
        ((TextView)workOverMidnight.findViewById(R.id.name)).setText("Do you work nights?");
        ((TextView)workOverMidnight.findViewById(R.id.summary)).setText("If you work over midnight this will help");
        ((CheckBox)workOverMidnight.findViewById(R.id.checkbox)).setOnCheckedChangeListener(startOfDayListener);
        if ( thisJob.getStartOfPayPeriod().getMillisOfDay() < 100){
            ((CheckBox)workOverMidnight.findViewById(R.id.checkbox)).setChecked(false);
            startOfDay.setVisibility(View.GONE);
        } else {
            ((CheckBox)workOverMidnight.findViewById(R.id.checkbox)).setChecked(true);
            startOfDay.setVisibility(View.VISIBLE);
        }

        //startOfDay = (LinearLayout)v.findViewById(R.id.startOfDay); @ start of file
        ((TextView)startOfDay.findViewById(R.id.name)).setText("When are you off work by?");
        ((TextView)startOfDay.findViewById(R.id.summary)).setText("Add a few hours to be sure...\n" +
                "Lock keeps you from accidentally changing it");
        ((TimePicker)startOfDay.findViewById(R.id.time)).setCurrentHour(thisJob.getStartOfPayPeriod().getHourOfDay());
        ((TimePicker)startOfDay.findViewById(R.id.time)).setCurrentMinute(thisJob.getStartOfPayPeriod().getMinuteOfHour());
        ((TimePicker)startOfDay.findViewById(R.id.time)).setCurrentMinute(thisJob.getStartOfPayPeriod().getMinuteOfHour());
        if (!DateFormat.is24HourFormat(getActivity())){
            ((TimePicker)startOfDay.findViewById(R.id.time)).setIs24HourView(false);
        } else {
            ((TimePicker)startOfDay.findViewById(R.id.time)).setIs24HourView(true);
        }
        ((CheckBox)startOfDay.findViewById(R.id.lock)).setOnCheckedChangeListener(
                new LockButton((startOfDay.findViewById(R.id.time)))
        );
        ((CheckBox)startOfDay.findViewById(R.id.lock)).setChecked(true);

        return v;
    }


    @Override
    public void onPause(){
        super.onPause();
    }
}
