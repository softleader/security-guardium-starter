package tw.com.softleader.boot.security.guardium;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcOperations;

import java.lang.reflect.Method;

@Slf4j
@RequiredArgsConstructor
public class IBMSecurityGuardium10GuardAppEvent implements GuardAppEvent {

  private static final String EVENT_START = "GuardAppEvent:Start";
  private static final String EVENT_RELEASED = "GuardAppEvent:Released";

  @NonNull private final JdbcOperations jdbcOperations;
  @NonNull private final EventDataSupplier eventDataSupplier;

  @Override
  public void start(Method method, Object[] args) {
    try {
      String sql =
          String.format("SELECT '%s',%s", EVENT_START, eventDataSupplier.collect(method, args));
      log.trace("Setting an application event: {}", sql);
      jdbcOperations.execute(sql);
    } catch (Exception e) {
      log.error(EVENT_START, e);
    }
  }

  @Override
  public void released() {
    try {
      String sql = String.format("SELECT '%s'", EVENT_RELEASED);
      log.trace("Clearing an application event: {}", sql);
      jdbcOperations.execute(sql);
    } catch (Exception e) {
      log.error(EVENT_RELEASED, e);
    }
  }
}
