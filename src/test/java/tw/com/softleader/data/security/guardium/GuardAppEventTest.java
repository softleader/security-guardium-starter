package tw.com.softleader.data.security.guardium;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GuardAppEventTest {

  @Test
  void testCollect() {
    GuardAppEvent event = new GuardAppEvent(
        "XXXX",
        "YYYY",
        "ZZZZ");

    String expected = "'GuardAppEventUserName:XXXX','GuardAppEventType:YYYY','GuardAppEventStrValue:ZZZZ'";
    String actual = event.toString();
    Assertions.assertEquals(expected, actual);
  }
}
