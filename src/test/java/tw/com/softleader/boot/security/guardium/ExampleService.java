package tw.com.softleader.boot.security.guardium;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.com.softleader.boot.security.guardium.annotation.Safeguard;

@Safeguard
@Service
public class ExampleService {

  @Transactional
  public void hello() {
    // no-op
  }
}
