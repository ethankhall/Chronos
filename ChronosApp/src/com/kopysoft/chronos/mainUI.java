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
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.util.Log;
import com.j256.ormlite.dao.Dao;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.enums.PayPeriodDuration;
import com.kopysoft.chronos.activities.MainActivity;
import com.kopysoft.chronos.types.Job;
import com.kopysoft.chronos.types.Note;
import com.kopysoft.chronos.types.Punch;
import com.kopysoft.chronos.types.Task;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Random;


public class mainUI extends FragmentActivity {
    /** Called when the activity is first created. */
    private static final String TAG = Defines.TAG + " - Main";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        dropAndTest();

        //ArrayAdapter<CharSequence> list =
        //        ArrayAdapter.createFromResource(this, R.array.navigation, R.layout.abs__simple_spinner_item);

         getSupportActionBar();
        //actionBar.setTitle("Clock");
        //actionBar.setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager()
                .beginTransaction()
                .add(android.R.id.content, new MainActivity())
                .commit();

        //enable drop down navigation
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        //list.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //getSupportActionBar().setListNavigationCallbacks(list, this);

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
        menu.add("Add Punch")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("New Note")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }

    /*

    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        Log.d(TAG, "Position: " + itemPosition);
        switch (itemPosition){
            case 0:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(android.R.id.content, ClockActivity.newInstance())
                        .commit();
                break;
            case 1:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(android.R.id.content, NoteActivity.newInstance())
                        .commit();
                break;
            default:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(android.R.id.content, ClockActivity.newInstance())
                        .commit();
                break;
        }
        return true;
    }
    */

    private void dropAndTest(){
        try{

            final int numberOfTasks = 3; //Number of tasks
            final int jobNumber = 3; //Number of tasks
            Chronos chrono = new Chronos(this);

            // instantiate the DAO to handle Account with String id
            Dao<Punch,String> punchDao = chrono.getPunchDao();
            Dao<Task,String> taskDAO = chrono.getTaskDao();
            Dao<Job,String> jobDAO = chrono.getJobDao();
            Dao<Note,String> noteDAO = chrono.getNoteDao();

            //Create 1 Job
            DateMidnight jobMidnight = DateTime.now().withDayOfWeek(1).toDateMidnight();
            Job currentJob = new Job("My First Job", 7,
                    jobMidnight, PayPeriodDuration.TWO_WEEKS);
            currentJob.setDoubletimeThreshold(60);
            currentJob.setOvertimeThreshold(40);
            currentJob.setOvertimeEnabled(true);
            jobDAO.create(currentJob);

            LinkedList<Task> tasks = new LinkedList<Task>();

            //create tasks
            for( int i = 0; i < numberOfTasks; i++){
                Task newTask = new Task(currentJob, i , "Task " + (i+1) );
                tasks.add(newTask);
                taskDAO.create(newTask);
            }

            DateTime iTime = new DateTime();
            Random rand = new Random();

            for(int i = 0; i < 15; i++){

                DateTime tempTime = iTime.minusHours(i);
                tempTime = tempTime.minusMinutes(rand.nextInt() % 60);
                Punch temp = new Punch(currentJob, tasks.get(i % numberOfTasks), tempTime);
                Note newNote = new Note(tempTime, currentJob,
                        "Note number " + String.valueOf(i + 1) );

                noteDAO.create(newNote);
                punchDao.create(temp);
            }

            chrono.close();
        } catch(SQLException e){
            Log.e(TAG, e.getMessage());
        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }
    }

}