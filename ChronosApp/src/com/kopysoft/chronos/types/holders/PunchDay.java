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
import com.kopysoft.chronos.types.Punch;
import com.kopysoft.chronos.types.Task;
import org.joda.time.DateMidnight;
import org.joda.time.Duration;
import org.joda.time.Interval;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PunchDay {

    private static final String TAG = Defines.TAG + " - PunchDay";
    
    List<Punch> gPunchList;
    DateMidnight gDay;
    TaskTable gTaskTable = new TaskTable();

    /**
     * Default Constructor
     *
     * @param thisDay Containing the information for the day
     */
    public PunchDay(DateMidnight thisDay) {
        gPunchList = new LinkedList<Punch>();
        gDay = thisDay;

    }

    /**
     * Gets a punch from the day
     * @param i index of the punch
     * @return Punch from the index
     */
    public Punch getPunch(int i){
        return gPunchList.get(i);
    }

    /**
     * Gets the number of punches in this day
     *
     * @return the number of punches this day
     */
    public int getSize(){
        return gPunchList.size();
    }

    /**
     * Adds a punch to today.
     *
     * @param addPunch Punch that contains the info.
     * @return true if the day matches, false otherwise
     */
    public boolean addPunch(Punch addPunch){
        if(addPunch.getTime().toDateMidnight().isEqual(gDay)){
            gPunchList.add(addPunch);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Wipe out the punches
     */
    public void clearPunch(){
        gPunchList.clear();
    }

    /**
     * Called when you want to get the current time of the Day. This operation may take some time...
     * @return @code{Duration} containing the information of the day
     */
   public Duration getTime(){
       Duration retDuration = new Duration(0);

       Collections.sort(gPunchList);

       for(Punch temp : gPunchList){
           Task tempTask = temp.getTask();
           gTaskTable.insert(tempTask, temp);
       }
       
       List<PunchPair> listPunchPair = generatePunchPair(gTaskTable);
       
       for(PunchPair punchPair :listPunchPair){
           Interval interval = punchPair.getInterval();
           if(interval != null){
               retDuration.plus(interval.toDuration());
           }
       }
       
       return retDuration;
   }

    /**
     * Takes a @code{ TaskTable } and will generate a List of PunchPairs based on Tasks
     * @param taskTable  TaskTable that contains all the punches for the day
     * @return List of PunchPairs
     */
    public List<PunchPair> generatePunchPair(TaskTable taskTable){
        List<PunchPair> listOfPunchPairs = new LinkedList<PunchPair>();
        List<Punch> punches;
        List<Integer> tasks = taskTable.getTasks();
        Log.d(TAG, "Number of Tasks: " + tasks.size());
        for(Integer curTask : tasks){
            punches = taskTable.getPunchesForKey(curTask);
            Collections.sort(punches);
            Log.d(TAG, "Task Number: " + curTask);
            //for(Punch temp : punches){
            //    Log.d(TAG, "Punch ID: " + temp.getID());
            //}

            for(int i = 0; i < punches.size(); i += 2){
                //Log.d(TAG, "Size: " + punches.size());
                //Log.d(TAG, "index: " + i);
                Punch inTime = punches.get(i);
                if(i < punches.size() - 1) {
                    Punch outTime = punches.get(i + 1);
                    listOfPunchPairs.add(new PunchPair(inTime, outTime));
                } else {
                    listOfPunchPairs.add(new PunchPair(inTime, null));
                }
            }
        }
        Collections.sort(gPunchList);

        return listOfPunchPairs;

    }
}
