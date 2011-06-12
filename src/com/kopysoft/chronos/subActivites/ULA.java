package com.kopysoft.chronos.subActivites;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.kopysoft.chronos.R;

public class ULA extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ula);
		TextView ulaText = (TextView)findViewById(R.id.ulaText);
		
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
}
