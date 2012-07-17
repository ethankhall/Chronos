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

import com.ehdev.chronos.enums.PayPeriodDuration;
import com.ehdev.chronos.types.holders.PayPeriodHolder;
import com.ehdev.chronos.types.holders.PunchTable;
import org.joda.time.DateMidnight;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.fail;

public class EndOfDayTest {

    @Test
    public void basicTest(){
        int numberToCreate = 5;
        //Create basic pay period, starting at midnight
        DateMidnight today = new DateMidnight();
        Job thisJob = new Job("test", (float)10.0, today.toDateTime(), PayPeriodDuration.ONE_WEEK);
        Task thisTask = new Task(thisJob, 1, "task");
        PayPeriodHolder pph = new PayPeriodHolder(thisJob);

        PunchTable table = new PunchTable(pph.getStartOfPayPeriod(), pph.getEndOfPayPeriod(), thisJob);
        for(int i = 0; i < numberToCreate; i++){
            Punch newPunch = new Punch(thisJob, thisTask, today.toDateTime().plusHours(i + 1));
            table.insert(newPunch);
        }

        List<Punch> listOfPunches = table.getPunchesByDay(today.toDateTime());
        if(listOfPunches.size() != numberToCreate){
            fail("Number of punches created didn't match the number of punches retieved");
        }

    }
}
