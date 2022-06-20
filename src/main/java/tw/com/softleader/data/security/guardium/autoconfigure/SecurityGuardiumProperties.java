package tw.com.softleader.data.security.guardium.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** @author matt */
@Getter
@Setter
@ConfigurationProperties(prefix = "security.guardium")
public class SecurityGuardiumProperties {

  static final String VERSION_10 = "10";
  private static final int DEFAULT_QUERY_TIMEOUT_SECOND = 10;
  private static final String DEFAULT_DATA_SOURCE_REF = "dataSource";

  /** Enable Security Guardium for the application. */
  private boolean enabled = true;

  /**
   * 設定客戶使用的 IBM Security Guardium 版本
   *
   * <p>
   * IBM Security Guardium 版本目前有 8, 9 跟 10 這幾個大版本, 目前只支援 10 的實作
   *
   * @see <a href= "https://www.ibm.com/support/knowledgecenter/en/SSMPHH/SSMPHH_welcome.html">https://www.ibm.com/support/knowledgecenter/en/SSMPHH/SSMPHH_welcome.html</a>
   */
  private String version = VERSION_10;

  /**
   * 設定 sql statement timeout 秒數, -1 代表使用 driver 預設值
   *
   * <p>
   * 但 driver default 有可能會 block main thread 太久, 因此提供參數可以控制, 建議要設定
   */
  private int queryTimeoutSecond = DEFAULT_QUERY_TIMEOUT_SECOND;

  /** 設定 dataSource bean 名稱 */
  private String dataSourceRef = DEFAULT_DATA_SOURCE_REF;
}
