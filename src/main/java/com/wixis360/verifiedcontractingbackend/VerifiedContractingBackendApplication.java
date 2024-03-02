package com.wixis360.verifiedcontractingbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class VerifiedContractingBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(VerifiedContractingBackendApplication.class, args);
	}

}
