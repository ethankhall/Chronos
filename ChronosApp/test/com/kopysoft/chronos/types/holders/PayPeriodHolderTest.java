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

import com.kopysoft.chronos.enums.PayPeriodDuration;
import com.kopysoft.chronos.types.Job;
import com.kopysoft.chronos.types.holders.PayPeriodHolder;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.fail;

public class PayPeriodHolderTest {
    @Test
    public void testGenerate() throws Exception {
        DateTime jobMidnight = DateTime.now().withDayOfWeek(1).minusWeeks(2);
        Job currentJob = new Job("My First Job", 10,
                jobMidnight.toDateTime(), PayPeriodDuration.TWO_WEEKS);
        PayPeriodHolder holder = new PayPeriodHolder(currentJob);

        DateTime startOfPPCalculated = jobMidnight.plusWeeks(2);
        if(holder.getStartOfPayPeriod().getMillis() != startOfPPCalculated.getMillis()){
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

        for(int i = 0; i < moveTimes; i++){
            holder.moveForwards();
        }

        for(int i = 0; i < moveTimes; i++){
            holder.moveBackwards();
        }

        if(! initialStartOfPP.isEqual(holder.getStartOfPayPeriod()) ){
            System.out.println("initial value: " + initialStartOfPP);
            System.out.println("moved value: " + holder.getStartOfPayPeriod() );
            fail("Moving forwards and backwards didn't match the original value, start");
        }

        if(! initialEndOfPP.isEqual(holder.getEndOfPayPeriod()) ) {
            System.out.println("initial value" + initialEndOfPP);
            System.out.println("moved value" + holder.getEndOfPayPeriod() );
            fail("Moving forwards and backwards didn't match the original value, end");
        }
    }
    
    @Test
    public void runMultipleTestsForSameValueAtEnd(){
        DateTime jobMidnight = DateTime.now().withDayOfWeek(1).minusWeeks(2);
        Job currentJob = new Job("My First Job", 10,
                jobMidnight, PayPeriodDuration.TWO_WEEKS);
        PayPeriodHolder holder = new PayPeriodHolder(currentJob);
        for(int i = 1; i < 100; i++){
            moveXTimes(i, holder);
        }
    }
}
