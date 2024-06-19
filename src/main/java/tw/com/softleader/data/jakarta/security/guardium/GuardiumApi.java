package tw.com.softleader.data.jakarta.security.guardium;

import java.lang.reflect.Method;

/**
 * {@code GuardiumApi} 定義串接 IBM Security Guardium API 的介面
 *
 * @author matt
 */
public interface GuardiumApi {

  /**
   * The underlying class should implement GuardAppEvent:Start
   *
   * <p>MUST NOT THROW ANY EXCEPTION OR EFFECT CURRENT TRANSACTION
   */
  void start(Method method, Object[] args);

  /**
   * The underlying class should implement GuardAppEvent:Released
   *
   * <p>MUST NOT THROW ANY EXCEPTION OR EFFECT CURRENT TRANSACTION
   */
  void released();
}
