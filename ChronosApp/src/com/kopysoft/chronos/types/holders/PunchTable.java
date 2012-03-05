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

package com.kopysoft.chronos.types.holders;

import android.util.Log;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.enums.PayPeriodDuration;
import com.kopysoft.chronos.types.Job;
import com.kopysoft.chronos.types.Punch;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.*;

public class PunchTable {

    private static final String TAG = Defines.TAG + " - TaskTable";
    Map gMap;
    List<DateTime> listOfDays;
    PayPeriodHolder gPayPeriod;
    private DateTime startOfTable;

    public PunchTable(DateTime start, DateTime end, Job inJob){
        int days = (int)(end.getMillis() - start.getMillis())/1000/60/60/24;
        startOfTable = start;

        try{
            Log.d(TAG, "Punch Table Size: " + days);
        } catch(Exception e) {
            System.out.println("Punch Table Size: " + days);
        }

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
        }        
    }

    public PunchTable(DateTime start, PayPeriodDuration dur, Job inJob){
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
            Log.d(TAG, "Punch Table Size: " + days);
        } catch(Exception e) {
            System.out.println("Punch Table Size: " + days);
        }

        createTable(inJob, days, start);

    }

    public PayPeriodHolder getPayPeriodInfo(){
        return gPayPeriod;
    }

    public void insert( Punch value){
        //Add key
        DateTime key = value.getTime();
        //DateTime(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour)
        Duration dur = new Duration(startOfTable, key);
        if( key.getSecondOfDay() >= startOfTable.getSecondOfDay()){
            //key is after start of day... set it to the start of the day
            key = startOfTable.plusDays((int)dur.getStandardDays());
            
        } else {
            //put it back a day
            key = startOfTable.plusDays((int)dur.getStandardDays() - 1);
        }

        LinkedList<Punch> list = (LinkedList) gMap.get(key);
        list.add(value);
        Collections.sort(list);
    }
    
    public List<Punch> getPunchesByDay(DateTime key){

        //DateTime(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour)
        Duration dur = new Duration(startOfTable, key);
        if( key.getSecondOfDay() >= startOfTable.getSecondOfDay()){
            //key is after start of day... set it to the start of the day
            key = startOfTable.plusDays((int)dur.getStandardDays());

        } else {
            //put it back a day
            key = startOfTable.plusDays((int)dur.getStandardDays() - 1);
        }
        return ((List)gMap.get(key));
    }

    public List<DateTime> getDays(){
        //Log.d(TAG, "Table Date Size: " + listOfDays.size());
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
