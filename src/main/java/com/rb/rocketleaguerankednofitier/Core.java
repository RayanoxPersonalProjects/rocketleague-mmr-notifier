package com.rb.rocketleaguerankednofitier;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.rb.rocketleaguerankednofitier.model.DayStats;

@Component
public class Core {

	private static final int TIME_SLEEP_NO_ACTIVITY_CHECKING_SECONDS = 60 + 30;
	private static final int TIME_SLEEP_IN_GAME_CHECKING_SECONDS = 10;
	
	private static final int TIMEOUT_IN_GAME_MINUTES = 11;
	private static final int HOUR_STATS_RESET = 5;
		
	@Value("${debugMode}")
	private Boolean debugMode;
	
	@Autowired
	RlTrackerProvider trackerProvider;
	
	@Autowired
	MailNotifier mailNotifier;
	
	private String lastDateServerUpdateSaved;
	
	private DayStats dayStats;
	
	private LocalDate lastDateReset; 
	
	public Core() {
		this.dayStats = new DayStats();
	}
	
	public void mainProcess() throws InterruptedException {
		printDebug("Beginning of application");
		
		while(true) {
			if(needToReset())
				resetProcessingInfos();
			else
				trackerProvider.reloadInformations();
			
			if(!lastDateServerUpdateSaved.equals(trackerProvider.getLastTnrScore()))
				processInGameCheckingMode();
						
			printDebug("Wait (NO ACTIVITY)");
			Thread.sleep(1000 * TIME_SLEEP_NO_ACTIVITY_CHECKING_SECONDS);
			printDebug("Loop (NO ACTIVITY)");
		}
	}

	private void processInGameCheckingMode() throws InterruptedException{
		Instant starting = Instant.now();
		
		printDebug("Activity Detected ! -> Processing In Game Mode");
		
		while(starting.until(Instant.now(), ChronoUnit.MINUTES) < TIMEOUT_IN_GAME_MINUTES) {
			boolean hasMmrChanged = trackerProvider.reloadInformations();
			
			if(hasMmrChanged) {
				printDebug("Game just finished ! MMR changed");
				dayStats.setCurrentStats(trackerProvider.getCurrentMmrInfos());
				mailNotifier.notifyUpdateMmr(dayStats);
				return;
			}
			
			printDebug("Wait (IN GAME MODE)");
			Thread.sleep(1000 * TIME_SLEEP_IN_GAME_CHECKING_SECONDS);
			printDebug("Loop (IN GAME MODE)");
		}
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
		lastDateServerUpdateSaved = trackerProvider.getLastTnrScore();
		dayStats.setStartingStats(trackerProvider.getCurrentMmrInfos());
		setLastDateReset(LocalDate.now());
	}

	private void setLastDateReset(LocalDate lastDateReset) {
		this.lastDateReset = lastDateReset;
	}
}
