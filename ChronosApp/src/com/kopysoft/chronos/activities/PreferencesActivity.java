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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.util.Log;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.ehdev.chronos.lib.JsonToSql;
import com.ehdev.chronos.lib.types.Punch;
import com.kopysoft.chronos.R;
import com.ehdev.chronos.lib.enums.Defines;
import com.ehdev.chronos.lib.Chronos;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class PreferencesActivity extends SherlockPreferenceActivity  {

    private static String TAG = Defines.TAG + " - PreferencesActivity";
    private final boolean enableLog = Defines.DEBUG_PRINT;

    private static final int BACKUP = 0;
    private static final int RESTORE = 1;
    private static final int BACKUP_LEGACY = 2;
    private static final int RESTORE_LEGACY = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(enableLog) Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //---backup
        Preference backupDB = (Preference) findPreference("backupDB");
        backupDB.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {
                BackupOptions opt = new BackupOptions(getApplicationContext());
                opt.doInBackground(BackupOptions.ASYNC_TASK.CVS_BACKUP);
                return true;
            }

        });
        Preference restoreDB = (Preference) findPreference("restoreDB");
        restoreDB.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {
                BackupOptions opt = new BackupOptions(getApplicationContext());
                opt.doInBackground(BackupOptions.ASYNC_TASK.CSV_RESTORE);
                return true;
            }
        });

        Preference BackupLegacyDB = (Preference) findPreference("BackupLegacyDB");
        BackupLegacyDB.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {
                BackupOptions opt = new BackupOptions(getApplicationContext());
                opt.doInBackground(BackupOptions.ASYNC_TASK.CVS_BACKUP);
                return true;
            }
        });

        Preference restoreLegacyDB = (Preference) findPreference("restoreLegacyDB");
        restoreLegacyDB.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {
                BackupOptions opt = new BackupOptions(getApplicationContext());
                opt.doInBackground(BackupOptions.ASYNC_TASK.CVS_RESTORE);
                return true;
            }
        });

        Preference fullBackup = (Preference) findPreference("fullBackup");
        fullBackup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                BackupOptions opt = new BackupOptions(getApplicationContext());
                opt.doInBackground(BackupOptions.ASYNC_TASK.JSON_BACKUP);
                return true;
            }
        });

        Preference fullRestore = (Preference) findPreference("fullRestore");
        fullRestore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {
                BackupOptions opt = new BackupOptions(getApplicationContext());
                opt.doInBackground(BackupOptions.ASYNC_TASK.JSON_RESTORE);
                return true;
            }
        });


        Preference emailDev = (Preference) findPreference("emailDev");
        emailDev.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {
                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                        new String[] { "ethan+chronos@ehdev.io" });
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Chronos");
                emailIntent.setType("message/rfc822");
                startActivity(emailIntent);
                return true;
            }
        });

        Preference readUla = (Preference) findPreference("readULA");
        readUla.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent().setClass(getApplicationContext(),
                        ShowEULA.class);
                startActivity(intent);

                return true;
            }
        });

        Preference donate = findPreference("donate");
        donate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {

                Intent viewIntent = new Intent("android.intent.action.VIEW",
                        Uri.parse("http://ethankhall.com/app/chronos/support.html"));
                startActivity(viewIntent);
                return true;
            }
        });

        Preference email_raw_json = findPreference("email_raw_json");
        email_raw_json.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {

                BackupOptions opt = new BackupOptions(getApplicationContext());
                opt.execute(BackupOptions.ASYNC_TASK.EMAIL_JSON);
                return true;
            }
        });

        Preference email_raw_csv = findPreference("email_raw_csv");
        email_raw_csv.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {

                BackupOptions opt = new BackupOptions(getApplicationContext());
                opt.execute(BackupOptions.ASYNC_TASK.EMAIL_CSV);

                return true;
            }
        });
    }

    /*
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case BACKUP:
                return new AlertDialog.Builder(PreferencesActivity.this)
                        .setTitle("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                BackupOptions opt = new BackupOptions(getApplicationContext());
                                opt.doInBackground(BackupOptions.ASYNC_TASK.CSV_BACKUP);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                        .create();
            case RESTORE:
                return new AlertDialog.Builder(PreferencesActivity.this)
                        .setTitle("Are you sure?")
                        .setMessage("This will replace all your punches. You will loose everything!")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                BackupOptions opt = new BackupOptions(getApplicationContext());
                                opt.doInBackground(BackupOptions.ASYNC_TASK.CSV_RESTORE);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                        .create();
            case BACKUP_LEGACY:
                return new AlertDialog.Builder(PreferencesActivity.this)
                    .setTitle("Are you sure?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            BackupOptions opt = new BackupOptions(getApplicationContext());
                            opt.doInBackground(BackupOptions.ASYNC_TASK.CVS_BACKUP);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    })
                    .create();
            case RESTORE_LEGACY:
                return new AlertDialog.Builder(PreferencesActivity.this)
                        .setTitle("Are you sure?")
                        .setMessage("This will replace all your punches. You will loose everything!")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                BackupOptions opt = new BackupOptions(getApplicationContext());
                                opt.doInBackground(BackupOptions.ASYNC_TASK.CVS_RESTORE);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                        .create();
            default:
                return null;
        }
    }
    */



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
