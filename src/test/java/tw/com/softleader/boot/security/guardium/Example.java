package tw.com.softleader.boot.security.guardium;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Data
public class Example implements EventDataSupplier {

  private int calls;

  @Override
  public Map<String, String> get(Method method, Object[] args) {
    calls++;
    Map<String, String> map = new LinkedHashMap<>();
    map.put("GuardAppEventUserName", "XXXX");
    map.put("GuardAppEventType", method.toString());
    map.put("GuardAppEventStrValue", "ZZZZ");
    return map;
  }
}
