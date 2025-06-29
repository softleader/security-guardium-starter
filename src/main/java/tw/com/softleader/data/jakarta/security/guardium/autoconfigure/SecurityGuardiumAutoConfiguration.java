package tw.com.softleader.data.jakarta.security.guardium.autoconfigure;

import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;
import static tw.com.softleader.data.jakarta.security.guardium.autoconfigure.SecurityGuardiumProperties.GUARDIUM_PREFIX;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import tw.com.softleader.data.jakarta.security.guardium.GuardAppEvent;
import tw.com.softleader.data.jakarta.security.guardium.GuardAppEventSupplier;
import tw.com.softleader.data.jakarta.security.guardium.GuardiumApi;
import tw.com.softleader.data.jakarta.security.guardium.IBMSecurityGuardium10Api;
import tw.com.softleader.data.jakarta.security.guardium.JdbcNativeQuery;
import tw.com.softleader.data.jakarta.security.guardium.JpaNativeQuery;
import tw.com.softleader.data.jakarta.security.guardium.NativeQuery;
import tw.com.softleader.data.jakarta.security.guardium.NativeQueryGuardiumApi;
import tw.com.softleader.data.jakarta.security.guardium.OracleNativeQueryGuardiumApi;
import tw.com.softleader.data.jakarta.security.guardium.SafeguardAspect;

/**
 * @author matt
 */
@Configuration
@EnableConfigurationProperties(SecurityGuardiumProperties.class)
@EnableAspectJAutoProxy
@RequiredArgsConstructor
@ConditionalOnProperty(
    prefix = GUARDIUM_PREFIX,
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
@Slf4j
public class SecurityGuardiumAutoConfiguration {

  private final SecurityGuardiumProperties properties;
  private final ListableBeanFactory factory;

  @Bean
  @ConditionalOnMissingBean(GuardiumApi.class)
  GuardiumApi guardiumApi(GuardAppEventSupplier supplier, NativeQuery nativeQuery) {
    var dataSourceRef = properties.getDataSourceRef();
    log.info(
        "Retrieving dataSource bean '{}' for {}",
        dataSourceRef,
        GuardAppEvent.class.getSimpleName());
    var ds = factory.getBean(dataSourceRef, DataSource.class);
    var guardiumApi = (NativeQueryGuardiumApi) new IBMSecurityGuardium10Api(supplier, nativeQuery);
    switch (properties.getDialect()) {
      case ORACLE:
        log.info("Initializing OracleNativeQueryGuardiumApi");
        return new OracleNativeQueryGuardiumApi(guardiumApi);
      case AUTO:
        log.info("Auto detecting dialect for GuardiumApi");
        return autoDetectedGuardiumApi(ds, guardiumApi);
      case DEFAULT:
      default:
        log.info("Initializing default NativeQueryGuardiumApi");
        return guardiumApi;
    }
  }

  private GuardiumApi autoDetectedGuardiumApi(DataSource ds, NativeQueryGuardiumApi guardiumApi) {
    try (Connection connection = ds.getConnection()) {
      if (connection.getMetaData().getDatabaseProductName().equalsIgnoreCase("Oracle")) {
        log.info("Detected Oracle database, initializing OracleNativeQueryGuardiumApi");
        return new OracleNativeQueryGuardiumApi(guardiumApi);
      }
      log.info("Initializing default NativeQueryGuardiumApi");
      return guardiumApi;
    } catch (SQLException e) {
      log.warn(
          "Failed to auto-detected GuardiumApi type, falling back to default: {}",
          getRootCauseMessage(e));
      return guardiumApi;
    }
  }

  @Bean
  @ConditionalOnBean(EntityManagerFactoryInfo.class)
  @ConditionalOnMissingBean(NativeQuery.class)
  NativeQuery jpaNativeQuery(EntityManagerFactoryInfo factory) {
    log.info(
        "Initializing JPA NativeQuery for persistence unit '{}'", factory.getPersistenceUnitName());
    return new JpaNativeQuery(factory.getNativeEntityManagerFactory());
  }

  @Bean
  @ConditionalOnMissingBean(NativeQuery.class)
  NativeQuery jdbcNativeQuery() {
    log.info("Initializing JDBC NativeQuery for datasource '{}'", properties.getDataSourceRef());
    DataSource ds = factory.getBean(properties.getDataSourceRef(), DataSource.class);
    JdbcTemplate template = new JdbcTemplate(ds);
    int timeout = properties.getQueryTimeoutSecond();
    if (timeout > 0) {
      log.info("Setting sql statement query timeout for {}s", timeout);
      template.setQueryTimeout(timeout);
    }
    return new JdbcNativeQuery(template);
  }

  @Bean
  @ConditionalOnMissingBean(GuardAppEventSupplier.class)
  GuardAppEventSupplier emptyGuardAppEventSupplier() {
    return (method, args) -> GuardAppEvent.builder().build();
  }

  @Bean
  @ConditionalOnMissingBean
  SafeguardAspect guardAppEventAspect(GuardiumApi guardiumApi) {
    return new SafeguardAspect(guardiumApi);
  }
}
