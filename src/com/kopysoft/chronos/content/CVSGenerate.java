package com.kopysoft.chronos.content;

/**
 * 			Copyright (C) 2011 by Ethan Hall
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 * 	in the Software without restriction, including without limitation the rights
 * 	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * 	copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 *  
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.types.Note;
import com.kopysoft.chronos.types.Punch;

import java.io.*;
import java.util.ArrayList;
import java.util.GregorianCalendar;

public class CVSGenerate {

	static boolean mExternalStorageAvailable = false;
	static boolean mExternalStorageWriteable = false;


	public static void readFromSDCard(Context context){
		getCardStatus();
		if(!mExternalStorageAvailable){

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
			String strLine;
			
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
                int jobNumber = 0;
				try{
					//id = Long.parseLong(ParcedString[0]);
					time = Long.parseLong(ParcedString[1]);
					type = Integer.parseInt(ParcedString[2]);
					actionReason = Integer.parseInt(ParcedString[3]);
					jobNumber = Integer.parseInt(ParcedString[4]);
                    newString = ParcedString[5];
				} catch(Exception ignored){

				}
				Punch newPunch = new Punch(time, type, Defines.NEW_PUNCH, actionReason, jobNumber);
				listOfPunches.add(newPunch);
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTimeInMillis(time);
				int[] newTime = {
						cal.get(GregorianCalendar.YEAR),
						cal.get(GregorianCalendar.MONTH),
						cal.get(GregorianCalendar.DAY_OF_MONTH)
				};
				Note newNote = new Note(newTime, jobNumber, context);
				newNote.setNote(newString);
				listOfNotes.add(newNote);
				newNote.update();
			}
			
			Chronos chrono = new Chronos(context);
			chrono.dropAll();

            for (Punch currPunch : listOfPunches) currPunch.commitToDb(context);


            for ( Note listOfNote : listOfNotes )    listOfNote.update();
			//for(int i = 0; i < listOfNotes.size(); i++)
			//	listOfNotes.get(i).update();

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
		if(!mExternalStorageWriteable){

			CharSequence text = "Could not write to SD Card!.";
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
			return;
		}

		Chronos chrono = new Chronos(context);
		SQLiteDatabase db = chrono.getReadableDatabase();
		ArrayList<Punch> punches = new ArrayList<Punch>();
		Cursor cursor = db.query(Chronos.TABLE_NAME_CLOCK, null, 
				null, null, null, null, "_id desc");

		final int colId = cursor.getColumnIndex("_id");
		final int colTime = cursor.getColumnIndex("time");
		//final int colType = cursor.getColumnIndex("punch_type");
		final int colAR = cursor.getColumnIndex("actionReason");
        final int colJobNumber = cursor.getColumnIndex("jobNumber");
		if (cursor.moveToFirst()) {
			do {				

				long id = cursor.getLong(colId);
				long time = cursor.getLong(colTime);
				//int type = cursor.getInt(colType);
				int actionReason = cursor.getInt(colAR);
                int jobNumber = cursor.getInt(colJobNumber);
				Punch temp = new Punch(time, Defines.IN, id, jobNumber, actionReason);
				punches.add(temp);


			} while (cursor.moveToNext());
		}

		if ( !cursor.isClosed()) {
			cursor.close();
		}
		db.close();

		File directory =  Environment.getExternalStorageDirectory();
		File backup = new File(directory, "Chronos_Backup.cvs");
		BufferedWriter br;

		try{
			br = new BufferedWriter( new FileWriter(backup));

            for (Punch punch : punches) {
                String write = getCVS(punch, context);
                br.write(write);
            }
			br.close();
		} catch (IOException ignored){
			
		}

	}

	private static String getCVS(Punch punch, Context context){
		String returnValue = "";
		long id = punch.getId();
		long time = punch.getTime();
		int type = punch.getType();
		int actionReason = punch.getAction();
        int jobNumber = punch.getJobNumber();

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(time);
		int[] newTime = {
				cal.get(GregorianCalendar.YEAR),
				cal.get(GregorianCalendar.MONTH),
				cal.get(GregorianCalendar.DAY_OF_MONTH)
		};
		Note newNote = new Note(newTime, jobNumber, context);
		String note = newNote.getNote(true);

		returnValue += String.valueOf(id) + ",";
		returnValue += String.valueOf(time) + ",";
		returnValue += String.valueOf(type) + ",";
        returnValue += String.valueOf(jobNumber) + ",";
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
