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

package com.kopysoft.chronos.adapter.note;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.types.Note;
import com.kopysoft.chronos.view.TwoLineElement;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class NoteAdapter extends BaseAdapter {


    private static final String TAG = Defines.TAG + " - TodayAdapterIndividual";

    Context gContext;
    List<Note> gListOfNotes;


    public NoteAdapter(Context context, List< Note > listOfPunches){
        gListOfNotes = new LinkedList<Note>(listOfPunches);
        gContext = context;
        Log.d(TAG, "Size: " + gListOfNotes.size());

        sort();
    }

    public void addNote(Note input){
        gListOfNotes.add(input);
        sort();
        notifyDataSetChanged();
    }

    public void removeNote(int id){
        gListOfNotes.remove(id);
        sort();
    }

    /**
     * Sorts the elements in the list
     */
    private void sort(){
        Collections.sort(gListOfNotes);
    }

    @Override
    public int getCount() {
        return gListOfNotes.size();
    }

    @Override
    public Object getItem(int i) {
        if(i > gListOfNotes.size())
            return null;
        return gListOfNotes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if(view == null){
            view = new TwoLineElement(gContext);
        }
        Note note = gListOfNotes.get(i);

        TwoLineElement curr = (TwoLineElement) view;

        TextView left = curr.left();
        TextView right = curr.right();
        TextView bottom = curr.secondRow();

        DateTimeFormatter fmt;
        if (!DateFormat.is24HourFormat(gContext))
            fmt = DateTimeFormat.forPattern("h:mm a");
        else
            fmt = DateTimeFormat.forPattern("HH:mm");

        left.setText(note.getTime().toString(fmt));

        right.setText(note.getTask().getName());

        bottom.setText(note.getNote());

        return curr;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
