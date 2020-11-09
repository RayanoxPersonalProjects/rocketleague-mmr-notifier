package com.rb.rocketleaguerankednofitier;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.rb.rocketleaguerankednofitier.model.DayStats;
import com.rb.rocketleaguerankednofitier.model.GameMode;
import com.rb.rocketleaguerankednofitier.model.Rank;
import com.rb.rocketleaguerankednofitier.model.WeekStat;

@Service
public class MailNotifier{

	@Autowired
	private WeekStat weekStats;
	
	@Autowired
    private JavaMailSender emailSender;
	
	@Autowired
	private DayStats dayStats;
	
	@Value("${notification.recipients.list}")
	private String [] destinationRecipientsList;
	
	public void notifyUpdateMmr() {
		Rank diffMmr = dayStats.getLastStatsDiff();
		GameMode gameMode = diffMmr.getGameMode();
		Rank diffOfDay = dayStats.getStatsDiffOfDay(gameMode);
		Rank diffOfWeek = weekStats.getStatsDiffOfWeek(gameMode);
		
		Rank currentMMR = dayStats.getCurrentStats().getRankByMode(gameMode);
		Rank startingMmrOfDay = dayStats.getStartingStats().getRankByMode(gameMode);
		Rank startingMmrOfWeek = weekStats.getStartingRank(gameMode);
		int mmrGoalOfWeek = weekStats.getRankGoalOfWeek(gameMode).getMmr();
		int gamesCountNeededEstimation = weekStats.getGameCountGoalEstimation(gameMode);
		
		int pointsStillNeededOfWeek = mmrGoalOfWeek-currentMMR.getMmr();
		
		String subject = String.format("%s [MMR- %s]: %s", getProgramSubjectHeader(), gameMode.getModeNameString(), diffMmr.getMmrStringSigned());

		StringBuilder builder = new StringBuilder("\n");
		builder.append("\t\t*** New MMRs stats ***").append("\n\n")
		
			   .append("- MMR:")
			   .append("   -> Current       = ").append(currentMMR.getMmr()).append(String.format(" (%s)", diffMmr.getMmrStringSigned())).append("\n")
			   .append("   -> Start of Day  =").append(startingMmrOfDay.getMmr()).append("\n")
			   .append("   -> Start of Week =").append(startingMmrOfWeek.getMmr()).append("\n\\n")
			   
			   .append("- Diff of:").append("\n")
			   .append("      -> Today = ").append(diffOfDay.getMmrStringSigned()).append("\n")
			   .append("      -> Week  = ").append(diffOfWeek.getMmrStringSigned()).append("\n\n")
			   
			   .append("=> Goal to reach this week = ").append(mmrGoalOfWeek).append("\n\n");
		
		if(weekStats.isWeekGoalReached(gameMode))
			builder.append("          *** CONGRATULATION ***").append("\n")
				   .append("           *** Goal Reached ***").append("\n\n");
		else	   
			builder.append("- Points needed until goal                = ").append(pointsStillNeededOfWeek).append("\n")
				   .append("- Games count needed of week (estimation) = ").append(gamesCountNeededEstimation).append("\n");
		
		sendSimpleMessage(subject, builder.toString());
	}

	public void notifyException(Exception e) {
		String subject = String.format("%s [ERROR] - Exception occured in program", getProgramSubjectHeader());
		
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String exceptionAsString = sw.toString();
		
		sendSimpleMessage(subject, exceptionAsString);
	}
	
	public void notifyError(String text) {
		String subject = String.format("%s [ERROR] - Error message from program", getProgramSubjectHeader());
		sendSimpleMessage(subject, text);
	}
	
	private String getProgramSubjectHeader() {
		return "[RL-Notifier] | ";
	}
	
	private void sendSimpleMessage(String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage(); 
		message.setFrom("noreply@baeldung.com");
		message.setSubject(subject); 
		message.setText(text);
		
		for (String destTo : destinationRecipientsList) {
			message.setTo(destTo); 
			emailSender.send(message);
		}
	}
}
