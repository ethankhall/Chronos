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

package com.kopysoft.chronos.views.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.kopysoft.chronos.R;

public class TwoLineElement extends LinearLayout {

	TextView rightText =  null;
    TextView leftText = null;
    TextView secondRow = null;

    View thisView = null;

	public TextView left(){
        if(leftText == null)
            leftText = (TextView)thisView.findViewById(R.id.leftTextElement);
		return leftText;
	}

	public TextView right(){
        if(rightText == null)
            rightText = (TextView)thisView.findViewById(R.id.rightTextElement);
		return rightText;
	}

    public TextView secondRow(){
        if(secondRow == null)
            secondRow = (TextView)thisView.findViewById(R.id.bottomRow);
		return secondRow;
    }

	public TwoLineElement(Context context){
        super(context);

        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        thisView=layoutInflater.inflate(R.layout.row_element_two_line,this);

	}
}
