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

package com.ehdev.chronos.types;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.junit.Test;

import static junit.framework.Assert.fail;

public class PunchTest {
    @Test
    public void testCompareTo() throws Exception {

        DateTime start = new DateTime();
        DateTime end = start.minusHours(1);
        Job newJob = new Job();
        Task thisTask = new Task(newJob, 0, "");
        Punch punch1 = new Punch(newJob, thisTask, start);
        Punch punch2 = new Punch(newJob, thisTask, end);
        if(punch1.compareTo(punch2) <= 0){
            fail("Compare is Wrong");
        }
    }

    @Test
    public void TestTimeZone() throws Exception {
        DateTimeZone zone1 = DateTimeZone.forID("America/New_York");
        DateTimeZone zone2 = DateTimeZone.forID("America/Chicago");
        DateTime currentTi = new DateTime(zone1);
        long time = currentTi.getMillis();

        DateTime testAgainst = new DateTime(time, zone2);
        //System.out.println("Local Time: " + currentTi.getHourOfDay());
        //System.out.println("Test Against: " + testAgainst.getHourOfDay());

        Period per = new Period(currentTi, testAgainst);

        if(per.getHours() != 0){
            fail("Time Zone Test Failed");
        }
    }
}
