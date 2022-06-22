package tw.com.softleader.data.security.guardium;

import java.lang.reflect.Method;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Follow IBM Security Guardium 版本 10 的規格的實作
 */
@RequiredArgsConstructor
public class IBMSecurityGuardium10Api extends NativeQueryGuardiumApi {

  @NonNull
  final GuardAppEventSupplier guardAppEventSupplier;
  @Getter
  @NonNull
  final NativeQuery nativeQuery;

  @Override
  public String startSql(Method method, Object[] args) {
    return String.format("SELECT '%s',%s",
        EVENT_START,
        guardAppEventSupplier.get(method, args).toString());
  }

  @Override
  public String releasedSql() {
    return String.format("SELECT '%s'", EVENT_RELEASED);
  }
}
