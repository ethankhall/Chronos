package com.kopysoft.chronos.subActivites.misc;

/**
 * 			Copyright (C) 2011 by Ethan Hall
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 * 	in the Software without restriction, including without limitation the rights
 * 	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * 	copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 *  
 */

import android.app.Dialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.kopysoft.chronos.R;

public class HelpActivity extends ListActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_action);
		setTitle("Chronos:  Help");
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String title = "";
		String message = "";
		switch(position){
		case 0:
			//<item>Add a Punch</item>
			//add_punch
			title = "Add a Punch";
			message = getApplicationContext().getResources().getString(R.string.add_punch);
			break;
		case 1:
			//<item>Remove a Punch</item>
			//remove_punch
			title = "Remove a Punch";
			message = getApplicationContext().getResources().getString(R.string.remove_punch);
			break;
		case 2:
			//<item>Edit a Punch</item>
			//edit_punch
			title = "Edit a Punch";
			message = getApplicationContext().getResources().getString(R.string.edit_punch);
			break;
		case 3:
			//<item>Edit the Note</item>
			//edit_note
			title = "Edit the Note";
			message = getApplicationContext().getResources().getString(R.string.edit_note);
			break;
		case 4:
			//<item>Edit a different Day</item>
			//edit_day
			title = "Edit a different Day";
			message = getApplicationContext().getResources().getString(R.string.edit_day);
			break;
		case 5:
			//<item>Email your Punches</item>	
			//email_punches
			title = "Email your Punches";
			message = getApplicationContext().getResources().getString(R.string.email_punches);
			break;
		default:
			break;
		}
		
		Dialog dialog = new Dialog(this);

		dialog.setContentView(R.layout.help_dialog);
		dialog.setTitle(title);

		TextView text = (TextView) dialog.findViewById(R.id.text);
		text.setText(message);
		dialog.show();
	}
}
