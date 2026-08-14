package tw.com.softleader.data.jakarta.security.guardium;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import tw.com.softleader.data.jakarta.security.guardium.EmbeddedJdbcSupport.SpyBeanPostProcessor;

class IBMSecurityGuardium10GuardAppEventApiTest {

  @DisplayName("@Safeguard 在運作過程中如果出了例外, 不應該影響原本的 Transaction")
  @Test
  void shouldNotEffectOriginalTransactionIfError() throws Throwable {
    EmbeddedJdbcSupport.runner()
        .withBean(
            SpyBeanPostProcessor.class,
            () ->
                new SpyBeanPostProcessor(
                    ExampleGuardAppEventSupplier.class, SafeguardAspect.class, GuardiumApi.class))
        .withBean(ExampleGuardAppEventSupplier.class, ExampleGuardAppEventSupplier::new)
        .withBean(ExampleService.class, ExampleService::new)
        .run(
            context -> {
              var template = context.getBean(JdbcTemplate.class);
              var exampleEventDataSupplier = context.getBean(ExampleGuardAppEventSupplier.class);
              var service = context.getBean(ExampleService.class);
              var aspect = context.getBean(SafeguardAspect.class);
              var guardiumApi = context.getBean(GuardiumApi.class);

              Assertions.assertThat(guardiumApi).isInstanceOf(NativeQueryGuardiumApi.class);

              String name = "shouldNotEffectOriginalTransactionIfError";
              doThrow(IllegalStateException.class).when(exampleEventDataSupplier).get(any(), any());
              assertDoesNotThrow(() -> service.save(name));
              assertEquals(
                  1,
                  template.queryForObject(
                      "select max(id) from test where name = ?", int.class, name));
              var inOrder = inOrder(aspect, guardiumApi, exampleEventDataSupplier);
              inOrder.verify(aspect, times(1)).around(Mockito.any());
              inOrder.verify(guardiumApi, times(1)).start(Mockito.any(), Mockito.any());
              inOrder.verify(exampleEventDataSupplier, times(1)).get(Mockito.any(), Mockito.any());
              inOrder.verify(guardiumApi, times(1)).released();
            });
  }
}
