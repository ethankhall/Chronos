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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TaskTable {

    private static final String TAG = Defines.TAG + " - TaskTable";
    Map gMap;
    List<Integer> listOfTasks;

    public TaskTable(){
        gMap = new HashMap<Integer, List<Punch>>();
        listOfTasks = new LinkedList<Integer>();
    }

    public void insert(Task key, Punch value){
        //Add key
        boolean needToAdd = true;
        for( int i = 0; i < listOfTasks.size(); i++){
            if(listOfTasks.get(i).compareTo(key.getID()) == 0 ) {
                needToAdd = false;
                break;
            }
        }
        if(needToAdd)
            listOfTasks.add(key.getID());

        LinkedList<Punch> list;
        if(gMap.containsKey(key.getID())){
            list = (LinkedList) gMap.get(key.getID());
        } else {
            list = new LinkedList<Punch>();
            gMap.put(key.getID(), list);
        }

        list.add(value);
    }

    public List<Punch> getPunchesForKey(Integer key){
        return (List<Punch>) gMap.get(key);
    }

    public List<Punch> getPunchesForKey(Task key){
        return getPunchesForKey(key.getID());
    }

    public List<Integer> getTasks(){
        //Log.d(TAG, "Table Punch Size: " + listOfTasks.size());
        return listOfTasks;
    }

}
