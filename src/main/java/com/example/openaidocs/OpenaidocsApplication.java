package com.example.openaidocs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.command.annotation.CommandScan;

@CommandScan
@SpringBootApplication
public class OpenaidocsApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenaidocsApplication.class, args);
	}

}
