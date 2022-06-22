package tw.com.softleader.data.security.guardium;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import tw.com.softleader.data.security.guardium.autoconfigure.SecurityGuardiumAutoConfiguration;

@JdbcTest
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ImportAutoConfiguration(classes = SecurityGuardiumAutoConfiguration.class)
class SafeguardAspectTest {

  @SpyBean
  ExampleGuardAppEventSupplier exampleEventDataSupplier;
  @SpyBean
  SafeguardAspect aspect;
  @Autowired
  ExampleService service;
  @Autowired
  JdbcTemplate template;
  @SpyBean
  GuardiumApi guardiumApi;

  @Test
  void testSafeguard() throws Throwable {
    var name = "testSafeguard";
    service.save(name);

    assertEquals(1,
        template.queryForObject("select max(id) from test where name = ?", int.class, name));

    var inOrder = inOrder(
        aspect,
        guardiumApi,
        exampleEventDataSupplier);
    inOrder.verify(aspect, times(1)).around(Mockito.any());
    inOrder.verify(guardiumApi, times(1)).start(Mockito.any(), Mockito.any());
    inOrder.verify(exampleEventDataSupplier, times(1)).get(Mockito.any(), Mockito.any());
    inOrder.verify(guardiumApi, times(1)).released();
  }
}
