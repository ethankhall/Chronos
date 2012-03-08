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
import org.joda.time.DateTime;

import java.sql.SQLException;
import java.util.Collections;
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

    public static final boolean enableLog = true;

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

            //create basic entries
            Dao<Task,String> taskDAO = getTaskDao();
            Dao<Job,String> jobDAO = getJobDao();

            //Create 1 Job
            DateTime jobMidnight = DateTime.now().withDayOfWeek(7).minusWeeks(1).toDateMidnight().toDateTime();
            Job currentJob = new Job("", 10,
                    jobMidnight.toDateTime(), PayPeriodDuration.TWO_WEEKS);
            currentJob.setDoubletimeThreshold(60);
            currentJob.setOvertimeThreshold(40);
            currentJob.setOvertimeEnabled(true);
            jobDAO.create(currentJob);
            
            Log.d(TAG, "Pay Rate: " + currentJob.getPayRate());

            Task newTask;   //Basic element
            newTask = new Task(currentJob, 0 , "Regular");
            taskDAO.create(newTask);
            newTask = new Task(currentJob, 1 , "Lunch Break");
            newTask.setEnablePayOverride(true);
            newTask.setPayOverride(0.0f);
            taskDAO.create(newTask);
            newTask = new Task(currentJob, 2 , "Other Break");
            newTask.setEnablePayOverride(true);
            newTask.setPayOverride(0.0f);
            taskDAO.create(newTask);
            newTask = new Task(currentJob, 3 , "Travel");
            taskDAO.create(newTask);
            newTask = new Task(currentJob, 4 , "Admin");
            taskDAO.create(newTask);
            newTask = new Task(currentJob, 5 , "Sick Leave");
            taskDAO.create(newTask);
            newTask = new Task(currentJob, 6 , "Personal Time");
            taskDAO.create(newTask);
            newTask = new Task(currentJob, 7 , "Other");
            taskDAO.create(newTask);
            newTask = new Task(currentJob, 8 , "Holiday Pay");
            taskDAO.create(newTask);
            
            Log.d(TAG, "Created Elements");


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

    public void deleteNote(Note note){
        try{
            Dao<Note, String> doa = getNoteDao();
            doa.delete(note);
        } catch( SQLException e){
            if(enableLog) Log.e(TAG, "Trouble deleting Note: " + e.getMessage());
        }
    }

    public void deletePunch(Punch punch){
        try{
            Dao<Punch, String> doa = getPunchDao();
            doa.delete(punch);
        } catch( SQLException e){
            if(enableLog) Log.e(TAG, "Trouble deleting Punch: " + e.getMessage());
        }
    }

    public void deleteTask(Task task){
        try{
            Dao<Task, String> doa = getTaskDao();
            doa.delete(task);
        } catch( SQLException e){
            if(enableLog) Log.e(TAG, "Trouble deleting Task: " + e.getMessage());
        }
    }

    public void insertPunch(Punch punch){
        try {
            Dao<Punch,String> punchDao = getPunchDao();
            punchDao.createOrUpdate(punch);
        } catch (SQLException e) {
            if(enableLog) Log.e(TAG, "Insert Punch: " + e.getMessage());
            e.getCause();
        }
    }

    public Punch getPunchById(int id){

        Punch retValue = null;
        try{
            // instantiate the DAO to handle Account with String id
            Dao<Task,String> taskDAO = getTaskDao();
            Dao<Punch,String> punchDao = getPunchDao();
            Dao<Job,String> jobDAO = getJobDao();

            //prep string
            QueryBuilder<Punch, String> queryBuilder = punchDao.queryBuilder();
            queryBuilder.where().eq(Punch.PUNCH_ID_FIELD, id);
            PreparedQuery<Punch> preparedQuery = queryBuilder.prepare();

            retValue = punchDao.queryForFirst(preparedQuery);
            if(retValue != null){
                taskDAO.refresh(retValue.getTask());
                jobDAO.refresh(retValue.getTask().getJob());
                jobDAO.refresh(retValue.getJob());
            }

        } catch(SQLException e){
            if(enableLog) Log.e(TAG, e.getMessage());
        } catch (Exception e) {
            if(enableLog) Log.e(TAG,e.getMessage());
        }
        return retValue;
    }

    public void updatePunch(Punch punch){

        try{
            // instantiate the DAO to handle Account with String id
            Dao<Punch,String> punchDao = getPunchDao();
            punchDao.update(punch);

        } catch(SQLException e){
            if(enableLog) Log.e(TAG, e.getMessage());
        } catch (Exception e) {
            if(enableLog) Log.e(TAG,e.getMessage());
        }
    }

    public List<Punch> getAllPunches(){

        List<Punch> retValue = null;
        try{
            // instantiate the DAO to handle Account with String id
            Dao<Punch,String> punchDao = getPunchDao();
            Dao<Task,String> taskDAO = getTaskDao();
            Dao<Job,String> jobDAO = getJobDao();

            //accountDao.refresh(order.getAccount());
            retValue = punchDao.queryForAll();
            for(Punch work : retValue){
                taskDAO.refresh(work.getTask());
                jobDAO.refresh(work.getTask().getJob());
            }

        } catch(SQLException e){
            if(enableLog) Log.e(TAG, e.getMessage());
        } catch (Exception e) {
            if(enableLog) Log.e(TAG,e.getMessage());
        }
        return retValue;
    }

    public List<Task> getAllTasks(){

        List<Task> retValue = null;
        try{
            // instantiate the DAO to handle Account with String id
            Dao<Task,String> taskDAO = getTaskDao();
            Dao<Job,String> jobDAO = getJobDao();
            

            //accountDao.refresh(order.getAccount());
            retValue = taskDAO.queryForAll();
            for(Task t : retValue){
                jobDAO.refresh(t.getJob());
                
            }


        } catch(SQLException e){
            if(enableLog) Log.e(TAG, e.getMessage());
        } catch (Exception e) {
            if(enableLog) Log.e(TAG,e.getMessage());
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
                jobDAO.refresh(work.getTask().getJob());
                jobDAO.refresh(work.getJob());
            }

        } catch(SQLException e){
            if(enableLog) Log.e(TAG, e.getMessage());
        } catch (Exception e) {
            if(enableLog) Log.e(TAG,e.getMessage());
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
            DateTime startOfDay = new DateTime(
                    date.getYear(),
                    date.getMonthOfYear(),
                    date.getDayOfMonth(),
                    startOfPP.getHourOfDay(),
                    startOfPP.getMinuteOfHour());

            DateTime endOfDay = startOfDay.plusDays(1);

            if(enableLog) Log.d(TAG, "Start of Day: " + startOfDay.getMillis());
            if(enableLog) Log.d(TAG, "End of Day: " + endOfDay.getMillis());

            QueryBuilder<Punch, String> queryBuilder = punchDao.queryBuilder();
            queryBuilder.where().eq(Job.JOB_FIELD_NAME, jobId.getID()).and()
                    .between(Punch.TIME_OF_PUNCH, startOfDay.getMillis(), endOfDay.getMillis());

            PreparedQuery<Punch> preparedQuery = queryBuilder.prepare();

            punches = punchDao.query(preparedQuery);
            Collections.sort(punches);
            if(enableLog) Log.d(TAG, "Punches for this day: " + punches.size());
            for(Punch work : punches){
                taskDAO.refresh(work.getTask());
                jobDAO.refresh(work.getTask().getJob());
                jobDAO.refresh(work.getJob());
                Log.d(TAG, "in loop Pay Rate: " + work.getJob().getPayRate());
                
            }


        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }

        return punches;
    }

    public PunchTable getAllPunchesForThisPayPeriodByJob(Job jobId){

        //Get the start and end of pay period
        PayPeriodHolder pph = new PayPeriodHolder(jobId);
        DateTime startOfPP = pph.getStartOfPayPeriod().toDateTime();
        DateTime endOfPP = pph.getEndOfPayPeriod().toDateTime();
    
        Log.d(TAG, "start of pp: " + startOfPP);
        Log.d(TAG, "end of pp: " + endOfPP);

        return getAllPunchesForPayPeriodByJob(jobId, startOfPP, endOfPP);
    }

    public PunchTable getAllPunchesForPayPeriodByJob(Job jobId, DateTime startDate, DateTime endDate){

        PunchTable punches = null;
        try{

            // instantiate the DAO to handle Account with String id
            Dao<Punch,String> punchDao = getPunchDao();
            Dao<Task,String> taskDAO = getTaskDao();
            Dao<Job,String> jobDAO = getJobDao();

            punches = new PunchTable(startDate, endDate, jobId);

            if(enableLog) Log.d(TAG, "Start of Pay Period: " + startDate.getMillis());
            if(enableLog) Log.d(TAG, "End of Pay Period: " + endDate.getMillis());

            QueryBuilder<Punch, String> queryBuilder = punchDao.queryBuilder();
            queryBuilder.where().eq(Job.JOB_FIELD_NAME, jobId.getID()).and()
                    .between(Punch.TIME_OF_PUNCH, startDate.getMillis(), endDate.getMillis());

            PreparedQuery<Punch> preparedQuery = queryBuilder.prepare();

            List<Punch> retValue = punchDao.query(preparedQuery);
            if(enableLog) Log.d(TAG, "Punches for this pay period: " + retValue.size());
            for(Punch work : retValue){
                taskDAO.refresh(work.getTask());
                jobDAO.refresh(work.getTask().getJob());
                jobDAO.refresh(work.getJob());
                punches.insert(work);
            }

        } catch(SQLException e){
            if(enableLog) Log.e(TAG, e.getMessage());
        }
        return punches;
    }

     public List<Job> getJobs(){

        List<Job> retValue = new LinkedList<Job>();
        try{

            // instantiate the DAO to handle Account with String id
            Dao<Job,String> jobDAO = getJobDao();

            retValue = jobDAO.queryForAll();

        } catch(SQLException e){
            if(enableLog) Log.e(TAG, e.getMessage());
        } catch (Exception e) {
            if(enableLog) Log.e(TAG,e.getMessage());
        }
        return retValue;
    }

    public void dropAndTest(){
        try{

            // instantiate the DAO to handle Account with String id
            Dao<Punch,String> punchDao = getPunchDao();
            Dao<Task,String> taskDAO = getTaskDao();
            Dao<Job,String> jobDAO = getJobDao();
            Dao<Note,String> noteDAO = getNoteDao();

            List<Task> tasks = taskDAO.queryForAll();
            Job currentJob =  jobDAO.queryForAll().get(0);

            DateTime iTime = new DateTime();
            Random rand = new Random();
            DateTime tempTime = null;

            for(int i = 0; i < 3; i++){
                for(int j = 0; j < 5; j++){

                    tempTime = iTime.minusHours(j);
                    tempTime = tempTime.minusMinutes(rand.nextInt() % 60);
                    Punch temp = new Punch(currentJob,
                            tasks.get(0),
                            tempTime);

                    Note newNote = new Note(tempTime, currentJob,
                            "Note number " + String.valueOf(j + 1) );
                    newNote.setTask(tasks.get(j % tasks.size()));

                    noteDAO.create(newNote);
                    punchDao.create(temp);
                }

                tempTime = tempTime.minusMinutes(rand.nextInt() % 60);
                punchDao.create(new Punch(currentJob,
                        tasks.get((int)(Math.random() * 100) % tasks.size()),
                        tempTime) );

                iTime = iTime.plusDays(1);
            }

        } catch(SQLException e){
            Log.e(TAG, e.getMessage());
        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }
    }
}
