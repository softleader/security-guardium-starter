package tw.com.softleader.data.jakarta.security.guardium;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import tw.com.softleader.data.jakarta.security.guardium.autoconfigure.SecurityGuardiumAutoConfiguration;

@JdbcTest
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ImportAutoConfiguration(classes = SecurityGuardiumAutoConfiguration.class)
class IBMSecurityGuardium10GuardAppEventTestApi {

  @Autowired JdbcTemplate template;
  @MockBean ExampleGuardAppEventSupplier exampleEventDataSupplier;
  @Autowired ExampleService service;
  @SpyBean SafeguardAspect aspect;
  @SpyBean GuardiumApi guardiumApi;

  @BeforeEach
  void setup() {
    Assertions.assertThat(guardiumApi).isInstanceOf(NativeQueryGuardiumApi.class);
  }

  @DisplayName("@Safeguard 在運作過程中如果出了例外, 不應該影響原本的 Transaction")
  @Test
  @Transactional
  void shouldNotEffectOriginalTransactionIfError() throws Throwable {
    String name = "shouldNotEffectOriginalTransactionIfError";
    doThrow(IllegalStateException.class).when(exampleEventDataSupplier).get(any(), any());
    assertDoesNotThrow(() -> service.save(name));
    assertEquals(
        1, template.queryForObject("select max(id) from test where name = ?", int.class, name));
    var inOrder = inOrder(aspect, guardiumApi, exampleEventDataSupplier);
    inOrder.verify(aspect, times(1)).around(Mockito.any());
    inOrder.verify(guardiumApi, times(1)).start(Mockito.any(), Mockito.any());
    inOrder.verify(exampleEventDataSupplier, times(1)).get(Mockito.any(), Mockito.any());
    inOrder.verify(guardiumApi, times(1)).released();
  }
}
