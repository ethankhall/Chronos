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

package com.ehdev.chronos.lib.overtime;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.ArrayList;
import java.util.List;


public class DurationHolder {
    Duration normal = new Duration(0);
    Duration saturday = new Duration(0);
    Duration sunday = new Duration(0);
    List<DurationWrapper> listOfNormalPay = new ArrayList<DurationWrapper>();
    List<DurationWrapper> listOfSundayPay = new ArrayList<DurationWrapper>();
    List<DurationWrapper> listOfSaturdayPay = new ArrayList<DurationWrapper>();

    public DurationHolder(){  }

    public void addNormalPay(DateMidnight dt, Duration duration){
        listOfNormalPay.add(new DurationWrapper(dt, duration));
        normal = normal.plus(duration);
    }

    public void addSaturdayPay(DateMidnight dt, Duration duration){
        listOfSaturdayPay.add(new DurationWrapper(dt, duration));
        saturday = saturday.plus(duration);
    }

    public void addSundayPay(DateMidnight dt, Duration duration){
        listOfSundayPay.add(new DurationWrapper(dt, duration));
        sunday = sunday.plus(duration);
    }

    public List<DurationWrapper> getNormalDates(){
        List<DurationWrapper> listOfDates = new ArrayList<DurationWrapper>();
        for(DurationWrapper w: listOfNormalPay){
            listOfDates.add(w);
        }
        return listOfDates;
    }

    public Duration getNormalDuration(){
        return normal;
    }

    public Duration getSundayDuration(){
        return sunday;
    }

    public Duration getSaturdayDuration(){
        return saturday;
    }

    public Duration getTotalTimeWorked(){
        return saturday.plus(sunday).plus(normal);
    }

    public class DurationWrapper{
        public DateMidnight dateTime;
        public Duration duration;
        DurationWrapper(DateMidnight date, Duration dur){
            dateTime = date;
            duration = dur;
        }
    }
}
