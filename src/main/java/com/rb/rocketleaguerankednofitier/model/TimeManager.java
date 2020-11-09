package com.rb.rocketleaguerankednofitier.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TimeManager {
	
	private boolean isInGamePeriod;
	private boolean isFirstSleepInGameActivated;
	private boolean hasBeenExceptionThrown;
	private Instant lastUpdateInstant;
	
	@Value("${game.checker.core.minutes-timeout-period-no-activity}")
	private Integer timeoutInGameMinutes;
	
	@Value("${game.checker.core.seconds-time-to-sleep-no-activity}")
	private Integer timeToSleepNoActivity;
	
	@Value("${game.checker.core.seconds-time-to-sleep-in-game-period}")
	private Integer timeToSleepInGamePeriod;
	
	@Value("${game.checker.core.seconds-time-to-sleep-when-exception}")
	private Integer timeToSleepWhenException;
	
	
	public TimeManager() {
		resetInGamePeriod();
	}
	
	public void processIsInGamePeriod() {
		setInGamePeriod(true);
		setFirstSleepInGameActivated(true);
	}
	
	public int getTimeSecondsToSleep() {
		if(isTimeoutReached())
			resetInGamePeriod();
		
		int timeToSleep = isInGamePeriod() ?
				this.timeToSleepInGamePeriod
				: this.timeToSleepNoActivity;

		if(hasBeenExceptionThrown()) {
			timeToSleep = this.timeToSleepWhenException;
			setHasBeenExceptionThrown(false);
		}else if(isFirstSleepInGameActivated()) {
			timeToSleep = this.timeToSleepNoActivity;
			setFirstSleepInGameActivated(false);
		}
		
		return timeToSleep;
	}
	
	private void resetInGamePeriod() {
		setInGamePeriod(false);
		setLastUpdateInstant(null);
		setFirstSleepInGameActivated(false);
		setHasBeenExceptionThrown(false);
	}
	
	/**
	 * Check if the timeout period is not reached without being updated
	 * @return true if the timeout is not reached, false otherwise
	 */
	boolean isTimeoutReached() {
		return lastUpdateInstant != null && lastUpdateInstant.until(Instant.now(), ChronoUnit.MINUTES) < timeoutInGameMinutes;
	}
	
	boolean isInGamePeriod() {
		return isInGamePeriod;
	}

	private void setInGamePeriod(boolean isInGamePeriod) {
		this.isInGamePeriod = isInGamePeriod;
	}

	private void setLastUpdateInstant(Instant lastUpdateInstant) {
		this.lastUpdateInstant = lastUpdateInstant;
	}
	
	public void setFirstSleepInGameActivated(boolean isFirstSleepInGameActivated) {
		this.isFirstSleepInGameActivated = isFirstSleepInGameActivated;
	}
	
	public boolean isFirstSleepInGameActivated() {
		return isFirstSleepInGameActivated;
	}

	public void processException() {
		setHasBeenExceptionThrown(true);
	}

	private boolean hasBeenExceptionThrown() {
		return hasBeenExceptionThrown;
	}

	private void setHasBeenExceptionThrown(boolean hasBeenExceptionThrown) {
		this.hasBeenExceptionThrown = hasBeenExceptionThrown;
	}
}
