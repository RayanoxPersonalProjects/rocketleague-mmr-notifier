package com.rb.rocketleaguerankednofitier.model;

public class MmrInformationsBuilder {

	private MmrInformations mmrInfos;
	
	private MmrInformationsBuilder() {
		this.mmrInfos = new MmrInformations();
	}
	
	public static MmrInformationsBuilder newBuilder() {
		return new MmrInformationsBuilder();
	}
	
	public MmrInformationsBuilder addRank(Rank rank) {
		this.mmrInfos.addRankInInformations(rank);
		return this;
	}
	
	public MmrInformations build() {
		return this.mmrInfos;
	}

	public static MmrInformations buildFrom(MmrInformations mmrInfos) {
		MmrInformationsBuilder builder = MmrInformationsBuilder.newBuilder();
		mmrInfos.getAllModes()
		.stream()
		.forEach(mode -> {
			Rank rankTarget = mmrInfos.getRankByMode(mode);
			builder.addRank(new Rank(rankTarget.getMmr(), rankTarget.getGameMode()));
		});
		return builder.build();
	}
}
