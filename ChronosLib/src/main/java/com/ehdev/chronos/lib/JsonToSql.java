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

package com.ehdev.chronos.lib;

import com.ehdev.chronos.lib.types.*;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class JsonToSql {
    private Chronos gChronos;

    public JsonToSql(Chronos chron){
        gChronos = chron;
    }

    public String getJson(){
        List<Job> listOfJobs = gChronos.getAllJobs();
        Gson gson = new Gson();
        List<JsonObj> json = new ArrayList<JsonObj>();

        for(Job j : listOfJobs){
            List<Task> tasks = gChronos.getAllTasks(j);
            List<Note> notes = gChronos.getAllNotes(j);
            List<Punch> punches = gChronos.getPunchesByJob(j);

            json.add(new JsonObj(j, punches, tasks, notes));
        }

        JsonObj[] jsonArray = new JsonObj[json.size()];
        for(int i = 0; i < json.size(); i++){
            jsonArray[i] = json.get(i);
        }

        return gson.toJson(jsonArray);
    }

}
