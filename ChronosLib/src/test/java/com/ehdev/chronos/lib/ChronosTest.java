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

package com.ehdev.chronos.lib;

import com.ehdev.chronos.lib.enums.PayPeriodDuration;
import com.ehdev.chronos.lib.types.Job;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.fail;

public class ChronosTest {
    @Test
    public void testGetDateFromStartOfPayPeriodDateDate() throws Exception {
        DateTime startOfPP = DateMidnight.now().toDateTime().minusDays(1);
        DateTime date = DateTime.now();
        DateTime retValue = Chronos.getDateFromStartOfPayPeriod(startOfPP, date);
        if(startOfPP.getHourOfDay() != retValue.getHourOfDay()){
            fail("Hour didn't match up with the start of pp");
        }

        if(startOfPP.getMinuteOfDay() != retValue.getMinuteOfDay()){
            fail("Minute didn't match up with the start of pp");
        }

        if(startOfPP.getSecondOfDay() != retValue.getSecondOfDay()){
            fail("Second didn't match up with the start of pp");
        }

        if(date.getDayOfYear() != retValue.getDayOfYear()){
            fail("Day of Year didn't match up with date given");
        }

        if(date.getYear() != retValue.getYear()){
            fail("Year didn't match up with date given");
        }

        if(date.getMonthOfYear() != retValue.getMonthOfYear()){
            fail("Year didn't match up with date given");
        }
    }

    @Test
    public void testGetDateFromStartOfPayPeriodDateDateWithoutMidnight() throws Exception {
        DateTime startOfPP;
        if(DateTime.now().getHourOfDay() == 0){
            startOfPP = DateTime.now().minusDays(1).minusMinutes(10);
        } else {
            startOfPP = DateTime.now().minusDays(1).minusHours(1);
        }
        DateTime date = DateTime.now();
        DateTime retValue = Chronos.getDateFromStartOfPayPeriod(startOfPP, date);

        if(startOfPP.getHourOfDay() != retValue.getHourOfDay()){
            fail("Hour didn't match up with the start of pp");
        }

        if(startOfPP.getMinuteOfDay() != retValue.getMinuteOfDay()){
            fail("Minute didn't match up with the start of pp");
        }

        if(startOfPP.getSecondOfDay() != retValue.getSecondOfDay()){
            fail("Second didn't match up with the start of pp");
        }

        if(date.getDayOfYear() != retValue.getDayOfYear()){
            fail("Day of Year didn't match up with date given");
        }

        if(date.getYear() != retValue.getYear()){
            fail("Year didn't match up with date given");
        }

        if(date.getMonthOfYear() != retValue.getMonthOfYear()){
            fail("Year didn't match up with date given");
        }
    }

    @Test
    public void testGetDateFromStartOfPayPeriodJobDate() throws Exception {
        DateTime startOfPP = DateTime.now().toDateMidnight().toDateTime().minusDays(7);
        Job thisJob = new Job("", 10, startOfPP, PayPeriodDuration.THREE_WEEKS);
        DateTime date = DateTime.now();

        DateTime retValue = Chronos.getDateFromStartOfPayPeriod(thisJob, date);
        if(startOfPP.getHourOfDay() != retValue.getHourOfDay()){
            fail("Hour didn't match up with the start of pp");
        }

        if(startOfPP.getMinuteOfDay() != retValue.getMinuteOfDay()){
            fail("Minute didn't match up with the start of pp");
        }

        if(startOfPP.getSecondOfDay() != retValue.getSecondOfDay()){
            fail("Second didn't match up with the start of pp");
        }

        if(date.getDayOfYear() != retValue.getDayOfYear()){
            fail("Day of Year didn't match up with date given");
        }

        if(date.getYear() != retValue.getYear()){
            fail("Year didn't match up with date given");
        }

        if(date.getMonthOfYear() != retValue.getMonthOfYear()){
            fail("Year didn't match up with date given");
        }
    }

    @Test
    public void testDST(){
        DateMidnight startDate = new DateMidnight(2012, 3, 4);
        Job thisJob = new Job("", 10, startDate.toDateTime(), PayPeriodDuration.THREE_WEEKS);
        
        DateTime startTimeBefore = startDate.plusDays(1).toDateTime().plusHours(12);
        DateTime dateBefore = Chronos.getDateFromStartOfPayPeriod(thisJob,startTimeBefore);
        if(!dateBefore.isEqual(startDate.plusDays(1).toDateTime())){
            fail("Time before DST failed.");
        }

        DateTime startTimeAfter = startDate.plusDays(1).plusWeeks(1).toDateTime().plusHours(12);
        DateTime dateAfter = Chronos.getDateFromStartOfPayPeriod(thisJob,startTimeAfter);
        if(!dateAfter.isEqual(startDate.plusDays(1).plusWeeks(1).toDateTime())){
            fail("Time after DST failed.");
        }
    }

    @Test
    public void testFailOnPunchTableWithJob(){

        DateTime startDate = DateTime.now().toDateMidnight().toDateTime().minusDays(7);
        Job thisJob = new Job("", 10, startDate, PayPeriodDuration.THREE_WEEKS);

        DateTime workFrom = startDate.plusDays(5);
        DateTime tempDate = workFrom.plusHours(0);
        DateTime ObjUT = Chronos.getDateFromStartOfPayPeriod(thisJob,tempDate);

        if(ObjUT.getDayOfYear() != tempDate.getDayOfYear()){
            fail("Day of Year is wrong");
        }

    }

    @Test
    public void testFailOnPunchTableWithDate(){
        DateTime startDate = DateTime.now().toDateMidnight().toDateTime().minusDays(7);

        DateTime workFrom = startDate.plusDays(5).withZoneRetainFields(startDate.getZone());
        DateTime tempDate = workFrom;
        DateTime ObjUT = Chronos.getDateFromStartOfPayPeriod(startDate,tempDate);

        if(ObjUT.getDayOfYear() != tempDate.getDayOfYear()){
            System.out.println("Day of year temp: " + tempDate.getDayOfYear());
            System.out.println("Day of year ObjUT: " + ObjUT.getDayOfYear());
            fail("Day of Year is wrong");
        }
    }
}
