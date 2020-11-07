package com.rb.rocketleaguerankednofitier;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.rb.rocketleaguerankednofitier.model.DayStats;
import com.rb.rocketleaguerankednofitier.model.Rank;

@Service
public class MailNotifier{

	@Autowired
    private JavaMailSender emailSender;
	
	@Value("${notification.recipients.list}")
	private String [] destinationRecipientsList;
 
	
	public void notifyUpdateMmr(DayStats stats) {
		Rank diffMmr = stats.getLastStatsDiff();
		Rank diffOfDay = stats.getStatsDiffOfDay(diffMmr.getGameMode());
		Rank currentMMR = stats.getCurrentStats().getMmrByMode(diffMmr.getGameMode());
		Rank startingMmr = stats.getStartingStats().getMmrByMode(diffMmr.getGameMode());
		
		String subject = String.format("%s [MMR- %s] - %s", getProgramSubjectHeader(), diffMmr.getGameMode().getModeNameString(), diffMmr.getMmrStringSigned());

		StringBuilder builder = new StringBuilder("\n");
		builder.append("\t\t*** New MMRs stats ***").append("\n\n")
			   .append("- Current MMR    = ").append(currentMMR.getMmr()).append("\n\n")
			   .append("- Diff with last = ").append(diffMmr.getMmrStringSigned()).append("\n\n")
			   .append("- Diff of today  = ").append(diffOfDay.getMmrStringSigned()).append("\n\n")
			   .append("- Starting MMR   = ").append(startingMmr.getMmr()).append("\n\n");
		
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
		return "[RL-Nofier] | ";
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
