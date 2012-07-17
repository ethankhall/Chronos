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

package com.kopysoft.chronos.activities.Editors;

import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ehdev.chronos.lib.Chronos;
import com.kopysoft.chronos.R;
import com.ehdev.chronos.enums.Defines;
import com.ehdev.chronos.types.Note;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class NoteEditor extends SherlockActivity {

    private static String TAG = Defines.TAG + " - NoteEditor";
    private final boolean enableLog = Defines.DEBUG_PRINT;
    private long date;
    private Note gNote;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(enableLog) Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_editor);

        EditText editText = (EditText)findViewById(R.id.textField);
        TextView tv = (TextView)findViewById(R.id.date);

        DateTimeFormatter fmt = DateTimeFormat.forPattern("E, MMM d, yyyy");

        if(savedInstanceState != null){
            date = savedInstanceState.getLong("date");
            Chronos chronos = new Chronos(this);
            gNote = chronos.getNoteByDay(new DateTime(date));
            chronos.close();
            editText.setText(savedInstanceState.getString("data"));

        } else {
            date = getIntent().getExtras().getLong("date");
            Chronos chronos = new Chronos(this);
            gNote = chronos.getNoteByDay(new DateTime(date));
            chronos.close();
            editText.setText(gNote.getNote());
        }

        //tv.setText(fmt.print(new DateTime(date)));
        tv.setText(fmt.print(gNote.getTime()));
        
        Log.d(TAG, "Note Editor with Date: " + date);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //This is a workaround for http://b.android.com/15340 from http://stackoverflow.com/a/5852198/132047
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            BitmapDrawable bg = (BitmapDrawable)getResources().getDrawable(R.drawable.bg_striped);
            bg.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            getSupportActionBar().setBackgroundDrawable(bg);

            BitmapDrawable bgSplit = (BitmapDrawable)getResources()
                    .getDrawable(R.drawable.bg_striped_split_img);
            bgSplit.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            getSupportActionBar().setSplitBackgroundDrawable(bgSplit);
        }
    }

    @Override
    protected void onSaveInstanceState (Bundle outState)
    {
        TextView tv = (TextView)findViewById(R.id.textField);
        outState.putString("data", tv.getText().toString());
        outState.putLong("date", date);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.save_cancel_note_editor, menu);

        return super.onCreateOptionsMenu(menu);
    }
    
    private void saveNote(){
        TextView tv = (TextView)findViewById(R.id.textField);
        gNote.setNote(tv.getText().toString());

        Chronos chrono = new Chronos(this);
        chrono.updateNote(gNote);
        chrono.close();
    }

    private void deleteNote(){
        Chronos chrono = new Chronos(this);
        chrono.deleteNote(gNote);
        chrono.close();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuDelete:
                deleteNote();
                finish();
                return true;
            case R.id.menuSave:
                saveNote();
                finish();
                return true;
            case R.id.menuCancel:
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
