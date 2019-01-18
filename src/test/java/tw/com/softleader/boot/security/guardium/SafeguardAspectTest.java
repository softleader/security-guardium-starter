package tw.com.softleader.boot.security.guardium;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@JdbcTest
@SpringBootTest(classes = TestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SafeguardAspectTest {

  @Autowired private ExampleService service;
  @Autowired private Example example;

  @Test
  public void testPeace() {
    service.hello();
    Assert.assertEquals(example.getCalls(), 1);
  }
}
