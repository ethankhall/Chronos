package com.kopysoft.chronos.service;

interface IAndroidService {
	void setClockAction(in boolean i_type, in long i_time);
	void setNotification(in boolean notification);
	void runUpdate();
	void setTextNotification(in String title, in String message);
}