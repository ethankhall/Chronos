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

package com.ehdev.chronos.types.holders;

import android.util.Log;
import com.ehdev.chronos.enums.PayPeriodDuration;
import com.ehdev.chronos.types.Job;
import com.ehdev.chronos.types.Punch;
import com.ehdev.chronos.types.Task;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.fail;

public class PunchTableTest {
    
    @Test
    public void testPunchTableLookupOverDST(){
        DateMidnight startDate = new DateMidnight(2012, 3, 4);
        Job thisJob = new Job("", 10, startDate.toDateTime(), PayPeriodDuration.THREE_WEEKS);
        Task newTask = new Task(thisJob, 0, " ");
        List<Punch> beforeDST = new LinkedList<Punch>();
        DateTime startTimeBefore = startDate.plusDays(1).toDateTime().plusHours(12);
        beforeDST.add(new Punch(thisJob, newTask, startTimeBefore));
        beforeDST.add(new Punch(thisJob, newTask, startTimeBefore.plusHours(1)));

        List<Punch> afterDST = new LinkedList<Punch>();
        DateTime startTimeAfter = startDate.plusDays(1).plusWeeks(1).toDateTime().plusHours(12);
        afterDST.add(new Punch(thisJob, newTask, startTimeAfter));
        afterDST.add(new Punch(thisJob, newTask, startTimeAfter.plusHours(1)));
        
        PunchTable table = new PunchTable(startDate.toDateTime(), startDate.plusWeeks(3).toDateTime(), thisJob);
        List<Punch> totalPunches = new LinkedList<Punch>();
        totalPunches.addAll(beforeDST);
        totalPunches.addAll(afterDST);
        for(Punch p : totalPunches){
            table.insert(p);
        }

        if( table.getPunchesByDay(startTimeBefore).size() != 2)
            fail("Didn't match the correct size of punches before DST");

        if( table.getPunchesByDay(startTimeAfter).size() != 2)
            fail("Didn't match the correct size of punches after DST");
    }

    @Test
    public void LookupTest(){
        DateTime startDate = DateTime.now().toDateMidnight().toDateTime().minusDays(7);
        Job thisJob = new Job("", 10, startDate, PayPeriodDuration.THREE_WEEKS);
        Task newTask = new Task(thisJob, 0, " ");
        PunchTable table = new PunchTable(startDate, startDate.plusWeeks(3), thisJob);

        DateTime workFrom = startDate.plusDays(5);
        List<Punch> punches = new LinkedList<Punch>();
        Punch temp;

        
        //System.out.println("Start Date: " + startDate);
        //make 10 punches adding up to 5 hours.
        for(int i = 0; i < 10; i++){
            DateTime tempDate = workFrom.plusHours(i);
            temp = new Punch(thisJob, newTask, tempDate);
            punches.add(temp);
        }

        for(Punch p : punches){
            table.insert(p);
        }

        Duration dur = getTime(table.getPunchPair(workFrom));
        if( dur.getStandardHours() != 5){
            //System.out.println("Hours: " + dur.getStandardHours());
            //System.out.println("Work From Date: " + workFrom);

            //System.out.println("Punches: " + table.getPunchPair(workFrom).size());
            
            for(DateTime time : table.getDays()){
                System.out.println("Date: " + time);
                for(Punch p : table.getPunchesByDay(time)){
                    System.out.println("\tTime: " + p.getTime());
                }
            }
            fail("Times didn't match up");
        }
    }

    @Test
    public void LookupTestFourWeeks(){
        DateTime startDate = DateTime.now().toDateMidnight().toDateTime().minusDays(7);
        Job thisJob = new Job("", 10, startDate, PayPeriodDuration.FOUR_WEEKS);
        Task newTask = new Task(thisJob, 0, " ");
        PayPeriodHolder holder = new PayPeriodHolder(thisJob);
        PunchTable table = new PunchTable(holder.getStartOfPayPeriod(), holder.getEndOfPayPeriod(), thisJob);

        DateTime workFrom = startDate.plusDays(5);
        List<Punch> punches = new LinkedList<Punch>();
        Punch temp;


        //System.out.println("Start Date: " + startDate);
        //make 10 punches adding up to 5 hours.
        for(int i = 0; i < 10; i++){
            DateTime tempDate = workFrom.plusHours(i);
            temp = new Punch(thisJob, newTask, tempDate);
            punches.add(temp);
        }

        for(Punch p : punches){
            table.insert(p);
        }

        Duration dur = getTime(table.getPunchPair(workFrom));
        if( dur.getStandardHours() != 5){
            //System.out.println("Hours: " + dur.getStandardHours());
            //System.out.println("Work From Date: " + workFrom);

            //System.out.println("Punches: " + table.getPunchPair(workFrom).size());

            for(DateTime time : table.getDays()){
                System.out.println("Date: " + time);
                for(Punch p : table.getPunchesByDay(time)){
                    System.out.println("\tTime: " + p.getTime());
                }
            }
            fail("Times didn't match up");
        }
    }

    public static Duration getTime(List<PunchPair> punches){
        return getTime(punches, false);
    }

    public static Duration getTime(List<PunchPair> punches, boolean allowNegative){
        Duration dur = new Duration(0);

        for(PunchPair pp : punches){
            if(!pp.getInPunch().getTask().getEnablePayOverride())
                dur = dur.plus(pp.getDuration());
            else if(pp.getInPunch().getTask().getPayOverride() > 0)
                dur = dur.plus(pp.getDuration());
            else
                dur = dur.minus(pp.getDuration());
        }

        if(dur.getMillis() < 0 && !allowNegative)
            dur = new Duration(0);

        return dur;
    }

    @Test
    public void CrossTimeZone(){

        DateTime startDate = DateTime.now().toDateMidnight().toDateTime().minusDays(7);
        Job thisJob = new Job("", 10, startDate, PayPeriodDuration.TWO_WEEKS);
        Task newTask = new Task(thisJob, 0, " ");
        
        DateTime setTime = new DateTime(2012, 2, 27, 0, 0);
        PunchTable table = new PunchTable(setTime, setTime.plusWeeks(2), thisJob);

        if(table.getDays().size() != 14){
            System.out.println("Days in PP: " + table.getDays().size() );
            fail("Not 14 days in a PP");
        }

    }
}
