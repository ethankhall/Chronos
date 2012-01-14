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

import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.types.Punch;
import com.kopysoft.chronos.types.Task;
import org.joda.time.Interval;

public class PunchPair implements Comparable<PunchPair>{

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

    public Task getTask(){
        return punchTask;
    }

    public Punch getPunch1(){
        return gPunch1;
    }

    public Punch getPunch2(){
        return gPunch2;
    }

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

    public Punch getOutPunch(){
        if(gPunch1 == null || gPunch2 == null)
            return null;
        else if(gPunch1.getTime().compareTo(gPunch2.getTime()) >= 0)
            return gPunch1;
        else
            return gPunch2;
    }
    
    public Interval getInterval(){
        if(getInPunch() == null || getOutPunch() == null){
            return null;
        } else {
            return new Interval(getInPunch().getTime(), getOutPunch().getTime());
        }
    }

    @Override
    public int compareTo(PunchPair punchPair) {
        return getInPunch().compareTo(punchPair.getInPunch());
    }
}
