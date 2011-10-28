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
import android.support.v4.view.ViewPager;
import android.util.Log;
import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.types.Punch;
import com.kopysoft.chronos.types.Task;
import com.kopysoft.chronos.view.ViewPagerAdapter;
import com.viewpagerindicator.TitlePageIndicator;
import org.joda.time.DateTime;

import java.sql.SQLException;
import java.util.LinkedList;

public class mainUI extends FragmentActivity {
    /** Called when the activity is first created. */
    private static final String TAG = Defines.TAG + " - Main";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        dropAndTest();

        ViewPagerAdapter adapter = new ViewPagerAdapter( this );
        ViewPager pager =
                (ViewPager)findViewById( R.id.viewpager );
        TitlePageIndicator indicator =
                (TitlePageIndicator)findViewById( R.id.indicator );
        pager.setAdapter( adapter );
        indicator.setViewPager( pager );
        indicator.setFooterIndicatorStyle(TitlePageIndicator.IndicatorStyle.None);
    }

    private void dropAndTest(){
        try{

            final int numberOfTasks = 3; //Number of tasks
            final int jobNumber = 3; //Number of tasks

            ConnectionSource connectionSource = new AndroidConnectionSource(new Chronos(this));

            //Punch
            TableUtils.dropTable(connectionSource, Punch.class, true); //Drop all
            TableUtils.createTable(connectionSource, Punch.class); //Create Table

            //Task
            TableUtils.dropTable(connectionSource, Task.class, true); //Drop all
            TableUtils.createTable(connectionSource, Task.class); //Create Table

            // instantiate the DAO to handle Account with String id
            Dao<Punch,String> punchDao = BaseDaoImpl.createDao(connectionSource, Punch.class);
            Dao<Task,String> taskDAO = BaseDaoImpl.createDao(connectionSource, Task.class);

            LinkedList<Task> tasks = new LinkedList<Task>();

            //create tasks
            for( int i = 0; i < numberOfTasks; i++){
                Task newTask = new Task(jobNumber,i , "Task " + (i+1) );
                tasks.add(newTask);
                taskDAO.create(newTask);
            }



            int iJobNumber = 0;
            DateTime iTime = new DateTime();

            for(int i = 0; i < 15; i++){

                DateTime tempTime = iTime.minusHours(i);
                tempTime = tempTime.minusMinutes((int)(Math.random() * 100) % 60);

                Punch temp = new Punch(iJobNumber, tasks.get(i % numberOfTasks), tempTime);

                Log.d(TAG, "Output: " + punchDao.create(temp));
            }


            connectionSource.close();
        } catch(SQLException e){
            Log.d(TAG, e.getMessage());
        } catch (Exception e) {
            Log.d(TAG,e.getMessage());
        }
    }
}