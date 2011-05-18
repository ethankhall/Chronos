package com.kopysoft.chronos.subActivites;

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
