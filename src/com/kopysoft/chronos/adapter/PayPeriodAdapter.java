package com.kopysoft.chronos.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

public class PayPeriodAdapter extends BaseExpandableListAdapter {
    public PayPeriodAdapter(Context context){

    }
    @Override
    public int getGroupCount() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasStableIds() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public View getGroupView(int groupPosition, boolean b, View view, ViewGroup viewGroup) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean b, View view, ViewGroup viewGroup) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
