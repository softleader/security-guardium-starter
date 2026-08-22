package tw.com.softleader.data.jakarta.security.guardium;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import tw.com.softleader.data.jakarta.security.guardium.autoconfigure.SecurityGuardiumAutoConfiguration;
import tw.com.softleader.data.jakarta.security.guardium.autoconfigure.SecurityGuardiumProperties;

/** 特徵化：開關（#1 #2）與方言（#6 #7 #8 #9）。 */
class SecurityGuardiumEnabledAndDialectTest {

  private final ApplicationContextRunner runner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(SecurityGuardiumAutoConfiguration.class));

  /**
   * autoconfigure 在 :58 與 :103 都以 properties.dataSourceRef（預設 "dataSource"）具名取 bean， 因此每個 context
   * 都必須有一顆叫 dataSource 的 bean。
   */
  private ApplicationContextRunner withDataSourceReporting(String databaseProductName)
      throws SQLException {
    DataSource dataSource = CharacterizationFixtures.dataSourceReporting(databaseProductName);
    return runner.withBean("dataSource", DataSource.class, () -> dataSource);
  }

  @DisplayName("#1 enabled=false → 整組 bean 都不建")
  @Test
  void disabledCreatesNothing() throws SQLException {
    withDataSourceReporting("H2")
        .withPropertyValues("security.guardium.enabled=false")
        .run(
            context -> {
              assertThat(context).hasNotFailed();
              assertThat(context).doesNotHaveBean(SecurityGuardiumAutoConfiguration.class);
              assertThat(context).doesNotHaveBean(SecurityGuardiumProperties.class);
              assertThat(context).doesNotHaveBean(GuardiumApi.class);
              assertThat(context).doesNotHaveBean(NativeQuery.class);
              assertThat(context).doesNotHaveBean(GuardAppEventSupplier.class);
              assertThat(context).doesNotHaveBean(SafeguardAspect.class);
            });
  }

  @DisplayName("#2 enabled 未設定 → 生效（matchIfMissing=true）")
  @Test
  void enabledByDefault() throws SQLException {
    withDataSourceReporting("H2")
        .run(
            context -> {
              assertThat(context).hasNotFailed();
              assertThat(context).hasSingleBean(SecurityGuardiumAutoConfiguration.class);
              assertThat(context).hasSingleBean(SecurityGuardiumProperties.class);
              assertThat(context).hasSingleBean(GuardiumApi.class);
              assertThat(context).hasSingleBean(NativeQuery.class);
              assertThat(context).hasSingleBean(GuardAppEventSupplier.class);
              assertThat(context).hasSingleBean(SafeguardAspect.class);
            });
  }

  @DisplayName("#6 dialect=oracle → OracleNativeQueryGuardiumApi")
  @Test
  void oracleDialect() throws SQLException {
    withDataSourceReporting("H2")
        .withPropertyValues("security.guardium.dialect=oracle")
        .run(
            context ->
                assertThat(context.getBean(GuardiumApi.class))
                    .isExactlyInstanceOf(OracleNativeQueryGuardiumApi.class));
  }

  @DisplayName("#7 dialect=default → IBMSecurityGuardium10Api")
  @Test
  void defaultDialect() throws SQLException {
    withDataSourceReporting("H2")
        .withPropertyValues("security.guardium.dialect=default")
        .run(
            context ->
                assertThat(context.getBean(GuardiumApi.class))
                    .isExactlyInstanceOf(IBMSecurityGuardium10Api.class));
  }

  @DisplayName("#8 dialect 未設定 → 等同 AUTO")
  @Test
  void dialectDefaultsToAuto() throws SQLException {
    withDataSourceReporting("H2")
        .run(
            context ->
                assertThat(context.getBean(SecurityGuardiumProperties.class).getDialect())
                    .isEqualTo(GuardiumDialect.AUTO));
  }

  @DisplayName("#9 AUTO + 資料庫回報 H2 → 退回 default 實作")
  @Test
  void autoDialectWithNonOracleFallsBackToDefault() throws SQLException {
    withDataSourceReporting("H2")
        .run(
            context ->
                assertThat(context.getBean(GuardiumApi.class))
                    .isExactlyInstanceOf(IBMSecurityGuardium10Api.class));
  }
}
