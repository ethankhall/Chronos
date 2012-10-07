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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.ehdev.chronos.lib.Chronos;
import com.ehdev.chronos.lib.types.Task;
import com.kopysoft.chronos.R;
import com.kopysoft.chronos.activities.Editors.TaskEditor;
import com.kopysoft.chronos.adapter.TaskAdapter;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ethan
 * Date: 10/7/12
 * Time: 1:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class TaskFragment  extends SherlockFragment {

    TaskAdapter adapter;

    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Task thisTask = adapter.getItem(position);
            Intent newIntent =
                    new Intent().setClass(getActivity(), TaskEditor.class);
            newIntent.putExtra("task",thisTask.getID());
            startActivity(newIntent);
        }
    };

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_bar_task_list, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_insert:
                Intent newIntent =
                        new Intent().setClass(getActivity(), TaskEditor.class);
                newIntent.putExtra("task", -1);
                startActivity(newIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        Chronos chron = new Chronos(getActivity());
        List<Task> listOfTasks = chron.getAllTasks();
        chron.close();
        adapter.updateTasks(listOfTasks);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.task_list, container, false);

        Chronos chron = new Chronos(getActivity());
        List<Task> listOfTasks = chron.getAllTasks();
        chron.close();

        adapter = new TaskAdapter(getActivity(), listOfTasks);
        ListView list = (ListView)v.findViewById(R.id.taskList);
        list.setAdapter(adapter);
        list.setOnItemClickListener(listener);


        return v;
    }


}
