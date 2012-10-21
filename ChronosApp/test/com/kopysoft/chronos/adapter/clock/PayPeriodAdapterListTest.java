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

import com.ehdev.chronos.lib.enums.OvertimeOptions;
import com.ehdev.chronos.lib.enums.PayPeriodDuration;
import com.ehdev.chronos.lib.types.Job;
import com.ehdev.chronos.lib.types.Punch;
import com.ehdev.chronos.lib.types.Task;
import com.ehdev.chronos.lib.overtime.DurationHolder;
import com.ehdev.chronos.lib.types.holders.PunchPair;
import com.ehdev.chronos.lib.types.holders.PunchTable;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.fail;

public class PayPeriodAdapterListTest {

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

        Duration dur = PayPeriodAdapterList.getTime(table.getPunchPair(workFrom));
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

        Duration dur = PayPeriodAdapterList.getTime(table.getPunchPair(workFrom));
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
        DateTime workFrom = startDate.plusDays(5);
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

        Duration dur = PayPeriodAdapterList.getTime(table.getPunchPair(workFrom), true);
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
        DurationHolder holder = new DurationHolder();

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

        for(DateTime date:  table.getDays()){
            Duration day = new Duration(0);
            for(PunchPair pp : table.getPunchPair(date)){
                day = day.plus(pp.getDuration());
            }

            holder.addNormalPay(date.toDateMidnight(), day);
        }

        Duration dur = PayPeriodAdapterList.getTime(table.getPunchPair(workFrom));
        if( dur.getStandardHours() != 5){
            fail("Times didn't match up");
        }

        float payableTime = PayPeriodAdapterList.getPayableTime(holder, thisJob);
        if (payableTime != thisJob.getPayRate() * 5){
            fail("Pay didn't match");
        }
    }

    @Test
    public void testGetPayableTimeOvertime() throws Exception {
        DateTime workFrom = startDate.plusDays(5);
        List<Punch> punches = new LinkedList<Punch>();
        Punch temp;
        DurationHolder holder = new DurationHolder();

        //make 10 punches adding up to 5 hours.
        for(int j = 0; j < 10; j++){
            DateTime workingDay = workFrom.plusDays(j);
            for(int i = 10; i < 20; i++){
                DateTime tempDate = workingDay.plusHours(i);
                temp = new Punch(thisJob, newTask, tempDate);
                punches.add(temp);
            }
        }

        for(Punch p : punches){
            //System.out.println("Inserting: " + p.getTime());
            table.insert(p);
        }

        for(DateTime date:  table.getDays()){
            Duration day = new Duration(0);
            for(PunchPair pp : table.getPunchPair(date)){
                day = day.plus(pp.getDuration());
            }

            holder.addNormalPay(date.toDateMidnight(), day);
        }

        Duration dur = PayPeriodAdapterList.getTime(table);
        if( dur.getStandardHours() != 50){
            System.out.println("Time returned:" + dur.getStandardHours());
            fail("Times didn't match up");
        }

        float payableTime = PayPeriodAdapterList.getPayableTime(holder, thisJob);
        if (payableTime != thisJob.getPayRate() * 40 + thisJob.getPayRate() * 1.5 * 10){
            System.out.println("Pay Rate: " + payableTime);
            fail("Pay didn't match");
        }
    }

    @Test
    public void testGetPayableTimeDoubleTime() throws Exception {
        DateTime workFrom = startDate.plusDays(5);
        List<Punch> punches = new LinkedList<Punch>();
        DurationHolder holder = new DurationHolder();
        Punch temp;

        //make 80 punches adding up to 80 hours.
        for(int j = 0; j < 8; j++){
            DateTime workingDay = workFrom.plusDays(j);
            for(int i = 0; i < 20; i++){
                DateTime tempDate = workingDay.plusHours(i);
                temp = new Punch(thisJob, newTask, tempDate);
                punches.add(temp);
            }
        }

        for(Punch p : punches){
            //System.out.println("Inserting: " + p.getTime());
            table.insert(p);
        }

        for(DateTime date:  table.getDays()){
            Duration day = new Duration(0);
            for(PunchPair pp : table.getPunchPair(date)){
                day = day.plus(pp.getDuration());
            }

            holder.addNormalPay(date.toDateMidnight(), day);
        }

        Duration dur = PayPeriodAdapterList.getTime(table);
        if( dur.getStandardHours() != 80){
            System.out.println("Time returned:" + dur.getStandardHours());
            fail("Times didn't match up");
        }

        float payableTime = PayPeriodAdapterList.getPayableTime(holder, thisJob);
        float payForDoubleTime = (float)((80 - 60 ) * thisJob.getPayRate() * 2
                + 20 * thisJob.getPayRate() * 1.5 + thisJob.getPayRate() * 40);
        if (Math.abs(payableTime - payForDoubleTime) > .001){
            System.out.println("Pay Rate: " + payableTime);
            System.out.println("Calculated Pay Rate: " + payForDoubleTime);
            fail("Pay didn't match");
        }
    }

    @Test
    public void testGetPayableTimeOvertimeHourly() throws Exception {
        DateTime workFrom = startDate.plusDays(5);
        thisJob.setOvertimeOptions(OvertimeOptions.DAY);
        thisJob.setOvertime(8);
        thisJob.setDoubletimeThreshold(10);
        List<Punch> punches = new LinkedList<Punch>();
        Punch temp;
        DurationHolder holder = new DurationHolder();

        //make 6 punches adding up to 30 hours.
        for(int j = 0; j < 3; j++){
            DateTime workingDay = workFrom.plusDays(j);
            DateTime tempDate = workingDay.plusHours(3);
            temp = new Punch(thisJob, newTask, tempDate);
            punches.add(temp);

            tempDate = workingDay.plusHours(13);
            temp = new Punch(thisJob, newTask, tempDate);
            punches.add(temp);
        }

        for(Punch p : punches){
            //System.out.println("Inserting: " + p.getTime());
            table.insert(p);
        }

        for(DateTime date:  table.getDays()){
            Duration day = new Duration(0);
            for(PunchPair pp : table.getPunchPair(date)){
                day = day.plus(pp.getDuration());
            }

            holder.addNormalPay(date.toDateMidnight(), day);
        }

        Duration dur = PayPeriodAdapterList.getTime(table);
        if( dur.getStandardHours() != 30){
            System.out.println("Time returned:" + dur.getStandardHours());
            fail("Times didn't match up");
        }

        float payableTime = PayPeriodAdapterList.getPayableTime(holder, thisJob);
        float payForDoubleTime = (float)(6 * thisJob.getPayRate() * 1.5 + thisJob.getPayRate() * 3 * 8);
        if (Math.abs(payableTime - payForDoubleTime) > .001){
            System.out.println("Pay Rate: " + thisJob.getPayRate());
            System.out.println("Pay Amount: " + payableTime);
            System.out.println("Calculated Pay Rate: " + payForDoubleTime);
            fail("Pay didn't match");
        }
    }

    @Test
    public void testGetPayableTimeDoubleTimeHourly() throws Exception {
        thisJob.setOvertimeOptions(OvertimeOptions.DAY);
        thisJob.setOvertime(8);
        thisJob.setDoubletimeThreshold(10);
        DateTime workFrom = startDate.plusDays(5);
        List<Punch> punches = new LinkedList<Punch>();
        Punch temp;
        DurationHolder holder = new DurationHolder();

        //make 6 punches adding up to 60 hours.
        for(int j = 0; j < 3; j++){
            DateTime workingDay = workFrom.plusDays(j);
            DateTime tempDate = workingDay.plusHours(1);
            temp = new Punch(thisJob, newTask, tempDate);
            punches.add(temp);

            tempDate = workingDay.plusHours(21);
            temp = new Punch(thisJob, newTask, tempDate);
            punches.add(temp);
        }

        for(Punch p : punches){
            //System.out.println("Inserting: " + p.getTime());
            table.insert(p);
        }

        for(DateTime date:  table.getDays()){
            Duration day = new Duration(0);
            for(PunchPair pp : table.getPunchPair(date)){
                day = day.plus(pp.getDuration());
            }

            holder.addNormalPay(date.toDateMidnight(), day);
        }

        Duration dur = PayPeriodAdapterList.getTime(table);
        if( dur.getStandardHours() != 60){
            System.out.println("Time returned:" + dur.getStandardHours());
            fail("Times didn't match up");
        }

        float payableTime = PayPeriodAdapterList.getPayableTime(holder, thisJob);
        float payForDoubleTime = (float)((3 * 10) * thisJob.getPayRate() * 2
                + 6 * thisJob.getPayRate() * 1.5 + thisJob.getPayRate() * 8 * 3);
        if (Math.abs(payableTime - payForDoubleTime) > .001){
            System.out.println("Pay Rate: " + payableTime);
            System.out.println("Calculated Pay Rate: " + payForDoubleTime);
            fail("Pay didn't match");
        }
    }

    @Test
    public void testGetPayableTimeDoubleTimeHourly2() throws Exception {
        thisJob.setOvertimeOptions(OvertimeOptions.DAY);
        thisJob.setOvertime(8);
        thisJob.setDoubletimeThreshold(10);
        thisJob.setPayRate(30);
        DateTime workFrom = startDate.plusDays(5);
        List<Punch> punches = new LinkedList<Punch>();
        Punch temp;
        DurationHolder holder = new DurationHolder();

        //make 2 punches adding up to 20 hours.
        for(int j = 0; j < 5; j++){
            DateTime workingDay = workFrom.plusDays(j);
            DateTime tempDate = workingDay.plusHours(10);
            temp = new Punch(thisJob, newTask, tempDate);
            punches.add(temp);

            tempDate = workingDay.plusHours(19);
            temp = new Punch(thisJob, newTask, tempDate);
            punches.add(temp);
        }

        for(Punch p : punches){
            //System.out.println("Inserting: " + p.getTime());
            table.insert(p);
        }

        for(DateTime date:  table.getDays()){
            Duration day = new Duration(0);
            for(PunchPair pp : table.getPunchPair(date)){
                day = day.plus(pp.getDuration());
            }

            holder.addNormalPay(date.toDateMidnight(), day);
        }

        Duration dur = PayPeriodAdapterList.getTime(table);
        if( dur.getStandardHours() != 45){
            System.out.println("Time returned:" + dur.getStandardHours());
            fail("Times didn't match up");
        }

        float payableTime = PayPeriodAdapterList.getPayableTime(holder, thisJob);
        float payForDoubleTime = (float)(5 * thisJob.getPayRate() * 1.5 + thisJob.getPayRate() * 8 * 5);
        if (Math.abs(payableTime - payForDoubleTime) > .001){
            System.out.println("Pay Rate: " + payableTime);
            System.out.println("Calculated Pay Rate: " + payForDoubleTime);
            fail("Pay didn't match");
        }
    }
}
