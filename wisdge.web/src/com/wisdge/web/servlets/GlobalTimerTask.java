package com.wisdge.web.servlets;

import java.util.TimerTask;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GlobalTimerTask extends TimerTask {
	private static Log logger = LogFactory.getLog(GlobalTimerTask.class);
	private int visitTimes;
	private int delay, period;

	public GlobalTimerTask() {
		this.visitTimes = 0;
		this.delay = 300000;	// set default delay as 10 minutes.
		this.period = 300000;
	}

	public int getVisitTimes() {
		return visitTimes;
	}

	public void setVisitTimes(int visitTimes) {
		this.visitTimes = visitTimes;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	@Override
	public void run() {
		onlineTask();
		doTask();
	}
	
	private void onlineTask() {
		int onlinePerson = GlobalSessionListener.getOnlinePerson().size();
		int visitors = GlobalSessionListener.getVisitors();
		logger.debug("TimerTask: [OnlinePerson = " + onlinePerson + "], [Visitors = " + visitors + "]");
		GlobalSessionListener.setVisitors(0);
	}

	protected void doTask() {
		
	}
}
