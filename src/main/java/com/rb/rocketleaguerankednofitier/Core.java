package com.rb.rocketleaguerankednofitier;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.rb.rocketleaguerankednofitier.model.DayStats;
import com.rb.rocketleaguerankednofitier.model.TimeManager;
import com.rb.rocketleaguerankednofitier.model.WeekStat;

@Component
public class Core {
	
	private static final int HOUR_STATS_RESET = 5;
	private static final int LIMIT_SUCCESSIVE_EXCEPTION = 5;
		
	@Value("${debugMode}")
	private Boolean debugMode;
	
	@Autowired
	RlTrackerProvider trackerProvider;
	
	@Autowired
	MailNotifier mailNotifier;
	
	@Autowired
	WeekStat weekStats;
	
	@Autowired
	private TimeManager timeManager;
	
	@Autowired
	private DayStats dayStats;
	
	private LocalDate lastDateReset; 
	
	private int countSuccessiveException;
	
	public Core() {
		this.countSuccessiveException = 0;
	}
	
	public void mainProcess() throws InterruptedException {
		printDebug("Beginning of application");
		
		while(true) {
			
			try {
				if(needToReset())
					resetProcessingInfos();
				
				if(weekStats.needToReset())
					weekStats.resetStats();
				
				boolean hasMmrChanged = trackerProvider.reloadInformations();
				if(hasMmrChanged) {
					printDebug("Game just finished ! MMR changed");
					weekStats.recordNewMmrGameIfWon(dayStats.getLastStatsDiff());
					timeManager.processIsInGamePeriod();
					mailNotifier.notifyUpdateMmr();
				}
				this.countSuccessiveException = 0;
			}catch(RuntimeException e) {
				manageException(e);
			}
					
			if(this.countSuccessiveException > LIMIT_SUCCESSIVE_EXCEPTION) {
				mailNotifier.notifyError("Due to excessive count of exception, the program stopped.");
				System.exit(-33);
			}
				
			
			
			printDebug("Wait...");
			Thread.sleep(1000 * timeManager.getTimeSecondsToSleep());
			printDebug("Looping");
		}
	}
	
	private void manageException(RuntimeException e) {
		if(!(e.getCause() instanceof org.jsoup.HttpStatusException || e.getCause() instanceof java.net.SocketTimeoutException))
			throw e;
		this.countSuccessiveException++;
		e.printStackTrace();
//		mailNotifier.notifyError(String.format("An exception %s occured, but the successive exception limit is not reached (successive count = %d | limit = %d)", e.getCause().getClass().getName(), this.countSuccessiveException, LIMIT_SUCCESSIVE_EXCEPTION));
		timeManager.processException();
	}

	/*
	 * Privates tools methods
	 */
	
	private void printDebug(String text) {
		if(debugMode != null && debugMode)
			System.out.println(String.format("[%s] - %s", LocalTime.now().toString(), text));
	}
	
	
	private boolean needToReset() {
		LocalTime now = LocalTime.now();
		LocalDate today = LocalDate.now();
		
		return this.lastDateReset == null ||
				(now.getHour() == HOUR_STATS_RESET && !today.equals(this.lastDateReset));
	}

	private void resetProcessingInfos() {
		printDebug("Processing Reset");
		trackerProvider.reloadInformations();
		dayStats.setStartingStats(trackerProvider.getCurrentMmrInfos());
		setLastDateReset(LocalDate.now());
	}

	private void setLastDateReset(LocalDate lastDateReset) {
		this.lastDateReset = lastDateReset;
	}
}
