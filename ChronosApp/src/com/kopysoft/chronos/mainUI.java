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

package com.kopysoft.chronos;

import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.util.Log;
import android.widget.ArrayAdapter;
import com.j256.ormlite.dao.Dao;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.enums.PayPeriodDuration;
import com.kopysoft.chronos.fragments.ClockFragment;
import com.kopysoft.chronos.types.Job;
import com.kopysoft.chronos.types.Note;
import com.kopysoft.chronos.types.Punch;
import com.kopysoft.chronos.types.Task;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Random;


public class mainUI extends FragmentActivity implements ActionBar.OnNavigationListener{
    /** Called when the activity is first created. */
    private static final String TAG = Defines.TAG + " - Main";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        dropAndTest();

        ArrayAdapter<CharSequence> list =
                ArrayAdapter.createFromResource(this, R.array.navigation, R.layout.abs__simple_spinner_item);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        list.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        getSupportActionBar().setListNavigationCallbacks(list, this);

        /*
        ClockViewer adapter = new ClockViewer( this );

        android.support.v4.view.ViewPager pager =
                (android.support.v4.view.ViewPager) findViewById( R.id.viewpager );

        TitlePageIndicator indicator =
                (TitlePageIndicator)findViewById( R.id.indicator );

        pager.setAdapter( adapter );
        indicator.setViewPager( pager );
        indicator.setFooterIndicatorStyle(TitlePageIndicator.IndicatorStyle.None);
        */

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Note")
                .setIcon(R.drawable.ic_menu_compose)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        FragmentTransaction fmTrans = getSupportFragmentManager().beginTransaction();
        switch (itemPosition){
            case 0:
                fmTrans.replace(android.R.id.content, ClockFragment.newInstance());
                break;
            case 1:
                fmTrans.replace(android.R.id.content, ClockFragment.newInstance());
                break;
            default:
                break;
        }
        fmTrans.commit();
        return true;
    }

    private void dropAndTest(){
        try{

            final int numberOfTasks = 3; //Number of tasks
            final int numberOfNotes = 3; //Number of tasks
            final int jobNumber = 3; //Number of tasks
            Chronos chrono = new Chronos(this);

            // instantiate the DAO to handle Account with String id
            Dao<Punch,String> punchDao = chrono.getPunchDoa();
            Dao<Task,String> taskDAO = chrono.getTaskDoa();
            Dao<Job,String> jobDAO = chrono.getJobDoa();
            Dao<Note,String> noteDAO = chrono.getNoteDoa();

            //Create 1 Job
            DateMidnight jobMidnight = DateTime.now().withDayOfWeek(1).toDateMidnight();
            Job currentJob = new Job("My First Job", 7,
                    jobMidnight, PayPeriodDuration.TWO_WEEKS);
            currentJob.setDoubletimeThreshold(60);
            currentJob.setOvertimeThreshold(40);
            currentJob.setOvertimeEnabled(true);
            jobDAO.create(currentJob);

            LinkedList<Task> tasks = new LinkedList<Task>();


            DateTime iTime = new DateTime();
            Random rand = new Random();
            //create notes
            /*
            for( int i = 0; i < numberOfNotes; i++){
                DateTime tempTime = iTime.minusHours(i);
                tempTime = tempTime.minusMinutes(rand.nextInt() % 60);
                Note newNote = new Note(tempTime, currentJob, "Note number" + String.valueOf(i + 1));

                noteDAO.create(newNote);
            } */


            //create tasks
            for( int i = 0; i < numberOfTasks; i++){
                Task newTask = new Task(currentJob, i , "Task " + (i+1) );
                tasks.add(newTask);
                taskDAO.create(newTask);
            }

            for(int i = 0; i < 15; i++){

                DateTime tempTime = iTime.minusHours(i);
                tempTime = tempTime.minusMinutes(rand.nextInt() % 60);
                Punch temp = new Punch(currentJob, tasks.get(i % numberOfTasks), tempTime);

                punchDao.create(temp);
            }

            chrono.close();
        } catch(SQLException e){
            Log.d(TAG, e.getMessage());
        } catch (Exception e) {
            Log.d(TAG,e.getMessage());
        }
    }

}