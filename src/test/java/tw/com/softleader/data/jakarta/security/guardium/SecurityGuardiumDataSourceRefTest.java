package tw.com.softleader.data.jakarta.security.guardium;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import tw.com.softleader.data.jakarta.security.guardium.autoconfigure.SecurityGuardiumAutoConfiguration;

/** 特徵化：方言自動偵測（#10 #11）與 data-source-ref 具名解析（#13 #14）。 */
class SecurityGuardiumDataSourceRefTest {

  private final ApplicationContextRunner runner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(SecurityGuardiumAutoConfiguration.class));

  @DisplayName("#10 AUTO + metadata 回報 Oracle → OracleNativeQueryGuardiumApi")
  @Test
  void autoDialectDetectsOracle() throws SQLException {
    DataSource dataSource = CharacterizationFixtures.dataSourceReporting("Oracle");
    runner
        .withBean("dataSource", DataSource.class, () -> dataSource)
        .run(
            context -> {
              assertThat(context).hasNotFailed();
              assertThat(context.getBean(GuardiumApi.class))
                  .isExactlyInstanceOf(OracleNativeQueryGuardiumApi.class);
            });
  }

  @DisplayName("#11 AUTO + getConnection() 丟 SQLException → 吞例外、退回 default、context 仍啟動成功")
  @Test
  void autoDialectSwallowsSqlExceptionAndFallsBack() throws SQLException {
    DataSource dataSource = CharacterizationFixtures.dataSourceThrowingOnGetConnection();
    runner
        .withBean("dataSource", DataSource.class, () -> dataSource)
        .run(
            context -> {
              assertThat(context).hasNotFailed();
              assertThat(context.getBean(GuardiumApi.class))
                  .isExactlyInstanceOf(IBMSecurityGuardium10Api.class);
            });
  }

  @DisplayName("#13 data-source-ref 具名解析，autoconfigure 兩處取到同一顆")
  @Test
  void namedDataSourceRefIsUsedByBothInjectionPoints() throws SQLException {
    DataSource defaultDataSource = CharacterizationFixtures.dataSourceReporting("H2");
    DataSource guardiumDataSource = CharacterizationFixtures.dataSourceReporting("Oracle");
    runner
        .withBean("dataSource", DataSource.class, () -> defaultDataSource)
        .withBean("guardiumDataSource", DataSource.class, () -> guardiumDataSource)
        .withPropertyValues("security.guardium.data-source-ref=guardiumDataSource")
        .run(
            context -> {
              assertThat(context).hasNotFailed();
              // :58 取到具名那顆 → AUTO 偵測到 Oracle（若取到預設那顆會是 H2，就不會包 Oracle）
              assertThat(context.getBean(GuardiumApi.class))
                  .isExactlyInstanceOf(OracleNativeQueryGuardiumApi.class);
              // :103 也取到同一顆
              JdbcNativeQuery nativeQuery = (JdbcNativeQuery) context.getBean(NativeQuery.class);
              assertThat(((JdbcTemplate) nativeQuery.operations).getDataSource())
                  .isSameAs(guardiumDataSource);
            });
  }

  @DisplayName("#14 data-source-ref 指向不存在的 bean → context 啟動失敗")
  @Test
  void unknownDataSourceRefFailsContextStartup() throws SQLException {
    DataSource dataSource = CharacterizationFixtures.dataSourceReporting("H2");
    runner
        .withBean("dataSource", DataSource.class, () -> dataSource)
        .withPropertyValues("security.guardium.data-source-ref=noSuchDataSource")
        .run(
            context -> {
              assertThat(context).hasFailed();
              assertThat(context)
                  .getFailure()
                  .hasRootCauseInstanceOf(NoSuchBeanDefinitionException.class);
            });
  }
}
