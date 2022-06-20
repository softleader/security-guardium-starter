package tw.com.softleader.data.security.guardium;

import java.lang.reflect.Method;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcOperations;

/**
 * IBM Security Guardium 10 Oracle的實作，多補上from dual
 *
 * @author Robert
 */
@Slf4j
@RequiredArgsConstructor
public class IBMSecurityGuardium10OracleGuardAppEventApi implements GuardAppEventApi {

  private static final String EVENT_START = "GuardAppEvent:Start";
  private static final String EVENT_RELEASED = "GuardAppEvent:Released";

  @NonNull
  private final JdbcOperations jdbcOperations;
  @NonNull
  private final GuardAppEventSupplier guardAppEventSupplier;

  @Override
  public void start(Method method, Object[] args) {
    try {
      String sql = String.format("SELECT '%s',%s from dual", EVENT_START,
          guardAppEventSupplier.get(method, args).toString());
      log.trace("Setting an application event: {}", sql);
      jdbcOperations.execute(sql);
    } catch (Exception e) {
      log.error(EVENT_START, e);
    }
  }

  @Override
  public void released() {
    try {
      String sql = String.format("SELECT '%s' from dual", EVENT_RELEASED);
      log.trace("Clearing an application event: {}", sql);
      jdbcOperations.execute(sql);
    } catch (Exception e) {
      log.error(EVENT_RELEASED, e);
    }
  }
}
