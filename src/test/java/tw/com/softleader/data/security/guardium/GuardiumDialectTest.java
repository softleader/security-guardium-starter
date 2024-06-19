package tw.com.softleader.data.security.guardium;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import tw.com.softleader.data.security.guardium.autoconfigure.SecurityGuardiumAutoConfiguration;

@JdbcTest(properties = {"security.guardium.dialect=oracle"})
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@ImportAutoConfiguration(classes = SecurityGuardiumAutoConfiguration.class)
class GuardiumDialectTest {

  @Autowired GuardiumApi guardiumApi;

  @DisplayName("手動指定成 oracle 方言")
  @Test
  @Transactional
  void manuallySetOracleDialect() {
    Assertions.assertThat(guardiumApi).isInstanceOf(OracleNativeQueryGuardiumApi.class);
  }
}
