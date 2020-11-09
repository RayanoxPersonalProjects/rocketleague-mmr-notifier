package com.rb.rocketleaguerankednofitier.model;

import org.springframework.stereotype.Component;

@Component
public class DayStats {

	private MmrInformations startingStats;
	private MmrInformations lastStats;
	private MmrInformations currentStats;
	
	public Rank getLastStatsDiff() {
		if(this.currentStats == null || this.lastStats == null)
			return null;
		
		return currentStats.firstDiffWith(lastStats);
	}
	public Rank getStatsDiffOfDay(GameMode mode) {
		if(this.currentStats == null || this.startingStats == null)
			return null;
		
		return currentStats.diffWith(startingStats, mode);
	}
	
	
	public MmrInformations getStartingStats() {
		return startingStats;
	}

	public void setStartingStats(MmrInformations startingStats) {
		this.startingStats = startingStats;
	}

	private void setLastStats(MmrInformations lastStats) {
		this.lastStats = lastStats;
	}

	public Rank getCurrentRank(GameMode mode) {
		return this.getCurrentStats().getRankByMode(mode);
	}
	
	public MmrInformations getCurrentStats() {
		return currentStats;
	}

	public void updateDayStats(MmrInformations currentStats) {
		this.setLastStats(this.currentStats != null ? this.currentStats : currentStats);
		this.currentStats = currentStats;
	}
	
}
