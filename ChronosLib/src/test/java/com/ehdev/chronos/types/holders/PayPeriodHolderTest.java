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

import com.ehdev.chronos.enums.PayPeriodDuration;
import com.ehdev.chronos.types.Job;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import static org.junit.Assert.fail;

public class PayPeriodHolderTest {
    @Test
    public void testGenerate() throws Exception {
        DateTime jobMidnight = DateTime.now().withDayOfWeek(1).minusWeeks(2);
        Job currentJob = new Job("My First Job", 10,
                jobMidnight, PayPeriodDuration.TWO_WEEKS);
        PayPeriodHolder holder = new PayPeriodHolder(currentJob);

        DateTime startOfPPCalculated = jobMidnight.plusWeeks(2);
        if(holder.getStartOfPayPeriod().getMillis() != startOfPPCalculated.getMillis()){
            System.out.println("Calculated: " + startOfPPCalculated);
            System.out.println("Worked out: " + holder.getStartOfPayPeriod());
            fail("Start of jobs didn't match.. Fuck");
        }

        DateTime endOfPP = startOfPPCalculated.plusDays(holder.getDays());
        if(holder.getEndOfPayPeriod().getMillis() != endOfPP.getMillis()){
            fail("End of jobs didn't match.. Fuck");
        }
    }

    @Test
    public void testMoveBackwards() throws Exception {

        DateTime jobMidnight = DateTime.now().withDayOfWeek(1).minusWeeks(2);
        Job currentJob = new Job("My First Job", 10,
                jobMidnight.toDateTime(), PayPeriodDuration.TWO_WEEKS);
        PayPeriodHolder holder = new PayPeriodHolder(currentJob);
        DateTime startOfPPCalculated = holder.getStartOfPayPeriod();
        holder.moveBackwards();

        //mode backwards
        startOfPPCalculated = startOfPPCalculated.minusDays(holder.getDays());
        if(holder.getStartOfPayPeriod().getMillis() != startOfPPCalculated.getMillis()){
            System.out.println("Calculated Start: \t" + startOfPPCalculated);
            System.out.println("Found Start: \t\t" + holder.getStartOfPayPeriod());
            fail("Start of jobs didn't match.. Fuck");
        }

        DateTime endOfPP = startOfPPCalculated.plusDays(holder.getDays());
        if(holder.getEndOfPayPeriod().getMillis() != endOfPP.getMillis()){
            System.out.println("Calculated End: \t" + endOfPP);
            System.out.println("Found End: \t\t" + holder.getEndOfPayPeriod());
            fail("End of jobs didn't match.. Fuck");
        }

    }

    @Test
    public void testMoveForwards() throws Exception {


        DateTime jobMidnight = DateTime.now().withDayOfWeek(1).minusWeeks(2);
        Job currentJob = new Job("My First Job", 10,
                jobMidnight.toDateTime(), PayPeriodDuration.TWO_WEEKS);
        PayPeriodHolder holder = new PayPeriodHolder(currentJob);
        DateTime startOfPPCalculated = holder.getStartOfPayPeriod();
        holder.moveForwards();

        //mode backwards
        startOfPPCalculated = startOfPPCalculated.plusWeeks(2);
        if(holder.getStartOfPayPeriod().getMillis() != startOfPPCalculated.getMillis()){
            System.out.println("Calculated Start: \t" + startOfPPCalculated);
            System.out.println("Found Start: \t\t" + holder.getStartOfPayPeriod());
            fail("Start of jobs didn't match.. Fuck");
        }

        DateTime endOfPP = startOfPPCalculated.plusDays(holder.getDays());
        if(holder.getEndOfPayPeriod().getMillis() != endOfPP.getMillis()){
            System.out.println("Calculated End: \t" + endOfPP);
            System.out.println("Found End: \t\t" + holder.getEndOfPayPeriod());
            fail("End of jobs didn't match.. Fuck");
        }
    }

    @Test
    public void testMoveForwardsAndBack(){
        DateTime jobMidnight = DateTime.now().withDayOfWeek(1).minusWeeks(2);
        Job currentJob = new Job("My First Job", 10,
                jobMidnight, PayPeriodDuration.TWO_WEEKS);
        PayPeriodHolder holder = new PayPeriodHolder(currentJob);
        moveXTimes(1, holder);
    }

    public void moveXTimes(int moveTimes, PayPeriodHolder holder){
        DateTime initialStartOfPP = holder.getStartOfPayPeriod();
        DateTime initialEndOfPP = holder.getEndOfPayPeriod();
        int startDayOfWeek = initialStartOfPP.getDayOfWeek();
        int endDayOfWeek = initialEndOfPP.getDayOfWeek();

        for(int i = 0; i < moveTimes; i++){
            holder.moveForwards();
            //don't test this with full month and 1st and 15th
            if(holder.gDuration != PayPeriodDuration.FIRST_FIFTEENTH &&
                    holder.gDuration != PayPeriodDuration.FULL_MONTH ) {
                if(holder.getStartOfPayPeriod().getDayOfWeek() != startDayOfWeek)
                    fail("Start of PP didn't match the initial one");

                if(holder.getEndOfPayPeriod().getDayOfWeek() != startDayOfWeek)
                    fail("End of PP didn't match the initial one");
            }
        }

        for(int i = 0; i < moveTimes; i++){
            holder.moveBackwards();
            //don't test this with full month and 1st and 15th
            if(holder.gDuration != PayPeriodDuration.FIRST_FIFTEENTH &&
                    holder.gDuration != PayPeriodDuration.FULL_MONTH ) {
                if(holder.getStartOfPayPeriod().getDayOfWeek() != startDayOfWeek)
                    fail("Start of PP didn't match the initial one");

                if(holder.getEndOfPayPeriod().getDayOfWeek() != startDayOfWeek)
                    fail("End of PP didn't match the initial one");
            }
        }

        if(! initialStartOfPP.isEqual(holder.getStartOfPayPeriod()) ){
            System.out.println("initial value: " + initialStartOfPP);
            System.out.println("moved value: " + holder.getStartOfPayPeriod() );
            System.out.println("Days in PP: " + holder.getDays());
            fail("Moving forwards and backwards didn't match the original value, start");
        }

        if(! initialEndOfPP.isEqual(holder.getEndOfPayPeriod()) ) {
            System.out.println("initial value" + initialEndOfPP);
            System.out.println("moved value" + holder.getEndOfPayPeriod() );
            System.out.println("Days in PP: " + holder.getDays());
            fail("Moving forwards and backwards didn't match the original value, end");
        }

        DateTimeZone startZone = holder.getEndOfPayPeriod().getZone();
        DateTimeZone endZone = holder.getStartOfPayPeriod().getZone();

        long offset = endZone.getOffset(holder.getEndOfPayPeriod()) - startZone.getOffset(holder.getStartOfPayPeriod());
        long duration = holder.getEndOfPayPeriod().getMillis() - holder.getStartOfPayPeriod().getMillis();
        int time = (int)((duration + offset) );

        if( time != holder.getDays()*24*60*60*1000 ){
            System.out.println("start of pp: " + holder.getStartOfPayPeriod());
            System.out.println("end of pp: " + holder.getEndOfPayPeriod() );
            System.out.println("Days in PP: " + holder.getDays());
            System.out.println("Time: " + time);
            fail("Number of days didn't match what it was supposed to.");
        }
    }

    @Test
    public void runMultipleTestsForSameValueAtEnd(){
        DateTime jobMidnight = DateTime.now().withDayOfWeek(1).minusWeeks(2);
        Job currentJob = new Job("My First Job", 10,
                jobMidnight, PayPeriodDuration.ONE_WEEK);
        PayPeriodHolder holder = new PayPeriodHolder(currentJob);
        for(int i = 1; i < 100; i++){
            moveXTimes(i, holder);
        }

        currentJob = new Job("My First Job", 10,
                jobMidnight, PayPeriodDuration.TWO_WEEKS);
        holder = new PayPeriodHolder(currentJob);
        for(int i = 1; i < 100; i++){
            moveXTimes(i, holder);
        }

        currentJob = new Job("My First Job", 10,
                jobMidnight, PayPeriodDuration.THREE_WEEKS);
        holder = new PayPeriodHolder(currentJob);
        for(int i = 1; i < 100; i++){
            moveXTimes(i, holder);
        }

        currentJob = new Job("My First Job", 10,
                jobMidnight, PayPeriodDuration.FOUR_WEEKS);
        holder = new PayPeriodHolder(currentJob);
        for(int i = 1; i < 100; i++){
            moveXTimes(i, holder);
        }

        currentJob = new Job("My First Job", 10,
                jobMidnight, PayPeriodDuration.FIRST_FIFTEENTH);
        holder = new PayPeriodHolder(currentJob);
        for(int i = 1; i < 100; i++){
            moveXTimes(i, holder);
        }

        currentJob = new Job("My First Job", 10,
                jobMidnight, PayPeriodDuration.FULL_MONTH);
        holder = new PayPeriodHolder(currentJob);
        for(int i = 1; i < 100; i++){
            moveXTimes(i, holder);
        }


    }

    @Test
    public void runTestGenerate(){
        DateTime jobMidnight = DateTime.now().withDayOfWeek(1).minusWeeks(2);
        Job currentJob = new Job("My First Job", 10,
                jobMidnight, PayPeriodDuration.TWO_WEEKS);
        PayPeriodHolder holder = new PayPeriodHolder(currentJob);

        DateTime initialStartOfPP = holder.getStartOfPayPeriod();

        for(int i = 0; i < 1; i++){
            holder.moveForwards();
        }
        holder.generate();

        if(! initialStartOfPP.isEqual(holder.getStartOfPayPeriod()) ){
            System.out.println("initial value: " + initialStartOfPP);
            System.out.println("moved value: " + holder.getStartOfPayPeriod() );
            fail("Moving forwards and backwards didn't match the original value, start");
        }
    }

    @Test
    public void runTestWithStartOfPPAfterToday(){
        DateTime jobMidnight = new DateTime(2013, 3, 14, 0, 0);
        Job currentJob = new Job("My First Job", 10,
                jobMidnight, PayPeriodDuration.TWO_WEEKS);
        PayPeriodHolder holder = new PayPeriodHolder(currentJob);
        DateTime startOfPP = holder.getStartOfPayPeriod();

        /*System.out.println(jobMidnight);
        System.out.println(startOfPP);
        System.out.println(DateTime.now());*/
        long daysInPP = Math.abs((startOfPP.getMillis() - DateTime.now().getMillis()) / 1000 / 60 / 60 / 24);
        if(daysInPP > 14 )
            fail("Start days greater then 2 weeks");

        if(startOfPP.isAfter(DateTime.now())){
            fail("The date should be before today");
        }
    }

    @Test
    public void runTestWithStartOfPPBeforeToday(){
        DateTime jobMidnight = new DateTime(2011, 1, 14, 0, 0);
        //System.out.println("midnight: " +jobMidnight );
        Job currentJob = new Job("My First Job", 10,
                jobMidnight, PayPeriodDuration.TWO_WEEKS);
        PayPeriodHolder holder = new PayPeriodHolder(currentJob);
        DateTime startOfPP = holder.getStartOfPayPeriod();

        long daysInPP = (DateTime.now().getMillis() - startOfPP.getMillis())
                / 1000 / 60 / 60 / 24 / 7;

        //System.out.println("-----" + daysInPP);
        //System.out.println("-----" + startOfPP);

        if(Math.abs(daysInPP) > 2 )
            fail("Start days greater then 2 weeks");
    }

    @Test
    public void runTestWithLengthOfPayPeriodFourWeeks(){
        DateTime jobMidnight = new DateTime(2012, 3, 11, 0, 0);
        //System.out.println("midnight: " +jobMidnight );
        Job currentJob = new Job("My First Job", 10,
                jobMidnight, PayPeriodDuration.FOUR_WEEKS);
        PayPeriodHolder holder = new PayPeriodHolder(currentJob);
        jobMidnight = holder.getStartOfPayPeriod();
        holder.moveForwards();
        DateTime startOfPP = holder.getStartOfPayPeriod();

        DateTime nextMonth = jobMidnight.plusWeeks(4);

        //System.out.println("-----" + daysInPP);
        //System.out.println("-----" + startOfPP);

        if(!startOfPP.isEqual(nextMonth)){
            System.out.println("Start of PP: " + startOfPP);
            System.out.println("Calculated: " + nextMonth);
            fail("Days didn't match up.");
        }
    }

    @Test
    public void runTestWithLengthOfPayPeriodFullMonth(){
        DateTime jobMidnight = new DateTime(2012, 3, 11, 0, 0);
        //System.out.println("midnight: " +jobMidnight );
        Job currentJob = new Job("My First Job", 10,
                jobMidnight, PayPeriodDuration.FULL_MONTH);
        PayPeriodHolder holder = new PayPeriodHolder(currentJob);
        DateTime startOfPP = holder.getStartOfPayPeriod();

        if(!startOfPP.isEqual(new DateTime(2012, DateTime.now().getMonthOfYear(), 1, 0, 0)))  {
            System.out.println(startOfPP);
            fail("Start of month didn't match");
        }

        jobMidnight = new DateTime(2014, 3, 11, 0, 0);
        //System.out.println("midnight: " +jobMidnight );
        currentJob = new Job("My First Job", 10,
                jobMidnight, PayPeriodDuration.FULL_MONTH);
        holder = new PayPeriodHolder(currentJob);
        startOfPP = holder.getStartOfPayPeriod();

        if(!startOfPP.isEqual(new DateTime(2012, DateTime.now().getMonthOfYear(), 1, 0, 0)))
            fail("Start of month didn't match");


    }
}
