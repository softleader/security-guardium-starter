package tw.com.softleader.data.security.guardium;

import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;

/** {@code NativeQueryGuardiumApi} 定義從 {@link GuardAppEvent} 取得 Native SQL 的介面 */
@Slf4j
public abstract class NativeQueryGuardiumApi implements GuardiumApi {

  public static final String EVENT_START = "GuardAppEvent:Start";
  public static final String EVENT_RELEASED = "GuardAppEvent:Released";

  /**
   * @param method 被 AOP 攔截的 method
   * @param args 被 AOP 攔截 method 的 input 參數
   * @return Start SQL statement to execute
   */
  protected abstract String startSql(Method method, Object[] args);

  /**
   * @return Released SQL statement to execute
   */
  protected abstract String releasedSql();

  protected abstract NativeQuery getNativeQuery();

  @Override
  public void start(Method method, Object[] args) {
    try {
      var sql = startSql(method, args);
      log.trace("Setting an application event: {}", sql);
      getNativeQuery().execute(sql);
    } catch (Exception e) {
      log.error(EVENT_START, e);
    }
  }

  @Override
  public void released() {
    try {
      var sql = releasedSql();
      log.trace("Clearing an application event: {}", sql);
      getNativeQuery().execute(sql);
    } catch (Exception e) {
      log.error(EVENT_RELEASED, e);
    }
  }
}
