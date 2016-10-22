package hootsuit.webhook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaRepositories("hootsuit.webhook.persistence")
@EnableJpaAuditing
@EnableTransactionManagement
public class WebhookConfiguration {
	
	public static void main(String[] args) {
		SpringApplication.run(WebhookConfiguration.class, args);
	}
	
}
