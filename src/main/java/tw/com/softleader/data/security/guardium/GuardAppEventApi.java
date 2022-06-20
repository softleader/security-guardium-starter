package tw.com.softleader.data.security.guardium;

import java.lang.reflect.Method;

/**
 * {@code GuardAppEventApi} 負責處理每次的呼叫 IBM Security Guardium
 *
 * @author matt
 */
public interface GuardAppEventApi {

  /**
   * The underlying class should implement GuardAppEvent:Start
   *
   * <p>
   * MUST NOT THROW ANY EXCEPTION OR EFFECT CURRENT TRANSACTION
   */
  void start(Method method, Object[] args);

  /**
   * The underlying class should implement GuardAppEvent:Released
   *
   * <p>
   * MUST NOT THROW ANY EXCEPTION OR EFFECT CURRENT TRANSACTION
   */
  void released();
}
