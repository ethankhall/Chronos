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

package com.kopysoft.chronos.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.types.Task;
import com.kopysoft.chronos.views.helpers.RowElement;

import java.util.LinkedList;
import java.util.List;

public class TaskAdapter extends BaseAdapter {

    private static final String TAG = Defines.TAG + " - TaskAdapter";

    Context gContext;
    List<Task> listOfTasks = new LinkedList<Task>();
    public static final boolean enableLog = Defines.DEBUG_PRINT;

    public TaskAdapter(Context context, List<Task> tasks){
        gContext = context;

        listOfTasks = tasks;

    }
    
    public void updateTasks(List<Task> tasks){
        listOfTasks = tasks;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listOfTasks.size();
    }

    @Override
    public Task getItem(int i) {
        if(i > listOfTasks.size())
            return null;
        return listOfTasks.get(i);
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
        Task task = listOfTasks.get(i);

        RowElement curr = (RowElement) view;
        TextView left = curr.left();
        TextView right = curr.right();
        TextView center = curr.center();

        //Set center text
        center.setText(task.getName());

        //Set right text
        right.setText("");

        //Set left text
        left.setText("");

        return curr;  //To change body of implemented methods use File | Settings | File Templates.
    }
    
    
}
