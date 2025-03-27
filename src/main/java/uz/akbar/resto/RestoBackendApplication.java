package uz.akbar.resto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class RestoBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestoBackendApplication.class, args);
	}

}
