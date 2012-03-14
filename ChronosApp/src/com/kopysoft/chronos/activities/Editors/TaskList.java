/*******************************************************************************
 * Copyright (c) 2011 Ethan Hall
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

package com.kopysoft.chronos.activities.Editors;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.kopysoft.chronos.R;
import com.kopysoft.chronos.adapter.TaskAdapter;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.types.Task;

import java.util.List;

public class TaskList extends SherlockActivity {

    private static String TAG = Defines.TAG + " - TaskList";
    private final boolean enableLog = Defines.DEBUG_PRINT;

    TaskAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(enableLog) Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_list);

        Chronos chron = new Chronos(this);
        List<Task> listOfTasks = chron.getAllTasks();
        chron.close();

        adapter = new TaskAdapter(getApplicationContext(), listOfTasks);
        ListView list = (ListView)findViewById(R.id.taskList);
        list.setAdapter(adapter);
        list.setOnItemClickListener(listener);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Task thisTask = adapter.getItem(position);
            Intent newIntent = 
                    new Intent().setClass(getApplicationContext(), TaskEditor.class);
            newIntent.putExtra("task",thisTask.getID());
            startActivity(newIntent);
        }
    };

    @Override
    public void onResume(){
        super.onResume();

        Chronos chron = new Chronos(this);
        List<Task> listOfTasks = chron.getAllTasks();
        chron.close();
        adapter.updateTasks(listOfTasks);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getSupportMenuInflater().inflate(R.menu.action_bar_task_list, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_insert:
                Intent newIntent =
                        new Intent().setClass(getApplicationContext(), TaskEditor.class);
                newIntent.putExtra("task", -1);
                startActivity(newIntent);
                return true;
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
