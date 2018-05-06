package com.vava.app;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JoinMeApplication {
	
    private static Logger logger = LogManager.getLogger(JoinMeApplication.class);

	
	public static void main(String[] args) throws Exception {
		logger.info("Application start");
		SpringApplication.run(JoinMeApplication.class, args);
	}

}
