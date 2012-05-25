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

package com.kopysoft.chronos.adapter.clock;

import com.kopysoft.chronos.enums.PayPeriodDuration;
import com.kopysoft.chronos.types.Job;
import com.kopysoft.chronos.types.Punch;
import com.kopysoft.chronos.types.Task;
import com.kopysoft.chronos.types.holders.PunchTable;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.fail;

public class TodayAdapterPairTest {

    DateTime startDate;
    Job thisJob;
    Task newTask;
    PunchTable table;

    @Before
    public void setupTable(){
        startDate = DateTime.now().toDateMidnight().toDateTime().minusDays(7);
        thisJob = new Job("", 10, startDate, PayPeriodDuration.THREE_WEEKS);
        newTask = new Task(thisJob, 0, " ");

        table = new PunchTable(startDate, startDate.plusWeeks(3), thisJob);
    }

    @Test
    public void testGetTimeList() throws Exception {
        DateTime workFrom = startDate.plusDays(5);
        List<Punch> punches = new LinkedList<Punch>();
        Punch temp;

        //make 10 punches adding up to 5 hours.
        for(int i = 0; i < 10; i++){
            DateTime tempDate = workFrom.plusHours(i);
            temp = new Punch(thisJob, newTask, tempDate);
            punches.add(temp);
        }
        
        for(Punch p : punches){
            //System.out.println("Inserting: " + p.getTime());
            table.insert(p);
        }

        Duration dur = TodayAdapterPair.getTime(table.getPunchPair(workFrom), false);
        if( dur.getStandardHours() != 5){
            //System.out.println("Hours: " + dur.getStandardHours());
            //System.out.println("Start Date: " + workFrom);
            
            //System.out.println("Punches: " + table.getPunchPair(workFrom).size());
            fail("Times didn't match up");
        }
        //create tests
    }

    @Test
    public void testGetTimeAroundNoonList() throws Exception {
        DateTime workFrom = startDate.plusDays(5);
        List<Punch> punches = new LinkedList<Punch>();
        Punch temp;

        //make 10 punches adding up to 5 hours.
        for(int i = 10; i < 20; i++){
            DateTime tempDate = workFrom.plusHours(i);
            temp = new Punch(thisJob, newTask, tempDate);
            punches.add(temp);
        }

        for(Punch p : punches){
            //System.out.println("Inserting: " + p.getTime());
            table.insert(p);
        }

        Duration dur = TodayAdapterPair.getTime(table.getPunchPair(workFrom), false);
        if( dur.getStandardHours() != 5){
            //System.out.println("Hours: " + dur.getStandardHours());
            //System.out.println("Start Date: " + workFrom);

            //System.out.println("Punches: " + table.getPunchPair(workFrom).size());
            fail("Times didn't match up");
        }
        //create tests
    }

    @Test
    public void testGetTimeListBoolean() throws Exception {
        DateTime workFrom = DateMidnight.now().toDateTime();
        List<Punch> punches = new LinkedList<Punch>();
        Punch temp;

        //make 10 punches adding up to 5 hours.
        for(int i = 0; i < 9; i++){
            DateTime tempDate = workFrom.plusHours(i);
            temp = new Punch(thisJob, newTask, tempDate);
            punches.add(temp);
        }

        for(Punch p : punches){
            table.insert(p);
        }

        Duration dur = TodayAdapterPair.getTime(table.getPunchPair(workFrom), true);
        if( dur.getMillis() >= 0 ){
            System.out.println(dur.getMillis());
            fail("Time Didn't go negative");
        }

        if( dur.getMillis() != -1 * workFrom.plusHours(8).getMillis() + 4 * 1000 * 60 * 60 ){
            System.out.println(dur.getMillis());
            fail("Time didn't match up");
        }
    }

    @Test
    public void testGetPayableTime() throws Exception {
        DateTime workFrom = startDate.plusDays(5);
        List<Punch> punches = new LinkedList<Punch>();
        Punch temp;

        //make 10 punches adding up to 5 hours.
        for(int i = 10; i < 20; i++){
            DateTime tempDate = workFrom.plusHours(i);
            temp = new Punch(thisJob, newTask, tempDate);
            punches.add(temp);
        }

        for(Punch p : punches){
            //System.out.println("Inserting: " + p.getTime());
            table.insert(p);
        }

        Duration dur = TodayAdapterPair.getTime(table.getPunchPair(workFrom), true);
        if( dur.getStandardHours() != 5){
            fail("Times didn't match up");
        }

        float payableTime = PayPeriodAdapterList.getPayableTime(table, thisJob);
        if (payableTime != thisJob.getPayRate() * 5){
            fail("Pay didn't match");
        }
    }

    @Test
    public void testGetPayableTimeOvertimeHourly() throws Exception  {
        thisJob.setFortyHourWeek(false);
        thisJob.setOvertime(8);
        thisJob.setDoubletimeThreshold(10);
        thisJob.setPayRate(30);

        DateTime workFrom = DateMidnight.now().toDateTime();
        List<Punch> punches = new LinkedList<Punch>();
        Punch temp;

        DateTime tempDate = workFrom.plusHours(3);
        temp = new Punch(thisJob, newTask, tempDate);
        punches.add(temp);

        tempDate = workFrom.plusHours(12);
        temp = new Punch(thisJob, newTask, tempDate);
        punches.add(temp);


        for(Punch p : punches){
            //System.out.println("Inserting: " + p.getTime());
            table.insert(p);
        }

        Duration dur = TodayAdapterPair.getTime(table.getPunchPair(workFrom), true);
        if( dur.getStandardHours() != 9){
            System.out.println("Time returned:" + dur.getStandardHours());
            fail("Times didn't match up");
        }

        float payableTime = TodayAdapterPair.getPayableTime(table.getPunchPair(workFrom), thisJob, true);
        float payForDoubleTime = (float)(thisJob.getPayRate() * 1.5 + thisJob.getPayRate() * 8);
        if (Math.abs(payableTime - payForDoubleTime) > .001){
            System.out.println("Pay Amount: " + payableTime);
            System.out.println("Calculated Pay Amount: " + payForDoubleTime);
            fail("Pay didn't match");
        }
    }

    @Test
    public void testGetPayableTimeDoubleTimeHourly() throws Exception {
        thisJob.setFortyHourWeek(false);
        thisJob.setOvertime(8);
        thisJob.setDoubletimeThreshold(10);
        DateTime workFrom = DateMidnight.now().toDateTime();
        List<Punch> punches = new LinkedList<Punch>();
        Punch temp;

        DateTime tempDate = workFrom.plusHours(1);
        temp = new Punch(thisJob, newTask, tempDate);
        punches.add(temp);

        tempDate = workFrom.plusHours(21);
        temp = new Punch(thisJob, newTask, tempDate);
        punches.add(temp);


        for(Punch p : punches){
            //System.out.println("Inserting: " + p.getTime());
            table.insert(p);
        }

        Duration dur = TodayAdapterPair.getTime(table.getPunchPair(workFrom), true);
        if( dur.getStandardHours() != 20){
            System.out.println("Time returned:" + dur.getStandardHours());
            fail("Times didn't match up");
        }

        float payableTime = TodayAdapterPair.getPayableTime(table.getPunchPair(workFrom), thisJob, true);
        float payForDoubleTime = (float)((10) * thisJob.getPayRate() * 2
                + 2 * thisJob.getPayRate() * 1.5 + thisJob.getPayRate() * 8);
        if (Math.abs(payableTime - payForDoubleTime) > .001){
            System.out.println("Pay Rate: " + payableTime);
            System.out.println("Calculated Pay Rate: " + payForDoubleTime);
            fail("Pay didn't match");
        }
    }
}
