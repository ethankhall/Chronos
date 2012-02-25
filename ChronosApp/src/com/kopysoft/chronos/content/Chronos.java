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

package com.kopysoft.chronos.content;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.kopysoft.chronos.enums.PayPeriodDuration;
import com.kopysoft.chronos.types.Job;
import com.kopysoft.chronos.types.Note;
import com.kopysoft.chronos.types.Punch;
import com.kopysoft.chronos.types.Task;
import com.kopysoft.chronos.types.holders.PayPeriodHolder;
import com.kopysoft.chronos.types.holders.PunchTable;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Chronos extends OrmLiteSqliteOpenHelper {

    private static final String TAG = "Chronos - SQL";

    //0.9 = 7
    //1.0.1 - 1.1.0 = 10
    //1.2.0	= 11
    //2.0.0 = 12

    private static final int DATABASE_VERSION = 12;
    public static final String DATABASE_NAME = "Chronos";

    Dao<Punch, String>  gPunchDoa = null;
    Dao<Task, String>   gTaskDoa = null;
    Dao<Job, String>    gJobDoa = null;
    Dao<Note, String>    gNoteDoa = null;

    public Chronos(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {

        try{
            //Punch
            TableUtils.createTable(connectionSource, Punch.class); //Create Table

            //Task
            TableUtils.createTable(connectionSource, Task.class); //Create Table

            //Job
            TableUtils.createTable(connectionSource, Job.class); //Create Table

            //Job
            TableUtils.createTable(connectionSource, Note.class); //Create Table

            //Create elements for testing
            dropAndTest();

        } catch (SQLException e) {
            Log.e(TAG, "Could not create new table for Thing", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try{
            Log.w(TAG, "Upgrading database, this will drop tables and recreate.");
            Log.w(TAG, "oldVerion: " + oldVersion + "\tnewVersion: " + newVersion);

            //Punch
            TableUtils.dropTable(connectionSource, Punch.class, true); //Drop all

            //Task
            TableUtils.dropTable(connectionSource, Task.class, true); //Drop all

            //Job
            TableUtils.dropTable(connectionSource, Job.class, true); //Drop all

            //Job
            TableUtils.dropTable(connectionSource, Note.class, true); //Drop all

            //Recreate DB
            onCreate(db, connectionSource);

        } catch (SQLException e) {
            Log.e(TAG, "Could not upgrade the table for Thing", e);
        }

    }

    @SuppressWarnings("unchecked")
    public Dao<Punch, String> getPunchDao() throws SQLException {
        if (gPunchDoa == null) {
            gPunchDoa = getDao(Punch.class);
        }
        return gPunchDoa;
    }

    @SuppressWarnings("unchecked")
    public Dao<Job, String> getJobDao() throws SQLException {
        if (gJobDoa == null) {
            gJobDoa = getDao(Job.class);
        }
        return gJobDoa;
    }

    @SuppressWarnings("unchecked")
    public Dao<Task, String> getTaskDao() throws SQLException {
        if (gTaskDoa == null) {
            gTaskDoa = getDao(Task.class);
        }
        return gTaskDoa;
    }

    @SuppressWarnings("unchecked")
    public Dao<Note, String> getNoteDao() throws SQLException {
        if (gNoteDoa == null) {
            gNoteDoa = getDao(Note.class);
        }
        return gNoteDoa;
    }

    public List<Punch> getAllPunches(){

        List<Punch> retValue = null;
        try{
            // instantiate the DAO to handle Account with String id
            Dao<Punch,String> punchDao = getPunchDao();
            Dao<Task,String> taskDAO = getTaskDao();

            //accountDao.refresh(order.getAccount());
            retValue = punchDao.queryForAll();
            for(Punch work : retValue){
                taskDAO.refresh(work.getTask());
                taskDAO.refresh(work.getTask());
            }

        } catch(SQLException e){
            Log.d(TAG, e.getMessage());
        } catch (Exception e) {
            Log.d(TAG,e.getMessage());
        }
        return retValue;
    }

    public List<Punch> getAllPunchesForJob(Job jobId){

        List<Punch> retValue = null;
        try{

            // instantiate the DAO to handle Account with String id
            Dao<Punch,String> punchDao = getPunchDao();
            Dao<Task,String> taskDAO = getTaskDao();
            Dao<Job,String> jobDAO = getJobDao();

            QueryBuilder<Punch, String> queryBuilder = punchDao.queryBuilder();
            queryBuilder.where().eq(Job.JOB_FIELD_NAME, jobId);
            PreparedQuery<Punch> preparedQuery = queryBuilder.prepare();

            retValue = punchDao.query(preparedQuery);
            for(Punch work : retValue){
                taskDAO.refresh(work.getTask());
                jobDAO.refresh(work.getJobNumber());
            }

        } catch(SQLException e){
            Log.d(TAG, e.getMessage());
        } catch (Exception e) {
            Log.d(TAG,e.getMessage());
        }
        return retValue;
    }

    public List<Punch> getPunchesByJobAndDate(Job jobId, DateTime date){
        List<Punch> punches = new LinkedList<Punch>();


        // instantiate the DAO to handle Account with String id
        try {
            Dao<Punch,String> punchDao = getPunchDao();
            Dao<Task,String> taskDAO = getTaskDao();
            Dao<Job,String> jobDAO = getJobDao();
            
            DateTime startOfPP = jobId.getStartOfPayPeriod();
            int days = (int)(date.getMillis() - startOfPP.getMillis())/1000/60/60/24;
            DateTime startOfDay = startOfPP.plusDays(days);
            DateTime endOfDay = startOfDay.plusDays(1);

            Log.d(TAG, "Days in: " + days);
            Log.d(TAG, "Start of Day: " + startOfDay.getMillis());
            Log.d(TAG, "End of Day: " + endOfDay.getMillis());

            QueryBuilder<Punch, String> queryBuilder = punchDao.queryBuilder();
            queryBuilder.where().eq(Job.JOB_FIELD_NAME, jobId.getID()).and()
                    .gt(Punch.TIME_OF_PUNCH, startOfDay.getMillis()).and()
                    .le(Punch.TIME_OF_PUNCH, endOfDay.getMillis());

            PreparedQuery<Punch> preparedQuery = queryBuilder.prepare();

            punches = punchDao.query(preparedQuery);
            Log.d(TAG, "Punches for this day: " + punches.size());
            for(Punch work : punches){
                taskDAO.refresh(work.getTask());
                jobDAO.refresh(work.getJobNumber());
            }


        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }

        return punches;
    }

    public PunchTable getAllPunchesForThisPayPeriodByJob(Job jobId){

        PunchTable punches = null;
        List<Punch> retValue = null;
        try{

            // instantiate the DAO to handle Account with String id
            Dao<Punch,String> punchDao = getPunchDao();
            Dao<Task,String> taskDAO = getTaskDao();
            Dao<Job,String> jobDAO = getJobDao();

            //Get the start and end of pay period
            PayPeriodHolder pph = new PayPeriodHolder(jobId);
            DateTime startOfPP = pph.getStartOfPayPeriod().toDateTime();
            DateTime endOfPP = pph.getEndOfPayPeriod().toDateTime();

            punches = new PunchTable(startOfPP.toDateMidnight(), endOfPP.toDateMidnight(), jobId);
            
            Log.d(TAG, "Start of Pay Period: " + startOfPP.getMillis());
            Log.d(TAG, "End of Pay Period: " + endOfPP.getMillis());

            QueryBuilder<Punch, String> queryBuilder = punchDao.queryBuilder();
            queryBuilder.where().eq(Job.JOB_FIELD_NAME, jobId.getID()).and()
                    .gt(Punch.TIME_OF_PUNCH, startOfPP.getMillis()).and()
                    .le(Punch.TIME_OF_PUNCH, endOfPP.getMillis());

            PreparedQuery<Punch> preparedQuery = queryBuilder.prepare();

            retValue = punchDao.query(preparedQuery);
            Log.d(TAG, "Punches for this pay period: " + retValue.size());
            for(Punch work : retValue){
                taskDAO.refresh(work.getTask());
                jobDAO.refresh(work.getJobNumber());
                punches.insert(work);
            }

        } catch(SQLException e){
            Log.d(TAG, e.getMessage());
        } catch (Exception e) {
            Log.d(TAG,e.getMessage());
        }
        return punches;
    }

     public List<Job> getJobs(){

        List<Job> retValue = null;
        try{
            ConnectionSource connectionSource = new AndroidConnectionSource(this);

            // instantiate the DAO to handle Account with String id
            Dao<Job,String> jobDAO = getJobDao();

            retValue = jobDAO.queryForAll();

            connectionSource.close();
        } catch(SQLException e){
            Log.d(TAG, e.getMessage());
        } catch (Exception e) {
            Log.d(TAG,e.getMessage());
        }
        return retValue;
    }

    public void dropAndTest(){
        try{

            final int numberOfTasks = 3; //Number of tasks
            final int jobNumber = 3; //Number of tasks

            // instantiate the DAO to handle Account with String id
            Dao<Punch,String> punchDao = getPunchDao();
            Dao<Task,String> taskDAO = getTaskDao();
            Dao<Job,String> jobDAO = getJobDao();
            Dao<Note,String> noteDAO = getNoteDao();

            //Create 1 Job
            DateMidnight jobMidnight = DateTime.now().withDayOfWeek(1).toDateMidnight();
            Job currentJob = new Job("My First Job", 7,
                    jobMidnight.toDateTime(), PayPeriodDuration.TWO_WEEKS);
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

            for(int i = 0; i < 3; i++){
                for(int j = 0; j < 15; j++){

                    DateTime tempTime = iTime.minusHours(j);
                    tempTime = tempTime.minusMinutes(rand.nextInt() % 60);
                    Punch temp = new Punch(currentJob, tasks.get(j % numberOfTasks), tempTime);
                    Note newNote = new Note(tempTime, currentJob,
                            "Note number " + String.valueOf(j + 1) );
                    newNote.setTask(tasks.get(j % numberOfTasks));

                    noteDAO.create(newNote);
                    punchDao.create(temp);
                }
                iTime = iTime.plusDays(1);
            }

        } catch(SQLException e){
            Log.e(TAG, e.getMessage());
        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }
    }
}
