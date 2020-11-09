package com.rb.rocketleaguerankednofitier.model;

import java.util.HashMap;
import java.util.Set;

public class MmrInformations {
	
	private HashMap<GameMode, Rank> rankByMode;
	
	public MmrInformations() {
		rankByMode = new HashMap<>();
	}
	
	public Rank diffWith(MmrInformations mmrInfosIn, GameMode mode) {
		if(mmrInfosIn == null)
			return null;
		
		return this.rankByMode.get(mode).minus(mmrInfosIn.getRankByMode(mode));
	}
	
	public Rank firstDiffWith(MmrInformations mmrInfosIn) {
		for (GameMode mode : mmrInfosIn.getAllModes()) {
			Rank diffRank = diffWith(mmrInfosIn, mode);
			if(diffRank.getMmr() != 0)
				return new Rank(diffRank.getMmr(), diffRank.getGameMode());
		}
		throw new RuntimeException("Must  be at least one diff but no difference was found.");
	}
	
	public Set<GameMode> getAllModes() {
		return rankByMode.keySet();
	}
	
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		
		if(!(obj instanceof MmrInformations))
			return false;
		
		MmrInformations mmrInfos = (MmrInformations) obj;
		
		Set<GameMode> allModes = mmrInfos.getAllModes();
		if(allModes.size() != this.getAllModes().size())
			throw new RuntimeException("Count of modes must be equal. Problem somewhere in the code");
		
		
		for (GameMode mode : allModes) {
			Rank thisRank = this.getRankByMode(mode);
			Rank comparedRank = mmrInfos.getRankByMode(mode);
			if(thisRank.getMmr() != comparedRank.getMmr())
				return false;
		}
		return true;
	}
	
	public void addRankPointsToAllModes(int pointsMmrToAdd) {
		this.getAllModes()
			.stream()
			.forEach(mode -> getRankByMode(mode).addMmr(pointsMmrToAdd));
	}
	

	public void addRankInInformations(Rank rank) {
		this.rankByMode.put(rank.getGameMode(), rank);
	}
	
	public Rank getRankByMode(GameMode mode) {
		Rank result = rankByMode.get(mode);
		if(result == null)
			throw new RuntimeException("Trying to access a rank which mode is not present in  the MAP (Mode = )" + mode.getModeNameString());
		return result;
	}
}
