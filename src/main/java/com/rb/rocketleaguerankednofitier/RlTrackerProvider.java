package com.rb.rocketleaguerankednofitier;

import java.io.IOException;
import java.time.LocalDate;

import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rb.rocketleaguerankednofitier.model.DayStats;
import com.rb.rocketleaguerankednofitier.model.GameMode;
import com.rb.rocketleaguerankednofitier.model.MmrInformations;
import com.rb.rocketleaguerankednofitier.model.MmrInformationsBuilder;
import com.rb.rocketleaguerankednofitier.model.Rank;

@Component
public class RlTrackerProvider {
	
	private final String urlRayanoxStats = "https://api.tracker.gg/api/v2/rocket-league/standard/profile/psn/Rayanoxx";
	
	@Autowired
	private DayStats dayStats;
	
	private MmrInformations lastRequestedMmrInfos;
	private MmrInformations currentMmrInfosAlreadySaved;

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
			
			Rank ranked1v1Footcar = Rank.buildFromNode(statsNode, GameMode.RANKED_FOOTCAR_1V1);
			Rank ranked2v2Footcar = Rank.buildFromNode(statsNode, GameMode.RANKED_FOOTCAR_2V2);
			Rank ranked3v3Footcar = Rank.buildFromNode(statsNode, GameMode.RANKED_FOOTCAR_3V3);
			Rank rankedDropshot = Rank.buildFromNode(statsNode, GameMode.RANKED_DROPSHOT);
			Rank rankedHoops = Rank.buildFromNode(statsNode, GameMode.RANKED_HOOPS); 
			Rank rankedUnranked = Rank.buildFromNode(statsNode, GameMode.UNRANKED);

			this.lastRequestedMmrInfos = MmrInformationsBuilder.newBuilder()
					.addRank(ranked1v1Footcar)
					.addRank(ranked2v2Footcar)
					.addRank(ranked3v3Footcar)
					.addRank(rankedDropshot)
					.addRank(rankedHoops)
					.addRank(rankedUnranked)
					.build();
			
			boolean hasBeenUpdated = updateCurrentMmrs(this.lastRequestedMmrInfos);
			return hasBeenUpdated;
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @return true if has been updated
	 */
	private boolean updateCurrentMmrs(MmrInformations mmrInfos) {
		boolean hasDatasChanged = false;

		if(this.currentMmrInfosAlreadySaved == null) {
			this.currentMmrInfosAlreadySaved = MmrInformationsBuilder.buildFrom(mmrInfos);
			dayStats.updateDayStats(currentMmrInfosAlreadySaved); //On le fait pointer sur cette variable puisque l'adresse n'est pas réallouée après
		}
		
		if(!this.currentMmrInfosAlreadySaved.equals(lastRequestedMmrInfos)) {
			this.currentMmrInfosAlreadySaved = MmrInformationsBuilder.buildFrom(mmrInfos);
			dayStats.updateDayStats(currentMmrInfosAlreadySaved); //On le fait pointer sur cette variable puisque l'adresse n'est pas réallouée après
			hasDatasChanged = true;
		}
		
		return hasDatasChanged;
	}

	public MmrInformations getCurrentMmrInfos() {
		return currentMmrInfosAlreadySaved;
	}
}
