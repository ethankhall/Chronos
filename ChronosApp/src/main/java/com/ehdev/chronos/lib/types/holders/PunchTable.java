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

package com.ehdev.chronos.lib.types.holders;

import android.util.Log;
import com.ehdev.chronos.lib.enums.Defines;
import com.ehdev.chronos.lib.Chronos;
import com.ehdev.chronos.lib.types.Job;
import com.ehdev.chronos.lib.types.Punch;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.*;

public class PunchTable {

    private static final String TAG = Defines.TAG + " - PunchTable";
    Map gMap;
    List<DateTime> listOfDays;
    PayPeriodHolder gPayPeriod;
    private DateTime startOfTable;
    private static final boolean enableLog = Defines.DEBUG_PRINT;

    public PunchTable(DateTime start, DateTime end, Job inJob){
        DateTimeZone startZone = start.getZone();
        DateTimeZone endZone = end.getZone();
        long offset = endZone.getOffset(end) - startZone.getOffset(start);

        int days = (int)((end.getMillis() - start.getMillis() + offset)/1000/60/60/24);

        startOfTable = start;

        try{
             if(enableLog) Log.d(TAG, "Punch Table Size: " + days);
        } catch(Exception e) {
            try{
                 if(enableLog) Log.d(TAG, "Punch Table Size: " + days);
                 if(enableLog) Log.d(TAG, e.getMessage());
            } catch (Exception e2){
                System.out.println("Punch Table Size: " + days);
                System.out.println(e.getMessage());
            }
        }
        
        if(enableLog) Log.d(TAG, "Start of table: " + start);

        createTable(inJob, days, start);
    }
    
    private void createTable(Job inJob, int days, DateTime start){
        listOfDays = new LinkedList<DateTime>();
        gMap = new HashMap<DateTime, List<Punch>>();
        gPayPeriod = new PayPeriodHolder(inJob);


        for(int i = 0; i < days; i++){

            DateTime key = start.plusDays(i);
            LinkedList<Punch> list = new LinkedList<Punch>();
            gMap.put(key, list);
            listOfDays.add(key);
            //System.out.println("Days: " + key);
        }        
    }

    /*public PunchTable(DateTime start, PayPeriodDuration dur, Job inJob){
        startOfTable = start;
        int days;
        switch (dur){
            case ONE_WEEK:
                days = 7;
                break;
            case TWO_WEEKS:
                days = 14;
                break;
            case THREE_WEEKS:
                days = 3 * 7;
                break;
            case FOUR_WEEKS:
                days = 4 * 7;
                break;
            //TODO: add more options
            default:
                days = 14;
                break;
        }

        try{
             if(enableLog) Log.d(TAG, "Punch Table Size: " + days);
        } catch(Exception e) {
            try{
                 if(enableLog) Log.d(TAG, "Punch Table Size: " + days);
                 if(enableLog) Log.d(TAG, e.getMessage());
            } catch (Exception e2){
                System.out.println("Punch Table Size: " + days);
                System.out.println(e.getMessage());
            }
        }

        createTable(inJob, days, start);

    }*/

    public PayPeriodHolder getPayPeriodInfo(){
        return gPayPeriod;
    }

    public DateTime insert( Punch value){
        //Add key
        DateTime key = value.getTime();
        //DateTime(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour)
        key = Chronos.getDateFromStartOfPayPeriod(startOfTable, key);

        if(enableLog) Log.d(TAG, "Key: " + key + "\tvalue: " +value.getTime() );
        List<Punch> list = (LinkedList) gMap.get(key);
        if(list != null){
            list.add(value);
            Collections.sort(list);
        }

        return key;
    }
    
    public List<Punch> getPunchesByDay(DateTime key){

        //DateTime(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour)
        /*
        Duration dur = new Duration(startOfTable, key);
        if(startOfTable.isBefore(key))
            key = startOfTable.plusDays((int)dur.getStandardDays() );
        else
            key = startOfTable.minusDays((int)dur.getStandardDays());
            */

        key = Chronos.getDateFromStartOfPayPeriod(startOfTable, key);

        try{
             if(enableLog) Log.d(TAG, "GetPunchesByDay: " + key);
        } catch (Exception e){

        }
        return ((List)gMap.get(key));
    }

    public List<DateTime> getDays(){
        // if(enableLog) Log.d(TAG, "Table Date Size: " + listOfDays.size());
        return listOfDays;
    }
    
    public List<PunchPair> getPunchPair(DateTime date){
        List<PunchPair> returnList = new LinkedList<PunchPair>();

        TaskTable taskTable = new TaskTable();
        
        List<Punch> punchList = getPunchesByDay(date);
        if(punchList != null)
            for(Punch temp : punchList){
                taskTable.insert(temp.getTask(), temp);
            }
        
        for(Integer task : taskTable.getTasks()){
            punchList = taskTable.getPunchesForKey(task);
            for(int i = 0; i < punchList.size(); i = i + 2){
                Punch punch1 = punchList.get(i);
                Punch punch2 = null;
                if(i + 1< punchList.size() ){
                    punch2 = punchList.get(i+1);
                }
                PunchPair pp = new PunchPair(punch1, punch2);
                returnList.add(pp);
            }
        }
        
        Collections.sort(returnList);
        
        return returnList;
    }
}