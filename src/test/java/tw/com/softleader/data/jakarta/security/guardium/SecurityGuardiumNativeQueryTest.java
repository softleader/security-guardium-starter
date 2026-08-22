package tw.com.softleader.data.jakarta.security.guardium;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import tw.com.softleader.data.jakarta.security.guardium.autoconfigure.SecurityGuardiumAutoConfiguration;

/** 特徵化：NativeQuery 分支選擇（#3 #4 #5）、query timeout（#12）、GuardAppEventSupplier 預設值（#15）。 */
class SecurityGuardiumNativeQueryTest {

  private final ApplicationContextRunner runner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(SecurityGuardiumAutoConfiguration.class));

  @DisplayName("#3 有 EntityManagerFactoryInfo bean → JpaNativeQuery")
  @Test
  void jpaBranchWhenEntityManagerFactoryInfoPresent() throws SQLException {
    DataSource dataSource = CharacterizationFixtures.dataSourceReporting("H2");
    EntityManagerFactoryInfo info = CharacterizationFixtures.entityManagerFactoryInfo("test-pu");
    runner
        .withBean("dataSource", DataSource.class, () -> dataSource)
        .withBean(EntityManagerFactoryInfo.class, () -> info)
        .run(
            context -> {
              assertThat(context).hasNotFailed();
              assertThat(context).hasSingleBean(NativeQuery.class);
              assertThat(context.getBean(NativeQuery.class))
                  .isExactlyInstanceOf(JpaNativeQuery.class);
              assertThat(context).doesNotHaveBean(JdbcNativeQuery.class);
              JpaNativeQuery nativeQuery = (JpaNativeQuery) context.getBean(NativeQuery.class);
              assertThat(nativeQuery.factory).isSameAs(info.getNativeEntityManagerFactory());
            });
  }

  @DisplayName("#4 無 JPA → JdbcNativeQuery")
  @Test
  void jdbcBranchWhenNoJpa() throws SQLException {
    DataSource dataSource = CharacterizationFixtures.dataSourceReporting("H2");
    runner
        .withBean("dataSource", DataSource.class, () -> dataSource)
        .run(
            context -> {
              assertThat(context).hasNotFailed();
              assertThat(context).hasSingleBean(NativeQuery.class);
              assertThat(context.getBean(NativeQuery.class))
                  .isExactlyInstanceOf(JdbcNativeQuery.class);
              assertThat(context).doesNotHaveBean(JpaNativeQuery.class);
              JdbcNativeQuery nativeQuery = (JdbcNativeQuery) context.getBean(NativeQuery.class);
              assertThat(((JdbcTemplate) nativeQuery.operations).getDataSource())
                  .isSameAs(dataSource);
            });
  }

  @DisplayName("#5 使用者自備 NativeQuery → jpaNativeQuery / jdbcNativeQuery 都不建")
  @Test
  void userSuppliedNativeQueryWins() throws SQLException {
    DataSource dataSource = CharacterizationFixtures.dataSourceReporting("H2");
    EntityManagerFactoryInfo info = CharacterizationFixtures.entityManagerFactoryInfo("test-pu");
    NativeQuery userNativeQuery = sql -> {};
    runner
        .withBean("dataSource", DataSource.class, () -> dataSource)
        .withBean(EntityManagerFactoryInfo.class, () -> info)
        .withBean("userNativeQuery", NativeQuery.class, () -> userNativeQuery)
        .run(
            context -> {
              assertThat(context).hasNotFailed();
              assertThat(context).hasSingleBean(NativeQuery.class);
              assertThat(context.getBean(NativeQuery.class)).isSameAs(userNativeQuery);
              assertThat(context).doesNotHaveBean(JpaNativeQuery.class);
              assertThat(context).doesNotHaveBean(JdbcNativeQuery.class);
            });
  }

  @DisplayName("#12 query-timeout-second=30 → 套用到 JdbcTemplate")
  @Test
  void positiveQueryTimeoutIsApplied() throws SQLException {
    DataSource dataSource = CharacterizationFixtures.dataSourceReporting("H2");
    runner
        .withBean("dataSource", DataSource.class, () -> dataSource)
        .withPropertyValues("security.guardium.query-timeout-second=30")
        .run(
            context -> {
              JdbcNativeQuery nativeQuery = (JdbcNativeQuery) context.getBean(NativeQuery.class);
              assertThat(((JdbcTemplate) nativeQuery.operations).getQueryTimeout()).isEqualTo(30);
            });
  }

  @DisplayName("#12 query-timeout-second=-1 → 不套用，維持 JdbcTemplate 預設值")
  @Test
  void negativeQueryTimeoutIsNotApplied() throws SQLException {
    DataSource dataSource = CharacterizationFixtures.dataSourceReporting("H2");
    runner
        .withBean("dataSource", DataSource.class, () -> dataSource)
        .withPropertyValues("security.guardium.query-timeout-second=-1")
        .run(
            context -> {
              JdbcNativeQuery nativeQuery = (JdbcNativeQuery) context.getBean(NativeQuery.class);
              assertThat(((JdbcTemplate) nativeQuery.operations).getQueryTimeout()).isEqualTo(-1);
            });
  }

  @DisplayName("#12 query-timeout-second=0 → 不套用（if (timeout > 0) 的邊界）")
  @Test
  void zeroQueryTimeoutIsNotApplied() throws SQLException {
    DataSource dataSource = CharacterizationFixtures.dataSourceReporting("H2");
    runner
        .withBean("dataSource", DataSource.class, () -> dataSource)
        .withPropertyValues("security.guardium.query-timeout-second=0")
        .run(
            context -> {
              JdbcNativeQuery nativeQuery = (JdbcNativeQuery) context.getBean(NativeQuery.class);
              assertThat(((JdbcTemplate) nativeQuery.operations).getQueryTimeout()).isEqualTo(-1);
            });
  }

  @DisplayName("#15 未提供 GuardAppEventSupplier → 補一個回傳空 GuardAppEvent 的實作")
  @Test
  void defaultGuardAppEventSupplierIsRegistered() throws SQLException {
    DataSource dataSource = CharacterizationFixtures.dataSourceReporting("H2");
    runner
        .withBean("dataSource", DataSource.class, () -> dataSource)
        .run(
            context -> {
              assertThat(context).hasSingleBean(GuardAppEventSupplier.class);
              GuardAppEvent event = context.getBean(GuardAppEventSupplier.class).get(null, null);
              assertThat(event.toString())
                  .isEqualTo(
                      "'GuardAppEventUserName:null','GuardAppEventType:null',"
                          + "'GuardAppEventStrValue:null'");
            });
  }

  @DisplayName("#15 使用者自備 GuardAppEventSupplier → 不覆蓋")
  @Test
  void userSuppliedGuardAppEventSupplierWins() throws SQLException {
    DataSource dataSource = CharacterizationFixtures.dataSourceReporting("H2");
    GuardAppEventSupplier userSupplier =
        (method, args) -> GuardAppEvent.builder().userName("user").build();
    runner
        .withBean("dataSource", DataSource.class, () -> dataSource)
        .withBean("userGuardAppEventSupplier", GuardAppEventSupplier.class, () -> userSupplier)
        .run(
            context -> {
              assertThat(context).hasSingleBean(GuardAppEventSupplier.class);
              assertThat(context.getBean(GuardAppEventSupplier.class)).isSameAs(userSupplier);
            });
  }
}
