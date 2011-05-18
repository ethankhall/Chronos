package com.kopysoft.chronos.types;

public class HoldNote {

	long _time = 0;
	String _text = "";
	
	public HoldNote(long time, String text){
		_time = time;
		_text = text;
	}
	
	public long getTime(){
		return _time;
	}
	
	public String getText(){
		return _text;
	}
}
