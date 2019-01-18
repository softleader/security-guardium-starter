package tw.com.softleader.boot.security.guardium;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcOperations;

import java.lang.reflect.Method;

@Slf4j
@RequiredArgsConstructor
public class IBMSecurityGuardium9GuardAppEvent implements GuardAppEvent {

  private static final String EVENT_START = "GuardAppEvent:Start";
  private static final String EVENT_RELEASED = "GuardAppEvent:Released";

  @NonNull private final JdbcOperations jdbcOperations;
  @NonNull private final EventDataSupplier eventDataSupplier;
  private final boolean enableExceptionLog;

  @Override
  public void start(Method method, Object[] args) {
    try {
      String sql =
          String.format("SELECT '%s',%s", EVENT_START, eventDataSupplier.collect(method, args));
      log.trace("Setting an application event: {}", sql);
      jdbcOperations.execute(sql);
    } catch (Exception e) {
      if (enableExceptionLog) {
        log.error(EVENT_START, e);
      }
    }
  }

  @Override
  public void released() {
    try {
      String sql = String.format("SELECT '%s'", EVENT_RELEASED);
      log.trace("Clearing an application event: {}", sql);
      jdbcOperations.execute(sql);
    } catch (Exception e) {
      if (enableExceptionLog) {
        log.error(EVENT_RELEASED, e);
      }
    }
  }
}
