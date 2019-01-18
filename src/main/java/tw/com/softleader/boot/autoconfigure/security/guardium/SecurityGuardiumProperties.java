package tw.com.softleader.boot.autoconfigure.security.guardium;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tw.com.softleader.boot.security.guardium.EventDataSupplier;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/** @author matt */
@Getter
@Setter
@ConfigurationProperties(prefix = "softleader.security.guardium")
public class SecurityGuardiumProperties {

  private static final String DEFAULT_VERSION = "9";
  private static final int DEFAULT_QUERY_TIMEOUT_SECOND = -1;

  /**
   * 設定客戶使用的 IBM Security Guardium 版本
   *
   * <p>版本 9 的 ibm 使用文件的頁面卻沒有 10 的版本, 因此推斷 ibm 在版本 10 的用法有改, 所以我們要有可以控制實作的選項
   *
   * <p>當然, 我們現在只有 9 的實作
   *
   * @see <a
   *     href="https://www.ibm.com/support/knowledgecenter/en/SSMPHH_10.6.0/com.ibm.guardium.doc/overview/product_overview.html,">https://www.ibm.com/support/knowledgecenter/en/SSMPHH_10.6.0/com.ibm.guardium.doc/overview/product_overview.html,</a>
   */
  private String version = DEFAULT_VERSION;

  /**
   * 設定 sql statement timeout 秒數, -1 代表使用 driver 預設值
   *
   * <p>但 driver default 有可能會 block main thread 太久, 因此提供參數可以控制, 建議要設定
   */
  private int queryTimeoutSecond = DEFAULT_QUERY_TIMEOUT_SECOND;

  /** 發生例外時是否要印出 log */
  private boolean logException;

  private String dataSourceRef;
}
