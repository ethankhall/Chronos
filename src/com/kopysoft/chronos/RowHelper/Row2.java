package com.kopysoft.chronos.RowHelper;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Row2 extends RelativeLayout{

	private Context mContext;

	TextView rightText, leftText;

	public TextView left(){
		return leftText;
	}

	public TextView right(){
		return rightText;
	}   

	public Row2(Context context){
		super(context);
		mContext = context;

		setPadding(10, 0, 10, 0);

		leftText = new TextView(mContext);
		leftText.setText("right");
		leftText.setTextSize(18);

		rightText = new TextView(mContext);
		rightText.setText("left");
		rightText.setTextSize(18);

		//Set up the relitive layout
		RelativeLayout.LayoutParams left = 
			new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 
					RelativeLayout.LayoutParams.WRAP_CONTENT);

		left.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		left.addRule(RelativeLayout.ALIGN_PARENT_LEFT);


		RelativeLayout.LayoutParams right = 
			new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 
					RelativeLayout.LayoutParams.WRAP_CONTENT);

		right.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		right.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		right.addRule(RelativeLayout.RIGHT_OF, leftText.getId());

		addView(leftText, left);
		addView(rightText, right);
	}
}
