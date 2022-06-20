package tw.com.softleader.data.security.guardium.autoconfigure;

import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;
import tw.com.softleader.data.security.guardium.GuardAppEvent;
import tw.com.softleader.data.security.guardium.GuardAppEventApi;
import tw.com.softleader.data.security.guardium.GuardAppEventSupplier;
import tw.com.softleader.data.security.guardium.IBMSecurityGuardium10GuardAppEventApi;
import tw.com.softleader.data.security.guardium.IBMSecurityGuardium10OracleGuardAppEventApi;
import tw.com.softleader.data.security.guardium.SafeguardAspect;

/**
 * @author matt
 */
@Configuration
@EnableConfigurationProperties(SecurityGuardiumProperties.class)
@EnableAspectJAutoProxy
@RequiredArgsConstructor
@ConditionalOnProperty(name = "softleader.security.guardium.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class SecurityGuardiumAutoConfiguration {

  private final SecurityGuardiumProperties properties;
  private final ListableBeanFactory factory;

  @Bean
  @ConditionalOnMissingBean(GuardAppEventApi.class)
  @ConditionalOnProperty(name = "softleader.security.guardium.version", havingValue = SecurityGuardiumProperties.VERSION_10, matchIfMissing = true)
  GuardAppEventApi guardAppEvent(@Autowired GuardAppEventSupplier supplier) {
    String dataSourceRef = properties.getDataSourceRef();
    if (!StringUtils.hasText(dataSourceRef)) {
      throw new IllegalArgumentException("dataSourceRef is required");
    }
    log.info("Retrieving dataSource bean '{}' for GuardAppEvent", dataSourceRef);
    DataSource ds = factory.getBean(dataSourceRef, DataSource.class);
    JdbcTemplate template = new JdbcTemplate(ds);
    int timeout = properties.getQueryTimeoutSecond();
    if (timeout > 0) {
      log.info("Setting sql statement query timeout for {}s", timeout);
      template.setQueryTimeout(timeout);
    }

    try (Connection connection = ds.getConnection()) {
      return autodetectGuardAppEvent(supplier, template, connection);
    } catch (SQLException e) {
      log.warn("Failed to auto-detected GuardAppEvent type, falling back to default: '{}' : {}",
          IBMSecurityGuardium10GuardAppEventApi.class.getSimpleName(), getRootCauseMessage(e));
      return new IBMSecurityGuardium10GuardAppEventApi(template, supplier);
    }
  }

  private GuardAppEventApi autodetectGuardAppEvent(GuardAppEventSupplier supplier, JdbcTemplate template,
      Connection connection) throws SQLException {
    GuardAppEventApi gae = (connection.getMetaData().getDatabaseProductName().equalsIgnoreCase("Oracle"))
        ? new IBMSecurityGuardium10OracleGuardAppEventApi(template, supplier)
        : new IBMSecurityGuardium10GuardAppEventApi(template, supplier);
    log.info("Initializing GuardAppEvent of '{}'", gae.getClass().getSimpleName());
    return gae;
  }

  @Bean
  @ConditionalOnMissingBean(GuardAppEventSupplier.class)
  GuardAppEventSupplier emptyGuardAppEventSupplier() {
    return (method, args) -> GuardAppEvent.builder().build();
  }

  @Bean
  SafeguardAspect guardAppEventAspect(GuardAppEventApi guardAppEventApi) {
    return new SafeguardAspect(guardAppEventApi);
  }
}
