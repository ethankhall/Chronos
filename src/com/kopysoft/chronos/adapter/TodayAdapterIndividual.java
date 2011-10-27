package com.kopysoft.chronos.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.types.Punch;
import com.kopysoft.chronos.view.RowElement;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TodayAdapterIndividual extends BaseAdapter {

    private static final String TAG = Defines.TAG + " - TodayAdapterIndividual";

    Context gContext;
    List<Punch> gListOfPunches;
    public TodayAdapterIndividual(Context context, List<Punch> listOfPunches){
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
    public int getCount() {
        return gListOfPunches.size();
    }

    @Override
    public Object getItem(int i) {
        if(i > gListOfPunches.size())
            return null;
        return gListOfPunches.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if(view == null){
            view = new RowElement(gContext);
        }
        Punch inTime = gListOfPunches.get(i);

        RowElement curr = (RowElement) view;
        TextView left = curr.left();
        TextView right = curr.right();

        DateTimeFormatter fmt;
        if (!DateFormat.is24HourFormat(gContext))
            fmt = DateTimeFormat.forPattern("h:mm a");
        else
            fmt = DateTimeFormat.forPattern("HH:mm");

        left.setText(inTime.getTime().toString(fmt));

        if(i % 2 == 0)
            right.setText("IN");
        else
            right.setText("OUT");

        return curr;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
