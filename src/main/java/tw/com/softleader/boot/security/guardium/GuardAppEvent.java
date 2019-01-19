package tw.com.softleader.boot.security.guardium;

import java.lang.reflect.Method;

/**
 * GuardAppEvent 也就是 IBM Security Guardium 的一個 event
 *
 * @author matt
 */
public interface GuardAppEvent {
  /**
   * The underlying class should implements GuardAppEvent:Start
   *
   * <p>MUST NOT THROW ANY EXCEPTION OR EFFECT CURRENT TRANSACTION
   */
  void start(Method method, Object[] args);

  /**
   * The underlying class should implements GuardAppEvent:Released
   *
   * <p>MUST NOT THROW ANY EXCEPTION OR EFFECT CURRENT TRANSACTION
   */
  void released();
}
