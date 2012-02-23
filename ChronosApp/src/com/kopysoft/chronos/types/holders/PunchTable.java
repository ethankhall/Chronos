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
import com.kopysoft.chronos.types.Job;
import com.kopysoft.chronos.types.Punch;
import org.joda.time.DateMidnight;

import java.util.*;

public class PunchTable {

    private static final String TAG = Defines.TAG + " - TaskTable";
    Map gMap;
    List<DateMidnight> listOfDays;
    PayPeriodHolder gPayPeriod;

    public PunchTable(DateMidnight start, DateMidnight end, Job inJob){
        int days = (int)(end.getMillis() - start.getMillis())/1000/60/60/24;
        listOfDays = new LinkedList<DateMidnight>();
        gMap = new HashMap<DateMidnight, List<Punch>>();
        gPayPeriod = new PayPeriodHolder(inJob);
        
        Log.d(TAG, "Punch Table Size: " + days);

        for(int i = 0; i < days; i++){

            DateMidnight key = start.plusDays(i);
            LinkedList<Punch> list = new LinkedList<Punch>();
            gMap.put(key, list);
            listOfDays.add(key);
        }
    }

    public PayPeriodHolder getPayPeriodInfo(){
        return gPayPeriod;
    }

    public void insert( Punch value){
        //Add key
        DateMidnight key = value.getTime().toDateMidnight();

        LinkedList<Punch> list = (LinkedList) gMap.get(key);
        list.add(value);
        Collections.sort(list);
    }
    
    public List<Punch> getPunchesByDay(DateMidnight date){
        return ((List)gMap.get(date));
    }

    public List<DateMidnight> getDays(){
        //Log.d(TAG, "Table Date Size: " + listOfDays.size());
        return listOfDays;
    }
    
    public List<PunchPair> getPunchPair(DateMidnight date){
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
