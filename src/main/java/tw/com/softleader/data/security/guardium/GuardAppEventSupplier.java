package tw.com.softleader.data.security.guardium;

import java.lang.reflect.Method;

/**
 * {@code GuardAppEventSupplier} 提供 {@link GuardAppEventApi} 在 runtime 每次呼叫取用
 *
 * @author matt
 */
public interface GuardAppEventSupplier {

  /**
   * 取得要寫入 IBM Security Guardium 的資料
   *
   * @param method 被 AOP 攔截的 method
   * @param args 被 AOP 攔截 method 的 input 參數
   * @return data to write to IBM Security Guardium, key=欄位名稱, value=值
   */
  GuardAppEvent get(Method method, Object[] args);
}
