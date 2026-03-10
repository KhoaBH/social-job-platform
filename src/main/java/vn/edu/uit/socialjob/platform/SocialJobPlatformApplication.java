package vn.edu.uit.socialjob.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing 
public class SocialJobPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialJobPlatformApplication.class, args);
	}

}
