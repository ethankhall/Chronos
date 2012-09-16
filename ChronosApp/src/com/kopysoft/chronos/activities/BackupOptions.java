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

package com.kopysoft.chronos.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import com.ehdev.chronos.lib.Chronos;
import com.ehdev.chronos.lib.JsonToSql;
import com.ehdev.chronos.lib.enums.Defines;
import com.ehdev.chronos.lib.types.Job;
import com.ehdev.chronos.lib.types.Note;
import com.ehdev.chronos.lib.types.Punch;
import com.ehdev.chronos.lib.types.Task;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;

import java.io.*;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: ethan
 * Date: 9/8/12
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class BackupOptions extends AsyncTask<BackupOptions.ASYNC_TASK, Void, Boolean> {
    private static String TAG = Defines.TAG + " - BackupOptions";
    Context gContext;
    BackupOptions.ASYNC_TASK option;

    public enum ASYNC_TASK { CVS_BACKUP, CSV_BACKUP, JSON_BACKUP, CVS_RESTORE, CSV_RESTORE, JSON_RESTORE,
        EMAIL_JSON, EMAIL_CSV};

    public BackupOptions(Context con){
        gContext = con;
    }

    protected void onPostExecute(Boolean result){
        Log.d(TAG, "onPostExecute");

        if(result){
            switch (option){
                case CVS_BACKUP:
                case CSV_BACKUP:
                case JSON_BACKUP:
                    Toast.makeText(gContext, "Backup Successful!", Toast.LENGTH_SHORT).show();
                    break;

                case EMAIL_JSON:
                case EMAIL_CSV:
                    gContext.startActivity(emailFile(option));
                    break;

                case CVS_RESTORE:
                case CSV_RESTORE:
                case JSON_RESTORE:
                    Toast.makeText(gContext, "Restore Successful!", Toast.LENGTH_SHORT).show();
                    break;
            }
        } else {
            switch (option){
                case CVS_BACKUP:
                case CSV_BACKUP:
                case JSON_BACKUP:
                    Toast.makeText(gContext, "Backup Failed! Please Contact developer...", Toast.LENGTH_SHORT).show();
                    break;
                case CVS_RESTORE:
                case CSV_RESTORE:
                case JSON_RESTORE:
                    Toast.makeText(gContext, "Restore Failed!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    protected Boolean doInBackground(BackupOptions.ASYNC_TASK... backup_option) {
        option = backup_option[0];
        boolean result = false;

        switch (option){
            case CVS_BACKUP:
                result = Chronos.putDataOnSDCard(gContext, true);
                break;
            case EMAIL_CSV:
            case CSV_BACKUP:
                result = Chronos.putDataOnSDCard(gContext, false);
                break;
            case EMAIL_JSON:
            case JSON_BACKUP:
                result = backupJson();
                break;
            case CVS_RESTORE:
                result = Chronos.getDataOnSDCard(gContext, true);
                break;
            case CSV_RESTORE:
                result = Chronos.getDataOnSDCard(gContext, false);
                break;
            case JSON_RESTORE:
                result = restoreJson();
                break;
        }

        return result;
    }

    public boolean backupJson(){
        Chronos chrono = new Chronos(gContext);
        JsonToSql json = new JsonToSql(chrono);
        String jsonOutput = json.getJson();
        //Log.d(TAG, jsonOutput);

        File directory =  Environment.getExternalStorageDirectory();
        //File backup = new File(directory, "Chronos_Backup.csv");
        File backup = new File(directory, "chronosBackup.json");
        BufferedWriter br;

        try{
            br = new BufferedWriter( new FileWriter(backup));
            br.write(jsonOutput);
            br.close();
        } catch (IOException e){
            Log.e(TAG, e.getMessage());
            return false;
        }

        return true;

    }

    public boolean restoreJson(){
        Chronos chrono = new Chronos(gContext);
        JsonToSql json = new JsonToSql(chrono);

        File directory =  Environment.getExternalStorageDirectory();
        //File backup = new File(directory, "Chronos_Backup.csv");
        File backup = new File(directory, "chronosBackup.json");

        try{
            String str = "";
            String line;
            BufferedReader bufferedReader
                    = new BufferedReader(new FileReader(backup));
            while((line = bufferedReader.readLine()) != null){
                str += line;
            }

            bufferedReader.close();

            json.putJson(str);

        } catch (IOException e){
            return false;
        }

        return true;

    }

    public Intent emailFile(ASYNC_TASK fileType){
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("application/json");
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(fileType == ASYNC_TASK.EMAIL_JSON)   {
            File directory =  Environment.getExternalStorageDirectory();
            File backup = new File(directory, "chronosBackup.json");

            emailIntent.setType("application/json");
            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + backup.getAbsolutePath()));

        }  else if( fileType == ASYNC_TASK.EMAIL_CSV ) {

            File directory =  Environment.getExternalStorageDirectory();
            File backup = new File(directory, "Chronos_Backup.csv");

            emailIntent.setType("text/csv");
            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+ backup.getAbsolutePath()));
        }

        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Time Card");
        Log.d(TAG, "Email Intent");
        return emailIntent;
    }

}
