package tw.com.softleader.data.jakarta.security.guardium.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tw.com.softleader.data.jakarta.security.guardium.GuardiumDialect;

/**
 * @author matt
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "security.guardium")
public class SecurityGuardiumProperties {

  private static final int DEFAULT_QUERY_TIMEOUT_SECOND = 10;
  private static final String DEFAULT_DATA_SOURCE_REF = "dataSource";

  /** Enable Security Guardium for the application. */
  private boolean enabled = true;

  /** Guardium dialect */
  private GuardiumDialect dialect = GuardiumDialect.AUTO;

  /**
   * 設定 sql statement timeout 秒數, -1 代表使用 driver 預設值
   *
   * <p>但 driver default 有可能會 block main thread 太久, 因此提供參數可以控制, 建議要設定
   */
  private int queryTimeoutSecond = DEFAULT_QUERY_TIMEOUT_SECOND;

  /** 設定 dataSource bean 名稱 */
  private String dataSourceRef = DEFAULT_DATA_SOURCE_REF;
}
