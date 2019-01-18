package tw.com.softleader.boot.security.guardium;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/** EventDataSupplier 提供 GuardAppEvent 在 runtime 每次呼叫取用 */
public interface EventDataSupplier {

  Map<String, String> get(Method method, Object[] args);

  /**
   * 將內容轉換成 event 格式
   *
   * @return
   */
  default String collect(Method method, Object[] args) {
    return get(method, args)
        .entrySet()
        .stream()
        .map(e -> String.format("%s:%s", e.getKey(), e.getValue()))
        .collect(Collectors.joining("','", "'", "'"));
  }
}
