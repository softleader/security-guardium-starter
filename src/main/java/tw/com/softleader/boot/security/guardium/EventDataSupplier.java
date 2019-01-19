package tw.com.softleader.boot.security.guardium;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * EventDataSupplier 提供 {@link GuardAppEvent} 在 runtime 每次呼叫取用
 *
 * @author matt
 */
public interface EventDataSupplier {

  /**
   * 整合 java 原生的 interface: {@link BiFunction}
   *
   * <p>目的是因為有些共用的 class (如開在 softleader-product/softleader-jasmine-config) 可以輕易地透過實作 java interface
   * 再 proxy 到此專案
   *
   * @see BiFunction
   * @param proxy
   * @return
   */
  static EventDataSupplier proxy(BiFunction<Method, Object[], Map<String, String>> proxy) {
    return proxy::apply;
  }

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
