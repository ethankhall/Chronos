package com.kopysoft.chronos.view;

import android.content.Context;
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

		rightText = new TextView(context);
		rightText.setText("left");
		rightText.setTextSize(18);

        centerText = new TextView(context);
        rightText.setText("");
        centerText.setTextSize(18);

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
