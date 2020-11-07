package com.rb.rocketleaguerankednofitier.model;

import java.time.LocalDate;

public class MmrInformations {
	
	private Rank unranked;
	private Rank rankedFootcar1v1;
	private Rank rankedFootcar2v2;
	private Rank rankedFootcar3v3;
	private Rank rankedHoops;
	private Rank rankedDropshot;
	
	private LocalDate lastUpdate = null;
	
	private MmrInformations(Rank rankedFootcar1v1, Rank rankedFootcar2v2, Rank rankedFootcar3v3, Rank rankedHoops, Rank rankedDropshot, Rank unranked) {
		this.rankedFootcar1v1 = rankedFootcar1v1; 
		this.rankedFootcar2v2 = rankedFootcar2v2;
		this.rankedFootcar3v3 = rankedFootcar3v3;
		this.rankedHoops = rankedHoops; 
		this.rankedDropshot = rankedDropshot;
		this.unranked = unranked;
	}
	
	public static MmrInformations buildFrom(Rank rankedFootcar1v1, Rank rankedFootcar2v2, Rank rankedFootcar3v3, Rank rankedHoops, Rank rankedDropshot, Rank unranked) {
		
		return new MmrInformations(rankedFootcar1v1, rankedFootcar2v2, rankedFootcar3v3, rankedHoops, rankedDropshot, unranked);
	}
	
	public Rank diffWith(MmrInformations mmrInfosIn, GameMode mode) {
		if(mmrInfosIn == null)
			return null;
		
		if(mode == null || mode.equals(rankedFootcar1v1.getGameMode()))
			if(rankedFootcar1v1.getMmr()-mmrInfosIn.getRankedFootcar1v1().getMmr() != 0)
				return new Rank(rankedFootcar1v1.getMmr(), rankedFootcar1v1.getGameMode());
		
		if(mode == null || mode.equals(rankedFootcar2v2.getGameMode()))
			if(rankedFootcar2v2.getMmr()-mmrInfosIn.getRankedFootcar2v2().getMmr() != 0)
				return new Rank(rankedFootcar2v2.getMmr(), rankedFootcar2v2.getGameMode());
		
		if(mode == null || mode.equals(rankedFootcar3v3.getGameMode()))
			if(rankedFootcar3v3.getMmr()-mmrInfosIn.getRankedFootcar3v3().getMmr() != 0)
				return new Rank(rankedFootcar3v3.getMmr(), rankedFootcar3v3.getGameMode());
		
		if(mode == null || mode.equals(rankedHoops.getGameMode()))
			if(rankedHoops.getMmr()-mmrInfosIn.getRankedHoops().getMmr() != 0)
				return new Rank(rankedHoops.getMmr(), rankedHoops.getGameMode());
		
		if(mode == null || mode.equals(rankedDropshot.getGameMode()))
			if(rankedDropshot.getMmr()-mmrInfosIn.getRankedDropshot().getMmr() != 0)
				return new Rank(rankedDropshot.getMmr(), rankedDropshot.getGameMode());
		
		if(mode == null || mode.equals(unranked.getGameMode()))
			if(unranked.getMmr()-mmrInfosIn.getUnranked().getMmr() != 0)
				return new Rank(unranked.getMmr(), unranked.getGameMode());
		
		throw new RuntimeException("Case not implemented");
	}
	
	public Rank diffWith(MmrInformations mmrInfosIn) {
		return diffWith(mmrInfosIn, null);
	}

	public LocalDate getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(LocalDate lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public void updateAllRanks(Rank mmrRanked1v1Footcar, Rank mmrRanked2v2Footcar, Rank mmrRanked3v3Footcar, Rank mmrRankedHoops, Rank mmrRankedDropshot, Rank unranked) {
		this.setRankedFootcar1v1(mmrRanked1v1Footcar);
		this.setRankedFootcar2v2(mmrRanked2v2Footcar);
		this.setRankedFootcar3v3(mmrRanked3v3Footcar);
		this.setRankedHoops(mmrRankedHoops);
		this.setRankedDropshot(mmrRankedDropshot);
		this.setUnranked(unranked);
		
		this.setLastUpdate(LocalDate.now());
	}
	
	
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		
		if(!(obj instanceof MmrInformations))
			return false;
		
		MmrInformations mmrInfos = (MmrInformations) obj;
		
		return this.rankedDropshot.getMmr() == mmrInfos.getRankedDropshot().getMmr()
				&& this.rankedHoops.getMmr() == mmrInfos.getRankedHoops().getMmr()
				&& this.rankedFootcar1v1.getMmr() == mmrInfos.getRankedFootcar1v1().getMmr()
				&& this.rankedFootcar2v2.getMmr() == mmrInfos.getRankedFootcar2v2().getMmr()
				&& this.rankedFootcar3v3.getMmr() == mmrInfos.getRankedFootcar3v3().getMmr()
				&& this.unranked.getMmr() == mmrInfos.getUnranked().getMmr();
	}
	
	public Rank getRankedDropshot() {
		return rankedDropshot;
	}
	public Rank getRankedFootcar1v1() {
		return rankedFootcar1v1;
	}
	public Rank getRankedFootcar2v2() {
		return rankedFootcar2v2;
	}
	public Rank getRankedFootcar3v3() {
		return rankedFootcar3v3;
	}
	public Rank getRankedHoops() {
		return rankedHoops;
	}

	public Rank getUnranked() {
		return unranked;
	}

	public void setUnranked(Rank unranked) {
		this.unranked = unranked;
	}
	public void setRankedDropshot(Rank rankedDropshot) {
		this.rankedDropshot = rankedDropshot;
	}
	public void setRankedFootcar1v1(Rank rankedFootcar1v1) {
		this.rankedFootcar1v1 = rankedFootcar1v1;
	}
	public void setRankedFootcar2v2(Rank rankedFootcar2v2) {
		this.rankedFootcar2v2 = rankedFootcar2v2;
	}
	public void setRankedFootcar3v3(Rank rankedFootcar3v3) {
		this.rankedFootcar3v3 = rankedFootcar3v3;
	}
	public void setRankedHoops(Rank rankedHoops) {
		this.rankedHoops = rankedHoops;
	}

	public Rank getMmrByMode(GameMode gameMode) {
		if(gameMode.equals(unranked.getGameMode()))
			return unranked;
		if(gameMode.equals(rankedFootcar1v1.getGameMode()))
			return rankedFootcar1v1;
		if(gameMode.equals(rankedFootcar2v2.getGameMode()))
			return rankedFootcar2v2;
		if(gameMode.equals(rankedFootcar3v3.getGameMode()))
			return rankedFootcar3v3;
		if(gameMode.equals(rankedDropshot.getGameMode()))
			return rankedDropshot;
		if(gameMode.equals(rankedHoops.getGameMode()))
			return rankedHoops;
		
		throw new RuntimeException("Case not implemented");
	}
}
