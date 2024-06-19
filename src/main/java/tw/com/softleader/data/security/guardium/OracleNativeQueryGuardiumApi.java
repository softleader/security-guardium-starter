package tw.com.softleader.data.security.guardium;

import java.lang.reflect.Method;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OracleNativeQueryGuardiumApi extends NativeQueryGuardiumApi {

  @NonNull final NativeQueryGuardiumApi delegate;

  @Override
  public String startSql(Method method, Object[] args) {
    return delegate.startSql(method, args) + " FROM DUAL";
  }

  @Override
  public String releasedSql() {
    return delegate.releasedSql() + " FROM DUAL";
  }

  @Override
  protected NativeQuery getNativeQuery() {
    return delegate.getNativeQuery();
  }
}
