package com.kopysoft.chronos.subActivites;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

import com.kopysoft.chronos.R;
import com.kopysoft.chronos.content.CVSGenerate;
import com.kopysoft.chronos.singelton.PreferenceSingelton;

public class Preferences extends PreferenceActivity {
	private static final int BACKUP = 0;
	private static final int RESTORE = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		//---- email me
		Preference emailMe = (Preference) findPreference("emailDev");
		emailMe.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { "ethan@kopysoft.com" });
	            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Chronos");
				emailIntent.setType("message/rfc822");
				startActivity(emailIntent);
				return true;
			}

		});

		//---backup
		Preference backupDB = (Preference) findPreference("backupDB");
		backupDB.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				showDialog(BACKUP);
				
				return true;
			}

		});
		Preference restoreDB = (Preference) findPreference("restoreDB");
		restoreDB.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				showDialog(RESTORE);
				return true;
			}
		});
	}
	
	@Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case BACKUP:
            return new AlertDialog.Builder(Preferences.this)
                .setTitle("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	CVSGenerate.putDataOnCard(getApplicationContext());
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        /* User clicked Cancel so do some stuff */
                    }
                })
                .create();
        case RESTORE:
            return new AlertDialog.Builder(Preferences.this)
                .setTitle("Are you sure?")
                .setMessage("This will replace all your punches. You will loosed everything!")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	CVSGenerate.readFromSDCard(getApplicationContext());
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
	public void onPause(){
		super.onPause();
		PreferenceSingelton.getInstance().updatePreferences(getApplicationContext());
	}
}
