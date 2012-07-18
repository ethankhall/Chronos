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

package com.ehdev.chronos.lib.types.holders;

import com.ehdev.chronos.lib.enums.Defines;
import com.ehdev.chronos.lib.types.Job;
import com.ehdev.chronos.lib.types.Punch;
import com.ehdev.chronos.lib.types.Task;
import org.joda.time.Duration;

public class PunchPair implements Comparable<PunchPair>{

    @SuppressWarnings("unused")
    private static final String TAG = Defines.TAG + " - PunchPair";

    Punch gPunch1, gPunch2;
    Task punchTask;
    public PunchPair(Punch punch1, Punch punch2){
        gPunch1 = punch1;
        gPunch2 = punch2;


        if ( gPunch2 != null && gPunch1.getTask().compareTo(gPunch2.getTask()) != 0)
            throw new IllegalArgumentException("Argument Tasks do not match");

        punchTask = gPunch1.getTask();
    }
    
    public Job getJob(){
        return punchTask.getJob();
    }

    /**
     * Gets the Task that the two Punches have
     *
     * @return Task of the two Punches
     */
    public Task getTask(){
        return punchTask;
    }

    @SuppressWarnings("unused")
    public Punch getPunch1(){
        return gPunch1;
    }

    @SuppressWarnings("unused")
    public Punch getPunch2(){
        return gPunch2;
    }

    /**
     * Gets the IN punch
     *
     * @return an IN punch or null if none exists
     */
    public Punch getInPunch(){
        if(gPunch1 == null)
            return gPunch2;
        else if(gPunch2 == null)
            return gPunch1;
        else if(gPunch1.getTime().compareTo(gPunch2.getTime()) <= 0)
            return gPunch1;
        else
            return gPunch2;
    }

    /**
     * Gets the OUT punch
     *
     * @return an OUT punch or null if none exists
     */
    public Punch getOutPunch(){
        if(gPunch1 == null || gPunch2 == null)
            return null;
        else if(gPunch1.getTime().compareTo(gPunch2.getTime()) >= 0)
            return gPunch1;
        else
            return gPunch2;
    }

    /**
     * Returns an interval based on the two Punches held in the class.
     *
     * @return Interval with the two punches. If one of the punches
     *  doesn't exists the return value will be a new Interval with the value of 0
     */
    public long getDuration(){
        if(getInPunch() == null || getOutPunch() == null){
            return -1 * getInPunch().getTime().getMillis();
        } else {
            return new Duration(getInPunch().getTime(), getOutPunch().getTime()).getMillis();
        }
    }

    @Override
    public int compareTo(PunchPair punchPair) {
        return getInPunch().compareTo(punchPair.getInPunch());
    }
}
