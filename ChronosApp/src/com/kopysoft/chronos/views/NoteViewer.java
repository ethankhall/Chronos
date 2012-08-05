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

package com.kopysoft.chronos.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import com.ehdev.chronos.lib.Chronos;
import com.j256.ormlite.dao.Dao;
import com.kopysoft.chronos.R;
import com.kopysoft.chronos.adapter.note.NoteAdapter;
import com.ehdev.chronos.lib.enums.Defines;
import com.ehdev.chronos.lib.types.Job;
import com.ehdev.chronos.lib.types.Note;

import java.util.List;

public class NoteViewer extends Fragment {
    
    private final String TAG = Defines.TAG + " - NoteViewer";

    private Job gJob = null;
    private int scrollPosition = 0;

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View layout = inflater.inflate(R.layout.note_layout, container);

        try{
            Chronos chrono = new Chronos(getActivity());
            Dao<Note, String> noteDoa =  chrono.getNoteDao();
            Dao<Job, String> jobDoa =  chrono.getJobDao();

            List<Note> listOfNotes = noteDoa.queryForAll();

            //Pull the links
            for(Note work : listOfNotes){
                jobDoa.refresh(work.getJob());
            }
            
            Log.d(TAG, "Size of Lists: " + listOfNotes.size());

            ListView retView = (ListView)layout.findViewById(R.id.listView);
            BaseAdapter adapter;

            adapter = new NoteAdapter(getActivity(), listOfNotes);
            retView.setAdapter( adapter );
            retView.setSelection( scrollPosition );

        } catch (java.sql.SQLException e){
            Log.e(TAG, e.getMessage());
            
        }
        return layout;
    }
}
