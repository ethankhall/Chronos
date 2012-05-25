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

import com.kopysoft.chronos.types.Job;
import com.kopysoft.chronos.types.Punch;
import com.kopysoft.chronos.types.Task;
import org.joda.time.DateTime;
import org.junit.Test;
import static org.junit.Assert.*;

public class PunchPairTest {

    //Test constructor exception
    @Test(expected = IllegalArgumentException.class)
    public void TestContructorExceptionFail() throws Exception{
        Job newJob = new Job();
        Task task1 = new Task(newJob,0, "");
        Task task2 = new Task(newJob,1, "");

        Punch punch1 = new Punch(newJob, task1, new DateTime());
        Punch punch2 = new Punch(newJob, task2, new DateTime());

        new PunchPair(punch1, punch2);
    }

    //Everything OKAY
    @Test
    public void EverythingNormal() throws Exception{
        Job newJob = new Job();
        Task task1 = new Task(newJob,0, "");

        Punch punch1 = new Punch(newJob, task1, new DateTime());
        Punch punch2 = new Punch(newJob, task1, new DateTime());

        new PunchPair(punch1, punch2);
    }

    @Test
    public void TestNullSent1() throws Exception{
        Job newJob = new Job();
        Task task1 = new Task(newJob,0, "");
        Punch punch1 = new Punch(newJob, task1, new DateTime());

        PunchPair pp = new PunchPair(punch1, null);
        assertNotNull(pp.getInPunch());
    }

    @Test
    public void TestNullSent2() throws Exception{
        Job newJob = new Job();
        Task task1 = new Task(newJob,0, "");
        Punch punch1 = new Punch(newJob, task1, new DateTime());

        PunchPair pp = new PunchPair(punch1, null);
        assertNull(pp.getOutPunch());
    }

    @Test
    public void TestPunchPairOrder() throws Exception{
        Job newJob = new Job();
        Task task1 = new Task(newJob,0, "");
        Punch punch1 = new Punch(newJob, task1, new DateTime());

        PunchPair pp = new PunchPair(punch1, null);
        assertNull(pp.getOutPunch());
        assertNotNull(pp.getInPunch());
    }

    @Test
    public void TestAssignment1() throws Exception{
        Job newJob = new Job();
        Task task1 = new Task(newJob,0, "");
        Punch punch1 = new Punch(newJob, task1, new DateTime());

        PunchPair pp = new PunchPair(punch1, null);
        assertEquals(punch1, pp.getPunch1());
        assertEquals(null, pp.getPunch2());
    }

    @Test
    public void TestAssignment2() throws Exception{
        Job newJob = new Job();
        Task task1 = new Task(newJob,0, "");

        Punch punch1 = new Punch(newJob, task1, new DateTime());
        Punch punch2 = new Punch(newJob, task1, new DateTime());

        PunchPair pp = new PunchPair(punch1, punch2);
        assertEquals(punch1, pp.getPunch1());
        assertEquals(punch2, pp.getPunch2());

    }

}
