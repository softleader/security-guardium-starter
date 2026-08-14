package tw.com.softleader.data.jakarta.security.guardium;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GuardiumDialectTest {

  @DisplayName("手動指定成 oracle 方言")
  @Test
  void manuallySetOracleDialect() {
    EmbeddedJdbcSupport.runner()
        .withPropertyValues("security.guardium.dialect=oracle")
        .run(
            context -> {
              GuardiumApi guardiumApi = context.getBean(GuardiumApi.class);
              Assertions.assertThat(guardiumApi).isInstanceOf(OracleNativeQueryGuardiumApi.class);
            });
  }
}
