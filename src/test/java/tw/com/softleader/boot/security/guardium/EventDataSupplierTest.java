package tw.com.softleader.boot.security.guardium;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class EventDataSupplierTest {

  @Test
  public void testCollect() {
    EventDataSupplier supplier =
        (target, args) -> {
          Map<String, String> map = new LinkedHashMap<>();
          map.put("GuardAppEventUserName", "XXXX");
          map.put("GuardAppEventType", "YYYY");
          map.put("GuardAppEventStrValue", "ZZZZ");
          return map;
        };

    String expected =
        "'GuardAppEventUserName:XXXX','GuardAppEventType:YYYY','GuardAppEventStrValue:ZZZZ'";
    String actual = supplier.collect(null, null);
    Assert.assertEquals(expected, actual);
  }
}
