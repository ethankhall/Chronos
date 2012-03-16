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

package com.kopysoft.chronos.types.holders;

import com.kopysoft.chronos.enums.PayPeriodDuration;
import com.kopysoft.chronos.types.Job;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.io.Serializable;

/**
 * @author Ethan Hall
 */
public class PayPeriodHolder implements Serializable {

    Job gJob;
    DateTime gStartOfPP = null;
    DateTime gEndOfPP = null;
    PayPeriodDuration gDuration;

    /**
     * Default constructor for PayPeriodHolder
     *
     * @param inputJob Job to do the work on
     */
    public PayPeriodHolder(Job inputJob){
        gJob = inputJob;
        generate();
    }

    /**
     * Gets the day's in this pay period
     * @return int of the day's in the the pay period
     */
    public int getDays(){
        switch (gJob.getDuration()){
            case ONE_WEEK:
                return 7;
            case TWO_WEEKS:
                return 7 * 2;
            case THREE_WEEKS:
                return 7 * 3;
            case FOUR_WEEKS:
                return 7 * 4;
            case FULL_MONTH:
                return gStartOfPP.dayOfMonth().getMaximumValue();
            case FIRST_FIFTEENTH:
                if(gStartOfPP.getDayOfMonth() <= 15)
                    return 15;
                else
                    return gStartOfPP.dayOfMonth().getMaximumValue() - 15;
        }
        return 0;
    }

    /**
     * Get the start of the Pay Period. It will call generate if it needs to be called.
     *
     * @return DateMidnight containing the start of the Pay Period
     */
    public DateTime getStartOfPayPeriod(){
        if(gStartOfPP == null){
            generate();
        }
        return gStartOfPP;
    }

    /**
     * Get the end of the Pay Period. It will call generate if it needs to be called.
     *
     * @return DateMidnight containing the end of the Pay Period
     */
    public DateTime getEndOfPayPeriod(){
        if(gEndOfPP == null){
            generate();
        }
        return gEndOfPP;
    }

    /**
     * Will do the calculations for the start and end of the process
     */
    public void generate(){
        //Get the start and end of pay period
        DateTime startOfPP = gJob.getStartOfPayPeriod();
        gDuration = gJob.getDuration();
        DateTime endOfPP = new DateTime(); //Today

        Interval interval;
        if(startOfPP.isBefore(endOfPP))
            interval =  new Interval(startOfPP, endOfPP);
        else
            interval =  new Interval(endOfPP, startOfPP);
        int days = (int)interval.toDuration().getStandardDays();
        switch (gDuration){
            case ONE_WEEK:
                days = days / 7;
                startOfPP = startOfPP.plusWeeks(days);
                endOfPP = startOfPP.plusWeeks(1);
                break;
            case TWO_WEEKS:
                days = days / (7 * 2);
                startOfPP = startOfPP.plusWeeks(days * 2);
                endOfPP = startOfPP.plusWeeks(2);
                break;
            case THREE_WEEKS:
                days = days / ( 7 * 3);
                startOfPP = startOfPP.plusWeeks(days * 3);
                endOfPP = startOfPP.plusWeeks(3);
                break;
            case FOUR_WEEKS:
                days = days / ( 7 * 4);
                startOfPP= startOfPP.plusWeeks(days * 4);
                endOfPP = startOfPP.plusWeeks(4);
                break;
            case FULL_MONTH:
                //in this case, endOfPP is equal to now
                startOfPP = gJob.getStartOfPayPeriod().withDayOfMonth(1);
                endOfPP = startOfPP.plusMonths(1);

                break;
            case FIRST_FIFTEENTH:
                DateTime now = DateTime.now();
                if(now.getDayOfMonth() >= 15){
                    startOfPP = now.withDayOfMonth(15);
                    endOfPP = startOfPP.plusDays(20).withDayOfMonth(1);
                } else {
                    startOfPP = now.withDayOfMonth(1);
                    endOfPP = now.withDayOfMonth(15);
                }
                break;
            default:
                break;
        }

        gStartOfPP = startOfPP;
        gEndOfPP = endOfPP;
    }

    public void moveBackwards(){
        if(gJob.getDuration() == PayPeriodDuration.FIRST_FIFTEENTH){
            if(gStartOfPP.getDayOfMonth() == 1){
                gStartOfPP = gStartOfPP.minusMonths(1).withDayOfMonth(15);
                gEndOfPP = gStartOfPP.withDayOfMonth(1).plusMonths(1);
            } else {
                gStartOfPP = gStartOfPP.withDayOfMonth(1);
                gEndOfPP = gStartOfPP.withDayOfMonth(15);
            }
        } else if(gJob.getDuration() == PayPeriodDuration.FULL_MONTH){
            gEndOfPP = gStartOfPP.minusDays(1);
            gStartOfPP = gStartOfPP.withDayOfMonth(1).minusMonths(1);

        } else {
            gEndOfPP = gStartOfPP.minusDays(1);
            gStartOfPP = gEndOfPP.minusDays(getDays());
            //gEndOfPP = gStartOfPP.plusDays(getDays() - 1);
        }
    }

    public void moveForwards(){
        if(gJob.getDuration() == PayPeriodDuration.FIRST_FIFTEENTH){
            if(gStartOfPP.getDayOfMonth() == 1){
                gStartOfPP = gStartOfPP.withDayOfMonth(15);
                gEndOfPP = gStartOfPP.withDayOfMonth(1).plusMonths(1).minusDays(1);
            } else {
                gStartOfPP = gStartOfPP.withDayOfMonth(1).plusMonths(1);
                gEndOfPP = gStartOfPP.withDayOfMonth(15);
            }
        } else if(gJob.getDuration() == PayPeriodDuration.FULL_MONTH){
            gStartOfPP = gEndOfPP.plusDays(1);
            gStartOfPP = gStartOfPP.plusMonths(1).minusMonths(1);
        } else {
            gStartOfPP = gStartOfPP.plusDays(getDays());
            gEndOfPP = gStartOfPP.plusDays(getDays() );
        }
    }
}
