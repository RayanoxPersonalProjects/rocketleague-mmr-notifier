package com.rb.rocketleaguerankednofitier.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WeekStat {

	private static final double DEFAULT_WIN_MMR_VALUE = 10;

	@Autowired
	private DayStats dayStats;
	
	@Value("${week.objectif.mmr-won}")
	private Integer mmrWonGoalOfWeek;
	
	private MmrInformations mmrGoalOfWeek;
	private MmrInformations startingMmr;
	
	private HashMap<GameMode, AverageBuilder> averagePointsWonBuilderByMode;
	
	public LocalDate lastWeekResetDate;
	
	public void resetStats() {
		MmrInformations currentStats = dayStats.getCurrentStats();
		
		mmrGoalOfWeek = MmrInformationsBuilder.buildFrom(currentStats);
		mmrGoalOfWeek.addRankPointsToAllModes(mmrWonGoalOfWeek);
		
		startingMmr = MmrInformationsBuilder.buildFrom(currentStats);
		
		averagePointsWonBuilderByMode = new HashMap<>();
		
		lastWeekResetDate = LocalDate.now();
	}
	
	public boolean needToReset() {
		LocalDate today = LocalDate.now();
		return lastWeekResetDate == null || 
				(today.getDayOfWeek().equals(DayOfWeek.MONDAY) && today.getDayOfMonth() != lastWeekResetDate.getDayOfMonth());
	}
	
	public void recordNewMmrGameIfWon(Rank newMmr) {
		if(newMmr.getMmr() <= 0)
			return;
		
		GameMode mode = newMmr.getGameMode();
		if(!averagePointsWonBuilderByMode.containsKey(mode))
			averagePointsWonBuilderByMode.put(mode, AverageBuilder.newBuilder());
		
		AverageBuilder avgbuilder = averagePointsWonBuilderByMode.get(mode);
		avgbuilder.addValue(newMmr.getMmr());
	}
	
	public Rank getMmrNeededToReachGoal(GameMode mode) {
		Rank goal = mmrGoalOfWeek.getRankByMode(mode);
		Rank currentRank = dayStats.getCurrentStats().getRankByMode(mode); 
		return goal.minus(currentRank);
	}

	public boolean isWeekGoalReached(GameMode mode) {
		return getMmrNeededToReachGoal(mode).getMmr() <= 0;
	}
	
	public Rank getStartingRank(GameMode mode) {
		return startingMmr.getRankByMode(mode);
	}
	
	public Rank getStatsDiffOfWeek(GameMode mode) {
		MmrInformations currentStats = dayStats.getCurrentStats();
		return currentStats.diffWith(startingMmr, mode);
	}
	
	public Rank getRankGoalOfWeek(GameMode mode) {
		return mmrGoalOfWeek.getRankByMode(mode);
	}

	public int getGameCountGoalEstimation(GameMode gameMode) {
		AverageBuilder avgBuilder = this.averagePointsWonBuilderByMode.get(gameMode);
		double winPointsEstimated = avgBuilder != null 
				? avgBuilder.buildAverage()
				: DEFAULT_WIN_MMR_VALUE;
		int winPointsEstimatedRounded = (int) Math.round(winPointsEstimated);
		
		int mmrNeeded = getMmrNeededToReachGoal(gameMode).getMmr();
		return (int) Math.ceil((double) mmrNeeded/winPointsEstimatedRounded);
	}
	
}
