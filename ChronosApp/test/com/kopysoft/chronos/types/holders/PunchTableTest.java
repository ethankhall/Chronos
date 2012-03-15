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
import com.kopysoft.chronos.types.Punch;
import com.kopysoft.chronos.types.Task;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
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
}
