package tw.com.softleader.data.security.guardium;

import java.lang.reflect.Method;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class ExampleGuardAppEventSupplier implements GuardAppEventSupplier {

  @Override
  public GuardAppEvent get(Method method, Object[] args) {
    return GuardAppEvent.builder().userName("XXX").type(method.toString()).strValue("ZZZ").build();
  }
}
