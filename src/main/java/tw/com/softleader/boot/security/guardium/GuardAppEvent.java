package tw.com.softleader.boot.security.guardium;

import java.lang.reflect.Method;

/**
 * @author matt
 * @see <a
 *     href="https://www.ibm.com/support/knowledgecenter/en/SSMPHH_9.5.0/com.ibm.guardium95.doc/monitor_audit/topics/identify_users_via_api.html">https://www.ibm.com/support/knowledgecenter/en/SSMPHH_9.5.0/com.ibm.guardium95.doc/monitor_audit/topics/identify_users_via_api.html</a>
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
