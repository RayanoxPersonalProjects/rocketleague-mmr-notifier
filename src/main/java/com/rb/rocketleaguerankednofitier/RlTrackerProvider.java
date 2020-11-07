package com.rb.rocketleaguerankednofitier;

import java.io.IOException;
import java.time.LocalDate;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rb.rocketleaguerankednofitier.model.GameMode;
import com.rb.rocketleaguerankednofitier.model.MmrInformations;
import com.rb.rocketleaguerankednofitier.model.Rank;

@Component
public class RlTrackerProvider {
	
	private final String urlRayanoxStats = "https://api.tracker.gg/api/v2/rocket-league/standard/profile/psn/Rayanoxx";
	
	private MmrInformations lastRequestedMmrInfos;
	private MmrInformations currentMmrInfos;
	
	private String lastTnrScore;
	/**
	 * 
	 * @return true if the MMR informations has changed
	 */
	public boolean reloadInformations() {
		String jsonDataResponse = getRest(urlRayanoxStats);
		return processPropertiesUpdates(jsonDataResponse);
	}
	
	private String getRest(String url) {
		try {
			return Jsoup.connect(url)
			        .timeout(1000*30) 
			        .ignoreContentType(true)
			        .userAgent("Mozilla/5.0 (Windows NT 6.1; rv:40.0) Gecko/20100101 Firefox/40.0")
			        .get()
			        .body()
			        .text();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private boolean processPropertiesUpdates(String jsonString) {
	    ObjectMapper mapper = new ObjectMapper();
	    try {
			JsonNode rootNode = mapper.readTree(jsonString);
			
			JsonNode statsNode = rootNode.get("data").get("segments");
			
			this.setLastTnrScore(statsNode.get(0).get("stats").get("score").get("value").asText());
			
			Rank ranked1v1Footcar = Rank.buildFromNode(statsNode, GameMode.RANKED_FOOTCAR_1V1);
			Rank ranked2v2Footcar = Rank.buildFromNode(statsNode, GameMode.RANKED_FOOTCAR_2V2);
			Rank ranked3v3Footcar = Rank.buildFromNode(statsNode, GameMode.RANKED_FOOTCAR_3V3);
			Rank rankedDropshot = Rank.buildFromNode(statsNode, GameMode.RANKED_DROPSHOT);
			Rank rankedHoops = Rank.buildFromNode(statsNode, GameMode.RANKED_HOOPS);
			Rank rankedUnranked = Rank.buildFromNode(statsNode, GameMode.UNRANKED);

			updateLastRequestedMmrs(ranked1v1Footcar, ranked2v2Footcar, ranked3v3Footcar, rankedHoops, rankedDropshot, rankedUnranked);
			boolean hasBeenUpdated = updateCurrentMmrs(ranked1v1Footcar, ranked2v2Footcar, ranked3v3Footcar, rankedHoops, rankedDropshot, rankedUnranked);
			return hasBeenUpdated;
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @param mmrRanked1v1Footcar
	 * @param mmrRanked2v2Footcar
	 * @param mmrRanked3v3Footcar
	 * @param mmrRankedHoops
	 * @param mmrRankedDropshot
	 * @param unranked
	 * 
	 * @return true if has been updated
	 */
	private boolean updateCurrentMmrs(Rank mmrRanked1v1Footcar, Rank mmrRanked2v2Footcar, Rank mmrRanked3v3Footcar, Rank mmrRankedHoops, Rank mmrRankedDropshot, Rank unranked) {
		if(this.currentMmrInfos == null)
			this.currentMmrInfos = MmrInformations.buildFrom(mmrRanked1v1Footcar, mmrRanked2v2Footcar, mmrRanked3v3Footcar, mmrRankedHoops, mmrRankedDropshot, unranked);
		
		if(!this.currentMmrInfos.equals(lastRequestedMmrInfos)) {
			this.currentMmrInfos.updateAllRanks(mmrRanked1v1Footcar, mmrRanked2v2Footcar, mmrRanked3v3Footcar, mmrRankedHoops, mmrRankedDropshot, unranked);
			return true;
		}
		return false;
	}

	private void updateLastRequestedMmrs(Rank mmrRanked1v1Footcar, Rank mmrRanked2v2Footcar, Rank mmrRanked3v3Footcar, Rank mmrRankedHoops, Rank mmrRankedDropshot, Rank unranked) {
		if(lastRequestedMmrInfos == null)
			this.lastRequestedMmrInfos = MmrInformations.buildFrom(mmrRanked1v1Footcar, mmrRanked2v2Footcar, mmrRanked3v3Footcar, mmrRankedHoops, mmrRankedDropshot, unranked);
		else
			this.lastRequestedMmrInfos.updateAllRanks(mmrRanked1v1Footcar, mmrRanked2v2Footcar, mmrRanked3v3Footcar, mmrRankedHoops, mmrRankedDropshot, unranked);
	}
	
	public MmrInformations getCurrentMmrInfos() {
		return currentMmrInfos;
	}

	public String getLastTnrScore() {
		return lastTnrScore;
	}

	public void setLastTnrScore(String lastTnrScore) {
		this.lastTnrScore = lastTnrScore;
	}
}
