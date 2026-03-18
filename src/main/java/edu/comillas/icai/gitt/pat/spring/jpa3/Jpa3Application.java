package edu.comillas.icai.gitt.pat.spring.jpa3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableScheduling

public class Jpa3Application {

	public static void main(String[] args) {
		SpringApplication.run(Jpa3Application.class, args);
	}

}
