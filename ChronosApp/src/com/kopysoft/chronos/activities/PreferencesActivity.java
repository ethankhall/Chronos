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

package com.kopysoft.chronos.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.util.Log;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.kopysoft.chronos.R;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.enums.Defines;

public class PreferencesActivity extends SherlockPreferenceActivity  {

    private static String TAG = Defines.TAG + " - PreferencesActivity";
    private final boolean enableLog = true;

    private static final int BACKUP = 0;
    private static final int RESTORE = 1;

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
                showDialog(BACKUP);

                return true;
            }

        });
        Preference restoreDB = (Preference) findPreference("restoreDB");
        restoreDB.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {
                showDialog(RESTORE);
                return true;
            }
        });

        Preference emailDev = (Preference) findPreference("emailDev");
        emailDev.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {
                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                        new String[] { "ethan@kopysoft.com" });
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
    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case BACKUP:
                return new AlertDialog.Builder(PreferencesActivity.this)
                        .setTitle("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                if (Chronos.getDataOnSDCard(getApplicationContext() )) {
                                    Toast.makeText(getApplicationContext(), "Backup Successful!",
                                            Toast.LENGTH_SHORT).show();;
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "Backup Failed! Contact developer.", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                /* User clicked Cancel so do some stuff */
                            }
                        })
                        .create();
            case RESTORE:
                return new AlertDialog.Builder(PreferencesActivity.this)
                        .setTitle("Are you sure?")
                        .setMessage("This will replace all your punches. You will loose everything!")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (Chronos.getDataOnSDCard(getApplicationContext())) {
                                    Toast.makeText(getApplicationContext(), "Restore Successful!",
                                            Toast.LENGTH_SHORT).show();;
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "Restore Failed! Contact developer.", Toast.LENGTH_LONG).show();
                                }
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
