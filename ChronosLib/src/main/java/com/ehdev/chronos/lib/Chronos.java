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

package com.ehdev.chronos.lib;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import com.ehdev.chronos.lib.enums.OvertimeOptions;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.ehdev.chronos.lib.enums.Defines;
import com.ehdev.chronos.lib.enums.PayPeriodDuration;
import com.ehdev.chronos.lib.types.Job;
import com.ehdev.chronos.lib.types.Note;
import com.ehdev.chronos.lib.types.Punch;
import com.ehdev.chronos.lib.types.Task;
import com.ehdev.chronos.lib.types.holders.PayPeriodHolder;
import com.ehdev.chronos.lib.types.holders.PunchTable;
import org.apache.commons.jxpath.ri.model.dom.DOMAttributeIterator;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.*;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.*;

public class Chronos extends OrmLiteSqliteOpenHelper {

    private static final String TAG = "Chronos - SQL";

    //0.9 = 7
    //1.0.1 - 1.1.0 = 10
    //1.2.0	= 11
    //2.0.0RC1 = 15
    //2.0.0 = 20
    //2.1.0 = 21

    private static final int DATABASE_VERSION = 21;
    public static final String DATABASE_NAME = "Chronos";
    private Context gContext;

    Dao<Punch, String>  gPunchDoa = null;
    Dao<Task, String>   gTaskDoa = null;
    Dao<Job, String>    gJobDoa = null;
    Dao<Note, String>    gNoteDoa = null;

    //public static final boolean enableLog = Defines.DEBUG_PRINT;
    public static final boolean enableLog = Defines.DEBUG_PRINT;

    public Chronos(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        gContext = context;
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
            DateTime jobMidnight = new DateMidnight().toDateTime().minusWeeks(1).withZone(DateTimeZone.getDefault());
            Log.d(TAG, "start of time:" + jobMidnight);
            Log.d(TAG, "time zone:" + DateTimeZone.getDefault() );
            Job currentJob = new Job("", 7.25f,
                    jobMidnight, PayPeriodDuration.TWO_WEEKS);
            currentJob.setDoubletimeThreshold(60);
            currentJob.setOvertimeThreshold(40);
            currentJob.setOvertimeOptions(OvertimeOptions.WEEK);
            jobDAO.create(currentJob);
            
            Log.d(TAG, "Pay Rate: " + currentJob.getPayRate());

            Task newTask;   //Basic element
            newTask = new Task(currentJob, 0 , "Regular");
            taskDAO.create(newTask);
            newTask = new Task(currentJob, 1 , "Lunch Break");
            newTask.setEnablePayOverride(true);
            newTask.setPayOverride(-7.25f);
            taskDAO.create(newTask);
            newTask = new Task(currentJob, 2 , "Other Break");
            newTask.setEnablePayOverride(true);
            newTask.setPayOverride(-7.25f);
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
            //dropAndTest();

        } catch (SQLException e) {
            Log.e(TAG, "Could not create new table for Thing", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try{
            Log.w(TAG, "Upgrading database, this will drop tables and recreate.");
            Log.w(TAG, "oldVerion: " + oldVersion + "\tnewVersion: " + newVersion);

            //Back up database
            try {
                File sd = Environment.getExternalStorageDirectory();
                File data = Environment.getDataDirectory();
                if (sd.canWrite()) {
                    String currentDBPath = "/data/com.kopysoft.chronos/databases/" + DATABASE_NAME;
                    String backupDBPath = DATABASE_NAME + ".db";
                    File currentDB = new File(data, currentDBPath);
                    File backupDB = new File(sd, backupDBPath);
                    if (currentDB.exists()) {
                        FileChannel src = new FileInputStream(currentDB).getChannel();
                        FileChannel dst = new FileOutputStream(backupDB).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();
                    }
                }
            }catch (Exception e) {
                Log.e(TAG, "ERROR: Can not move file");
            }

            /*
            db.execSQL("CREATE TABLE " + TABLE_NAME_CLOCK +
                    " ( _id INTEGER PRIMARY KEY NOT NULL, time LONG NOT NULL, actionReason INTEGER NOT NULL )");
            db.execSQL("CREATE TABLE " + TABLE_NAME_NOTE +
                    " ( _id LONG PRIMARY KEY, note_string TEXT NOT NULL, time LONG NOT NULL )");
            */


            if(oldVersion < 15){


                DateTime jobMidnight = DateTime.now().withDayOfWeek(7).minusWeeks(1)
                        .toDateMidnight().toDateTime().withZone(DateTimeZone.getDefault());
                Job currentJob = new Job("", 10,
                        jobMidnight, PayPeriodDuration.TWO_WEEKS);

                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(gContext);
                currentJob.setPayRate(Float.valueOf(pref.getString("normal_pay", "7.25")) );
                currentJob.setOvertime(Float.valueOf(pref.getString("over_time_threshold", "40")) );
                currentJob.setDoubletimeThreshold(Float.valueOf(pref.getString("double_time_threshold", "60")) );
                SharedPreferences.Editor edit = pref.edit();
                edit.remove("8_or_40_hours");   //Moved from string to boolean
                edit.commit();
                String date[] = pref.getString("date", "2011.1.17").split("\\p{Punct}");
                jobMidnight = new DateTime(Integer.parseInt(date[0]),
                        Integer.parseInt(date[1]),
                        Integer.parseInt(date[2]),
                        0,
                        0);

                currentJob.setStartOfPayPeriod(jobMidnight.withZone(DateTimeZone.getDefault()));


                List<Punch> punches = new LinkedList<Punch>();
                List<Task> tasks = new LinkedList<Task>();
                List<Note> notes = new LinkedList<Note>();

                Task newTask;   //Basic element
                newTask = new Task(currentJob, 0 , "Regular");
                tasks.add(newTask);
                newTask = new Task(currentJob, 1 , "Lunch Break");
                newTask.setEnablePayOverride(true);
                newTask.setPayOverride(-7.25f);
                tasks.add(newTask);
                newTask = new Task(currentJob, 2 , "Other Break");
                newTask.setEnablePayOverride(true);
                newTask.setPayOverride(-7.25f);
                tasks.add(newTask);
                newTask = new Task(currentJob, 3 , "Travel");
                tasks.add(newTask);
                newTask = new Task(currentJob, 4 , "Admin");
                tasks.add(newTask);
                newTask = new Task(currentJob, 5 , "Sick Leave");
                tasks.add(newTask);
                newTask = new Task(currentJob, 6 , "Personal Time");
                tasks.add(newTask);
                newTask = new Task(currentJob, 7 , "Other");
                tasks.add(newTask);
                newTask = new Task(currentJob, 8 , "Holiday Pay");
                tasks.add(newTask);

                Cursor cursor = db.query("clockactions", null,
                        null, null, null, null, "_id desc");
                
                final int colTime = cursor.getColumnIndex("time");
                final int colAR = cursor.getColumnIndex("actionReason");

                if (cursor.moveToFirst()) {
                    do {
                        long time = cursor.getLong(colTime);
                        Task type = tasks.get(0);
                        if(colAR != -1){
                            type = tasks.get(cursor.getInt(colAR));
                        }                             
                        punches.add(new Punch(currentJob, type, new DateTime(time)));


                    } while (cursor.moveToNext());
                }

                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }

                cursor = db.query("notes", null,
                        null, null, null, null, "_id desc");

                final int colInsertTime = cursor.getColumnIndex("time");
                final int colText = cursor.getColumnIndex("note_string");

                if (cursor.moveToFirst()) {
                    do {
                        long time = cursor.getLong(colInsertTime);
                        String note = cursor.getString(colText);
                        notes.add(new Note(new DateTime(time), currentJob,  note));


                    } while (cursor.moveToNext());
                }

                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }

                db.execSQL("DROP TABLE IF EXISTS clockactions");
                db.execSQL("DROP TABLE IF EXISTS notes");
                db.execSQL("DROP TABLE IF EXISTS misc");

                //Recreate DB
                TableUtils.createTable(connectionSource, Punch.class); //Punch - Create Table
                TableUtils.createTable(connectionSource, Task.class); //Task - Create Table
                TableUtils.createTable(connectionSource, Job.class); //Job - Create Table
                TableUtils.createTable(connectionSource, Note.class); //Task - Create Table

                //recreate entries
                Dao<Task,String> taskDAO = getTaskDao();
                Dao<Job,String> jobDAO = getJobDao();
                Dao<Note,String> noteDAO = getNoteDao();
                Dao<Punch,String> punchDOA = getPunchDao();

                jobDAO.create(currentJob);

                for(Task t: tasks){
                    taskDAO.create(t);
                }

                for(Note n: notes){
                    noteDAO.create(n);
                }

                for(Punch p: punches){
                    punchDOA.create(p);
                }

                //"CREATE TABLE " + TABLE_NAME_NOTE " ( _id LONG PRIMARY KEY, note_string TEXT NOT NULL, time LONG NOT NULL )");
            } else if(oldVersion == 15) {

                //Drop
                //DB - 15
                //TableUtils.dropTable(connectionSource, Punch.class, true); //Punch - Drop all
                //TableUtils.dropTable(connectionSource, Task.class, true); //Task - Drop all
                //TableUtils.dropTable(connectionSource, Job.class, true); //Job - Drop all
                //TableUtils.dropTable(connectionSource, Note.class, true); //Note - Drop all
                Dao<Task,String> taskDAO = getTaskDao();
                List<Task> tasks = taskDAO.queryForAll();

                db.execSQL("DROP TABLE IF EXISTS tasks");

                //create
                TableUtils.createTable(connectionSource, Task.class); //Task - Create Table

                for(Task t: tasks){
                    taskDAO.create(t);
                }
            } else if(oldVersion == 16) {

                //Drop
                //DB - 15
                //TableUtils.dropTable(connectionSource, Punch.class, true); //Punch - Drop all
                //TableUtils.dropTable(connectionSource, Task.class, true); //Task - Drop all
                //TableUtils.dropTable(connectionSource, Job.class, true); //Job - Drop all
                TableUtils.dropTable(connectionSource, Note.class, true); //Note - Drop all

                //create
                TableUtils.createTable(connectionSource, Note.class); //Task - Create Table

            } else if(oldVersion == 17) {

                //update db from old version
                Dao<Job, String> dao = getJobDao();
                dao.executeRaw("ALTER TABLE `jobs` ADD COLUMN fourtyHourWeek BOOLEAN DEFAULT 1;");

            } else if(oldVersion == 18){


                Dao<Task,String> taskDAO = getTaskDao();
                List<Task> tasks = taskDAO.queryForAll();
                Job currentJob = getAllJobs().get(0);
                if(tasks.size() == 0){
    
                    Task newTask;   //Basic element
                    newTask = new Task(currentJob, 0 , "Regular");
                    tasks.add(newTask);
                    newTask = new Task(currentJob, 1 , "Lunch Break");
                    newTask.setEnablePayOverride(true);
                    newTask.setPayOverride(-7.25f);
                    tasks.add(newTask);
                    newTask = new Task(currentJob, 2 , "Other Break");
                    newTask.setEnablePayOverride(true);
                    newTask.setPayOverride(-7.25f);
                    tasks.add(newTask);
                    newTask = new Task(currentJob, 3 , "Travel");
                    tasks.add(newTask);
                    newTask = new Task(currentJob, 4 , "Admin");
                    tasks.add(newTask);
                    newTask = new Task(currentJob, 5 , "Sick Leave");
                    tasks.add(newTask);
                    newTask = new Task(currentJob, 6 , "Personal Time");
                    tasks.add(newTask);
                    newTask = new Task(currentJob, 7 , "Other");
                    tasks.add(newTask);
                    newTask = new Task(currentJob, 8 , "Holiday Pay");
                    tasks.add(newTask);
                    
                    for(Task t : tasks){
                        taskDAO.createOrUpdate(t);
                    }
                }
            } else if(oldVersion == 19){

                /*
                Cursor cursor = db.query("jobs", null,
                        null, null, null, null, Job.JOB_FIELD_NAME +" desc");
                final int colid= cursor.getColumnIndex(Job.JOB_FIELD_NAME);

                if (cursor.moveToFirst()) {
                    long id = cursor.getLong(colid);
                    db.execSQL("UPDATE jobs SET " + Job.DURATION_FIELD_NAME + " = '" + PayPeriodDuration.FIRST_FIFTEENTH
                            + "' WHERE " + Job.JOB_FIELD_NAME + "=" + id );
                */
                try {
                    TableUtils.dropTable(connectionSource, Job.class, true); //Job - Create Table

                    TableUtils.createTable(connectionSource, Job.class); //Job - Create Table


                    DateTime jobMidnight = new DateMidnight().toDateTime().minusWeeks(1).withZone(DateTimeZone.getDefault());

                    Job thisJob = new Job("", 7.25f,
                            jobMidnight, PayPeriodDuration.TWO_WEEKS);

                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(gContext);
                    try{
                        thisJob.setPayRate(Float.valueOf(pref.getString("normal_pay", "7.25")) );
                    } catch (NumberFormatException e){
                        thisJob.setPayRate(7.25f);
                        Log.d(TAG, e.getMessage());
                    }

                    try{
                        thisJob.setOvertime(Float.valueOf(pref.getString("over_time_threshold", "40")) );
                    } catch (NumberFormatException e){
                        thisJob.setOvertime(40f);
                        Log.d(TAG, e.getMessage());
                    }

                    try{
                        thisJob.setDoubletimeThreshold(Float.valueOf(pref.getString("double_time_threshold", "60")) );
                    } catch (NumberFormatException e){
                        thisJob.setDoubletimeThreshold( 60f );
                        Log.d(TAG, e.getMessage());
                    }

                    String date[] = pref.getString("date", "2011.1.17").split("\\p{Punct}");
                    String time[] = pref.getString("time", "00:00").split("\\p{Punct}");
                    thisJob.setStartOfPayPeriod(new DateTime(Integer.parseInt(date[0]),
                            Integer.parseInt(date[1]),
                            Integer.parseInt(date[2]),
                            Integer.parseInt(time[0]),
                            Integer.parseInt(time[1])
                    ));
                    switch (Integer.parseInt(pref.getString("len_of_month", "2"))){
                        case 1:
                            thisJob.setDuration(PayPeriodDuration.ONE_WEEK);
                            break;
                        case 2:
                            thisJob.setDuration(PayPeriodDuration.TWO_WEEKS);
                            break;
                        case 3:
                            thisJob.setDuration(PayPeriodDuration.THREE_WEEKS);
                            break;
                        case 4:
                            thisJob.setDuration(PayPeriodDuration.FOUR_WEEKS);
                            break;
                        case 5:
                            thisJob.setDuration(PayPeriodDuration.FULL_MONTH);
                            break;
                        case 6:
                            thisJob.setDuration(PayPeriodDuration.FIRST_FIFTEENTH);
                            break;
                        default:
                            thisJob.setDuration(PayPeriodDuration.TWO_WEEKS);
                            break;
                    }

                    getJobDao().create(thisJob);

                } catch (SQLException e1) {
                    e1.printStackTrace();
                }


            } else if(oldVersion == 20 ){
                getJobDao().executeRaw("ALTER TABLE 'jobs' ADD COLUMN '" + Job.OVERTIME_OPTIONS + "'  VARCHAR default 'NONE';");
                getJobDao().executeRaw("ALTER TABLE 'jobs' ADD COLUMN '" + Job.SATURDAY_OVERRIDE_FIELD + "'  VARCHAR default 'NONE';");
                getJobDao().executeRaw("ALTER TABLE 'jobs' ADD COLUMN '" + Job.SUNDAY_OVERRIDE_FIELD + "'  VARCHAR default 'NONE';");
                List<Job> jobList = getAllJobs();
                for(Job job : jobList){
                    GenericRawResults<String[]> rawResults =
                            getJobDao().queryRaw(
                                    "select fourtyHourWeek,overTimeEnabled  from jobs where job_id = " + job.getID());
                    String[] results = rawResults.getResults().get(0);
                    if(results[0] == "0"){
                        job.setOvertimeOptions(OvertimeOptions.NONE);
                    } else {
                        if(results[1] == "0"){
                            job.setOvertimeOptions(OvertimeOptions.DAY);
                        } else if(results[1] == "1"){ //being paranoid
                            job.setOvertimeOptions(OvertimeOptions.WEEK);
                        }
                    }
                }

                //delete stuff
                getJobDao().executeRaw("ALTER TABLE 'jobs' DROP COLUMN 'fourtyHourWeek';");
                getJobDao().executeRaw("ALTER TABLE 'jobs' DROP COLUMN 'overTimeEnabled';");
            }

        } catch (SQLException e) {
            e.printStackTrace();
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
            Log.e(TAG, "Insert Punch: " + e.getMessage());
        }
    }

    public Task getTaskById(int id){

        Task retValue = new Task();
        try{
            // instantiate the DAO to handle Account with String id
            Dao<Task,String> taskDAO = getTaskDao();
            Dao<Job,String> jobDAO = getJobDao();

            //prep string
            QueryBuilder<Task, String> queryBuilder = taskDAO.queryBuilder();
            queryBuilder.where().eq(Task.TASK_FIELD_NAME, id);
            PreparedQuery<Task> preparedQuery = queryBuilder.prepare();

            retValue = taskDAO.queryForFirst(preparedQuery);
            if(retValue != null){
                jobDAO.refresh(retValue.getJob());
            }

        } catch(SQLException e){
            if(enableLog) Log.e(TAG, e.getMessage());
        } catch (Exception e) {
            if(enableLog) Log.e(TAG,e.getMessage());
        }
        return retValue;
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

    public void updateNote(Note note){

        try{
            // instantiate the DAO to handle Account with String id
            Dao<Note,String> noteDAO = getNoteDao();
            noteDAO.createOrUpdate(note);

        } catch(SQLException e){
            if(enableLog) Log.e(TAG, e.getMessage());
        } catch (Exception e) {
            if(enableLog) Log.e(TAG,e.getMessage());
        }
    }

    public void updateTask(Task task){

        try{
            // instantiate the DAO to handle Account with String id
            Dao<Task,String> taskDAO = getTaskDao();
            taskDAO.createOrUpdate(task);

        } catch(SQLException e){
            if(enableLog) Log.e(TAG, e.getMessage());
        } catch (Exception e) {
            if(enableLog) Log.e(TAG,e.getMessage());
        }
    }

    public void updateJob(Job job){

        try{
            // instantiate the DAO to handle Account with String id
            Dao<Job,String> jobDAO = getJobDao();
            jobDAO.update(job);
            Log.d(TAG, "Pay: " + job.getPayRate());

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

    public static DateTime getDateFromStartOfPayPeriod(DateTime StartOfPP, DateTime date){

        DateTime newDate;
        DateTimeZone startZone = StartOfPP.getZone();
        DateTimeZone endZone = date.getZone();
        long dateTime = date.getMillis() - StartOfPP.getMillis();
        
        long offset = endZone.getOffset(date) - startZone.getOffset(StartOfPP);
        //System.out.println("offset: " + offset);
        dateTime += offset;
        
        //System.out.println("millis diff: " + (dateTime) );
        int days = (int)(dateTime / 1000 / 60 / 60 / 24);
        newDate = StartOfPP.plusDays(days);
        //System.out.println("Days to add: " + days);

        return newDate;
    }

    public static DateTime getDateFromStartOfPayPeriod(Job thisJob, DateTime date){

        return getDateFromStartOfPayPeriod(thisJob.getStartOfPayPeriod(), date);
    }
    
    public Note getNoteByDay(DateTime date){

        Note retValue = new Note(date, null,  "");
        try{
            // instantiate the DAO to handle Account with String id
            Dao<Note,String> noteDAO = getNoteDao();
            Dao<Job,String> jobDAO = getJobDao();
            Job thisJob = getAllJobs().get(0);

            DateTime newDate = getDateFromStartOfPayPeriod(thisJob, date);

            QueryBuilder<Note, String> queryBuilder = noteDAO.queryBuilder();
            queryBuilder.where().eq(Note.DATE_FIELD, newDate.getMillis());
            PreparedQuery<Note> preparedQuery = queryBuilder.prepare();

            retValue = noteDAO.queryForFirst(preparedQuery);
            if(retValue != null){
                jobDAO.update(retValue.getJob());
            } else {
                retValue = new Note(newDate, thisJob,  "");
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

    public List<Task> getAllTasks(Job curJob){

        List<Task> retValue = null;
        try{
            // instantiate the DAO to handle Account with String id
            Dao<Task,String> taskDAO = getTaskDao();
            Dao<Job,String> jobDAO = getJobDao();

            //accountDao.refresh(order.getAccount());
            retValue = taskDAO.queryForAll();

            QueryBuilder<Task, String> queryBuilder = taskDAO.queryBuilder();
            queryBuilder.where().eq(Job.JOB_FIELD_NAME, curJob.getID());
            PreparedQuery<Task> preparedQuery = queryBuilder.prepare();

            retValue = taskDAO.query(preparedQuery);
            for(Task work : retValue){
                jobDAO.refresh(work.getJob());
            }

        } catch(SQLException e){
            if(enableLog) Log.e(TAG, e.getMessage());
        } catch (Exception e) {
            if(enableLog) Log.e(TAG,e.getMessage());
        }
        return retValue;
    }

    public List<Note> getAllNotes(Job curJob){

        List<Note> retValue = null;
        try{
            // instantiate the DAO to handle Account with String id
            Dao<Task,String> taskDAO = getTaskDao();
            Dao<Job,String> jobDAO = getJobDao();
            Dao<Note,String> noteDAO = getNoteDao();

            //accountDao.refresh(order.getAccount());

            QueryBuilder<Note, String> queryBuilder = noteDAO.queryBuilder();
            queryBuilder.where().eq(Job.JOB_FIELD_NAME, curJob.getID());
            PreparedQuery<Note> preparedQuery = queryBuilder.prepare();

            retValue = noteDAO.query(preparedQuery);
            for(Note work : retValue){
                jobDAO.refresh(work.getJob());
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

            //Period prd = new Period(jobId.getStartOfPayPeriod(), date);
            //Duration dur = new Duration(jobId.getStartOfPayPeriod(), date);
            DateTime startOfDay = date;
            //if(jobId.getStartOfPayPeriod().isBefore(date))
            //    startOfDay = jobId.getStartOfPayPeriod().plusDays(prd.toStandardDays().get(DurationFieldType.days()));
            //else
            //    startOfDay = jobId.getStartOfPayPeriod().minusDays(prd.toStandardDays().get(DurationFieldType.days()));
            //DateTime startOfDay = jobId.getStartOfPayPeriod().plusDays(displacementInDays);

            DateTime endOfDay = startOfDay.plusDays(1);

            if(enableLog) Log.d(TAG, "Start of Day: " + startOfDay.getMillis());
            if(enableLog) Log.d(TAG, "End of Day: " + endOfDay.getMillis());

            QueryBuilder<Punch, String> queryBuilder = punchDao.queryBuilder();
            queryBuilder.where().eq(Job.JOB_FIELD_NAME, jobId.getID()).and()
                    .between(Punch.TIME_OF_PUNCH, startOfDay.getMillis(), endOfDay.getMillis());

            PreparedQuery<Punch> preparedQuery = queryBuilder.prepare();

            punches = punchDao.query(preparedQuery);
            if(enableLog) Log.d(TAG, "Punches for this day: " + punches.size());
            for(Punch work : punches){
                taskDAO.refresh(work.getTask());
                jobDAO.refresh(work.getTask().getJob());
                jobDAO.refresh(work.getJob());
                //if(enableLog) Log.d(TAG, "in loop Pay Rate: " + work.getJob().getPayRate());
                
            }
            Collections.sort(punches);

        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }

        //Log.d(TAG, "Number of punches: " + punches.size());
        return punches;
    }

    public List<Punch> getPunchesByJob(Job jobId){
        List<Punch> punches = new LinkedList<Punch>();


        // instantiate the DAO to handle Account with String id
        try {
            Dao<Punch,String> punchDao = getPunchDao();
            Dao<Task,String> taskDAO = getTaskDao();
            Dao<Job,String> jobDAO = getJobDao();

            QueryBuilder<Punch, String> queryBuilder = punchDao.queryBuilder();
            queryBuilder.where().eq(Job.JOB_FIELD_NAME, jobId.getID());

            PreparedQuery<Punch> preparedQuery = queryBuilder.prepare();

            punches = punchDao.query(preparedQuery);
            if(enableLog) Log.d(TAG, "Punches for this day: " + punches.size());
            for(Punch work : punches){
                taskDAO.refresh(work.getTask());
                jobDAO.refresh(work.getTask().getJob());
                jobDAO.refresh(work.getJob());
                //if(enableLog) Log.d(TAG, "in loop Pay Rate: " + work.getJob().getPayRate());

            }
            Collections.sort(punches);

        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }

        //Log.d(TAG, "Number of punches: " + punches.size());
        return punches;
    }


    public PunchTable getAllPunchesForThisPayPeriodByJob(Job jobId){

        //Get the start and end of pay period
        PayPeriodHolder pph = new PayPeriodHolder(jobId);
        DateTime startOfPP = pph.getStartOfPayPeriod();
        DateTime endOfPP = pph.getEndOfPayPeriod();
    
        if(enableLog) Log.d(TAG, "start of pp: " + startOfPP);
        if(enableLog) Log.d(TAG, "end of pp: " + endOfPP);

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

            if(enableLog) Log.d(TAG, "Chronos Start of Pay Period: " + startDate.getMillis());
            if(enableLog) Log.d(TAG, "Chronos End of Pay Period: " + endDate.getMillis());

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

     public List<Job> getAllJobs(){

        List<Job> retValue = new LinkedList<Job>();
        try{

            // instantiate the DAO to handle Account with String id
            Dao<Job,String> jobDAO = getJobDao();

            retValue = jobDAO.queryForAll();

        } catch(SQLException e){
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
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
    
    static public boolean putDataOnSDCard(Context context, boolean oldFormat){

        if(getCardWriteStatus() == false){

            CharSequence text = "Could not write to SD Card!.";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return false;
        }

        File directory =  Environment.getExternalStorageDirectory();
        //File backup = new File(directory, "Chronos_Backup.csv");
        File backup;
        if(!oldFormat)
            backup = new File(directory, "Chronos_Backup.csv");
        else
            backup = new File(directory, "Chronos_Backup.cvs");
        BufferedWriter br;

        Chronos chron = new Chronos(context);
        List<Punch> punches = chron.getAllPunches();
        chron.close();
        Log.d(TAG, "Backup Size: " + punches.size());

        try{
            br = new BufferedWriter( new FileWriter(backup));

            for(Punch p : punches){
                if(!oldFormat)
                    br.write(p.toCVS(context));
                else
                    br.write(p.toCVSLegacy(context));
            }
            br.close();
        } catch (IOException e){
            Log.e(TAG, e.getMessage());
            return false;
        }
        return true;
    }

    static public boolean getDataOnSDCard(Context context, boolean  oldFormat) {
        if(getCardReadStatus() == false){

            CharSequence text = "Could not read to SD Card!.";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return false;
        }

        //Create 1 Job
        DateTime jobMidnight = DateTime.now().withDayOfWeek(7).minusWeeks(1)
                .toDateMidnight().toDateTime().withZone(DateTimeZone.getDefault());
        Job currentJob = new Job("", 10,
                jobMidnight, PayPeriodDuration.TWO_WEEKS);
        currentJob.setDoubletimeThreshold(60);
        currentJob.setOvertimeThreshold(40);
        currentJob.setOvertimeOptions(OvertimeOptions.WEEK);

        List<Punch> punches = new LinkedList<Punch>();
        List<Task> tasks = new LinkedList<Task>();
        List<Note> notes = new LinkedList<Note>();

        Task newTask;   //Basic element
        newTask = new Task(currentJob, 0 , "Regular");
        tasks.add(newTask);
        newTask = new Task(currentJob, 1 , "Lunch Break");
        newTask.setEnablePayOverride(true);
        newTask.setPayOverride(0.0f);
        tasks.add(newTask);
        newTask = new Task(currentJob, 2 , "Other Break");
        newTask.setEnablePayOverride(true);
        newTask.setPayOverride(0.0f);
        tasks.add(newTask);
        newTask = new Task(currentJob, 3 , "Travel");
        tasks.add(newTask);
        newTask = new Task(currentJob, 4 , "Admin");
        tasks.add(newTask);
        newTask = new Task(currentJob, 5 , "Sick Leave");
        tasks.add(newTask);
        newTask = new Task(currentJob, 6 , "Personal Time");
        tasks.add(newTask);
        newTask = new Task(currentJob, 7 , "Other");
        tasks.add(newTask);
        newTask = new Task(currentJob, 8 , "Holiday Pay");
        tasks.add(newTask);

        try{
            File directory =  Environment.getExternalStorageDirectory();
            File backup;
            if(!oldFormat)
                backup = new File(directory, "Chronos_Backup.csv");
            else
                backup = new File(directory, "Chronos_Backup.cvs");
            if(!backup.exists()){
                return false;
            }

            BufferedReader br = new BufferedReader( new FileReader(backup));
            String strLine = br.readLine();


            //id,date,name,task name, date in ms, job num, task num
            //1,Sun Mar 11 2012 15:46,null,Regular,1331498803269,1,1
            while( strLine != null){
                //Log.d(TAG, strLine);
                String[] parcedString = strLine.split(",");
                long time;
                int task;

                if(!oldFormat){
                    time = Long.parseLong(parcedString[4]);
                    task = Integer.parseInt(parcedString[6]);
                } else {
                    time = Long.parseLong(parcedString[1]);
                    task = Integer.parseInt(parcedString[2]);
                    //System.out.println(parcedString.length);

                    if(parcedString.length > 4 && StringUtils.isNotBlank(parcedString[4])){
                        String noteContent = parcedString[4];
                        Note note = new Note(Chronos.getDateFromStartOfPayPeriod(currentJob, new DateTime(time)),
                                currentJob, noteContent);
                        notes.add(note);
                    }
                }

                //Job iJob, Task iPunchTask, DateTime iTime
                punches.add(new Punch(currentJob, tasks.get(task - 1), new DateTime(time)));
                strLine = br.readLine();
            }
        } catch (Exception e){
            
            e.printStackTrace();
            if(e != null && e.getCause() != null){
                Log.e(TAG, e.getCause().toString());
            }
            return false;
        }

        //Log.d(TAG, "Number of punches: " + punches.size());
        try{
            Chronos chronos = new Chronos(context);
            TableUtils.dropTable(chronos.getConnectionSource(), Punch.class, true); //Punch - Drop all
            TableUtils.dropTable(chronos.getConnectionSource(), Task.class, true); //Task - Drop all
            TableUtils.dropTable(chronos.getConnectionSource(), Job.class, true); //Job - Drop all
            TableUtils.dropTable(chronos.getConnectionSource(), Note.class, true); //Note - Drop all

            //Recreate DB
            TableUtils.createTable(chronos.getConnectionSource(), Punch.class); //Punch - Create Table
            TableUtils.createTable(chronos.getConnectionSource(), Task.class); //Task - Create Table
            TableUtils.createTable(chronos.getConnectionSource(), Job.class); //Job - Create Table
            TableUtils.createTable(chronos.getConnectionSource(), Note.class); //Task - Create Table

            //recreate entries
            Dao<Task,String> taskDAO = chronos.getTaskDao();
            Dao<Job,String> jobDAO = chronos.getJobDao();
            Dao<Punch,String> punchDOA = chronos.getPunchDao();
            Dao<Note,String> noteDOA = chronos.getNoteDao();

            jobDAO.create(currentJob);

            for(Task t: tasks){
                taskDAO.create(t);
            }

            for(Punch p: punches){
                punchDOA.create(p);
            }

            HashMap<DateTime, Note> merger= new HashMap<DateTime, Note>();
            for(Note n: notes){
                merger.put(n.getTime(), n);
            }

            for(DateTime dt : merger.keySet()){
                noteDOA.create(merger.get(dt));
            }

            chronos.close();

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }

        return true;
    }

    public static boolean getCardWriteStatus(){

        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            return true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            return false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            return false;
        }
    }

    public static boolean getCardReadStatus(){

        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            return true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            return true;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            return false;
        }
    }
}