package com.rb.rocketleaguerankednofitier.model;

import com.fasterxml.jackson.databind.JsonNode;

public class Rank {

	private GameMode 	gameMode;
	private int 		mmr;
	
	public Rank() {
	}
	
	public Rank(int mmr, GameMode mode) {
		this.mmr = mmr;
		this.gameMode = mode;
	}
	
	public GameMode getGameMode() {
		return gameMode;
	}
	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
	}
	public int getMmr() {
		return mmr;
	}
	public String getMmrStringSigned() {
		return mmr < 0 ? String.format("-%d", mmr)
				: String.format("+%d", mmr);
	}
	public void setMmr(int mmr) {
		this.mmr = mmr;
	} 
	
	public static Rank buildFromNode(JsonNode statsNode, GameMode gameMode) {
		Rank rank = new Rank();
		rank.setGameMode(gameMode);
		rank.setMmr(statsNode.get(gameMode.getOrderJsonSegment()).get("stats").get("rating").get("value").asInt());
		return rank;
	}
}
