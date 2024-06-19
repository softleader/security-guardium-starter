package tw.com.softleader.data.jakarta.security.guardium;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/** Entry point for test */
@SpringBootApplication
class TestApplication {

  public static void main(String[] args) {
    SpringApplication.run(TestApplication.class, args);
  }

  @Bean
  ExampleService exampleService() {
    return new ExampleService();
  }

  @Bean
  ExampleGuardAppEventSupplier example() {
    return new ExampleGuardAppEventSupplier();
  }
}
