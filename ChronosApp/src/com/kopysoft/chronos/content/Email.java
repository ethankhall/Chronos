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

package com.kopysoft.chronos.content;

import android.content.Context;
import android.text.format.DateFormat;
import com.kopysoft.chronos.adapter.clock.PayPeriodAdapterList;
import com.kopysoft.chronos.types.Job;
import com.kopysoft.chronos.types.holders.PayPeriodHolder;
import com.kopysoft.chronos.types.holders.PunchPair;
import com.kopysoft.chronos.types.holders.PunchTable;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

public class Email {
    PayPeriodHolder gPayPeriodHolder;
    Job gThisJob;
    Context gContext;
    PunchTable punchTable;
    
    public Email(PayPeriodHolder payPeriodHolder, Job thisJob, Context context){
        gPayPeriodHolder = payPeriodHolder;
        gThisJob = thisJob;
        gContext = context;
        
        Chronos chronos = new Chronos(context);
        punchTable = chronos.getAllPunchesForPayPeriodByJob(thisJob, 
                payPeriodHolder.getStartOfPayPeriod(), payPeriodHolder.getEndOfPayPeriod());
        chronos.close();
    }
    
    public String getBriefView(){
        String retString = "";
        List<DateTime> dates = punchTable.getDays();
        for(DateTime date : dates){
            DateTimeFormatter fmt = DateTimeFormat.forPattern("E, MMM d, yyyy");
            String time = fmt.print(date);

            Duration dur = PayPeriodAdapterList.getTime(punchTable.getPunchPair(date));
            retString += time +  String.format(" - %02d:%02d\n",
                    dur.toPeriod().getHours(), dur.toPeriod().getMinutes());
        }
        
    return retString;
    }

    public String getExpandedView(){
        String retString = "";
        List<DateTime> dates = punchTable.getDays();

        DateTimeFormatter dateFormat = DateTimeFormat.forPattern("E, MMM d, yyyy:\n");
        DateTimeFormatter fmt;
        if (!DateFormat.is24HourFormat(gContext))
            fmt = DateTimeFormat.forPattern("h:mm a");
        else
            fmt = DateTimeFormat.forPattern("HH:mm");
        
        for(DateTime date : dates){
            if( punchTable.getPunchPair(date).size() > 0)
                retString += dateFormat.print(date);
            
            for(PunchPair pp : punchTable.getPunchPair(date)){
                retString += "\t" + fmt.print(pp.getInPunch().getTime()) + " - \tIN - "
                        + pp.getInPunch().getTask().getName() + "\n";
                if(pp.getOutPunch() != null){
                    retString += "\t" + fmt.print(pp.getOutPunch().getTime()) + " - \tOUT - "
                            + pp.getOutPunch().getTask().getName() + "\n";
                }


            }

        }
        return retString;
    }
}
