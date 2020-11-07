package com.rb.rocketleaguerankednofitier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.rb"})
@SpringBootApplication
public class RocketLeagueRankedNofitierApplication implements CommandLineRunner {

	@Autowired
	Core core;
	
	@Autowired
	MailNotifier mailNotifier;
	
	public static void main(String[] args) {
		SpringApplication.run(RocketLeagueRankedNofitierApplication.class, args);
	}

	public void run(String... args) throws Exception {
		try {
			core.mainProcess();
		}catch(Exception e) {
			e.printStackTrace();
			mailNotifier.notifyException(e);
			mailNotifier.notifyError("The program has stopped because of exception thrown (see the last email)");
		}
	}
}
