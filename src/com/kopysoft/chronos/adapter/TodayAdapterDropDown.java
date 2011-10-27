package com.kopysoft.chronos.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.types.Punch;
import com.kopysoft.chronos.view.RowElement;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TodayAdapterDropDown extends BaseExpandableListAdapter {

    private static final String TAG = Defines.TAG + " - TodayAdapterIndividual";

    Context gContext;
    List<Punch> gListOfPunches;
    public TodayAdapterDropDown(Context context, List<Punch> listOfPunches){
        gListOfPunches = new LinkedList<Punch>(listOfPunches);
        gContext = context;
        Log.d(TAG, "Size: " + gListOfPunches.size());

        sort();
    }

    public void addPunch(Punch input){
        gListOfPunches.add(input);
        sort();
        notifyDataSetChanged();
    }

    public void rmPunch(int id){
        gListOfPunches.remove(id);
        sort();
    }

    /**
     * Sorts the elements in the list
     */
    private void sort(){
        Collections.sort(gListOfPunches);
    }

    @Override
    public int getGroupCount() {
        return gListOfPunches.size() / 2;
    }

    @Override
    public int getChildrenCount(int i) {
        if(i < 0 )  //Catch Error
            return 0;

        int diff = gListOfPunches.size() - i * 2;
        if(diff >= 2)
            return 2;
        else
            return 1;
    }

    @Override
    public List<Punch> getGroup(int i) {
        LinkedList<Punch> retValue = new LinkedList<Punch>();
        if(i > gListOfPunches.size() / 2)
            return null;
        retValue.add(gListOfPunches.get(i/2));
        if (gListOfPunches.size() >= i/2 + 1)
            retValue.add(gListOfPunches.get(i/2 + 1));
        return retValue;
    }

    @Override
    public Punch getChild(int i, int i1) {
        return gListOfPunches.get(i * 2 + i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false; //TODO: Check this
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

        RowElement curr = new RowElement(gContext, 10, 15, 15, 10);
        TextView left = curr.left();
        TextView right = curr.right();
        TextView center = curr.center();

        //Reset current view
        center.setText("");
        left.setText("");
        int index = i * 2;

        if( index + 1 <= gListOfPunches.size()){
            DateTime inTime = gListOfPunches.get(index).getTime();
            DateTime outTime = gListOfPunches.get(index + 1).getTime();
            Interval diff = new Interval(inTime, outTime);

            String time = String.format("Hours %d:%02d ", diff.toPeriod().getHours(), diff.toPeriod().getMinutes());
            center.setText(time);
            center.setTypeface(null, Typeface.ITALIC);
            right.setText(String.valueOf(gListOfPunches.get(index).getTag()));
        } else{

            center.setText("In Progress");
            center.setTypeface(null, Typeface.ITALIC);
            right.setText(String.valueOf(gListOfPunches.get(index).getTag()));
        }

        return curr;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        if(view == null){
            view = new RowElement(gContext, 10, 3, 10, 3);
        }

        RowElement curr = (RowElement) view;
        TextView left = curr.left();
        TextView right = curr.right();
        TextView center = curr.center();
        left.setText("");

        int index = i * 2 + i1;
        Punch time = gListOfPunches.get(index);

        DateTimeFormatter fmt;
        if (!DateFormat.is24HourFormat(gContext))
            fmt = DateTimeFormat.forPattern("h:mm a");
        else
            fmt = DateTimeFormat.forPattern("HH:mm");

        right.setText(time.getTime().toString(fmt));

        if(index % 2 == 0)
            center.setText("In");
        else
            center.setText("Out");

        return curr;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

}
