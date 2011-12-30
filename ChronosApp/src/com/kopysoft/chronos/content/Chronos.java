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
import com.kopysoft.chronos.types.Job;
import com.kopysoft.chronos.types.Note;
import com.kopysoft.chronos.types.Punch;
import com.kopysoft.chronos.types.Task;

import java.sql.SQLException;
import java.util.List;

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

    public Dao<Punch, String> getPunchDao() throws SQLException {
        if (gPunchDoa == null) {
            gPunchDoa = getDao(Punch.class);
        }
        return gPunchDoa;
    }

    public Dao<Job, String> getJobDao() throws SQLException {
        if (gJobDoa == null) {
            gJobDoa = getDao(Job.class);
        }
        return gJobDoa;
    }

    public Dao<Task, String> getTaskDao() throws SQLException {
        if (gTaskDoa == null) {
            gTaskDoa = getDao(Task.class);
        }
        return gTaskDoa;
    }

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
            queryBuilder.where().eq(Punch.JOB_FIELD_NAME, jobId);
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
}
