package com.kopysoft.chronos.singelton;

public class ViewingPayPeriod {
	
	private static ViewingPayPeriod instance = null;
	
	int[] weekStart = {0,0,0};
	
	protected ViewingPayPeriod(){
		
	}
	
	public static ViewingPayPeriod getInstance(){
		if ( instance == null ) {
			instance = new ViewingPayPeriod();
		}
		return instance;
	}
	
	public void setWeek(int[] date){
		weekStart = date;
	}
	
	public int[] getWeek(){
		return weekStart;
	}
}
