package com.rb.rocketleaguerankednofitier.model;

public enum GameMode {
	
	UNRANKED (1, "Unranked"),
	
	RANKED_FOOTCAR_1V1 (2, "Foorcar 1v1"),
	RANKED_FOOTCAR_2V2 (3, "Foorcar 2v2"),
	RANKED_FOOTCAR_3V3 (4, "Foorcar 3v3"),
	RANKED_DROPSHOT (7, "DropShot"),
	RANKED_HOOPS (5, "Hoops");
	
	private int orderJsonSegment;
	private String modeNameString;
	
	private GameMode(int orderJsonSegment, String modeNameString) {
		this.orderJsonSegment = orderJsonSegment;
		this.modeNameString = modeNameString;
	}
	
	public int getOrderJsonSegment() {
		return orderJsonSegment;
	}
	
	public String getModeNameString() {
		return modeNameString;
	}
}
