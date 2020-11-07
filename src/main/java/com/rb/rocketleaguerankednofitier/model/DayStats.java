package com.rb.rocketleaguerankednofitier.model;

public class DayStats {

	private MmrInformations startingStats;
	private MmrInformations lastStats;
	private MmrInformations currentStats;
	
	public Rank getLastStatsDiff() {
		if(this.currentStats == null || this.lastStats == null)
			return null;
		
		return lastStats.diffWith(currentStats);
	}
	public Rank getStatsDiffOfDay(GameMode mode) {
		if(this.currentStats == null || this.startingStats == null)
			return null;
		
		return startingStats.diffWith(currentStats, mode);
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

	public MmrInformations getCurrentStats() {
		return currentStats;
	}

	public void setCurrentStats(MmrInformations currentStats) {
		this.setLastStats(this.currentStats);
		this.currentStats = currentStats;
	}
	
}
