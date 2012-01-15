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

import android.util.Log;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.types.Punch;
import org.joda.time.DateMidnight;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PunchTable {

    private static final String TAG = Defines.TAG + " - TaskTable";
    Map gMap;
    List<DateMidnight> listOfDays;

    public PunchTable(){
        gMap = new HashMap<DateMidnight, List<Punch>>();
        listOfDays = new LinkedList<DateMidnight>();
    }

    public void insert( Punch value){
        //Add key
        DateMidnight key = value.getTime().toDateMidnight();
        boolean needToAdd = true;
        for( int i = 0; i < listOfDays.size(); i++){
            if(listOfDays.get(i).compareTo(key) == 0 ) {
                needToAdd = false;
                break;
            }
        }
        if(needToAdd)
            listOfDays.add(key);

        LinkedList<Punch> list;
        if(gMap.containsKey(key)){
            list = (LinkedList) gMap.get(key);
        } else {
            list = new LinkedList<Punch>();
            gMap.put(key, list);
        }

        list.add(value);
    }
    
    public List<Punch> getPunchesByDay(DateMidnight date){
        return ((List)gMap.get(date));
    }

    public List<DateMidnight> getDays(){
        Log.d(TAG, "Table Date Size: " + listOfDays.size());
        return listOfDays;
    }

}
