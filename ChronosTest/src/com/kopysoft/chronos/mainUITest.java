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

import android.test.ActivityUnitTestCase;
import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.enums.PayPeriodDuration;
import com.kopysoft.chronos.types.Job;
import com.kopysoft.chronos.types.Punch;
import com.kopysoft.chronos.types.Task;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class mainUITest extends ActivityUnitTestCase<mainUI> {
    mainUI mainActivity;
    public mainUITest() {
        super(mainUI.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        mainActivity = getActivity();
    }

    @Test
    public void testJobs(){
        try{
            Chronos chrono = new Chronos(mainActivity.getApplicationContext());
            List<Job> jobs = chrono.getJobs();

            for(Job curJob : jobs){
                List<Punch> punches = chrono.getAllPunchesForJob(curJob);
                assertEquals(punches.size(), 15);
            }
            chrono.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Before
    public void createDB(){
        try{

            final int numberOfTasks = 3; //Number of tasks
            final int jobNumber = 3; //Number of tasks
            Chronos chrono = new Chronos(mainActivity.getApplicationContext());
            ConnectionSource connectionSource = new AndroidConnectionSource(chrono);

            //Punch
            TableUtils.dropTable(connectionSource, Punch.class, true); //Drop all
            TableUtils.createTable(connectionSource, Punch.class); //Create Table

            //Task
            TableUtils.dropTable(connectionSource, Task.class, true); //Drop all
            TableUtils.createTable(connectionSource, Task.class); //Create Table

            //Job
            TableUtils.dropTable(connectionSource, Job.class, true); //Drop all
            TableUtils.createTable(connectionSource, Job.class); //Create Table

            // instantiate the DAO to handle Account with String id
            Dao<Punch,String> punchDao = BaseDaoImpl.createDao(connectionSource, Punch.class);
            Dao<Task,String> taskDAO = BaseDaoImpl.createDao(connectionSource, Task.class);
            Dao<Job,String> jobDAO = BaseDaoImpl.createDao(connectionSource, Job.class);

            //Create 2 Job
            List<Job> jobs = new LinkedList<Job>();
            for(int i = 0; i < jobNumber; i++){
                DateMidnight jobMidnight = DateTime.now().withDayOfWeek(1).toDateMidnight();
                Job currentJob = new Job("My " + i +" Job", 7,
                        jobMidnight, PayPeriodDuration.TWO_WEEKS);
                jobs.add(currentJob);
                jobDAO.create(currentJob);
            }


            LinkedList<Task> tasks = new LinkedList<Task>();

            //create tasks
            for (Job thisJob : jobs) {
                for (int i = 0; i < numberOfTasks; i++) {
                    Task newTask = new Task(thisJob, i, "Task " + (i + 1));
                    tasks.add(newTask);
                    taskDAO.create(newTask);
                }

                DateTime iTime = new DateTime();
                Random rand = new Random();

                for (int i = 0; i < 15; i++) {

                    DateTime tempTime = iTime.minusHours(i);
                    tempTime = tempTime.minusMinutes(rand.nextInt() % 60);
                    Punch temp = new Punch(thisJob,
                            tasks.get(i % numberOfTasks), tempTime);

                    punchDao.create(temp);
                }

            }


            connectionSource.close();
            chrono.close();
        } catch(SQLException e){
            System.err.println(e.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
