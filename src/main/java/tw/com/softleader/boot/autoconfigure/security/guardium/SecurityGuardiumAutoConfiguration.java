package tw.com.softleader.boot.autoconfigure.security.guardium;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.core.JdbcTemplate;
import tw.com.softleader.boot.security.guardium.EventDataSupplier;
import tw.com.softleader.boot.security.guardium.GuardAppEvent;
import tw.com.softleader.boot.security.guardium.IBMSecurityGuardium10GuardAppEvent;
import tw.com.softleader.boot.security.guardium.IBMSecurityGuardium10OracleGuardAppEvent;
import tw.com.softleader.boot.security.guardium.SafeguardAspect;
import tw.com.softleader.util.StringUtils;

/** @author matt */
@Configuration
@EnableConfigurationProperties(SecurityGuardiumProperties.class)
@ComponentScan(basePackageClasses = SecurityGuardiumAutoConfiguration.class)
@EnableAspectJAutoProxy
@RequiredArgsConstructor
@ConditionalOnProperty(
    name = "softleader.security.guardium.enabled",
    havingValue = "true",
    matchIfMissing = true)
@Slf4j
public class SecurityGuardiumAutoConfiguration {

  private final SecurityGuardiumProperties properties;
  private final ListableBeanFactory factory;

  @Bean
  @ConditionalOnMissingBean(GuardAppEvent.class)
  @ConditionalOnProperty(
      name = "softleader.security.guardium.version",
      havingValue = SecurityGuardiumProperties.VERSION_10,
      matchIfMissing = true)
  public GuardAppEvent guardAppEvent(@Autowired EventDataSupplier supplier){
    String dataSourceRef = properties.getDataSourceRef();
    if (StringUtils.isBlank(dataSourceRef)) {
      throw new IllegalArgumentException("dataSourceRef is required");
    }
    log.info(
        "Retrieving dataSource bean '{}' for initialize '{}'",
        dataSourceRef,
        IBMSecurityGuardium10GuardAppEvent.class);
    DataSource ds = factory.getBean(dataSourceRef, DataSource.class);
    JdbcTemplate template = new JdbcTemplate(ds);
    int timeout = properties.getQueryTimeoutSecond();
    if (timeout > 0) {
      log.info("setting sql statement query timeout for {} seconds", timeout);
      template.setQueryTimeout(timeout);
    }

    try (Connection connection = ds.getConnection()) {
      if (connection.getMetaData().getDatabaseProductName().equalsIgnoreCase("Oracle")) {
        return new IBMSecurityGuardium10OracleGuardAppEvent(template, supplier);
      } else {
        return new IBMSecurityGuardium10GuardAppEvent(template, supplier);
      }
    } catch (SQLException e) {
      log.error("GuardAppEvent getting connection fail");
      return new IBMSecurityGuardium10GuardAppEvent(template, supplier);
    }
  }

  @Bean
  @ConditionalOnMissingBean(EventDataSupplier.class)
  public EventDataSupplier emptyEventDataSupplier() {
    return (target, args) -> Collections.unmodifiableMap(new HashMap<>());
  }

  @Bean
  public SafeguardAspect guardAppEventAspect(GuardAppEvent guardAppEvent) {
    return new SafeguardAspect(guardAppEvent);
  }
}
