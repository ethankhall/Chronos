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

package com.kopysoft.chronos.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.kopysoft.chronos.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by IntelliJ IDEA.
 * User: ethan
 * Date: 3/12/12
 * Time: 8:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class ShowEULA extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ula);
        TextView ulaText = (TextView)findViewById(R.id.ulaText);
        Button closeButton = (Button)findViewById(R.id.ulaClose);
        closeButton.setOnClickListener(listener);

        InputStream inputStream = getApplicationContext().getResources().openRawResource(R.raw.ula);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String textField = "";
        try {
            line = reader.readLine();
            while (line != null) {
                textField += line + "\n";
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ulaText.setText(textField);
    }
    
    Button.OnClickListener listener = new Button.OnClickListener(){

        @Override
        public void onClick(View view) {
            finish();
        }
    };
}
