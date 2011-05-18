package com.kopysoft.chronos.content;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.types.Note;
import com.kopysoft.chronos.types.Punch;

public class CVSGenerate {

	static boolean mExternalStorageAvailable = false;
	static boolean mExternalStorageWriteable = false;


	public static void readFromSDCard(Context context){
		getCardStatus();
		if(mExternalStorageAvailable == false){

			CharSequence text = "Could not read from SD Card!.";
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
			return;
		}

		try{
			File directory =  Environment.getExternalStorageDirectory();
			File backup = new File(directory, "Chronos_Backup.cvs");

			BufferedReader br = new BufferedReader( new FileReader(backup));
			String strLine = "";
			
			ArrayList<Punch> listOfPunches = new ArrayList<Punch>();
			ArrayList<Note> listOfNotes = new ArrayList<Note>();
			
			while( (strLine = br.readLine()) != null)
			{
				String[] ParcedString = strLine.split(",");
				//long i_time, int i_type, long i_id, int i_actionReason
				//long id = 0;
				long time = 0;
				int type = 0;
				int actionReason = Defines.REGULAR_TIME;
				String newString = "";
				try{
					//id = Long.parseLong(ParcedString[0]);
					time = Long.parseLong(ParcedString[1]);
					type = Integer.parseInt(ParcedString[2]);
					actionReason = Integer.parseInt(ParcedString[3]);
					newString = ParcedString[4];
				} catch(Exception e){

				}
				Punch newPunch = new Punch(time, type, -1, actionReason);
				listOfPunches.add(newPunch);
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTimeInMillis(time);
				int[] newTime = {
						cal.get(GregorianCalendar.YEAR),
						cal.get(GregorianCalendar.MONTH),
						cal.get(GregorianCalendar.DAY_OF_MONTH)
				};
				Note newNote = new Note(newTime, context);
				newNote.setNote(newString);
				listOfNotes.add(newNote);
				newNote.update();
			}
			
			Chronos chrono = new Chronos(context);
			chrono.dropAll();
			
			for(int i = 0; i < listOfPunches.size(); i++)
				listOfPunches.get(i).commitToDb(context);
			
			for(int i = 0; i < listOfNotes.size(); i++)
				listOfNotes.get(i).update();

		} catch (FileNotFoundException e) {
			Log.e(Defines.TAG, e.getMessage());

			CharSequence text = "An error occured when reading the XML backup.";
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();

		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}

	public static void putDataOnCard(Context context){
		getCardStatus();
		if(mExternalStorageWriteable == false){

			CharSequence text = "Could not write to SD Card!.";
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
			return;
		}

		Chronos chrono = new Chronos(context);
		SQLiteDatabase db = chrono.getReadableDatabase();
		ArrayList<Punch> punches = new ArrayList<Punch>();
		Cursor cursor = db.query(Chronos.TABLE_NAME_CLOCK, new String[] { "_id","punch_type", "time" }, 
				null, null, null, null, "_id desc");

		final int colId = cursor.getColumnIndex("_id");
		final int colTime = cursor.getColumnIndex("time");
		final int colType = cursor.getColumnIndex("punch_type");
		if (cursor.moveToFirst()) {
			do {				

				long id = cursor.getLong(colId);
				long time = cursor.getLong(colTime);
				int type = cursor.getInt(colType);
				Punch temp = new Punch(time, type, id, Defines.REGULAR_TIME);
				punches.add(temp);


			} while (cursor.moveToNext());
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		db.close();

		File directory =  Environment.getExternalStorageDirectory();
		File backup = new File(directory, "Chronos_Backup.cvs");
		BufferedWriter br;

		try{
			br = new BufferedWriter( new FileWriter(backup));

			for(int i = 0; i < punches.size(); i++){
				String write = getCVS(punches.get(i), context);
				br.write(write);
			}
			br.close();
		} catch (IOException e){
			
		}

	}

	private static String getCVS(Punch punch, Context context){
		String returnValue = "";
		long id = punch.getId();
		long time = punch.getTime();
		int type = punch.getType();
		int actionReason = punch.getAction();

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(time);
		int[] newTime = {
				cal.get(GregorianCalendar.YEAR),
				cal.get(GregorianCalendar.MONTH),
				cal.get(GregorianCalendar.DAY_OF_MONTH)
		};
		Note newNote = new Note(newTime, context);
		String note = newNote.getNote(true);

		returnValue += String.valueOf(id) + ",";
		returnValue += String.valueOf(time) + ",";
		returnValue += String.valueOf(type) + ",";
		returnValue += String.valueOf(actionReason) + ",";
		returnValue += note + "\n";

		return returnValue;
	}

	public static void getCardStatus(){

		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but all we need
			//  to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
	}

}
