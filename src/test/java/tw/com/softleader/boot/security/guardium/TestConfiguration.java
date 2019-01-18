package tw.com.softleader.boot.security.guardium;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import tw.com.softleader.boot.autoconfigure.security.guardium.SecurityGuardiumAutoConfiguration;

@Configuration
@ImportAutoConfiguration(classes = SecurityGuardiumAutoConfiguration.class)
@ComponentScan(basePackageClasses = TestConfiguration.class)
public class TestConfiguration {

  @Bean
  public EventDataSupplier example() {
    return new Example();
  }
}
