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

package com.kopysoft.chronos.views.helpers;

import android.content.Context;
import android.graphics.Color;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RowElement extends RelativeLayout {

	TextView rightText, leftText, centerText;

	public TextView left(){
		return leftText;
	}

	public TextView right(){
		return rightText;
	}

    public TextView center(){
		return centerText;
	}

    public RowElement(Context context, int left_p, int top_p, int right_p, int bottom_p ) {
        super(context);

		setPadding(left_p, top_p, right_p, bottom_p);

		leftText = new TextView(context);
		leftText.setText("right");
		leftText.setTextSize(18);
        leftText.setTextColor(Color.BLACK);

		rightText = new TextView(context);
		rightText.setText("left");
		rightText.setTextSize(18);
        rightText.setTextColor(Color.BLACK);

        centerText = new TextView(context);
        centerText.setText("");
        centerText.setTextSize(18);
        centerText.setTextColor(Color.BLACK);

		//Set up the relitive layout
		RelativeLayout.LayoutParams left =
			new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);

		left.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		left.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        RelativeLayout.LayoutParams center =
			new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);

		center.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		center.addRule(RelativeLayout.CENTER_HORIZONTAL);


		RelativeLayout.LayoutParams right =
			new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);

		right.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		right.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		//right.addRule(RelativeLayout.RIGHT_OF, leftText.getId());

		addView(leftText, left);
        addView(centerText, center);
		addView(rightText, right);

    }

	public RowElement(Context context){
        this(context, 10, 3, 10, 3);
	}
}
