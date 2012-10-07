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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.actionbarsherlock.app.SherlockFragment;
import com.ehdev.chronos.lib.Chronos;
import com.ehdev.chronos.lib.enums.Defines;
import com.ehdev.chronos.lib.enums.OvertimeOptions;
import com.ehdev.chronos.lib.enums.WeekendOverride;
import com.ehdev.chronos.lib.types.Job;
import com.kopysoft.chronos.R;


public class OvertimeFragment extends SherlockFragment {

    private static String TAG = Defines.TAG + " - " + OvertimeFragment.class.getSimpleName();

    //used for disableing
    View overtimeOptions;

    public class WeekendOverrideListener implements AdapterView.OnItemSelectedListener {
        CheckBox viewToChange;
        Spinner spinner1;
        Spinner spinner2;
        public WeekendOverrideListener(Spinner sv1, Spinner sv2, CheckBox vtc){
            sv1.setOnItemSelectedListener(this);
            sv2.setOnItemSelectedListener(this);
            spinner1 = sv1;
            spinner2 = sv2;
            viewToChange = vtc;
        }
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            WeekendOverride wo1 = WeekendOverride.values()[spinner1.getSelectedItemPosition()];
            WeekendOverride wo2 = WeekendOverride.values()[spinner2.getSelectedItemPosition()];
            if(wo1 == WeekendOverride.NONE && wo2 == WeekendOverride.NONE){
                viewToChange.setEnabled(true);
            } else {
                viewToChange.setEnabled(false);
                viewToChange.setChecked(true);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            viewToChange.setEnabled(true);
        }
    }

    public class onClickHider implements AdapterView.OnItemSelectedListener{

        View viewer;
        public onClickHider(View mod){
            viewer = mod;
        }
        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            OvertimeOptions ppd = OvertimeOptions.values()[position];
            switch (ppd){
                case NONE:
                    viewer.setVisibility(View.GONE);
                    break;
                default:
                    viewer.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            viewer.setVisibility(View.VISIBLE);
        }

    }

    private class ViewButton implements CompoundButton.OnCheckedChangeListener{
        View locker;
        public ViewButton(View viewToLock){
            locker = viewToLock;
        }
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean value) {
            if(value)
                locker.setVisibility(View.VISIBLE);
            else
                locker.setVisibility(View.GONE);
        }
    }

    //save
    Spinner dataOvertimeOptions;
    EditText dataOvertimeThreshold;
    EditText dataDoubletimeThreshold;

    Spinner dataSaturdaySpinner;
    Spinner dataSundaySpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //create view
        View v = inflater.inflate(R.layout.preferences_overtime, container, false);
        Chronos chrono = new Chronos(getActivity());
        Job thisJob = chrono.getAllJobs().get(0);

        //we need to define things that are used globally
        overtimeOptions = v.findViewById(R.id.overtimeOptions);

        //select overtime type
        View overtimeType = v.findViewById(R.id.overtime_type);
        ((TextView)overtimeType.findViewById(R.id.name)).setText("Overtime Type");
        ((TextView)overtimeType.findViewById(R.id.summary)).setText("How does your overtime get calculated.");
        String[] ppLengthArray = getResources().getStringArray(R.array.overtime_options);
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                ppLengthArray);

        dataOvertimeOptions = ((Spinner)overtimeType.findViewById(R.id.spinnerValue));
        dataOvertimeOptions.setAdapter(spinnerArrayAdapter);
        dataOvertimeOptions.setOnItemSelectedListener(new onClickHider(overtimeOptions));
        dataOvertimeOptions.setSelection(thisJob.getOvertimeOptions().ordinal());

        if(thisJob.getOvertimeOptions() == OvertimeOptions.NONE){
            overtimeOptions.setVisibility(View.GONE);
        } else {
            overtimeOptions.setVisibility(View.VISIBLE);
        }
        //overtimetime
        View overtimeThreshold = v.findViewById(R.id.overtimeThreshold);
        ((TextView)overtimeThreshold.findViewById(R.id.name)).setText("Overtime threshold");
        ((TextView)overtimeThreshold.findViewById(R.id.summary)).setText("When do you start making overtime?");
        dataOvertimeThreshold =((EditText)overtimeThreshold.findViewById(R.id.editText));
        dataOvertimeThreshold.setText(Float.toString(thisJob.getOvertime()));

        //doubletime
        View doubletimeThreshold = v.findViewById(R.id.doubleThreshold);
        ((TextView)doubletimeThreshold.findViewById(R.id.name)).setText("Double Time threshold");
        ((TextView)doubletimeThreshold.findViewById(R.id.summary)).setText("When do you start making dubletime?");
        dataDoubletimeThreshold =((EditText)doubletimeThreshold.findViewById(R.id.editText));
        dataDoubletimeThreshold.setText(Float.toString(thisJob.getDoubleTime()));

        //special changes for weekend
        View specialWeekendTimes = v.findViewById(R.id.specialTimeForWeekend);
        ((TextView)specialWeekendTimes.findViewById(R.id.name)).setText("Weekend overtime");
        ((TextView)specialWeekendTimes.findViewById(R.id.summary)).setText("Do you always make overtime/doubletime on weekends?");
        CheckBox specialWeekendTimesEnable = (CheckBox)specialWeekendTimes.findViewById(R.id.checkbox);
        specialWeekendTimesEnable.setOnCheckedChangeListener(new ViewButton(v.findViewById(R.id.specialTimeForWeekendLayout)));
        if(thisJob.getSundayOverride() != WeekendOverride.NONE || thisJob.getSaturdayOverride() != WeekendOverride.NONE){
            specialWeekendTimesEnable.setChecked(true);
            v.findViewById(R.id.specialTimeForWeekendLayout).setVisibility(View.VISIBLE);
        } else {
            specialWeekendTimesEnable.setChecked(false);
            v.findViewById(R.id.specialTimeForWeekendLayout).setVisibility(View.GONE);
        }

        //saturday
        View saturdayOvertimeOverride = v.findViewById(R.id.saturdayDefined);
        ((TextView)saturdayOvertimeOverride.findViewById(R.id.name)).setText("Saturday Override");
        ((TextView)saturdayOvertimeOverride.findViewById(R.id.summary)).setText("Do you get payed differently?");
        String[] overtimeOptions = getResources().getStringArray(R.array.weekend_override);
        ArrayAdapter saturdaySpinner = new ArrayAdapter(getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                overtimeOptions);

        dataSaturdaySpinner = ((Spinner)saturdayOvertimeOverride.findViewById(R.id.spinnerValue));
        dataSaturdaySpinner.setAdapter(saturdaySpinner);
        dataSaturdaySpinner.setSelection(thisJob.getSaturdayOverride().ordinal());

        //sunday
        View sundayOvertimeOverride = v.findViewById(R.id.sundayDefined);
        ((TextView)sundayOvertimeOverride.findViewById(R.id.name)).setText("Sunday Override");
        ((TextView)sundayOvertimeOverride.findViewById(R.id.summary)).setText("Do you get payed differently?");
        ArrayAdapter sundaySpinner = new ArrayAdapter(getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                overtimeOptions);

        dataSundaySpinner = ((Spinner)sundayOvertimeOverride.findViewById(R.id.spinnerValue));
        dataSundaySpinner.setAdapter(sundaySpinner);
        dataSundaySpinner.setSelection(thisJob.getSundayOverride().ordinal());

        new WeekendOverrideListener(dataSaturdaySpinner, dataSundaySpinner, (CheckBox)specialWeekendTimes.findViewById(R.id.checkbox));

        chrono.close();

        return v;
    }


    @Override
    public void onPause(){
        super.onPause();
        /*
            EditText dataOvertimeThreshold;
            EditText dataDoubletimeThreshold;

            Spinner dataSaturdaySpinner;
            Spinner dataSundaySpinner;
         */

        Chronos chrono = new Chronos(getActivity());
        Job thisJob = chrono.getAllJobs().get(0);

        OvertimeOptions oto = OvertimeOptions.values()[dataOvertimeOptions.getSelectedItemPosition()];
        thisJob.setOvertimeOptions(oto);

        try{
            thisJob.setOvertimeThreshold(Float.parseFloat(dataOvertimeThreshold.getText().toString()));
        } catch (NumberFormatException e){
            Toast.makeText(getActivity(), "Overtime threshold incorrect", Toast.LENGTH_SHORT);
        }

        try{
            thisJob.setDoubletimeThreshold(Float.parseFloat(dataDoubletimeThreshold.getText().toString()));
        } catch (NumberFormatException e){
            Toast.makeText(getActivity(), "Double Time threshold incorrect", Toast.LENGTH_SHORT);
        }

        WeekendOverride sat = WeekendOverride.values()[dataSaturdaySpinner.getSelectedItemPosition()];
        thisJob.setSaturdayOverride(sat);

        WeekendOverride sun = WeekendOverride.values()[dataSundaySpinner.getSelectedItemPosition()];
        thisJob.setSundayOverride(sun);


        chrono.close();
    }
}
