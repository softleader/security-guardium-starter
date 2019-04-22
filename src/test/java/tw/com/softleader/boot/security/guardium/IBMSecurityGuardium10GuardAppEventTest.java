package tw.com.softleader.boot.security.guardium;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import tw.com.softleader.boot.autoconfigure.security.guardium.SecurityGuardiumAutoConfiguration;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@JdbcTest
@SpringBootTest(classes = IBMSecurityGuardium10GuardAppEventTest.Config.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class IBMSecurityGuardium10GuardAppEventTest {

  @Configuration
  @ComponentScan(basePackageClasses = SafeguardAspectTest.class)
  @ImportAutoConfiguration(classes = SecurityGuardiumAutoConfiguration.class)
  static class Config {

    @Bean
    public Example example() {
      return new Example() {
        @Override
        public String collect(Method method, Object[] args) {
          super.collect(method, args);
          throw new IllegalStateException();
        }
      };
    }
  }

  @Autowired private JdbcTemplate template;
  @Autowired private Example example;
  @Autowired private ExampleService service;

  /** testCommit 測試 Safeguard 在 select from db 出了例外, 不應該影響原本的 transaction */
  @Test
  @Transactional
  public void testCommit() {
    template.update("INSERT INTO EXAMPLE (CALLS) VALUES (?)", 1);
    example.setCalls(1);
    try {
      service.hello();
    } finally {
      int calls = template.queryForObject("SELECT MAX(CALLS) FROM EXAMPLE", int.class);
      Assert.assertEquals(2, calls);
    }
  }
}
